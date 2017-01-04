package com.delhivery.clustering.algorithm;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public interface ClusterFactory<E extends Cluster> {

    /**
     * Creates new clusters of {@link Cluster}
     * @return a new cluster
     */
    E create();
}
