package com.delhivery.clustering.algorithm;

import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.utils.Coordinate;
import com.delhivery.clustering.utils.DistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 *  Generic version of Leader Cluster Algorithm
 */

public class LeaderCluster<T extends Cluster<T,V>, V extends Clusterable<V>> {

    private final static Logger logger = LoggerFactory.getLogger(LeaderCluster.class);

    private DistanceCalculator calculator;
    private PriorityQueue<T> clusters = new PriorityQueue<T>();

    //max allowed radius of the cluster
    private int radius;
    //all elements to be clustered
    private PriorityQueue<V> toBeClustered;
    //for creating a new cluster
    private ClusterFactory<T> factory;

    /**
     * Constructor for leader cluster class
     * @param factory - manages creation of new clusters
     * @param toBeClustered - elements to be clustered
     * @param radius - max allowed radius of any cluster
     */
    public LeaderCluster(ClusterFactory<T> factory, Collection<V> toBeClustered,
                         DistanceCalculator calculator, int radius){
        this.factory = factory;
        this.toBeClustered = new PriorityQueue<>(toBeClustered);
        this.radius = radius;
        this.calculator = calculator;
    }

    /**
     * Attempts adding to a cluster, return true if successful
     * @param cluster - cluster to be added into
     * @param member - element to be added
     * @return true if successful, else false
     */
    private boolean addToCluster(T cluster, V member) throws InvalidDataException{

        if (calculator.getDistance(cluster.getCoordinate(), member.getCoordinate()) <= radius) {

            double oldClusterWeight = cluster.getWeight();
            double memberWeight = member.getWeight();

            if (memberWeight == 0){
                cluster.addMember(member);
                clusters.add(cluster);
                return true;
            }

            double newWeight = memberWeight + oldClusterWeight;
            Coordinate p1 = cluster.getCoordinate();
            Coordinate p2 = member.getCoordinate();
            double newClusterLat = (p1.lat * oldClusterWeight + p2.lat * memberWeight) / newWeight;
            double newClusterLon = (p1.lng * oldClusterWeight + p2.lng * memberWeight) / newWeight;
            Coordinate newClusterCoord = new Coordinate(newClusterLat, newClusterLon);

            if (verifyCluster(cluster, newClusterCoord)) {
                cluster.setWeight(newWeight);
                cluster.setCoordinate(newClusterCoord);
                cluster.addMember(member);
                clusters.add(cluster);
                return true;
            }
        }

        return false;
    }

    /**
     * Ensures that new cluster coordinate created due to addition of a new member
     * does not result in any of its previous member having a distance greater than
     * max allowed radius of the cluster
     * @param cluster - cluster being checked
     * @param newClusterCoord - new cluster centroid to be set if a new member is added
     * @return true, if member can be added, else false
     */
    private boolean verifyCluster(T cluster, Coordinate newClusterCoord){

        for (V member : cluster.getAllMembers())
            if (calculator.getDistance(member.getCoordinate(), newClusterCoord) > radius)
                return false;

        return true;
    }

    /**
     * Performs the actual clustering operation
     * @return a collection of clusters
     */
    public Collection<T> cluster() throws InvalidDataException{

        logger.info("Clustering Started");
        while(!toBeClustered.isEmpty()){

            V unassignedMember = toBeClustered.poll();
            Collection<T> tracker = new LinkedList<>();

            while (!clusters.isEmpty()) {

                T cluster = clusters.poll();

                if (addToCluster(cluster, unassignedMember))
                    break;

                tracker.add(cluster);
            }

            if (clusters.isEmpty())
                clusters.add(createNewCluster(unassignedMember));

            clusters.addAll(tracker);
        }
        logger.info("Clustering Finished");
        return clusters;
    }

    /**
     * Creates a new cluster
     * @param firstMember first element to be added
     * @return a newly created cluster on
     */
    private T createNewCluster(V firstMember){

        T cluster = factory.create();
        cluster.addMember(firstMember);
        cluster.setWeight(firstMember.getWeight());
        cluster.setCoordinate(firstMember.getCoordinate());

        return cluster;
    }
}