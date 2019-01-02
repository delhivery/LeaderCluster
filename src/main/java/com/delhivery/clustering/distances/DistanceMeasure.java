package com.delhivery.clustering.distances;

import com.delhivery.clustering.elements.Geocode;

/**
 * @author Shiv Krishna Jaiswal
 */
@FunctionalInterface
public interface DistanceMeasure {

    double distance(Geocode from, Geocode to);

}
