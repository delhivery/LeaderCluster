package com.delhivery.clustering;

import java.util.Collection;

public interface Cluster extends Clusterable {

    Collection<Clusterable> getMembers();

    void consumeClusterer(Clusterable point);


}
