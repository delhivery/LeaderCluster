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

package com.delhivery.clustering.algorithm;

import com.delhivery.clustering.exceptions.ClusteringException;
import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.utils.Coordinate;
import com.delhivery.clustering.utils.DistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 *  Generic version of Leader Cluster Algorithm
 */

public class LeaderClusterAlgorithm<T extends Cluster<T,V>, V extends Clusterable> {

    private final static Logger logger = LoggerFactory.getLogger(LeaderClusterAlgorithm.class);

    private DistanceCalculator calculator;
    private Collection<T> clusters;

    //max allowed radius of the cluster
    private int radius;
    //all elements to be clustered
    private List<V> toBeClustered = new ArrayList<>();
    //for creating a new cluster
    private Generator<T, V> factory;
    // check if duplicates removal is disabled, by defaults it uniqifies
    private boolean uniqify = true;

    private DataCleaner<T, V> dataCleaner;

    /**
     * Constructor for leader cluster class
     * @param factory - manages creation of new clusters
     * @param toBeClustered - elements to be clustered
     * @param radius - max allowed radius of any cluster
     */
    public LeaderClusterAlgorithm(Generator<T, V> factory, Collection<V> toBeClustered,
                                  DistanceCalculator calculator, int radius, boolean uniqify){
        this.factory = factory;

        this.toBeClustered.addAll(toBeClustered);
        this.radius = radius;
        this.calculator = calculator;
        this.uniqify = uniqify;
        clusters = new TreeSet<>(Collections.reverseOrder());
    }

    /**
     * Attempts adding to a cluster, return true if successful
     * @param cluster - cluster to be added into
     * @param member - element to be added
     * @return true if successful, else false
     * @throws InvalidDataException coordinate creation error
     * @throws ClusteringException any error in clustering
     */
    private boolean addToCluster(T cluster, V member) throws InvalidDataException, ClusteringException{

        if (calculator.getDistance(cluster.getCoordinate(), member.getCoordinate()) <= radius) {

            double oldClusterWeight = cluster.getWeight();
            double memberWeight = member.getWeight();

            // if member has zero weight, it is not going to change cluster's coordinates
            // so can be directly added to cluster
            if (memberWeight == 0){
                cluster.addMember(member);
                return true;
            }

            // here, cluster's new weight and new coordinates are calculated if point is added
            double newWeight = memberWeight + oldClusterWeight;
            Coordinate p1 = cluster.getCoordinate();
            Coordinate p2 = member.getCoordinate();

            double newClusterLat = (p1.lat * oldClusterWeight + p2.lat * memberWeight) / newWeight;
            double newClusterLon = (p1.lng * oldClusterWeight + p2.lng * memberWeight) / newWeight;
            Coordinate newClusterCoord = new Coordinate(newClusterLat, newClusterLon);

            // this step checks if addition of a new data point violates cluster conditions
            // for any of the existing points, if they do, point is not added to the cluster
            if (verifyCluster(cluster, newClusterCoord)) {
                cluster.addMember(member).setWeight(newWeight).setCoordinate(newClusterCoord);
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

        for (V member : cluster.getMembers())
            if (calculator.getDistance(member.getCoordinate(), newClusterCoord) > radius)
                return false;

        return true;
    }

    /**
     * Performs the actual clustering operation
     * @param reduceOverlaps re-run over all clusters to minimize overlaps; significantly costly step
     * @param numIters number of iterations for reducing overlaps, recommended max 10
     * @return a collection of clusters
     * @throws InvalidDataException incorrect lat, longs sent to Coordinate
     * @throws ClusteringException any error in clustering
     */
    public Collection<T> cluster(boolean reduceOverlaps, int numIters) throws InvalidDataException, ClusteringException{

        if(uniqify) {
            dataCleaner = new DataCleaner<>(toBeClustered, factory);
            dataCleaner.uniqify();

            this.toBeClustered = new ArrayList<>();
            this.toBeClustered.addAll(dataCleaner.getOutput());
        }

        this.toBeClustered.sort(Collections.reverseOrder());

        logger.info("Clustering Started with data size as: {}", toBeClustered.size());

        for(V unassignedMember: toBeClustered){

            boolean addedToExistingCluster = false;

            // this step checks if unassignedMember can be added to
            // any of the existing clusters
            for(T cluster: clusters) {
                if (addToCluster(cluster, unassignedMember)) {
                    addedToExistingCluster = true;
                    break;
                }
            }

            // if no clusters exist or if unassignedMember was not able to be
            // added to any of the existing clusters, then createCluster a cluster
            // with the unassignedMember
            if (!addedToExistingCluster)
                clusters.add(createNewCluster(unassignedMember));
        }

        logger.info("Clustering Finished with {} clusters", clusters.size());

        if(reduceOverlaps) {
            for(int i=0; i<numIters; i++) {
                logger.info("Refinement Iteration: {}", i+1);
                refineClusters();
            }
        }

        if(uniqify)
            return dataCleaner.expandClusters(clusters);
        else
            return clusters;
    }

    /**
     * Reassign members to their nearest cluster
     */
    private void refineClusters() throws InvalidDataException {

        List<T> refinedClusters = new ArrayList<>(clusters);

        for(T cluster: refinedClusters) {
            cluster.resetMembers();
            cluster.setWeight(0);
        }

        for(V member: toBeClustered) {
            double minDistance = Double.MAX_VALUE;
            int minCluster = -1;

            for (int i = 0; i < refinedClusters.size(); i++) {
                T cluster = refinedClusters.get(i);
                double distance = calculator.getDistance(cluster.getCoordinate(), member.getCoordinate());

                if (distance <= radius && distance < minDistance) {
                    minCluster = i;
                    minDistance = distance;
                }
            }

            T cluster = refinedClusters.get(minCluster).addMember(member);
            cluster.setWeight(cluster.getWeight() + member.getWeight());
        }

        for (Iterator<T> iterator = refinedClusters.iterator(); iterator.hasNext(); ) {
            T cluster = iterator.next();
            if(cluster.getMembers().size() > 0)
                cluster.setCoordinate(getUpdatedCoordinate(cluster.getMembers()));
            else
                iterator.remove();
        }

        clusters = refinedClusters;
    }

    /**
     * Creates a new cluster
     * @param firstMember first element to be added
     * @return a newly created cluster on
     */
    private T createNewCluster(V firstMember) throws ClusteringException {

        T cluster = factory.createCluster();
        cluster.addMember(firstMember);
        cluster.setWeight(firstMember.getWeight())
                .setCoordinate(firstMember.getCoordinate());

        return cluster;
    }

    /**
     * Use cluster members to calculate its coordinate
     */
    private Coordinate getUpdatedCoordinate(Collection<V> members) throws InvalidDataException {

        double weight = 0;
        double newLat = 0, newLng = 0;

        for(V member: members) {
            newLat += member.getCoordinate().lat * member.getWeight();
            newLng += member.getCoordinate().lng * member.getWeight();
            weight += member.getWeight();
        }

        return new Coordinate(newLat/weight, newLng/weight);
    }
}