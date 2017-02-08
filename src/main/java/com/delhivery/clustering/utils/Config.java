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

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private static final String fileName = "config/credentials.ini";
    static final Properties CREDENTIALS = loadCredentials();

    private static Properties loadCredentials(){
        Properties properties = new Properties();
        FileInputStream credentialsFileStream = null;

        try {
            credentialsFileStream = new FileInputStream(fileName);
            properties.load(credentialsFileStream);

        } catch (FileNotFoundException exception) {

            logger.error("Settings File not found.", exception);

        } catch (IOException exception) {

            logger.error("Settings File I/O problem. IOException: ", exception);

        } finally {

            if (credentialsFileStream != null) {
                try {

                    credentialsFileStream.close();

                } catch (IOException exception) {

                    logger.error("Settings File Stream could not be closed", exception);
                }
            }
        }
        return properties;
    }
}
