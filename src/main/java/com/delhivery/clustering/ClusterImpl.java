package com.delhivery.clustering;

import static com.delhivery.clustering.utils.Utils.isZero;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedList;

final class ClusterImpl implements Cluster {
    private final String                  id;
    private double                        lat , lng , weight;
    private final Collection<Clusterable> members;

    public ClusterImpl(String id) {
        this.id = id;
        this.lat = 0;
        this.lng = 0;
        this.weight = 0;

        this.members = new LinkedList<>();
    }

    public String id() {
        return id;
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

    private void updateCentroid(Clusterable point) {
        double newWeight = this.weight + point.weight();

        Geocode ptCoords = point.geocode();

        if (!isZero(newWeight)) {

            this.lat = (this.lat * this.weight + ptCoords.lat * point.weight()) / newWeight;
            this.lng = (this.lng * this.weight + ptCoords.lng * point.weight()) / newWeight;

        } else {

            this.lat = ptCoords.lat;
            this.lng = ptCoords.lng;

        }

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
