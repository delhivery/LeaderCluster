package com.delhivery.clustering.reduction;

import java.util.Collection;

import com.delhivery.clustering.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */
@FunctionalInterface
public interface ReductionFactory {
    ReductionFactory NO_REDUCTION      = NoReduction::new;
    ReductionFactory REDUCE_ON_GEOCODE = GeocodeReducer::new;

    /**
     * Creates Reducer object by consuming all clusterables.
     * @param clusterables
     * @return
     */
    Reducer<?> createReducer(Collection<? extends Clusterable> clusterables);

}
