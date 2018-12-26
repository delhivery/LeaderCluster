package com.delhivery.refactoring;

import java.util.Collection;

@FunctionalInterface
public interface Refinement {

    Collection<Cluster> apply(Collection<Cluster> clusters);

}
