package com.delhivery.clustering.elements;

import static com.delhivery.clustering.elements.Geocode.ZERO;
import static com.delhivery.clustering.utils.Utils.isZero;
import static com.delhivery.clustering.utils.Utils.weightedGeocode;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class ClusterImpl extends AbstractClusterable implements Cluster {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private double                        weight;
    private Geocode                       geocode;
    private final Collection<Clusterable> members;

    private ClusterImpl(ClusterBuilder builder) {
        super(builder.id);

        this.geocode = builder.geocode;
        this.weight = builder.weight;

        this.members = new LinkedList<>();
    }

    @Override
    public Collection<Clusterable> getMembers() {
        return unmodifiableCollection(members);
    }

    @Override
    public void consumeClusterer(Clusterable point) {
        double newWeight = this.weight + point.weight();

        this.geocode = isZero(newWeight) ? point.geocode() : weightedGeocode(this, point);
        this.weight = newWeight;

        this.members.add(point);

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
        return geocode;
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
