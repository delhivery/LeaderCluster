package com.delhivery.clustering.refinement;

import static com.delhivery.clustering.utils.Utils.distanceConstraint;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

import org.junit.Before;
import org.junit.Test;

import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.ClusterImpl.ClusterBuilder;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.ClusterableImpl;
import com.delhivery.clustering.elements.Geocode;

public class TestRefinement {
	DistanceMeasure distanceMeasure = null;
	Cluster         cluster1, cluster2;

	@Before
	public void setup() {
		this.distanceMeasure = DistanceMeasure.EUDLIDEAN_DISTANCE;
		this.cluster1 = ClusterBuilder.newInstance("1").geocode(new Geocode(0, 0)).weight(1).build();
		this.cluster2 = ClusterBuilder.newInstance("2").geocode(new Geocode(1, 0)).weight(1).build();
	}

	@Test
	public void test1() {

		Clusterable point = new ClusterableImpl("0", new Geocode(0.9, 0), 1);
		cluster1.consumeClusterer(point);

		BiPredicate<Geocode, Geocode> distanceConstaint = distanceConstraint(1, distanceMeasure);

		UnaryOperator<Collection<Cluster>> refiner = new AssignToNearest(distanceMeasure,
		                                                                 (from, to) -> distanceConstaint.test(from.geocode(), to.geocode()));

		Collection<Cluster> outputClusters = refiner.apply(asList(cluster1, cluster2));

		assertEquals(outputClusters.size(), 1);

		Cluster onlyCluster = outputClusters.iterator().next();

		assertEquals(onlyCluster.id(), cluster2.id());
	}

	@Test
	public void test2() {
		Cluster cluster1 = ClusterBuilder.newInstance("1").geocode(new Geocode(0, 0)).weight(1).build();
		Cluster cluster2 = ClusterBuilder.newInstance("2").geocode(new Geocode(1, 0)).weight(1).build();

		Clusterable point = new ClusterableImpl("0", new Geocode(0.9, 0), 1);
		cluster1.consumeClusterer(point);

		DistanceMeasure distanceMeasure = DistanceMeasure.EUDLIDEAN_DISTANCE;

		BiPredicate<Geocode, Geocode> distanceConstaint = distanceConstraint(0, distanceMeasure);// setting threshold distance to 0 sothat new cluster is
		                                                                                         // created.
		UnaryOperator<Collection<Cluster>> refiner = new AssignToNearest(distanceMeasure, (from, to) -> distanceConstaint.test(from.geocode(), to.geocode()));

		Collection<Cluster> outputClusters = refiner.apply(asList(cluster1, cluster2));

		assertEquals(outputClusters.size(), 1);

		Cluster onlyCluster = outputClusters.iterator().next();

		assertEquals(onlyCluster.id(), point.id());
	}

}
