/*
 * This file is part of the JavaLeaderCluster distribution.
 * Copyright (c) 2017 Delhivery India Pvt. Ltd.
 *
 * JavaLeaderCluster is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * JavaLeaderCluster is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.delhivery.clustering;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.distances.DistanceMeasureFactory;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.ClusterableImpl;
import com.delhivery.clustering.elements.Geocode;

public class LeaderClusterTest {

    @Test
    public void leaderClusterTest() {
        for (boolean generateWeight : new boolean[] { true, false }) {

            int numPoints = 1000;
            double divisor = 1000;

            Collection<Clusterable> data = new ArrayList<>(1000);

            for (double i = 0; i < numPoints; i++) {
                double ratio = i / divisor;

                double lat = i % 2 == 0 ? 28 + ratio : 28 - ratio;
                double lng = i % 2 == 0 ? 77 - ratio : 77 + ratio;
                double weight = generateWeight ? (i % 2 == 0 ? 2 * i + 1 : 3 * i - 2) : 1;

                data.add(new ClusterableImpl(i + "", new Geocode(lat, lng), weight));
            }

            Assert.assertTrue(data.size() == numPoints);

            int clusterRadius = 500;

            DistanceMeasure haversine = DistanceMeasureFactory.HAVERSINE;

            Collection<Cluster> clusters = LCBuilder.newInstance(data)
                                                    .distanceConstraint(clusterRadius, haversine)
                                                    .refineAssignToClosestCluster(1, haversine)
                                                    .build();

            Cluster prevCluster = null;

            // check number of clusters
            for (Cluster cluster : clusters) {

                // checks decreasing order
                if (prevCluster == null)
                    prevCluster = cluster;
                else
                    Assert.assertTrue(prevCluster.weight() >= cluster.weight());

                double sumWeight , lat , lng , ratio;
                sumWeight = lat = lng = 0.0;

                for (Clusterable member : cluster.getMembers()) {
                    sumWeight += member.weight();
                    ratio = member.weight() / cluster.weight();
                    lat += ratio * member.geocode().lat;
                    lng += ratio * member.geocode().lng;

                    // checks if each member is within the specified radius from the cluster centroid
                    Assert.assertTrue(clusterRadius >= haversine.distance(cluster.geocode(), member.geocode()));
                }

                // checks weight of cluster = sum of weights of its members
                Assert.assertEquals(cluster.weight(), sumWeight, 0.0);

                // checks coordinate of cluster is weighted sum of the coordinates of its members
                Assert.assertEquals(cluster.geocode().lat, lat, 10e-8);
                Assert.assertEquals(cluster.geocode().lng, lng, 10e-8);
            }

            // check if all data points were clustered
            Assert.assertEquals(numPoints, clusters.stream().map(Cluster::getMembers).mapToInt(Collection::size).sum());
        }
    }
}
