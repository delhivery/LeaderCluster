package com.delhivery.clustering.distances;

import com.delhivery.clustering.Geocode;

/**
 * @author Shiv Krishna Jaiswal
 */
@FunctionalInterface
public interface DistanceMeasure {

    double distance(Geocode from, Geocode to);

}
