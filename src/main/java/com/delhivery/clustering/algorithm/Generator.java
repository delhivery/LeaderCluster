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

package com.delhivery.clustering.algorithm;

import com.delhivery.clustering.utils.Coordinate;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public interface Generator<T extends Cluster<T, V>, V extends Clusterable> {

    /**
     * Creates new clusters of {@link Cluster}
     * @return a new cluster
     */
    T createCluster();

    /**
     * Creates replacement clusterable data-points
     * @param coordinate point's coordinate
     * @param weight point's weight
     * @return clusterable point
     */
    V createClusterable(Coordinate coordinate, double weight);
}
