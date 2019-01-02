package com.delhivery.clustering.elements;

/**
 * @author Shiv Krishna Jaiswal
 */
import static java.util.Objects.requireNonNull;

public abstract class AbstractClusterable implements Clusterable {
    private final String id;

    protected AbstractClusterable(String id) {
        this.id = requireNonNull(id);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
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

        if (!(obj instanceof Clusterable))
            return false;

        Clusterable other = (Clusterable) obj;

        return other.id().equals(id);
    }

}
