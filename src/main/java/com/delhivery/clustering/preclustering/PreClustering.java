package com.delhivery.clustering.preclustering;

import java.util.Collection;
import java.util.function.Supplier;

import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */

public interface PreClustering {

    Collection<Cluster> preclusters(Supplier<String> idCreator);

    Collection<Clusterable> unclusteredPoints();

}
