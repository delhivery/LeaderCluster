package com.delhivery.clustering;

public class ClusterableImpl extends AbstractClusterable {
    private final Geocode geocode;
    private final double  weight;

    public ClusterableImpl(String id, Geocode geocode, double weight) {
        super(id);
        this.geocode = geocode;
        this.weight = weight;
    }

    @Override
    public Geocode geocode() {

        return geocode;
    }

    @Override
    public double weight() {

        return weight;
    }

}
