package com.delhivery.clustering;

import static java.util.Objects.requireNonNull;

public class ClusterableImpl extends AbstractClusterable {
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
