package com.delhivery.clustering.distances;

import com.delhivery.clustering.elements.Geocode;

/**
 * @author Shiv Krishna Jaiswal
 */
@FunctionalInterface
public interface DistanceMeasure {

    DistanceMeasure EUDLIDEAN_DISTANCE = DistanceMeasureFactory::euclideanDistance;
    DistanceMeasure HAVERSINE          = DistanceMeasureFactory::haversineDistance;
    DistanceMeasure OSRM               = DistanceMeasureFactory::osrm;
    DistanceMeasure GOOGLE_DISTANCE    = DistanceMeasureFactory::googleDistance;

    double distance(Geocode from, Geocode to);

}
