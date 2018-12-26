package com.delhivery.clustering.demo;

import static com.delhivery.clustering.distances.DistanceMeasureFactory.GOOGLE_DISTANCE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.HAVERSINE;
import static com.delhivery.clustering.distances.DistanceMeasureFactory.OSRM;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;
import com.delhivery.clustering.Clusterer;
import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class Demo {
    private static final Gson GSON = new Gson();

    /**
     * Sample Input format: see json file at data/sampleInput.json
     * Sample output format: 
     * 
     * [
     *      {
     *          "lat": cluster latitude,
     *          "lng": cluster longitude,
     *          "weight": weight of cluster,
     *          "points":[
     *              {   "id": id of point,
     *                  "lat": point latitude,
     *                  "lng": point longitude,
     *                  "weight": point weight (defaults to 1)
     *              }
     *          ]
     *      }
     * ]
     * 
     */
    public static void main(String[] args) {
        String fileName = args.length >= 1 ? args[0] : "data/sampleInput.json";

        JsonObject input = loadFile(get(fileName));
        JsonArray output = run(input);

        String outputFile = args.length >= 2 ? args[1] : "data/sampleOutput.json";

        dumpToFile(output, get(outputFile));
    }

    private static JsonObject loadFile(Path path) {
        try (Reader reader = newBufferedReader(path)) {

            return GSON.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            throw new UncheckedIOException(e);

        }
    }

    private static void dumpToFile(Object data, Path out) {
        try (Writer writer = newBufferedWriter(out)) {

            GSON.toJson(data, writer);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static JsonArray run(JsonObject input) {

        DistanceMeasure distanceMeasure = getDistanceMeasure(input.get("distanceMeasure").getAsString());

        double throwDistance = input.get("throwDistance").getAsDouble();

        boolean enableGeocodeCompression = input.get("enableGeocodeCompression").getAsBoolean();

        int assignToNearestCluster = input.get("assignToNearest").getAsInt();

        Collection<Clusterable> points = createClusterables(input.getAsJsonArray("points"));

        LCBuilder builder = LCBuilder.newInstance()
                                     .distanceConstraint(throwDistance, distanceMeasure)
                                     .refineAssignToClosestCluster(assignToNearestCluster, distanceMeasure);

        if (enableGeocodeCompression)
            builder = builder.enableLcOnCompressedClusterables();

        Clusterer algorithm = builder.build();

        Collection<Cluster> clusters = algorithm.cluster(points);

        JsonArray output = new JsonArray();

        for (Cluster cluster : clusters)
            output.add(createClusterOutput(cluster));

        return output;

    }

    private static DistanceMeasure getDistanceMeasure(String name) {
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

    private static Clusterable create(JsonObject point) {
        Geocode geocode = new Geocode(point.get("lat").getAsDouble(), point.get("lng").getAsDouble());

        double weight = point.has("weight") ? point.get("weight").getAsDouble() : 1;

        return new UniqueClusterableImpl(point.get("id").getAsString(), geocode, weight);
    }

    private static Collection<Clusterable> createClusterables(JsonArray points) {
        Collection<Clusterable> clusterables = new ArrayList<>(points.size());

        for (JsonElement e : points)
            clusterables.add(create(e.getAsJsonObject()));

        return clusterables;
    }

    private static JsonObject createClusterOutput(Cluster cluster) {
        JsonObject output = new JsonObject();

        output.addProperty("lat", cluster.geocode().lat);
        output.addProperty("lng", cluster.geocode().lng);
        output.addProperty("weight", cluster.weight());

        JsonArray docs = new JsonArray();
        output.add("points", docs);

        for (Clusterable c : cluster.getMembers()) {
            UniqueClusterable m = (UniqueClusterable) c;

            JsonObject member = new JsonObject();

            member.addProperty("id", m.id());
            member.addProperty("lat", m.geocode().lat);
            member.addProperty("lng", m.geocode().lng);
            member.addProperty("weight", m.weight());

            docs.add(member);
        }

        return output;
    }

}
