package com.delhivery.clustering.utils;

import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import static com.delhivery.clustering.utils.Config.OSRM_URL;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
class OsrmDistanceCalculator implements DistanceCalculator {

    private Logger logger = LoggerFactory.getLogger(OsrmDistanceCalculator.class);

    @Override
    public int getDistance(Coordinate source, Coordinate destination) {

        int distance =-1;

        HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();

        if (UrlHandler.isServerListening(OSRM_URL)) {
            while (distance == -1) {
                try {

                    DecimalFormat df = new DecimalFormat(".######");

                    String link = OSRM_URL + df.format(source.lng) + "," + df.format(source.lat) + ";" +
                            df.format(destination.lng) + "," + df.format(destination.lat);

                    String output = UrlHandler.processUrl(link, null);
                    // Find distance from returned JSON
                    JsonParser parser = new JsonParser();
                    distance = (int) (parser.parse(output)
                            .getAsJsonObject()
                            .getAsJsonArray("routes")
                            .get(0).getAsJsonObject()
                            .get("distance").getAsDouble());

                } catch (NullPointerException exception) {

                    logger.warn("FLP> Could not calculate road distance for " + source + " to " + destination);

                    //If OSRM does not return result, then use Haversine distance
                    distance = calculator.getDistance(source, destination);
                }
            }
        }
        else{
            logger.warn("OSRM server is not running; Cannot get distances");

            //If OSRM is not running, then use Haversine distance
            distance = calculator.getDistance(source, destination);
        }

        return distance;
    }
}
