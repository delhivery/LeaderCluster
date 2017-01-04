package com.delhivery.clustering.utils;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class HaversineDistanceCalculator implements DistanceCalculator{

    /**
     * Returns haversine distance between two given points
     *
     * @param p1 facility coordinate
     * @param p2 destination coordinate
     * @return distance in meters
     */
    @Override
    public int getDistance(Coordinate p1, Coordinate p2) {

        double deltaLat = Math.toRadians(p2.lat - p1.lat);
        double deltaLng = Math.toRadians(p2.lng - p1.lng);
        double lat1 = Math.toRadians(p1.lat);
        double lat2 = Math.toRadians(p2.lat);
        double a = Math.sin(deltaLat / 2.0D) * Math.sin(deltaLat / 2.0D)
                + Math.sin(deltaLng / 2.0D) * Math.sin(deltaLng / 2.0D) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2.0D * Math.asin(Math.sqrt(a));

        return (int) (6372.8D * c * 1000.0D);
    }
}
