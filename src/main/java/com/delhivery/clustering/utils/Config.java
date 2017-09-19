package com.delhivery.clustering.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static final Properties CONFIG = loadConfigFile();

    static String OSRM_URL = CONFIG.getProperty("OSRM_URL", "http://localhost:5000/route/v1/driving/");
    static String OSRM_USER = CONFIG.getProperty("OSRM_USER", ""); //optional
    static String OSRM_PWD = CONFIG.getProperty("OSRM_PWD", ""); //optional

    static String GOOGLE_URL = CONFIG.getProperty("GOOGLE_URL",
                                                        "https://maps.googleapis.com/maps/api/distancematrix/json");
    static String GOOGLE_KEY = CONFIG.getProperty("GOOGLE_KEY", ""); //get from file

    static final double AERIAL_TO_ROAD_MULTIPLIER = 1.5;

    public static void setOsrmUrl(String osrmUrl) {
        OSRM_URL = osrmUrl;
    }

    public static void setOsrmUser(String osrmUser) {
        OSRM_USER = osrmUser;
    }

    public static void setOsrmPwd(String osrmPwd) {
        OSRM_PWD = osrmPwd;
    }

    public static void setGoogleUrl(String googleUrl) {
        GOOGLE_URL = googleUrl;
    }

    public static void setGoogleKey(String googleKey) {
        GOOGLE_KEY = googleKey;
    }

    /**
     * Reads a CONFIG file and converts it into a Properties object
     * @return Properties extracted from CONFIG file
     */
    private static Properties loadConfigFile() {

        Properties settings = new Properties();
        FileInputStream fileStream = null;

        try {

            fileStream = new FileInputStream("config/CONFIG.ini");
            settings.load(fileStream);

        } catch (FileNotFoundException exception) {

            logger.error("Config File not found.", exception);

        } catch (IOException exception) {

            logger.error("Config File I/O problem. IOException: ", exception);

        } finally {

            if (fileStream != null) {
                try {

                    fileStream.close();

                } catch (IOException exception) {

                    logger.error("Settings File Stream could not be closed", exception);
                }
            }
        }
        return settings;
    }
}
