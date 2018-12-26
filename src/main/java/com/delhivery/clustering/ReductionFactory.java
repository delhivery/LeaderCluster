package com.delhivery.clustering;

import java.util.Collection;

@FunctionalInterface
public interface ReductionFactory {
    /**
     * Creates Reducer object by consuming all clusterables.
     * @param clusterables
     * @return
     */
    Reducer<?> createReducer(Collection<? extends Clusterable> clusterables);

}
