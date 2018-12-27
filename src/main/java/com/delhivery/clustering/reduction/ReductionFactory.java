package com.delhivery.clustering.reduction;

import java.util.Collection;

import com.delhivery.clustering.Clusterable;

@FunctionalInterface
public interface ReductionFactory {
    ReductionFactory NO_REDUCTION      = NoReduction::new;
    ReductionFactory REDUCE_ON_GEOCODE = DuplicacyRemoval::new;

    /**
     * Creates Reducer object by consuming all clusterables.
     * @param clusterables
     * @return
     */
    Reducer<?> createReducer(Collection<? extends Clusterable> clusterables);

}
