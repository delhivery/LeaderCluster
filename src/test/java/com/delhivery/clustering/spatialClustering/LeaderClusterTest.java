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

package com.delhivery.clustering.spatialClustering;

import com.delhivery.clustering.exceptions.ClusteringException;
import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.utils.Coordinate;
import com.delhivery.clustering.utils.DistanceCalculator;
import com.delhivery.clustering.utils.HaversineDistanceCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class LeaderClusterTest {

    @Test
    public void leaderClusterTest() throws InvalidDataException, ClusteringException{

        Set<SpatialPoint> data = new HashSet<>();

        for (double i = 0; i < 100000; i++){
            double lat = i%2 == 0 ? 28 + i/10000 : 28 - i/10000;
            double lng = i%2 == 0 ? 77 - i/10000 : 77 + i/10000;
            SpatialPoint point = new SpatialPoint(new Coordinate(lat, lng), i%2 == 0 ? 2*i + 1: 3*i - 2);
            data.add(point);
        }

        int clusterRadius = 500;

        long start = System.currentTimeMillis();
        Collection<SpatialCluster> clusters = LeaderCluster.cluster(data, clusterRadius);
        System.out.println("Time Taken:" + (System.currentTimeMillis() - start));

        DistanceCalculator calculator = new HaversineDistanceCalculator();
        SpatialCluster prevCluster = null;

        //check number of clusters
        Assert.assertEquals(3004, clusters.size());

        for(SpatialCluster cluster : clusters){

            //checks decreasing order
            if(prevCluster == null)
                prevCluster = cluster;
            else{
                Assert.assertTrue(prevCluster.getWeight() >= cluster.getWeight());
            }

            double sumWeight, lat, lng, ratio;
            sumWeight = lat = lng = 0.0;

            for(SpatialPoint member: cluster.getMembers()) {
                sumWeight += member.getWeight();
                ratio = member.getWeight()/cluster.getWeight();
                lat += ratio * member.getCoordinate().lat;
                lng += ratio * member.getCoordinate().lng;

                //checks if each member is within the specified radius from the cluster centroid
                Assert.assertTrue(clusterRadius >= calculator.getDistance(cluster.getCoordinate(),
                        member.getCoordinate()));
            }

            //checks weight of cluster = sum of weights of its members
            Assert.assertEquals(cluster.getWeight(), sumWeight, 0.0);

            //checks coordinate of cluster is weighted sum of the coordinates of its members
            Assert.assertEquals(cluster.getCoordinate().toString(), new Coordinate(lat, lng).toString());
        }
    }
}
