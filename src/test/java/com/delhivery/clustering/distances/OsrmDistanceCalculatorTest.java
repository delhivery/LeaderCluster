package com.delhivery.clustering.distances;

import static com.delhivery.clustering.config.Config.OSRM_URL;

import org.junit.Assert;
import org.junit.Test;

import com.delhivery.clustering.distances.DistanceMeasureFactory;
import com.delhivery.clustering.elements.Geocode;
import com.delhivery.clustering.utils.UrlHandler;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class OsrmDistanceCalculatorTest {

    @Test
    public void getDistanceTest() {

        Geocode p1 = new Geocode(28.454812, 77.070350);
        Geocode p2 = new Geocode(28.452029, 77.067657);

        if (UrlHandler.isServerListening(OSRM_URL)) {
            double distance = DistanceMeasureFactory.OSRM.distance(p1, p2);

            Assert.assertEquals(406, distance, 10.);
        } else
            System.out.println("Skipped OSRM test as server not available");
    }
}
