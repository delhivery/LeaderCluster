package com.delhivery.clustering.reduction;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.delhivery.clustering.Clusterable;

public abstract class HashReducer<T> extends Reducer<T> {
    /**
     * Hashes Clusterable on given function and produces
     * a representable clusterable point.
     * After clustering, clusters can be expanded with individual clusterable points
     * using "decompressClusterable" methods.
     */

    private final Map<T, Collection<Clusterable>> mapper;
    private final Function<Clusterable, T>        hasher;

    protected HashReducer(Collection<? extends Clusterable> clusterables, Function<Clusterable, T> hasher) {
        this.mapper = clusterables.stream().collect(groupingBy(hasher, toCollection(LinkedList::new)));
        this.hasher = hasher;
    }

    /**
     * @return clusterables which is distinct on hash given by "hasher" function.
     */
    public Collection<Clusterable> compressedClusterables() {
        return this.mapper.entrySet()
                          .stream()
                          .map(this::create)
                          .collect(toList());
    }

    /**
     * @param clusterable
     * @return clusterables point which share same hash as that of "clusterable"
     */

    public Collection<Clusterable> decompressClusterable(Clusterable clusterable) {
        Collection<Clusterable> points = mapper.get(hasher.apply(clusterable));

        if (isNull(points))
            throw new IllegalArgumentException("Invalid Clusterable " + clusterable);

        return unmodifiableCollection(points);

    }

    public abstract Clusterable create(Entry<T, Collection<Clusterable>> e);

}
