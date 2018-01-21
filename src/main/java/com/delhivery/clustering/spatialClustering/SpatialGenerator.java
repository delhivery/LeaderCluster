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

package com.delhivery.clustering.spatialClustering;

import com.delhivery.clustering.algorithm.Generator;
import com.delhivery.clustering.utils.Coordinate;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class SpatialGenerator implements Generator<SpatialCluster, SpatialPoint> {

    @Override
    public SpatialCluster createCluster() {
        return new SpatialCluster();
    }

    @Override
    public SpatialPoint createClusterable(Coordinate coordinate, double weight) {
        return new SpatialPoint(coordinate, weight);
    }
}
