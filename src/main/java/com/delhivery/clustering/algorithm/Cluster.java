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

import com.delhivery.clustering.utils.Coordinate;

import java.util.Collection;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */

/**
 * This interface has to be implemented by a class which represents a cluster of {@link Clusterable} Objects.
 * @param <T>
 * @param <V>
 */
public interface Cluster<T, V extends Clusterable> extends Comparable<T> {

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
    Collection<V> getMembers();

    /**
     * Adds a new member to the cluster
     * @param member a clusterable object
     */
    boolean addMember(V member);

    /**
     * Resets the members collection
     */
    void resetMembers();
}