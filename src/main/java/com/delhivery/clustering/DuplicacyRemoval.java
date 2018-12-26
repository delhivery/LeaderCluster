package com.delhivery.clustering;

import java.util.Collection;
import java.util.Map.Entry;

final class DuplicacyRemoval extends Reducer<Geocode> {

    DuplicacyRemoval(Collection<? extends Clusterable> points) {
        super(points, Clusterable::geocode);
    }

    /**
     * Creates clusterable point with geocode of this entry and having weight 
     * which is sum of weight of clusterables given by value of this entry.
     */
    public Clusterable create(Entry<Geocode, Collection<Clusterable>> e) {

        double weight = e.getValue()
                         .stream()
                         .mapToDouble(Clusterable::weight)
                         .sum();

        return new DuplicateClustreables(e.getKey(), weight);
    }

    private final static class DuplicateClustreables extends ClusterableImpl {

        public DuplicateClustreables(Geocode geocode, double weight) {
            super(geocode, weight);
        }

        @Override
        public int hashCode() {

            return geocode().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DuplicateClustreables)
                return ((DuplicateClustreables) obj).geocode().equals(geocode());

            return false;
        }

        @Override
        public String toString() {
            return "Clusterable compressed on Geocode:" + geocode();
        }

    }
}
