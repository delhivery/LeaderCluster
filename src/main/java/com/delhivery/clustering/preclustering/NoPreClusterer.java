package com.delhivery.clustering.preclustering;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;

final class NoPreClusterer extends AbstractPreClusterer {
    private final Collection<Clusterable> points;

    public NoPreClusterer(Collection<? extends Clusterable> points) {
        this.points = unmodifiableCollection(points);
    }

    @Override
    public Collection<Cluster> preclusters() {
        return emptyList();
    }

    @Override
    public Collection<Clusterable> unclusteredPoints() {
        return points;
    }

}
