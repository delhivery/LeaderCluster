package com.delhivery.clustering.utils;

import static com.delhivery.clustering.distances.DistanceMeasureFactory.GOOGLE_DISTANCE;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.delhivery.clustering.Geocode;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class GoogleDistanceCalculatorTest {
    @Ignore
    @Test
    public void getDistanceTest() {

        Geocode p1 = new Geocode(28.454812, 77.070350);
        Geocode p2 = new Geocode(28.452029, 77.067657);

        double distance = GOOGLE_DISTANCE.distance(p1, p2);
        System.out.println(distance);
        Assert.assertEquals(406, distance, 10.);
    }
}
