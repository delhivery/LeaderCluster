package com.delhivery.clustering;

import static com.delhivery.clustering.distances.DistanceMeasure.HAVERSINE;
import static com.delhivery.clustering.elements.ClusterImpl.ClusterBuilder.newInstance;
import static com.delhivery.clustering.preclustering.PreClusteringFactory.NO_PRECLUSTERING;
import static com.delhivery.clustering.reduction.ReductionFactory.NO_REDUCTION;
import static com.delhivery.clustering.utils.Utils.iDCreator;
import static com.google.common.collect.Sets.difference;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.isNull;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import org.slf4j.Logger;

import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.Geocode;
import com.delhivery.clustering.exception.BuilderException;
import com.delhivery.clustering.preclustering.PreClustering;
import com.delhivery.clustering.preclustering.PreClusteringFactory;
import com.delhivery.clustering.reduction.Reducer;
import com.delhivery.clustering.reduction.ReductionFactory;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class LC {
    private static final Logger                  LOGGER        = getLogger(LC.class);
    private static final Comparator<Clusterable> WEIGHT_SORTED = reverseOrder(comparingDouble(Clusterable::weight)).thenComparing(comparing(Clusterable::id));

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

        Reducer reducer = reducerFactory.createReducer(points);

        PreClustering preClustering = this.preClusterer.createPreClusterer(reducer.compressedClusterables());

        Collection<Cluster> clusters = new TreeSet<>(WEIGHT_SORTED);

        clusters.addAll(preClustering.preclusters(idFactory));

        List<Clusterable> toBeClustered = new ArrayList<>(preClustering.unclusteredPoints());

        toBeClustered.sort(WEIGHT_SORTED);

        LOGGER.info("Sorted clusterables point in decending order of their weights.");
        
        Set<Integer> allIndex = range(0, toBeClustered.size()).boxed().collect(toSet());
        Set<Integer> addedClusterables = new HashSet<>();

        for (int ptIdx = 0; ptIdx < toBeClustered.size(); ptIdx++) {
            if (addedClusterables.contains(ptIdx))
                continue;

            Clusterable point = toBeClustered.get(ptIdx);
            addedClusterables.add(ptIdx);
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

            boolean goReverse = false;

            if (isNull(fitCluster)) {
                fitCluster = newInstance(idFactory.get()).build();
                goReverse = true;
                LOGGER.debug("No cluster found for Clusterable: {}. So creating new cluster. cluster id: {}", point, fitCluster.id());
            }

            fitCluster.consumeClusterer(point);

            if (goReverse) {
                List<Integer> remaining = difference(allIndex, addedClusterables).stream()
                                                                                 .sorted(comparingDouble(
                                                                                         j -> HAVERSINE.distance(point.geocode(),
                                                                                                 toBeClustered.get(j).geocode())))
                                                                                 .collect(toList());

                for (Integer r : remaining) {
                    Clusterable pt = toBeClustered.get(r);
                    if (fitForCluster.test(fitCluster, pt)) {
                        fitCluster.consumeClusterer(pt);
                        addedClusterables.add(r);

                    }
                }
            }

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
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        final Cluster                 cluster;
        final Collection<Clusterable> members;

        ClusterWithDecompressedClusterables(Cluster cluster, Reducer reducer) {
            this.cluster = cluster;
            this.members = cluster.getMembers()
                                  .stream()
                                  .map(reducer::decompressClusterable)
                                  .flatMap(Collection::stream)
                                  .collect(toList());;
        }

        @Override
        public Geocode geocode() {
            return cluster.geocode();
        }

        @Override
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

        /**
         * Constraint whether or not a clusterable should be added to a cluster.
         * @param fitForCluster
         * @return
         */
        public LCBuilder constraint(BiPredicate<Cluster, Clusterable> fitForCluster) {
            this.fitForCluster = fitForCluster;
            return this;
        }

        /**
         * Refinement of output cluster from Leader clustering algorithm.
         * @param refinement
         * @return
         */
        public LCBuilder refine(UnaryOperator<Collection<Cluster>> refinement) {
            this.refineCluster = refinement;
            return this;
        }

        /**
         * Merges some of the clusterables points. These merged points will then
         * be sent to LC and after that clusterables in Cluster will be expanded.
         *  
         * @param reductionFactory
         * @return
         */
        public LCBuilder reductingClusterables(ReductionFactory reductionFactory) {
            this.reducerFactory = reductionFactory;
            return this;
        }

        /**
         * Provides initial clusters for LC. This clusters will serve as initial seed points
         * for remaining clusterbles points.
         * 
         * @param preClustering
         * @return
         */
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

            if (isNull(this.reducerFactory))
                this.reducerFactory = NO_REDUCTION;

            if (isNull(this.preClustering))
                this.preClustering = NO_PRECLUSTERING;

            return new LC(this).process();
        }
    }

}
