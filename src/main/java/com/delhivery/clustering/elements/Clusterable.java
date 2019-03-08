package com.delhivery.clustering.elements;

import java.io.Serializable;

/**
 * @author Shiv Krishna Jaiswal
 */
public interface Clusterable extends Serializable {

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
