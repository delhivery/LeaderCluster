package com.delhivery.clustering.preclustering;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.function.Supplier;

import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;

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
        return emptySet();
    }

    @Override
    public Collection<Clusterable> unclusteredPoints() {
        return points;
    }

}
