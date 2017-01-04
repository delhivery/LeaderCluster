package com.delhivery.clustering.utils;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public interface DistanceCalculator {

    int getDistance(Coordinate source, Coordinate destination);
}
