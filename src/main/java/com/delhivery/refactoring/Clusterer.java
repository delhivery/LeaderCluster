package com.delhivery.refactoring;

import java.util.Collection;

@FunctionalInterface
public interface Clusterer {

    Collection<Cluster> cluster(Collection<? extends Clusterable> clusterables);

}
