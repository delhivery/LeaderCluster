package com.delhivery.clustering;

import static com.delhivery.clustering.elements.ClusterImpl.ClusterBuilder.newInstance;
import static com.delhivery.clustering.preclustering.PreClusteringFactory.NO_PRECLUSTERING;
import static com.delhivery.clustering.reduction.ReductionFactory.NO_REDUCTION;
import static com.delhivery.clustering.reduction.ReductionFactory.REDUCE_ON_GEOCODE;
import static com.delhivery.clustering.utils.Utils.iDCreator;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.isNull;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.Geocode;
import com.delhivery.clustering.exception.BuilderException;
import com.delhivery.clustering.preclustering.PreClustering;
import com.delhivery.clustering.preclustering.PreClusteringFactory;
import com.delhivery.clustering.reduction.Reducer;
import com.delhivery.clustering.reduction.ReductionFactory;
import com.delhivery.clustering.refinement.AssignToNearest;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class LC {
    private static final Logger                  LOGGER        = getLogger(LC.class);
    private static final Comparator<Clusterable> WEIGHT_SORTED = reverseOrder(comparingDouble(Clusterable::weight));
    private static final Comparator<Cluster>     CLUSTER_COMP  = reverseOrder(comparingDouble(Cluster::weight).thenComparing(comparing(Cluster::id)));

    private final Supplier<String>                   idFactory;
    private final Collection<Clusterable>            points;
    private final BiPredicate<Cluster, Clusterable>  fitForCluster;
    private final PreClusteringFactory               preClusterer;
    private final ReductionFactory                   reducerFactory;
    private final UnaryOperator<Collection<Cluster>> refineCluster;

    private LC(LCBuilder builder) {
        this.idFactory = iDCreator();
        this.points = builder.clusterables;
        this.fitForCluster = builder.fitForCluster;
        this.preClusterer = builder.preClustering;
        this.reducerFactory = builder.reducerFactory;
        this.refineCluster = builder.refineCluster;
    }

    private Collection<Cluster> process() {
        LOGGER.info("Leader clustering starts with: {} clusterable points", points.size());

        Reducer<?> reducer = reducerFactory.createReducer(points);

        PreClustering preClustering = this.preClusterer.createPreClusterer(reducer.compressedClusterables());

        Collection<Cluster> clusters = new TreeSet<>(CLUSTER_COMP);

        clusters.addAll(preClustering.preclusters(idFactory));

        List<Clusterable> toBeClustered = new ArrayList<>(preClustering.unclusteredPoints());

        toBeClustered.sort(WEIGHT_SORTED);

        LOGGER.info("Sorted clusterables point in decending order of their weights.");

        for (Clusterable point : toBeClustered) {
            LOGGER.debug("Clusterable: {} is going to be assigned to a cluster", point);

            Iterator<Cluster> itrCluster = clusters.iterator();
            Cluster fitCluster = null;

            while (itrCluster.hasNext()) {
                Cluster cluster = itrCluster.next();

                if (fitForCluster.test(cluster, point)) {
                    itrCluster.remove();
                    fitCluster = cluster;
                    break;
                }
            }

            if (isNull(fitCluster)) {
                fitCluster = newInstance(idFactory.get()).build();
                LOGGER.debug("No cluster found for Clusterable: {}. So creating new cluster. cluster id: {}", point, fitCluster.id());
            }

            fitCluster.consumeClusterer(point);
            clusters.add(fitCluster);

            LOGGER.debug("clusterable: {} added to cluster: {}", point, fitCluster);
        }

        clusters = refineCluster.apply(clusters);

        LOGGER.info("Number of clusters created: {}", clusters.size());

        return unmodifiableCollection(clusters.stream()
                                              .map(c -> new ClusterWithDecompressedClusterables(c, reducer))
                                              .collect(toList()));
    }

    private final static class ClusterWithDecompressedClusterables implements Cluster {
        final Cluster                 cluster;
        final Collection<Clusterable> members;

        ClusterWithDecompressedClusterables(Cluster cluster, Reducer<?> reducer) {
            this.cluster = cluster;
            this.members = cluster.getMembers()
                                  .stream()
                                  .map(reducer::decompressClusterable)
                                  .flatMap(Collection::stream)
                                  .collect(toList());;
        }

        public Geocode geocode() {
            return cluster.geocode();
        }

        public double weight() {
            return cluster.weight();
        }

        @Override
        public Collection<Clusterable> getMembers() {

            return unmodifiableCollection(members);
        }

        @Override
        public String toString() {
            return members.toString();
        }

        @Override
        public void consumeClusterer(Clusterable point) {
            throw new UnsupportedOperationException("Cluster has stopped comsuming more points.");
        }

        @Override
        public String id() {
            return cluster.id();
        }

    }

    public static final class LCBuilder {
        private static final Logger LOGGER = getLogger(LCBuilder.class);

        private final Collection<Clusterable> clusterables;

        private BiPredicate<Cluster, Clusterable>  fitForCluster;
        private UnaryOperator<Collection<Cluster>> refineCluster;
        private ReductionFactory                   reducerFactory;
        private PreClusteringFactory               preClustering;

        private LCBuilder(Collection<? extends Clusterable> clusterables) {
            this.clusterables = unmodifiableCollection(clusterables);
        }

        public static LCBuilder newInstance(Collection<? extends Clusterable> clusterables) {
            return new LCBuilder(clusterables);
        }

        private static class DistanceConstraint implements BiPredicate<Cluster, Clusterable> {
            final BiPredicate<Clusterable, Clusterable> constraint;

            DistanceConstraint(double distance, DistanceMeasure distanceMeasure) {
                this.constraint = (x, y) -> distanceMeasure.distance(x.geocode(), y.geocode()) <= distance;
            }

            @Override
            public boolean test(Cluster t, Clusterable u) {
                return constraint.test(t, u) && t.getMembers().stream().allMatch(m -> constraint.test(m, u));
            }
        }

        /**
         * Constraint that each point in cluster should not be more than
         * "max distance".
         * Notice that if Refinement of leader clusters is used then this may be violated. 
         * @param maxDistance
         * @param distanceMeasure
         * @return
         */
        public LCBuilder distanceConstraint(double maxDistance, DistanceMeasure distanceMeasure) {
            return constraint(new DistanceConstraint(maxDistance, distanceMeasure));
        }

        /**
         * Constraint whether or not a clusterable should be added to a cluster.
         * @param fitForCluster
         * @return
         */
        public LCBuilder constraint(BiPredicate<Cluster, Clusterable> fitForCluster) {
            if (isNull(this.fitForCluster))
                this.fitForCluster = fitForCluster;
            else
                this.fitForCluster = this.fitForCluster.and(fitForCluster);

            return this;
        }

        /**
         * Refinement of output cluster from Leader clustering algorithm.
         * @param refinement
         * @return
         */
        public LCBuilder refine(UnaryOperator<Collection<Cluster>> refinement) {

            if (isNull(this.refineCluster))
                this.refineCluster = refinement;
            else
                this.refineCluster = this.refineCluster.andThen(refinement)::apply;

            return this;
        }

        /**
         * A refine strategy which assigns clusterables to nearest cluster available.
         * @param times: Number of times this strategy should be used.
         * @param distanceMeasure
         * @return
         */
        public LCBuilder refineAssignToClosestCluster(int times, DistanceMeasure distanceMeasure) {

            UnaryOperator<Collection<Cluster>> assignToNearest = new AssignToNearest(distanceMeasure);

            UnaryOperator<Collection<Cluster>> refinement = identity();

            while (times-- > 0)
                refinement = refinement.andThen(assignToNearest)::apply;

            return refine(refinement::apply);
        }

        /**
         * A refine strategy which assigns clusterables to nearest cluster available considering hard constraint.
         * @param times: Number of times this strategy should be used.
         * @param distanceMeasure
         * @param hardConstraint
         * @return
         */
        public LCBuilder refineAssignToClosestCluster(int times, DistanceMeasure distanceMeasure, BiPredicate<Geocode, Geocode> hardConstraint) {

            UnaryOperator<Collection<Cluster>> assignToNearest = new AssignToNearest(distanceMeasure, hardConstraint);

            UnaryOperator<Collection<Cluster>> refinement = identity();

            while (times-- > 0)
                refinement = refinement.andThen(assignToNearest)::apply;

            return refine(refinement::apply);
        }

        /**
         * If enabled, then clusterables having same Geocode will be grouped before
         * Leader cluster and after clustering, clusters will be expanded with
         * actual points having representation earlier in cluster.
         * @return
         */
        public LCBuilder enableLcOnCompressedClusterables() {
            this.reducerFactory = REDUCE_ON_GEOCODE;

            LOGGER.debug("Enabling leadering clustering on clusterable which "
                + "will be reduced/aggregated on their geocode.");

            return this;
        }

        /**
         * @param clusters: returning clusters in decreasing order of their weight
         * @return
         */
        private static Collection<Cluster> inDecreasingOrderOfWeight(Collection<Cluster> clusters) {
            List<Cluster> out = new ArrayList<>(clusters);

            out.sort(WEIGHT_SORTED);

            LOGGER.info("Sorted clusters in decreasing order of their weights");

            return out;
        }

        public LCBuilder preclustering(PreClusteringFactory preClustering) {
            this.preClustering = preClustering;
            return this;
        }

        public Collection<Cluster> build() {
            if (isNull(this.fitForCluster)) {
                LOGGER.error("Criteria to add a clusterable point to a cluster has not been provided");
                throw new BuilderException("Criteria to add clusterable to a cluster has not been provided");
            }

            if (isNull(this.refineCluster)) {
                this.refineCluster = identity();
                LOGGER.info("No refinement strategy provided. Defaulting to identity.");
            }

            refine(LCBuilder::inDecreasingOrderOfWeight);// output clusters will be in decreasing order of their weight.

            if (isNull(this.reducerFactory))
                this.reducerFactory = NO_REDUCTION;

            if (isNull(this.preClustering))
                this.preClustering = NO_PRECLUSTERING;

            return new LC(this).process();
        }
    }

}
