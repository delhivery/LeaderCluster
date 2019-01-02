package com.delhivery.clustering.refinement;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.ClusterImpl.ClusterBuilder;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.Geocode;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class AssignToNearest implements UnaryOperator<Collection<Cluster>> {
    private static final Logger                        LOGGER             = getLogger(AssignToNearest.class);
    private static final BiPredicate<Geocode, Geocode> NO_HARD_CONSTRAINT = (from, to) -> true;

    private final DistanceMeasure               distanceMeasure;
    private final BiPredicate<Geocode, Geocode> hardConstraint;

    public AssignToNearest(DistanceMeasure distanceMeasure, BiPredicate<Geocode, Geocode> hardConstraint) {
        this.distanceMeasure = distanceMeasure;
        this.hardConstraint = hardConstraint;
    }

    public AssignToNearest(DistanceMeasure distanceMeasure) {
        this(distanceMeasure, NO_HARD_CONSTRAINT);
    }

    private static Predicate<Cluster> distinctCluster() {
        Set<Geocode> geocodes = new HashSet<>();
        return c -> geocodes.add(c.geocode());
    }

    @Override
    public Collection<Cluster> apply(Collection<Cluster> clusters) {

        LOGGER.info("Number of clusters before 'AssignToNearest' refinement: {}", clusters.size());

        Map<Cluster, Collection<Clusterable>> closestMapping = clusters.stream()
                                                                       .filter(distinctCluster())
                                                                       .collect(toMap(identity(), c -> new LinkedList<>()));

        LOGGER.info("Distinct geocode count: {}", closestMapping.size());

        clusters.stream()
                .map(Cluster::getMembers)
                .flatMap(Collection::stream)
                .forEach(c -> addToClosestServicebleCluster(c, closestMapping));

        Collection<Cluster> output = new ArrayList<>();

        for (Entry<Cluster, Collection<Clusterable>> e : closestMapping.entrySet())
            if (!e.getValue().isEmpty())
                output.add(createCluster(e.getKey().id(), e.getValue()));
            else
                LOGGER.debug("Removing cluster located at: {}, as no point is associated with it.", e.getKey().geocode());

        LOGGER.info("Number of clusters after 'AssignToNearest' refinement: {}", output.size());

        return output;
    }

    private static Cluster createCluster(String id, Collection<Clusterable> clusterables) {
        Cluster cluster = ClusterBuilder.newInstance(id).build();

        clusterables.forEach(cluster::consumeClusterer);

        return cluster;
    }

    private void addToClosestServicebleCluster(Clusterable clusterable, Map<Cluster, Collection<Clusterable>> mapping) {
        Optional<Entry<Cluster, Collection<Clusterable>>> cluster = mapping.entrySet()
                                                                           .stream()
                                                                           .filter(e -> canServe(e.getKey(), clusterable))
                                                                           .min(comparingDouble(e -> distance(e.getKey(), clusterable)));

        if (cluster.isPresent())
            cluster.get().getValue().add(clusterable);
        else {
            Collection<Clusterable> clusterables = new LinkedList<>();

            clusterables.add(clusterable);
            mapping.put(createCluster(clusterable.id(), emptyList()), clusterables);
        }
    }

    private double distance(Cluster cluster, Clusterable clusterable) {
        return distanceMeasure.distance(cluster.geocode(), clusterable.geocode());
    }

    private boolean canServe(Cluster cluster, Clusterable clusterable) {
        return hardConstraint.test(cluster.geocode(), clusterable.geocode());
    }

}
