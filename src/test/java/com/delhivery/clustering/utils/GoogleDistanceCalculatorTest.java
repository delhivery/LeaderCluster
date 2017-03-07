package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class GoogleDistanceCalculatorTest {
    @Test
    public void getDistanceTest() throws InvalidDataException {

        Coordinate p1 = new Coordinate(28.454812, 77.070350);
        Coordinate p2 = new Coordinate(28.452029, 77.067657);

        int distance = new GoogleDistanceCalculator().getDistance(p1, p2);

        Assert.assertEquals(406, distance, 10.);
    }
}
