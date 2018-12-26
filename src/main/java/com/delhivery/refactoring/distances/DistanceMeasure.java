package com.delhivery.refactoring.distances;

import com.delhivery.refactoring.Geocode;

@FunctionalInterface
public interface DistanceMeasure {

    double distance(Geocode from, Geocode to);

}
