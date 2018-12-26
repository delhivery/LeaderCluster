package com.delhivery.refactoring;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.refactoring.distances.DistanceMeasure;

public final class LC implements Clusterer {
    private static final Logger                  LOGGER        = getLogger(LC.class);
    private static final Comparator<Clusterable> WEIGHT_SORTED = reverseOrder(comparingDouble(Clusterable::weight));
    private static final Comparator<ClusterImpl> CLUSTER_COMP  = reverseOrder(comparingDouble(ClusterImpl::weight).thenComparing(comparing(ClusterImpl::id)));

    private final BiPredicate<Cluster, Clusterable> fitForCluster;

    private LC(LCBuilder builder) {
        this.fitForCluster = builder.fitForCluster;
    }

    @Override
    public Collection<Cluster> cluster(Collection<? extends Clusterable> points) {
        LOGGER.info("Leader clustering starts with: {} clusterable points", points.size());

        List<Clusterable> toBeClustered = new ArrayList<>(points);
        toBeClustered.sort(WEIGHT_SORTED);

        LOGGER.info("Sorted clusterables point in decending order of their weights.");

        SortedSet<ClusterImpl> clusters = new TreeSet<>(CLUSTER_COMP);

        int clusterIndexer = 0;

        for (Clusterable point : toBeClustered) {
            LOGGER.debug("Clusterable: {} is going to be assigned to a cluster", point);

            Iterator<ClusterImpl> itrCluster = clusters.iterator();
            ClusterImpl fitCluster = null;

            while (itrCluster.hasNext()) {
                ClusterImpl cluster = itrCluster.next();

                if (fitForCluster.test(cluster, point)) {
                    itrCluster.remove();
                    fitCluster = cluster;

                    LOGGER.debug("Cluster: {} is found for clusterables:", fitCluster, point);
                }
            }

            if (isNull(fitCluster)) {
                fitCluster = new ClusterImpl(clusterIndexer++ + "");
                LOGGER.debug("No cluster found for Clusterable: {}. So creating new cluster.", point);
            }

            fitCluster.consumeClusterer(point);
            clusters.add(fitCluster);

            LOGGER.debug("clusterable: {} added to cluster: {}", point, fitCluster);
        }

        LOGGER.info("Number of clusters created: {}", clusters.size());

        return unmodifiableCollection(clusters);
    }

    public static final class LCBuilder {
        private static final Logger LOGGER = getLogger(LCBuilder.class);

        private BiPredicate<Cluster, Clusterable>  fitForCluster;
        private UnaryOperator<Collection<Cluster>> refineCluster;
        private ReductionFactory                   reducerFactory;

        private LCBuilder() {}

        public static LCBuilder newInstance() {
            return new LCBuilder();
        }

        private static class DistanceConstraint implements BiPredicate<Cluster, Clusterable> {
            final BiPredicate<Clusterable, Clusterable> checking;

            DistanceConstraint(double distance, DistanceMeasure distanceMeasure) {
                this.checking = (x, y) -> distanceMeasure.distance(x.geocode(), y.geocode()) <= distance;
            }

            @Override
            public boolean test(Cluster t, Clusterable u) {
                return checking.test(t, u) && t.getMembers().stream().allMatch(m -> checking.test(m, u));
            }
        }

        public LCBuilder distanceConstraint(double maxDistance, DistanceMeasure distanceMeasure) {
            return constraint(new DistanceConstraint(maxDistance, distanceMeasure));
        }

        public LCBuilder constraint(BiPredicate<Cluster, Clusterable> fitForCluster) {
            if (isNull(this.fitForCluster))
                this.fitForCluster = fitForCluster;
            else
                this.fitForCluster = fitForCluster.and(this.fitForCluster);

            return this;
        }

        public LCBuilder refine(UnaryOperator<Collection<Cluster>> refinement) {

            if (isNull(this.refineCluster))
                this.refineCluster = refinement;
            else {
                this.refineCluster = refinement.andThen(this.refineCluster)::apply;
            }

            return this;
        }

        public LCBuilder refineAssignToClosestCluster(int times, DistanceMeasure distanceMeasure) {

            UnaryOperator<Collection<Cluster>> assignToNearest = new AssignToNearest(distanceMeasure);

            UnaryOperator<Collection<Cluster>> refinement = x -> x;

            while (times-- > 0)
                refinement = refinement.andThen(assignToNearest::apply)::apply;

            return refine(refinement::apply);
        }

        public LCBuilder enableLcOnCompressedClusterables() {
            this.reducerFactory = DuplicacyRemoval::new;

            LOGGER.debug("Enabling leadering clustering on clusterable which "
                + "will be reduced/aggregated on their geocode.");

            return this;
        }

        public Clusterer build() {
            if (isNull(this.fitForCluster)) {
                LOGGER.error("Criteria to add a clusterable point to a cluster has not been provided");
                throw new BuilderException("Criteria to add clusterable to a cluster has not been provided");
            }

            if (isNull(this.refineCluster)) {
                this.refineCluster = x -> x;
                LOGGER.info("No refinement strategy provided. Defaulting to identity.");
            }

            Clusterer lc = new LC(this);

            Clusterer algorithm = clusterables -> this.refineCluster.apply(lc.cluster(clusterables));

            if (nonNull(this.reducerFactory)) {
                LOGGER.info("Reduction factory provided. This will be used to run LC "
                    + "on reduced number of points.");
                algorithm = new ReduceLC(reducerFactory, algorithm);
            }

            return algorithm;
        }
    }

    private final static class ReduceLC implements Clusterer {
        private static final Logger LOGGER = getLogger(ReduceLC.class);

        final ReductionFactory reductionFactory;
        final Clusterer        lc;

        ReduceLC(ReductionFactory reducerFactory, Clusterer lc) {
            this.reductionFactory = reducerFactory;
            this.lc = lc;
        }

        @Override
        public Collection<Cluster> cluster(Collection<? extends Clusterable> clusterables) {
            LOGGER.info("Number of point before reduction:" + clusterables.size());

            Reducer<?> reducer = reductionFactory.createReducer(clusterables);

            clusterables = reducer.compressedClusterables();

            LOGGER.info("Number of points after reduction: {}", clusterables.size());

            Collection<Cluster> clusters = lc.cluster(clusterables);

            LOGGER.debug("Decompressing starts.");

            clusters = clusters.stream()
                               .map(cluster -> new ClusterWithDecompressedClusterables(cluster, reducer))
                               .collect(toList());

            LOGGER.debug("Decompressing Ends.");

            return clusters;
        }

        private static class ClusterWithDecompressedClusterables implements Cluster {
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

        }

    }

}
