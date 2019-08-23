package com.delhivery.clustering.preclustering;

import java.util.Collection;
import java.util.function.Supplier;

import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */

public interface PreClustering {

	Collection<Cluster> preclusters(Supplier<Cluster> idCreator);

	Collection<Clusterable> unclusteredPoints();

}
