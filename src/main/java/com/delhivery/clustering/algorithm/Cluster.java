package com.delhivery.clustering.algorithm;

import com.delhivery.clustering.utils.Coordinate;

import java.util.Collection;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 *  This interface has to be implemented by a class which represents a cluster of {@link Clusterable} Objects.
 */
public interface Cluster<T, V extends Clusterable<V>> extends Comparable<T> {

    /**
     * Sets cluster's centroid's coordinates
     */
    void setCoordinate(Coordinate coordinate);

    /**
     * Sets the weight of the cluster which is a function of the weights of its members
     * @param weight new weight of the cluster
     */
    void setWeight(double weight);

    /**
     * Returns centroid's coordinates
     * @return centroid's coordinates
     */
    Coordinate getCoordinate();

    /**
     * Returns the current weight of the cluster
     * @return cluster's weight
     */
    double getWeight();

    /**
     * Returnbs all the current members of the cluster
     * @return a collection of Clusterable objects which are members of the cluster
     */
    Collection<V> getAllMembers();

    /**
     * Adds a new member to the cluster
     * @param member a clusterable object
     */
    boolean addMember(V member);
}