/*
 * This file is part of the LeaderCluster distribution.
 * Copyright (c) 2017 Delhivery India Pvt. Ltd.
 *
 * LeaderCluster is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * LeaderCluster is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.delhivery.clustering.spatialClustering;

import com.delhivery.clustering.algorithm.Cluster;
import com.delhivery.clustering.utils.Coordinate;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class SpatialCluster implements Cluster<SpatialCluster, SpatialPoint>{

    private Coordinate coordinate;
    private double weight;
    private Set<SpatialPoint> members = new HashSet<>();

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Set<SpatialPoint> getMembers() {
        return members;
    }

    @Override
    public boolean addMember(SpatialPoint member) {
        return members.add(member);
    }

    @Override
    public int compareTo(SpatialCluster spatialCluster) {
        return new Double(weight).compareTo(spatialCluster.weight);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
