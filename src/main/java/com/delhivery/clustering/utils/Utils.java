package com.delhivery.clustering.utils;

import static com.delhivery.clustering.config.Constants.DECIMAL_FORMAT;
import static com.delhivery.clustering.config.Constants.TOLERANCE;
import static com.delhivery.clustering.config.StaticObjects.GSON;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.GOOGLE_DISTANCE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.HAVERSINE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.OSRM;
import static java.lang.Math.abs;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;
import com.delhivery.clustering.ClusterableImpl;
import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class Utils {

    private Utils() {}

    public static String formatNumber(double n) {
        return DECIMAL_FORMAT.format(n);
    }

    public static boolean isZero(double d) {
        return abs(d) <= TOLERANCE;
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

        return new ClusterableImpl(point.get("id").getAsString(), geocode, weight);
    }

    public static JsonObject clusterData(Cluster cluster) {
        JsonObject output = new JsonObject();

        output.addProperty("lat", cluster.geocode().lat);
        output.addProperty("lng", cluster.geocode().lng);
        output.addProperty("weight", cluster.weight());

        JsonArray docs = new JsonArray();
        output.add("points", docs);

        for (Clusterable c : cluster.getMembers()) {
            JsonObject member = new JsonObject();

            member.addProperty("id", c.id());
            member.addProperty("lat", c.geocode().lat);
            member.addProperty("lng", c.geocode().lng);
            member.addProperty("weight", c.weight());

            docs.add(member);
        }

        return output;
    }
}
