package com.delhivery.clustering.elements;

import static com.delhivery.clustering.utils.Utils.formatNumber;
import static java.lang.Double.doubleToLongBits;
import static java.util.Objects.hash;

import java.io.Serializable;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class Geocode implements Serializable {
    /**
     * 
     */
    private static final long   serialVersionUID = 1L;
    public static final Geocode ZERO             = new Geocode(0, 0);

    public final double lat , lng;

    public Geocode(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "[x=" + formatNumber(lat) + ", y=" + formatNumber(lng) + "]";
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

        if (doubleToLongBits(lat) != doubleToLongBits(other.lat))
            return false;

        if (doubleToLongBits(lng) != doubleToLongBits(other.lng))
            return false;

        return true;
    }

}
