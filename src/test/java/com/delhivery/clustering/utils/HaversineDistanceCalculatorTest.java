/*
 * This file is part of the JavaLeaderCluster distribution.
 * Copyright (c) 2017 Delhivery India Pvt. Ltd.
 *
 * JavaLeaderCluster is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * JavaLeaderCluster is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.delhivery.clustering.utils;

import org.junit.Assert;
import org.junit.Test;

import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.distances.DistanceMeasureFactory;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class HaversineDistanceCalculatorTest {

    @Test
    public void getDistanceTest() {

        Geocode p1 = new Geocode(28.234, 78.123);
        Geocode p2 = new Geocode(29.123, 78.234);

        double distance = DistanceMeasureFactory.HAVERSINE.distance(p1, p2);

        Assert.assertEquals(99450, distance, 30.);
    }
}
