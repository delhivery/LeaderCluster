package com.delhivery.clustering.utils;

import static com.delhivery.clustering.config.Constants.TOLERANCE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.EUDLIDEAN_DISTANCE;
import static com.delhivery.clustering.utils.Utils.clusterData;
import static com.delhivery.clustering.utils.Utils.createClusterable;
import static com.delhivery.clustering.utils.Utils.distanceConstraint;
import static com.delhivery.clustering.utils.Utils.formatNumber;
import static com.delhivery.clustering.utils.Utils.isZero;
import static com.delhivery.clustering.utils.Utils.loadFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Path;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.ClusterImpl.ClusterBuilder;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.ClusterableImpl;
import com.delhivery.clustering.elements.Geocode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TestUtils {
    private static final Logger LOGGER = getLogger(TestUtils.class);

    @Test
    public void testFormatNumber() {
        assertEquals(formatNumber(10.9), "10.9");
        assertEquals(formatNumber(10.99), "10.99");
        assertEquals(formatNumber(10.999), "10.999");
        assertEquals(formatNumber(10.9999), "10.9999");
        assertEquals(formatNumber(10.99999), "10.99999");
        assertEquals(formatNumber(10.999999), "10.999999");
        assertEquals(formatNumber(10.9999999), "11");// now round off
    }

    @Test
    public void testIsZero() {
        assertTrue(isZero(10e-9));
    }

    @Test
    public void testIDCreator() {
        Supplier<String> supplier = Utils.iDCreator();
        IntStream.range(0, 100).noneMatch(i -> supplier.get().equals(i + ""));
    }

    @Test
    public void testDistanceConstraint() {
        BiPredicate<Cluster, Clusterable> predicate = distanceConstraint(5, EUDLIDEAN_DISTANCE);
        Cluster cluster = ClusterBuilder.newInstance("0")
                                        .geocode(new Geocode(0, 0))
                                        .build();

        assertTrue(predicate.test(cluster, new ClusterableImpl("1", new Geocode(3, 4), 0)));
        assertTrue(predicate.test(cluster, new ClusterableImpl("1", new Geocode(3, 1), 0)));
        Assert.assertFalse(predicate.test(cluster, new ClusterableImpl("1", new Geocode(3, 4.1), 0)));
    }

    @Test
    public void testLoadFile() {
        Path file = get("data/sampleInput.json");

        if (exists(file))
            loadFile(file);
        else {
            LOGGER.warn("File: {} is not found so skipping this test.", file);
        }
    }

    @Test
    public void testCreateClusterable1() {
        String id = "0";
        double lat = 10 , lng = 23 , weight = 21412;

        JsonObject point = new JsonObject();

        point.addProperty("id", id);
        point.addProperty("lat", lat);
        point.addProperty("lng", lng);
        point.addProperty("weight", weight);

        Clusterable clusterable = createClusterable(point);

        assertEquals(clusterable.geocode().lat, lat, TOLERANCE);
        assertEquals(clusterable.geocode().lng, lng, TOLERANCE);
        assertEquals(clusterable.weight(), weight, TOLERANCE);
        assertEquals(clusterable.id(), id);

    }

    @Test
    public void testCreateClusterable2() {
        String id = "0";
        double lat = 10 , lng = 23;

        JsonObject point = new JsonObject();

        point.addProperty("id", id);
        point.addProperty("lat", lat);
        point.addProperty("lng", lng);

        Clusterable clusterable = createClusterable(point);

        assertEquals(clusterable.geocode().lat, lat, TOLERANCE);
        assertEquals(clusterable.geocode().lng, lng, TOLERANCE);
        assertEquals(clusterable.weight(), 1, TOLERANCE);
        assertEquals(clusterable.id(), id);

    }

    @Test
    public void testClusterData() {
        String id = "0";

        Cluster cluster = ClusterBuilder.newInstance(id)
                                        .geocode(new Geocode(0, 0))
                                        .weight(0)
                                        .build();
        Geocode coords1 = new Geocode(1, 0);
        double w1 = 1;
        String id1 = "1";
        Clusterable point1 = new ClusterableImpl(id1, coords1, w1);

        String id2 = "2";
        Geocode coords2 = new Geocode(0, 1);
        double w2 = 2;
        Clusterable point2 = new ClusterableImpl(id2, coords2, w2);

        cluster.consumeClusterer(point1);
        cluster.consumeClusterer(point2);

        JsonObject clusterData = clusterData(cluster);

        assertEquals(clusterData.get("weight").getAsDouble(), w1 + w2, TOLERANCE);
        assertEquals(clusterData.get("lat").getAsDouble(), (coords1.lat * w1 + coords2.lat * w2) / (w1 + w2), TOLERANCE);
        assertEquals(clusterData.get("lng").getAsDouble(), (coords1.lng * w1 + coords2.lng * w2) / (w1 + w2), TOLERANCE);

        JsonArray points = clusterData.getAsJsonArray("points");
        assertEquals(points.size(), 2);

        assertEquals(points.get(0).getAsJsonObject().get("id").getAsString(), id1);
        assertEquals(points.get(0).getAsJsonObject().get("lat").getAsDouble(), coords1.lat, TOLERANCE);
        assertEquals(points.get(0).getAsJsonObject().get("lng").getAsDouble(), coords1.lng, TOLERANCE);
        assertEquals(points.get(0).getAsJsonObject().get("weight").getAsDouble(), w1, TOLERANCE);

        assertEquals(points.get(1).getAsJsonObject().get("id").getAsString(), id2);
        assertEquals(points.get(1).getAsJsonObject().get("lat").getAsDouble(), coords2.lat, TOLERANCE);
        assertEquals(points.get(1).getAsJsonObject().get("lng").getAsDouble(), coords2.lng, TOLERANCE);
        assertEquals(points.get(1).getAsJsonObject().get("weight").getAsDouble(), w2, TOLERANCE);

    }

}
