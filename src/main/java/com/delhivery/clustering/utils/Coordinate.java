/*
 * This file is part of the JavaLeaderCluster distribution.
 * Copyright (c) 2017 Delhivery India Pvt. Ltd.
 *
 * JavaLeaderCluster is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * JavaLeaderCluster is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;

import java.text.DecimalFormat;
import java.util.Objects;

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

    public String wktGeometry() {
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

    @Override
    public int hashCode() {
        return Objects.hash(lat,lng);
    }
}
