package com.delhivery.refactoring;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedList;

final class ClusterImpl implements Cluster {
    private final String                  id;
    private final Collection<Clusterable> members;
    private double                        lat , lng , weight;

    public ClusterImpl(String id) {
        this.id = id;
        this.members = new LinkedList<>();
        this.lat = 0;
        this.lng = 0;
        this.weight = 0;
    }

    @Override
    public Collection<Clusterable> getMembers() {
        return unmodifiableCollection(members);
    }

    @Override
    public void consumeClusterer(Clusterable point) {
        updateCentroid(point);
        this.members.add(point);
    }

    public String id() {
        return id;
    }

    private void updateCentroid(Clusterable point) {
        double newWeight = this.weight + point.weight();

        Geocode ptCoords = point.geocode();

        this.lat = (this.lat * this.weight + ptCoords.lat * point.weight()) / newWeight;
        this.lng = (this.lng * this.weight + ptCoords.lng * point.weight()) / newWeight;

        this.weight = newWeight;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof ClusterImpl))
            return false;

        return this.id.equals(((ClusterImpl) obj).id);
    }

    @Override
    public String toString() {
        return members.toString();
    }

    @Override
    public Geocode geocode() {
        return new Geocode(lat, lng);
    }

    @Override
    public double weight() {

        return weight;
    }

}
