package com.delhivery.clustering.algorithm;

import com.delhivery.clustering.utils.Coordinate;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public interface Clusterable<T> extends Comparable<T>{

    /**
     * Returns the coordinate of the {@link Clusterable} object
     * @return Coordinate
     */
    Coordinate getCoordinate();

    /**
     * Returns the {@link Clusterable} object's weight
     * @return weight
     */
    int getWeight();
}
