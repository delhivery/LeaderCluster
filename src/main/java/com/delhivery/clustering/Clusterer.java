package com.delhivery.clustering;

import java.util.Collection;

@FunctionalInterface
public interface Clusterer {

    Collection<Cluster> cluster(Collection<? extends Clusterable> clusterables);

}
