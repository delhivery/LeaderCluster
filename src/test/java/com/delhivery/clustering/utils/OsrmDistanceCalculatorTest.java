package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;
import org.junit.Assert;
import org.junit.Test;

import static com.delhivery.clustering.utils.Config.OSRM_URL;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class OsrmDistanceCalculatorTest {

    @Test
    public void getDistanceTest() throws InvalidDataException {

        Coordinate p1 = new Coordinate(28.454812, 77.070350);
        Coordinate p2 = new Coordinate(28.452029, 77.067657);

        if(UrlHandler.isServerListening(OSRM_URL)) {
            int distance = new OsrmDistanceCalculator(1000).getDistance(p1, p2);
            Assert.assertEquals(406, distance, 10.);
        } else
            System.out.println("Skipped OSRM test as server not available");
    }
}
