package com.delhivery.clustering;

import java.util.Collection;

/**
 * @author Shiv Krishna Jaiswal
 */
public interface Cluster extends Clusterable {
    /**
     * 
     * @return assigned clusterable points of this cluster.
     */
    Collection<Clusterable> getMembers();

    /**
     * updates state of cluster with this clusterable point.
     * @param point
     */
    void consumeClusterer(Clusterable point);

}
