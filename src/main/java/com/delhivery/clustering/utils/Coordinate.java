package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;

import java.text.DecimalFormat;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class Coordinate implements Comparable<Coordinate> {

    public final double lat;
    public final double lng;

    public Coordinate(double lat, double lng) throws InvalidDataException {
        if (isLatValid(lat)) {
            this.lat = lat;
        } else
            throw new InvalidDataException("Invalid Latitude: " + lat);

        if (isLngValid(lng)) {
            this.lng = lng;
        } else
            throw new InvalidDataException("Invalid Longitude: " + lng);
    }

    public String geometry() {
        return "POINT(" + lng + " " + lat + ")";
    }

    /**
     * checks if Latitude is valid
     *
     * @param lat Latitude
     * @return True if valid, else False
     */
    private boolean isLatValid(double lat) {
        final double MAX_LAT = 90.0;
        final double MIN_LAT = -90.0;

        return Double.compare(lat, MAX_LAT) <= 0 && Double.compare(lat, MIN_LAT) >= 0;
    }

    /**
     * checks if Longitude is valid
     *
     * @param lng Longitude
     * @return True if valid, else False
     */
    private boolean isLngValid(double lng) {
        final double MAX_LNG = 180.0;
        final double MIN_LNG = -180.0;

        return Double.compare(lng, MAX_LNG) <= 0 && Double.compare(lng, MIN_LNG) >= 0;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat(".######");
        return "(" + decimalFormat.format(this.lat) + "," + decimalFormat.format(this.lng) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Coordinate && this.compareTo((Coordinate) obj) == 0);
    }

    @Override
    public int compareTo(Coordinate coordinate) {

        if ((Double.compare(coordinate.lat, this.lat) == 0) &&
                (Double.compare(coordinate.lng, this.lng) == 0))
            return 0;

        return -1;
    }
}
