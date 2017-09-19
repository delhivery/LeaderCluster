package com.delhivery.clustering.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import static com.delhivery.clustering.utils.Config.*;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
class OsrmDistanceCalculator implements DistanceCalculator {

    private static final int VERY_HIGH_DISTANCE = 100000; //100km
    private Logger logger = LoggerFactory.getLogger(OsrmDistanceCalculator.class);
    private int radius = VERY_HIGH_DISTANCE;

    public OsrmDistanceCalculator(int radius) {
        this.radius = radius;
    }

    @Override
    public int getDistance(Coordinate source, Coordinate destination) {

        HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();
        int distance = calculator.getDistance(source, destination);

        if (UrlHandler.isServerListening(OSRM_URL)) {
            if (distance < radius) {

                DecimalFormat df = new DecimalFormat(".######");

                String link = OSRM_URL + df.format(source.lng) + "," + df.format(source.lat) + ";" +
                        df.format(destination.lng) + "," + df.format(destination.lat);

                String output;

                if(OSRM_USER.length() > 0)
                    output = UrlHandler.processUrl(link, OSRM_USER, OSRM_PWD).orElse(null);
                else
                    output = UrlHandler.processUrl(link).orElse(null);

                if(output != null) {
                    // Find distance from returned JSON
                    JsonObject osrmOutput = new JsonParser().parse(output).getAsJsonObject();

                    String OutputCode = osrmOutput.get("code").getAsString();

                    if(OutputCode.equals("Ok")) {
                        distance = (int) (osrmOutput.getAsJsonArray("routes")
                                .get(0).getAsJsonObject()
                                .get("distance").getAsDouble());
                    }else if (OutputCode.equals("NoRoute")){
                        distance = VERY_HIGH_DISTANCE;
                    }

                }
                else {
                    logger.error("FLP> Could not calculate road distance for " + source + " to " + destination);
                    distance *= AERIAL_TO_ROAD_MULTIPLIER;
                }
            }
        }
        else{
            logger.warn("OSRM server is not running; Cannot get distances");
            distance *= AERIAL_TO_ROAD_MULTIPLIER;
        }

        return distance;
    }
}
