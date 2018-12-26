package com.delhivery.clustering;

import static com.delhivery.clustering.utils.Utils.formatNumber;
import static java.lang.Double.doubleToLongBits;

public final class Geocode {
    public final double lat , lng;

    public Geocode(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return " [x=" + formatNumber(lat) + ", y=" + formatNumber(lng) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;

        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(lng);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Geocode))
            return false;

        Geocode other = (Geocode) obj;

        if (doubleToLongBits(lat) != doubleToLongBits(other.lat))
            return false;

        if (doubleToLongBits(lng) != doubleToLongBits(other.lng))
            return false;

        return true;
    }

}
