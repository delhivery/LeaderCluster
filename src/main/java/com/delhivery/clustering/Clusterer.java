package com.delhivery.clustering;

import java.util.Collection;

@FunctionalInterface
public interface Clusterer {
    /**
     * Defines clustering strategy for given clusterables.
     * 
     * @param clusterables
     * @return clusters
     */
    Collection<Cluster> cluster(Collection<? extends Clusterable> clusterables);

}
