package com.delhivery.clustering;

import static com.delhivery.clustering.reduction.ReductionFactory.NO_REDUCTION;
import static com.delhivery.clustering.reduction.ReductionFactory.REDUCE_ON_GEOCODE;
import static com.delhivery.clustering.utils.Utils.distanceConstraint;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.UnaryOperator.identity;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.Geocode;
import com.delhivery.clustering.reduction.ReductionFactory;
import com.delhivery.clustering.refinement.AssignToNearest;

/**
 * This class simplifies Clustering using high level description.
 * 
 * @author Shiv Krishna Jaiswal
 *
 */
public final class Builder {
    private static final Logger LOGGER = getLogger(Builder.class);

    private final Collection<Clusterable> clusterables;
    private Double                        throwDistance;
    private Double                        maxLoad;
    private DistanceMeasure               distanceMeasure;
    private BiPredicate<Geocode, Geocode> connectivity;
    private int                           assignToNearest;
    private boolean                       mergeDuplicateClusterables;

    private Builder(Collection<? extends Clusterable> clusterables) {
        this.clusterables = unmodifiableCollection(clusterables);
        this.assignToNearest = 0;
        this.mergeDuplicateClusterables = false;
    }

    public static Builder newInstance(Collection<? extends Clusterable> clusterables) {
        return new Builder(clusterables);
    }

    /**
     * Throw distance for leader cluster. Each clusterable point in 
     * the cluster will be atmost this far from the cluster centroid.
     * 
     * @param throwDistance
     * @return
     */
    public Builder throwDistance(double throwDistance) {
        this.throwDistance = throwDistance;
        return this;
    }

    /**
     * Method to calculate distance between two point.
     * @param distanceMeasure
     * @return
     */
    public Builder distanceMeasure(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
        return this;
    }

    public Builder maxLoad(double maxLoad) {
        this.maxLoad = maxLoad;
        return this;

    }

    /**
     * 
     * @param connectivity: method to check if two location are connected.
     * @return
     */
    public Builder connectivity(BiPredicate<Geocode, Geocode> connectivity) {
        this.connectivity = connectivity;
        return this;
    }

    /**
     * Assigning clusterables to nearest cluster considering distance and 
     * connectivity constraint,
     * 
     * @param times
     * @return
     */
    public Builder refinementAfterClustering(int times) {
        this.assignToNearest = times;
        return this;
    }

    /**
     * Before running LC, merging clusterables on the basis of their Coordinate.
     * @return
     */
    public Builder enableMergingSameLocationClusterables() {
        this.mergeDuplicateClusterables = true;
        return this;
    }

    private static Collection<Cluster> inDecreasingOrderOfWeight(Collection<Cluster> clusters) {
        List<Cluster> out = new ArrayList<>(clusters);

        out.sort(reverseOrder(comparingDouble(Cluster::weight)));

        LOGGER.info("Sorted clusters in decreasing order of their weights");

        return out;
    }

    public Collection<Cluster> build() {
        requireNonNull(this.throwDistance, "Throw distance has not been set");
        requireNonNull(this.distanceMeasure, "distance measure has not been set");

        BiPredicate<Cluster, Clusterable> constraint = distanceConstraint(this.throwDistance, this.distanceMeasure);

        if (nonNull(this.maxLoad))
            constraint = constraint.and((x, y) -> x.weight() + y.weight() <= this.maxLoad);

        if (nonNull(this.connectivity))
            constraint = constraint.and((from, to) -> this.connectivity.test(from.geocode(), to.geocode()));

        ReductionFactory reductionFactory = this.mergeDuplicateClusterables ? REDUCE_ON_GEOCODE : NO_REDUCTION;

        UnaryOperator<Collection<Cluster>> refinement = identity();

        UnaryOperator<Collection<Cluster>> assignToNearest = new AssignToNearest(distanceMeasure, constraint);

        while (this.assignToNearest-- > 0)
            refinement = refinement.andThen(assignToNearest)::apply;

        refinement = refinement.andThen(Builder::inDecreasingOrderOfWeight)::apply;
        // sorting clusters in decreasing of their weights

        return LCBuilder.newInstance(clusterables)
                        .constraint(constraint)
                        .reductingClusterables(reductionFactory)
                        .refine(refinement)
                        .build();
    }
}
