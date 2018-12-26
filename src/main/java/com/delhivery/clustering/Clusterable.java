package com.delhivery.clustering;

public interface Clusterable {
    /**
     * @return geocode of this point.
     */
    Geocode geocode();

    /**
     * 
     * @return importance of this clusterable point.
     */
    double weight();

}
