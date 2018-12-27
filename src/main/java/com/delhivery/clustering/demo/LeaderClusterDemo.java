package com.delhivery.clustering.demo;

import static com.delhivery.clustering.utils.Utils.dumpToFile;
import static com.delhivery.clustering.utils.Utils.getDistanceMeasure;
import static com.delhivery.clustering.utils.Utils.loadFile;
import static com.google.common.collect.Streams.stream;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;

import java.util.Collection;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;
import com.delhivery.clustering.Clusterer;
import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.delhivery.clustering.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class LeaderClusterDemo {

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

    private static JsonArray run(JsonObject input) {

        DistanceMeasure distanceMeasure = getDistanceMeasure(input.has("distanceMeasure") ? input.get("distanceMeasure").getAsString() : "haversine");

        double throwDistance = input.get("throwDistance").getAsDouble();// in meter.

        boolean enableGeocodeCompression = input.has("enableGeocodeCompression") ? input.get("enableGeocodeCompression").getAsBoolean() : false;

        int assignToNearestCluster = input.has("assignToNearest") ? input.get("assignToNearest").getAsInt() : 0;

        Collection<Clusterable> points = stream(input.getAsJsonArray("points")).map(JsonElement::getAsJsonObject)
                                                                               .map(Utils::createClusterable)
                                                                               .collect(toList());
        LCBuilder builder = LCBuilder.newInstance()
                                     .distanceConstraint(throwDistance, distanceMeasure)
                                     .refineAssignToClosestCluster(assignToNearestCluster, distanceMeasure);

        if (enableGeocodeCompression)
            builder = builder.enableLcOnCompressedClusterables();

        Clusterer algorithm = builder.build();

        Collection<Cluster> clusters = algorithm.cluster(points);

        JsonArray output = new JsonArray();

        clusters.stream().map(Utils::clusterData).forEach(output::add);

        return output;

    }

}
