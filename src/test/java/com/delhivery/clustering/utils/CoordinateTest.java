package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class CoordinateTest {

    @Test
    public void basicTest(){

        double lats[] = { 90.0000001, -90.000001, 89.9999999, -89.9999999};
        double lngs[] = { 180.000001, -180.000001, 179.999999, -179.999999};

        for (int i = 0; i < 4; i++) {

            try {

                Coordinate test = new Coordinate(lats[i], lngs[i]);
                Assert.assertEquals(lats[i], test.lat, 0.0);
                Assert.assertEquals(lngs[i], test.lng, 0.0);

            } catch (InvalidDataException exception) {

                System.out.println("Rejected Illegal Coordinate: (" + lats[i] + "," + lngs[i] + ")");
            }
        }
    }

    @Test
    public void geometryTest() throws InvalidDataException{

        Coordinate coordinate = new Coordinate(1.23, 5.45);
        String expectedGeometry = "POINT(5.45 1.23)";
        Assert.assertEquals(expectedGeometry, coordinate.wktGeometry());
    }
}
