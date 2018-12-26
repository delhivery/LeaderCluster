package com.delhivery.clustering.distances;

import static com.delhivery.clustering.config.Config.GOOGLE_KEY;
import static com.delhivery.clustering.config.Config.GOOGLE_URL;
import static com.delhivery.clustering.config.Config.OSRM_PWD;
import static com.delhivery.clustering.config.Config.OSRM_URL;
import static com.delhivery.clustering.config.Config.OSRM_USER;
import static com.delhivery.clustering.utils.Utils.formatNumber;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import java.util.Optional;

import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.utils.UrlHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class DistanceMeasureFactory {
    private static final JsonParser JSON_PARSER = new JsonParser();

    public static final DistanceMeasure HAVERSINE       = DistanceMeasureFactory::haversineDistance;
    public static final DistanceMeasure GOOGLE_DISTANCE = DistanceMeasureFactory::googleDistance;
    public static final DistanceMeasure OSRM            = DistanceMeasureFactory::osrm;

    private static double haversineDistance(Geocode to, Geocode from) {

        double deltaLat = toRadians(from.lat - to.lat);
        double deltaLng = toRadians(from.lng - to.lng);

        double lat1 = toRadians(to.lat);
        double lat2 = toRadians(from.lat);

        double a = haversine(deltaLat) + haversine(deltaLng) * cos(lat1) * cos(lat2);

        double c = 2.0 * asin(sqrt(a));

        return 6372.8 * c * 1000;
    }

    private static double haversine(double theta) {
        return pow(sin(theta / 2), 2);
    }

    private static double googleDistance(Geocode source, Geocode destination) {

        String link = GOOGLE_URL + "?origins=" + formatNumber(source.lat) + "," + formatNumber(source.lng) +
            "&destinations=" + formatNumber(destination.lat) + "," + formatNumber(destination.lng) +
            "&language=en&sensor=false&key=" + GOOGLE_KEY;

        Optional<String> output = UrlHandler.processUrl(link);
        System.out.println(output.get());
        if (output.isPresent())
            return JSON_PARSER.parse(output.get())
                              .getAsJsonObject()
                              .get("rows")
                              .getAsJsonArray()
                              .get(0)
                              .getAsJsonObject()
                              .get("elements")
                              .getAsJsonArray()
                              .get(0)
                              .getAsJsonObject()
                              .get("distance")
                              .getAsJsonObject()
                              .get("value")
                              .getAsDouble();

        throw new DistanceFetchException("unable to fetch distances from Google");
    }

    private static double osrm(Geocode source, Geocode destination) {

        if (UrlHandler.isServerListening(OSRM_URL)) {

            String link = OSRM_URL
                + formatNumber(source.lng) + ","
                + formatNumber(source.lat) + ";"
                + formatNumber(destination.lng) + ","
                + formatNumber(destination.lat);

            Optional<String> output;

            if (OSRM_USER.length() > 0)
                output = UrlHandler.processUrl(link, OSRM_USER, OSRM_PWD);
            else
                output = UrlHandler.processUrl(link);

            if (output.isPresent()) {
                // Find distance from returned JSON
                JsonObject osrmOutput = JSON_PARSER.parse(output.get()).getAsJsonObject();

                String OutputCode = osrmOutput.get("code").getAsString();

                if (OutputCode.equalsIgnoreCase("ok")) {
                    return (osrmOutput.getAsJsonArray("routes")
                                      .get(0)
                                      .getAsJsonObject()
                                      .get("distance")
                                      .getAsDouble());
                }

                throw new DistanceFetchException("Response code from osrm is not 'ok'. It is: " + OutputCode);

            }
            throw new DistanceFetchException("unable to fetch distances from OSRM");

        } else
            throw new DistanceFetchException("OSRM server is not running; Cannot get distances");

    }
}
