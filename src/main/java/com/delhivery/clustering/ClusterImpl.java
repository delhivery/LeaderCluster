package com.delhivery.clustering;

import static com.delhivery.clustering.utils.Utils.isZero;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedList;

public final class ClusterImpl extends AbstractClusterable implements Cluster {
    private double                        lat , lng , weight;
    private final Collection<Clusterable> members;

    public ClusterImpl(String id) {
        super(id);

        this.lat = 0;
        this.lng = 0;
        this.weight = 0;

        this.members = new LinkedList<>();
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof Cluster))
            return false;

        return this.id().equals(((Cluster) obj).id());
    }

    @Override
    public int hashCode() {
        return id().hashCode();
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
