package com.delhivery.clustering.demo;

import com.delhivery.clustering.ClusterableImpl;
import com.delhivery.clustering.Geocode;

public final class UniqueClusterableImpl extends ClusterableImpl implements UniqueClusterable {
    private final String id;

    public UniqueClusterableImpl(String id, Geocode geocode, double weight) {
        super(geocode, weight);
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

}
