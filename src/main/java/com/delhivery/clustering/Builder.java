package com.delhivery.clustering;

import static com.delhivery.clustering.reduction.ReductionFactory.NO_REDUCTION;
import static com.delhivery.clustering.reduction.ReductionFactory.REDUCE_ON_GEOCODE;
import static com.delhivery.clustering.utils.Utils.distanceConstraint;
import static com.delhivery.clustering.utils.Utils.isZero;
import static com.delhivery.clustering.utils.Utils.weightedGeocode;
import static com.google.common.collect.Streams.stream;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.Geocode;
import com.delhivery.clustering.exception.BuilderException;
import com.delhivery.clustering.reduction.ReductionFactory;
import com.delhivery.clustering.refinement.AssignToNearest;
import com.delhivery.clustering.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * This class simplifies Clustering using high level description.
 * 
 * @author Shiv Krishna Jaiswal
 *
 */
public final class Builder {
	private static final Logger LOGGER = getLogger(Builder.class);

	private final Collection<Clusterable>     clusterables;
	private Double                            throwDistance;
	private DistanceMeasure                   distanceMeasure;
	private BiPredicate<Geocode, Geocode>     connectivity;
	private int                               assignToNearest;
	private boolean                           mergeDuplicateClusterables;
	private BiPredicate<Cluster, Clusterable> otherConstraints;
	private Double                            reverseThrow;
	private Supplier<Cluster>                 clusterSupplier;

	private Builder(Collection<? extends Clusterable> clusterables) {
		this.clusterables = unmodifiableCollection(clusterables);
		this.assignToNearest = 0;
		this.mergeDuplicateClusterables = false;
	}

	public static Builder newInstance(Collection<? extends Clusterable> clusterables) {
		return new Builder(clusterables);
	}

	public Builder clusterSupplier(Supplier<Cluster> supplier) {
		this.clusterSupplier = supplier;
		return this;
	}

	/**
	 * Each element of jsonArray "clusterables" must be jsonObject with 
	 * keys "id", "lat", "lng", "weight".
	 * 
	 * @param clusterables
	 * @return
	 */
	public static Builder newInstance(JsonArray clusterables) {
		return new Builder(stream(clusterables).map(JsonElement::getAsJsonObject).map(Utils::createClusterable).collect(toList()));
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

	/**
	 * To add other clustering constraints which needs to 
	 * be respected while creating cluster.
	 * 
	 * @param otherConstraints
	 * @return
	 */
	public Builder otherConstraint(BiPredicate<Cluster, Clusterable> otherConstraints) {
		this.otherConstraints = otherConstraints;
		return this;
	}

	public Builder reverseThrow(double reverseThrow) {
		this.reverseThrow = reverseThrow;
		return this;
	}

	private static Collection<Cluster> inDecreasingOrderOfWeight(Collection<Cluster> clusters) {
		List<Cluster> out = new ArrayList<>(clusters);

		out.sort(reverseOrder(comparingDouble(Cluster::weight)));

		LOGGER.info("Sorted clusters in decreasing order of their weights");

		return out;
	}

	private static class DistanceConstraint implements BiPredicate<Cluster, Clusterable> {
		/**
		 * This class tests if a clusterable qualifies to be added to a cluster. 
		 */
		final BiPredicate<Geocode, Geocode> insideThrow;

		DistanceConstraint(BiPredicate<Geocode, Geocode> insideThrow) {
			this.insideThrow = insideThrow;
		}

		@Override
		public boolean test(Cluster cluster, Clusterable clusterable) {
			Geocode clusterCoord = cluster.geocode(), clusterableCoord = clusterable.geocode();

			if (insideThrow.test(clusterCoord, clusterableCoord)) {
				double newWeight = cluster.weight() + clusterable.weight();

				Geocode updatedCoord = isZero(newWeight) ? clusterableCoord : weightedGeocode(cluster, clusterable);

				Predicate<Geocode> reachable = c -> insideThrow.test(updatedCoord, c);

				return cluster.getMembers()
				              .stream()
				              .map(Clusterable::geocode)
				              .allMatch(reachable);

			}

			return false;

		}
	}

	private BiPredicate<Cluster, Clusterable> createThrowConstraint(Double distance) {
		if (nonNull(distance))
			return new DistanceConstraint(distanceConstraint(distance, distanceMeasure));

		return (x, y) -> true;
	}

	public Collection<Cluster> build() {
		requireNonNull(this.distanceMeasure, "distance measure has not been set");

		if (isNull(this.throwDistance) && isNull(this.otherConstraints))
			throw new BuilderException("No constraint has been provided for clustering");

		BiPredicate<Cluster, Clusterable> distanceConstraint = createThrowConstraint(this.throwDistance);

		BiPredicate<Cluster, Clusterable> constraint = distanceConstraint;

		if (nonNull(this.otherConstraints))
			constraint = constraint.and(this.otherConstraints);

		BiPredicate<Cluster, Clusterable> connectivityConstraint = null;

		if (nonNull(this.connectivity))
			connectivityConstraint = (from, to) -> this.connectivity.test(from.geocode(), to.geocode());
		else
			connectivityConstraint = (from, to) -> true;

		constraint = constraint.and(connectivityConstraint);

		ReductionFactory reductionFactory = this.mergeDuplicateClusterables ? REDUCE_ON_GEOCODE : NO_REDUCTION;

		UnaryOperator<Collection<Cluster>> refinement = identity();

		if (this.assignToNearest > 0) {
			BiPredicate<Cluster, Clusterable> refinementConstraint = distanceConstraint.and(connectivityConstraint);

			UnaryOperator<Collection<Cluster>> assignToNearest = new AssignToNearest(distanceMeasure, refinementConstraint);

			while (this.assignToNearest-- > 0)
				refinement = refinement.andThen(assignToNearest)::apply;
		}

		refinement = refinement.andThen(Builder::inDecreasingOrderOfWeight)::apply;
		BiPredicate<Cluster, Clusterable> reverseStrategy = null;

		if (nonNull(reverseThrow))
			reverseStrategy = createThrowConstraint(reverseThrow);
		else
			reverseStrategy = (x, y) -> false;

		return LCBuilder.newInstance(clusterables)
		                .constraint(constraint)
		                .reductingClusterables(reductionFactory)
		                .refine(refinement)
		                .reverseClusteringStrategy(reverseStrategy)
		                .clusterSupplier(clusterSupplier)
		                .build();

	}
}
