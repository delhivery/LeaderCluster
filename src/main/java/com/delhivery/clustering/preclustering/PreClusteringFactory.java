package com.delhivery.clustering.preclustering;

import java.util.Collection;

import com.delhivery.clustering.elements.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */

@FunctionalInterface
public interface PreClusteringFactory {

	PreClusteringFactory NO_PRECLUSTERING = NoPreClusterer::new;

	PreClustering createPreClusterer(Collection<? extends Clusterable> clusterables);

}
