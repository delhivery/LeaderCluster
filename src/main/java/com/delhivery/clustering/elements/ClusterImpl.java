package com.delhivery.clustering.elements;

import static com.delhivery.clustering.elements.Geocode.ZERO;
import static com.delhivery.clustering.utils.Utils.isZero;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class ClusterImpl extends AbstractClusterable implements Cluster {
    private double                        lat , lng , weight;
    private final Collection<Clusterable> members;

    private ClusterImpl(ClusterBuilder builder) {
        super(builder.id);

        this.lat = builder.geocode.lat;
        this.lng = builder.geocode.lng;
        this.weight = builder.weight;

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
        return "[Cluster=" + id() + ", members=" + members.toString() + "]";
    }

    @Override
    public Geocode geocode() {
        return new Geocode(lat, lng);
    }

    @Override
    public double weight() {

        return weight;
    }

    public final static class ClusterBuilder {
        private final String id;
        private double       weight;
        private Geocode      geocode;

        private ClusterBuilder(String id) {
            this.id = requireNonNull(id);
        }

        public static ClusterBuilder newInstance(String id) {
            return new ClusterBuilder(id);
        }

        public ClusterBuilder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public ClusterBuilder geocode(Geocode geocode) {
            this.geocode = geocode;
            return this;
        }

        public Cluster build() {
            if (isNull(this.geocode))
                this.geocode = ZERO;

            return new ClusterImpl(this);
        }
    }

}
