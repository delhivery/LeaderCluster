package com.delhivery.clustering.spatialClustering;

import com.delhivery.clustering.algorithm.ClusterFactory;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class SpatialClusterFactory implements ClusterFactory<SpatialCluster>{

    @Override
    public SpatialCluster create() {
        return new SpatialCluster();
    }
}
