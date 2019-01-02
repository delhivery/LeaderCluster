package com.delhivery.clustering.reduction;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.delhivery.clustering.elements.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */
final class NoReduction extends Reducer<Object> {
    private final Collection<Clusterable> clusterables;

    public NoReduction(Collection<? extends Clusterable> clusterables) {
        this.clusterables = unmodifiableCollection(clusterables);
    }

    @Override
    public Collection<Clusterable> compressedClusterables() {
        return clusterables;
    }

    @Override
    public Collection<Clusterable> decompressClusterable(Clusterable clusterable) {

        return singletonList(clusterable);
    }

}
