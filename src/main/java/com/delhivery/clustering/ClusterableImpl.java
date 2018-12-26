package com.delhivery.clustering;

public class ClusterableImpl implements Clusterable {
    private final Geocode geocode;
    private final double  weight;

    public ClusterableImpl(Geocode geocode, double weight) {
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

    @Override
    public String toString() {
        return "Clusterable {}[geocode=" + geocode + ", weight=" + weight + "]";
    }

}
