package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class HaversineDistanceCalculatorTest {
    @Test
    public void testGetDistance() throws InvalidDataException{
        Coordinate p1 = new Coordinate(28.234, 78.123);
        Coordinate p2 = new Coordinate(29.123, 78.234);
        int distance = new HaversineDistanceCalculator().getDistance(p1, p2);
        assertTrue(distance > 99000 && distance < 100000);
    }
}
