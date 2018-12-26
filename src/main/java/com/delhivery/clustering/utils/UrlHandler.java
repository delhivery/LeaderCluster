package com.delhivery.clustering.utils;

import static com.mashape.unirest.http.Unirest.setTimeouts;
import static java.net.HttpURLConnection.HTTP_BAD_GATEWAY;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;

import org.slf4j.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class UrlHandler {

    private final static Logger LOGGER = getLogger(UrlHandler.class);

    static {
        // connectionTimeout, socketTimeout in milliseconds
        setTimeouts(90000, 30000);
    }

    /**
     * Allows getting data from a HTTP API without authorization
     *
     * @param link HTTP URL link
     * @return output of the API request
     * @throws NullPointerException just in case of no output
     */
    public static Optional<String> processUrl(String link) throws NullPointerException {

        GetRequest request = Unirest.get(link)
                                    .header("content-type", "application/json")
                                    .header("accept", "application/json")
                                    .header("cache-control", "no-cache");

        return getUrlResponse(request);
    }

    /**
     * Allows getting data from a HTTP API with apikey authorization
     *
     * @param link HTTP URL link
     * @return output of the API request
     * @throws NullPointerException just in case of no output
     */
    public static Optional<String> processUrl(String link, String username, String password)
            throws NullPointerException {

        GetRequest request = Unirest.get(link)
                                    .header("content-type", "application/json")
                                    .header("accept", "application/json")
                                    .header("cache-control", "no-cache")
                                    .basicAuth(username, password);

        return getUrlResponse(request);
    }

    private static Optional<String> getUrlResponse(GetRequest request) {
        String output = null , error = null;
        boolean UrlProcessed = false;
        int attempts = 0;

        while (!UrlProcessed) {

            try {
                HttpResponse<String> response = request.asString();

                if (response.getStatus() == HTTP_OK)
                    output = response.getBody();
                else if (response.getStatus() == HTTP_BAD_REQUEST)
                    error = response.getBody();

                UrlProcessed = true;

            } catch (UnirestException exception) {

                LOGGER.error("UnirestException", exception);

            } catch (Exception exception) {

                LOGGER.error("IOException: ", exception);

                if (!UrlHandler.testInet("google.com"))
                    throw new RuntimeException("No Internet Connection!");
            }
            attempts++;
            // Limit the number of attempts to 3
            if (attempts > 3)
                break;
        }

        return ofNullable(output != null ? output : error);
    }

    /**
     * to check if internet is connected
     *
     * @param site any website
     * @return True if internet is connected
     */
    private static boolean testInet(String site) {

        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(site, 80);

        try {
            sock.connect(addr, 3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                sock.close();
            } catch (IOException exception) {
                LOGGER.error("IOException: ", exception);
            }
        }
    }

    /**
     * To check if a port is open
     *
     * @param host server
     * @param port port of the server
     * @return True if a server is listening on a specified port
     */
    public static boolean isServerListening(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * To check if server is up and running
     * @param host server to be checked
     * @return true or false
     */
    public static boolean isServerListening(String host) {
        HttpResponse<String> response;
        try {
            response = Unirest.get(host).asString();

            return response.getStatus() != HTTP_BAD_GATEWAY;
        } catch (UnirestException exception) {
            return false;
        }
    }
}
