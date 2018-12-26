package com.delhivery.clustering.distances;

import com.delhivery.clustering.Geocode;

@FunctionalInterface
public interface DistanceMeasure {

    double distance(Geocode from, Geocode to);

}
