package com.delhivery.clustering;

public interface Clusterable {
    
    String id();

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
