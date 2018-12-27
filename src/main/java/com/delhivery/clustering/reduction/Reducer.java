package com.delhivery.clustering.reduction;

import java.util.Collection;

import com.delhivery.clustering.Clusterable;

public abstract class Reducer<T> {

    /**
     * @return clusterables which is distinct on hash given by "hasher" function.
     */
    public abstract Collection<Clusterable> compressedClusterables();

    /**
     * @param clusterable
     * @return clusterables point which share same hash as that of "clusterable"
     */

    public abstract Collection<Clusterable> decompressClusterable(Clusterable clusterable);

}
