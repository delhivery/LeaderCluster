package com.delhivery.clustering.utils;

import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import static com.delhivery.clustering.utils.Config.GOOGLE_KEY;
import static com.delhivery.clustering.utils.Config.GOOGLE_URL;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
class GoogleDistanceCalculator implements DistanceCalculator {

    private static Logger logger = LoggerFactory.getLogger(GoogleDistanceCalculator.class);

    private static final double AERIAL_TO_ROAD_MULTIPLIER = 1.5;

    @Override
    public int getDistance(Coordinate source, Coordinate destination) {

        int distance =-1;
        HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();

        while (distance == -1) {

            DecimalFormat df = new DecimalFormat(".######");

            String link = GOOGLE_URL + "?origins=" + df.format(source.lat) + "," + df.format(source.lng) +
                    "&destinations="+ df.format(destination.lat) + "," + df.format(destination.lng) +
                    "&language=en&sensor=false&key=" + GOOGLE_KEY;

            String output = UrlHandler.processUrl(link).orElse(null);

            if(output != null) {
                // Find distance from returned JSON
                JsonParser parser = new JsonParser();
                distance = (int) (parser.parse(output)
                        .getAsJsonObject()
                        .get("rows").getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("elements").getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("distance").getAsJsonObject()
                        .get("value").getAsDouble());
            }else {
                logger.warn("FLP> Could not calculate road distance for " + source + " to " + destination);
                //If OSRM does not return result, then use Haversine distance * multiplier
                distance = (int) Math.round(AERIAL_TO_ROAD_MULTIPLIER * calculator.getDistance(source, destination));
            }
        }

        return distance;
    }
}
