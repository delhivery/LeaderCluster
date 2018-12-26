package com.delhivery.clustering;

public class ClusterableImpl implements Clusterable {
    private final String  id;
    private final Geocode geocode;
    private final double  weight;

    public ClusterableImpl(String id, Geocode geocode, double weight) {
        this.id = id;
        this.geocode = geocode;
        this.weight = weight;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Geocode geocode() {

        return geocode;
    }

    @Override
    public double weight() {

        return weight;
    }

    @Override
    public String toString() {
        return id;
    }

}
