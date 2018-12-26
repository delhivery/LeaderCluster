package com.delhivery.clustering;

import static java.util.stream.Collectors.joining;

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

        String id = e.getValue()
                     .stream()
                     .map(Clusterable::id)
                     .collect(joining(";"));

        return new DuplicateClustreables(id, e.getKey(), weight);
    }

    private final static class DuplicateClustreables extends ClusterableImpl {

        DuplicateClustreables(String id, Geocode geocode, double weight) {
            super(id, geocode, weight);
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
