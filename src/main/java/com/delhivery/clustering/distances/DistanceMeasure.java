package com.delhivery.clustering.distances;

import static com.delhivery.clustering.distances.DistanceMeasureFactory.aerialToRoad;

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
	DistanceMeasure OSRM_APPROXIMATE   = (a, b) -> aerialToRoad(EUDLIDEAN_DISTANCE.distance(a, b));
	DistanceMeasure GOOGLE_DISTANCE    = DistanceMeasureFactory::googleDistance;

	double distance(Geocode from, Geocode to);

}
