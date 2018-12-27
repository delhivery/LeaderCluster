package com.delhivery.clustering.preclustering;

import java.util.Collection;

import com.delhivery.clustering.Clusterable;

@FunctionalInterface
public interface PreClusteringFactory {

    PreClusteringFactory NO_PRECLUSTERING = NoPreClusterer::new;

    PreClustering createPreClusterer(Collection<? extends Clusterable> clusterables);

}
