package com.delhivery.clustering.elements;

import static java.util.Objects.requireNonNull;

/**
 * @author Shiv Krishna Jaiswal
 */
public class ClusterableImpl extends AbstractClusterable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Geocode geocode;
    private final double  weight;
    private Object        userData;

    public ClusterableImpl(String id, Geocode geocode, double weight) {
        super(id);

        this.geocode = requireNonNull(geocode);
        this.weight = weight;
        this.userData = null;
    }

    @Override
    public Geocode geocode() {

        return geocode;
    }

    @Override
    public double weight() {

        return weight;
    }

    public void userData(Object userData) {
        this.userData = userData;
    }

    @SuppressWarnings("unchecked")
    public <T> T userData() {
        return (T) userData;
    }

}