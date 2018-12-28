package com.delhivery.clustering.preclustering;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.function.Supplier;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */

final class NoPreClusterer implements PreClustering {
    private final Collection<Clusterable> points;

    public NoPreClusterer(Collection<? extends Clusterable> points) {
        this.points = unmodifiableCollection(points);
    }

    @Override
    public Collection<Cluster> preclusters(Supplier<String> idSupplier) {
        return emptyList();
    }

    @Override
    public Collection<Clusterable> unclusteredPoints() {
        return points;
    }

}
