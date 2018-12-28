package com.delhivery.clustering.refinement;

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
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.ClusterImpl.ClusterBuilder;
import com.delhivery.clustering.Clusterable;
import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.distances.DistanceMeasure;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class AssignToNearest implements UnaryOperator<Collection<Cluster>> {
    private static final Logger LOGGER = getLogger(AssignToNearest.class);

    private final DistanceMeasure distanceMeasure;

    public AssignToNearest(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
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
                .forEach(c -> addToClosest(c, closestMapping));

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

    private void addToClosest(Clusterable clusterable, Map<Cluster, Collection<Clusterable>> mapping) {
        mapping.entrySet()
               .stream()
               .min(comparingDouble(e -> distance(e.getKey(), clusterable)))
               .get()
               .getValue()
               .add(clusterable);
    }

    private double distance(Cluster cluster, Clusterable clusterable) {
        return distanceMeasure.distance(cluster.geocode(), clusterable.geocode());
    }

}
