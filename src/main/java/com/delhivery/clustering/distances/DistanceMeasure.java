package com.delhivery.clustering.distances;

import java.io.Serializable;

import com.delhivery.clustering.elements.Geocode;

/**
 * @author Shiv Krishna Jaiswal
 */
@FunctionalInterface
public interface DistanceMeasure extends Serializable {

    DistanceMeasure EUDLIDEAN_DISTANCE = DistanceMeasureFactory::euclideanDistance;
    DistanceMeasure HAVERSINE          = DistanceMeasureFactory::haversineDistance;
    DistanceMeasure OSRM               = DistanceMeasureFactory::osrm;
    DistanceMeasure GOOGLE_DISTANCE    = DistanceMeasureFactory::googleDistance;

    double distance(Geocode from, Geocode to);

}
