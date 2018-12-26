package com.delhivery.refactoring;

import static java.util.Objects.hash;

public final class Geocode {
    public final double lat , lng;

    public Geocode(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return " [x=" + lat + ", y=" + lng + "]";
    }

    @Override
    public int hashCode() {
        return hash(lat, lng);
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
        if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
            return false;
        if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
            return false;

        return true;
    }

}
