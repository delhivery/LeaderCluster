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
import com.delhivery.clustering.ClusterableImpl;
import com.delhivery.clustering.Clusterer;
import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class LeaderClusterDemo {
    private static final Gson GSON = new Gson();

    /**
     * Sample Input format: see json file at data/sampleInput.json
     * {
            "throwDistance": 500,             // required field
            "enableGeocodeCompression": true, // defaults to false
            "assignToNearest": 5,             // refinement strategy, defaults to 0.
            "distanceMeasure": "haversine",   // distance measure, defaults to "haversine"
            "points": [
                {
                    "id": "1",
                    "lat": 28.011,
                    "lng": 77.01100000000001,
                    "weight": 1
                },
                {
                    "id": "2",
                    "lat": 28.011999999999997,
                    "lng": 77.009,
                    "weight": 2
                }
            ]
      }
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

        DistanceMeasure distanceMeasure = getDistanceMeasure(input.has("distanceMeasure") ? input.get("distanceMeasure").getAsString() : "haversine");

        double throwDistance = input.get("throwDistance").getAsDouble();// in meter.

        boolean enableGeocodeCompression = input.has("enableGeocodeCompression") ? input.get("enableGeocodeCompression").getAsBoolean() : false;

        int assignToNearestCluster = input.has("assignToNearest") ? input.get("assignToNearest").getAsInt() : 0;

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

        return new ClusterableImpl(point.get("id").getAsString(), geocode, weight);
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
