package com.delhivery.clustering.utils;

import static com.delhivery.clustering.config.Constants.LAT_LNG_FORMATTER;
import static com.delhivery.clustering.config.Constants.TOLERANCE;
import static com.delhivery.clustering.config.StaticObjects.GSON;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.GOOGLE_DISTANCE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.HAVERSINE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.OSRM;
import static java.lang.Math.abs;
import static java.lang.String.valueOf;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.elements.Cluster;
import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.ClusterableImpl;
import com.delhivery.clustering.elements.Geocode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class Utils {

    private Utils() {}

    public static String formatNumber(double n) {
        return LAT_LNG_FORMATTER.format(n);
    }

    public static boolean isZero(double d) {
        return abs(d) <= TOLERANCE;
    }

    public static Supplier<String> iDCreator() {
        AtomicLong idProvider = new AtomicLong(0);
        return () -> valueOf(idProvider.getAndIncrement());
    }

    public static Geocode weightedGeocode(Clusterable a, Clusterable b) {
        double totalWeight = a.weight() + b.weight();

        if (isZero(totalWeight))
            throw new IllegalArgumentException("sum of weight of clusterable=" + a + " and " + b + " is zero");

        Geocode from = a.geocode() , to = b.geocode();
        double fromW = a.weight() , toW = b.weight();

        double lat = (from.lat * fromW + to.lat * toW) / totalWeight;
        double lng = (from.lng * fromW + to.lng * toW) / totalWeight;

        return new Geocode(lat, lng);
    }

    // --------------------------------------------------------------

    public static BiPredicate<Geocode, Geocode> distanceConstraint(double maxDistance, DistanceMeasure distanceMeasure) {
        return (from, to) -> distanceMeasure.distance(from, to) <= maxDistance;
    }

    // ------------------ Parser utility ----------------------------
    public static JsonObject loadFile(Path path) {
        try (Reader reader = newBufferedReader(path)) {

            return GSON.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            throw new UncheckedIOException(e);

        }
    }

    public static void dumpToFile(Object data, Path out) {
        try (Writer writer = newBufferedWriter(out)) {

            GSON.toJson(data, writer);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static DistanceMeasure getDistanceMeasure(String name) {
        name = name.toLowerCase();

        switch (name) {
            case "google":
                return GOOGLE_DISTANCE;
            case "osrm":
                return OSRM;
            case "haversine":
                return HAVERSINE;
            default:
                throw new IllegalArgumentException("distance measure name: '" + name + "' is not valid");
        }
    }

    public static Clusterable createClusterable(JsonObject point) {
        Geocode geocode = new Geocode(point.get("lat").getAsDouble(), point.get("lng").getAsDouble());

        double weight = point.has("weight") ? point.get("weight").getAsDouble() : 1;

        ClusterableImpl out = new ClusterableImpl(point.get("id").getAsString(), geocode, weight);

        out.userData(point);

        return out;
    }

    public static JsonObject clusterData(Cluster cluster) {
        JsonObject output = new JsonObject();

        output.addProperty("lat", cluster.geocode().lat);
        output.addProperty("lng", cluster.geocode().lng);
        output.addProperty("weight", cluster.weight());

        JsonArray docs = new JsonArray();
        output.add("points", docs);

        for (Clusterable c : cluster.getMembers()) {
            JsonObject member = null;

            if (c instanceof ClusterableImpl && nonNull(((ClusterableImpl) c).userData()))
                member = ((ClusterableImpl) c).userData();
            else {

                member = new JsonObject();
                member.addProperty("id", c.id());
                member.addProperty("lat", c.geocode().lat);
                member.addProperty("lng", c.geocode().lng);
                member.addProperty("weight", c.weight());

            }

            docs.add(member);
        }

        return output;
    }
}
