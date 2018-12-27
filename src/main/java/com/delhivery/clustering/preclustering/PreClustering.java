package com.delhivery.clustering.preclustering;

import java.util.Collection;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;

public interface PreClustering {

    Collection<Cluster> preclusters();

    Collection<Clusterable> unclusteredPoints();

    String nextClusterID();

}
