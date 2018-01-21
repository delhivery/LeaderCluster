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

import com.delhivery.clustering.algorithm.Clusterable;
import com.delhivery.clustering.utils.Coordinate;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class SpatialPoint implements Clusterable {

    protected String id = "undefined";
    protected Coordinate coordinate = null;
    protected double weight = 1.0;

    public SpatialPoint(){}

    public SpatialPoint(Coordinate coordinate, double weight){
        this.coordinate = coordinate;
        this.weight = weight;
        this.id = coordinate.toString();
    }

    public SpatialPoint(String id, Coordinate coordinate, double weight){
        this.id = id;
        this.coordinate = coordinate;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Clusterable clusterable) {
        SpatialPoint spatialPoint = (SpatialPoint) clusterable;
        if (coordinate.compareTo(spatialPoint.coordinate) == 0)
            return 0;
        else {
            return Double.compare(weight, spatialPoint.weight);
        }
    }

    @Override
    public String toString() {
        return String.format("{ id :%s Coordinate: %s, Weight: %f }", id, coordinate.toString(), weight);
    }

    @Override
    public int hashCode() {
        return coordinate.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SpatialPoint && ((SpatialPoint) o).compareTo(this) == 0;
    }
}
