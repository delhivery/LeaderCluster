package com.delhivery.clustering;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;

import com.delhivery.clustering.distances.DistanceMeasure;

public final class AssignToNearest implements UnaryOperator<Collection<Cluster>> {
    private static final Logger LOGGER = getLogger(AssignToNearest.class);

    private final DistanceMeasure distanceMeasure;

    public AssignToNearest(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }

    @Override
    public Collection<Cluster> apply(Collection<Cluster> clusters) {

        LOGGER.info("Number of clusters before 'AssignToNearest' refinement: {}", clusters.size());

        Map<Geocode, Collection<Clusterable>> closestMapping = clusters.stream()
                                                                       .map(Cluster::geocode)
                                                                       .distinct()
                                                                       .collect(toMap(Function.identity(), g -> new LinkedList<>()));

        LOGGER.info("Distinct geocode count: {}", closestMapping.size());

        clusters.stream()
                .map(Cluster::getMembers)
                .flatMap(Collection::stream)
                .forEach(c -> addToClosest(c, closestMapping));

        Collection<Cluster> output = new ArrayList<>();

        int clusterIndexer = 0;

        for (Entry<Geocode, Collection<Clusterable>> e : closestMapping.entrySet())
            if (!e.getValue().isEmpty())
                output.add(createCluster(clusterIndexer++ + "", e.getValue()));
            else
                LOGGER.debug("Removing cluster located at: {}, as no point is associated with it.", e.getKey());

        LOGGER.info("Number of clusters after 'AssignToNearest' refinement: {}", output.size());

        return output;
    }

    private static Cluster createCluster(String id, Collection<Clusterable> clusterables) {
        Cluster cluster = new ClusterImpl(id);

        clusterables.forEach(cluster::consumeClusterer);

        return cluster;
    }

    private void addToClosest(Clusterable clusterable, Map<Geocode, Collection<Clusterable>> mapping) {
        mapping.entrySet()
               .stream()
               .min(comparingDouble(e -> distanceMeasure.distance(e.getKey(), clusterable.geocode())))
               .get()
               .getValue()
               .add(clusterable);
    }

}
