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

        for (double i = 0; i < 10; i++){
            double lat = i%2 == 0 ? 28 + i/1000 : 28 - i/1000;
            double lng = i%2 == 0 ? 77 - i/1000 : 77 + i/1000;
            SpatialPoint point = new SpatialPoint(new Coordinate(lat, lng), i%2 == 0 ? 2*i + 1: 3*i - 2);
            data.add(point);
        }

        int clusterRadius = 500;
        Collection<SpatialCluster> clusters = LeaderCluster.cluster(data, clusterRadius);

        DistanceCalculator calculator = new HaversineDistanceCalculator();
        SpatialCluster prevCluster = null;
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
