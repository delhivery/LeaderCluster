package com.delhivery.clustering.preclustering;

import static java.lang.String.valueOf;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractPreClusterer implements PreClustering {
    private final AtomicLong idCreator;

    protected AbstractPreClusterer() {
        this.idCreator = new AtomicLong();
    }

    @Override
    public String nextClusterID() {
        return valueOf(idCreator.getAndIncrement());
    }
}
