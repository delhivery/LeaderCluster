package com.delhivery.clustering.preclustering;

import java.util.Collection;
import java.util.function.Supplier;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */

public interface PreClustering {

    Collection<Cluster> preclusters(Supplier<String> idCreator);

    Collection<Clusterable> unclusteredPoints();

}
