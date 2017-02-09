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

        Coordinate p1 = new Coordinate(28.234, 78.123);
        Coordinate p2 = new Coordinate(29.123, 78.234);

        int distance = new GoogleDistanceCalculator().getDistance(p1, p2);

        Assert.assertEquals(128000, distance, 1000.);
    }
}
