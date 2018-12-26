package com.delhivery.clustering.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static final Properties CONFIG = loadConfigFile();

    public final static String OSRM_URL  = CONFIG.getProperty("OSRM_URL", "http://localhost:5000/route/v1/driving/");
    public final static String OSRM_USER = CONFIG.getProperty("OSRM_USER", "");                                      // optional
    public final static String OSRM_PWD  = CONFIG.getProperty("OSRM_PWD", "");                                       // optional

    public final static String GOOGLE_URL = CONFIG.getProperty("GOOGLE_URL",
            "https://maps.googleapis.com/maps/api/distancematrix/json");
    public final static String GOOGLE_KEY = CONFIG.getProperty("GOOGLE_KEY", ""); // get from file

    public static final double AERIAL_TO_ROAD_MULTIPLIER = 1.5;

    /**
     * Reads a CONFIG file and converts it into a Properties object
     * @return Properties extracted from CONFIG file
     */
    private static Properties loadConfigFile() {

        try (FileInputStream fileStream = new FileInputStream("config/CONFIG.ini")) {
            Properties properties = new Properties();
            properties.load(fileStream);
            return properties;
        } catch (FileNotFoundException exception) {
            logger.error("Config File not found.", exception);
            throw new UncheckedIOException(exception);
        } catch (IOException exception) {
            logger.error("Config File I/O problem. IOException: ", exception);
            throw new UncheckedIOException(exception);

        }
    }
}
