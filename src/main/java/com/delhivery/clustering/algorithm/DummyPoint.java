package com.delhivery.clustering.algorithm;

import com.delhivery.clustering.utils.Coordinate;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 19/9/17
 */
public class DummyPoint implements Clusterable {

    private Coordinate coordinate;
    private double weight;

    DummyPoint(Coordinate coordinate, double weight){

    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Clusterable clusterable) {
        if (coordinate.compareTo(clusterable.getCoordinate()) == 0)
            return 0;
        else {
            return new Double(weight).compareTo(clusterable.getWeight());
        }
    }
}
