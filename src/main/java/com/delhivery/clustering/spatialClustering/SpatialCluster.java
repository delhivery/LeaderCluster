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

import com.delhivery.clustering.algorithm.Cluster;
import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.utils.Coordinate;
import com.google.gson.Gson;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.*;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class SpatialCluster implements Cluster<SpatialCluster, SpatialPoint>{

    protected String id = null;
    protected Coordinate coordinate;
    protected double weight;
    protected Set<SpatialPoint> members = new HashSet<>();
    protected List<Coordinate> chull;

    public SpatialCluster(){}

    public String getId() {
        if(id == null)
            createId();
        return id;
    }

    private void createId() {
        SpatialPoint maxPoint = Collections.max(members, Comparator.comparing(SpatialPoint::getWeight));
        id = maxPoint.getId();
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

    /**
     * Generates convex hull of the cluster
     * @throws InvalidDataException
     */
    public void generateConvexHull() throws InvalidDataException{
        List<com.vividsolutions.jts.geom.Coordinate> clientCoords = new ArrayList<>();

        for (SpatialPoint point: members){
            Coordinate coordinate = point.getCoordinate();
            clientCoords.add(new com.vividsolutions.jts.geom.Coordinate(coordinate.lng, coordinate.lat));
        }
        ConvexHull convexHull = new ConvexHull(clientCoords
                .toArray(new com.vividsolutions.jts.geom.Coordinate[clientCoords.size()]),new GeometryFactory());
        Geometry polygon = convexHull.getConvexHull();

        chull = new LinkedList<>();
        for(com.vividsolutions.jts.geom.Coordinate coordinate: polygon.getCoordinates()){
            Coordinate coord = new Coordinate(coordinate.y, coordinate.x);
            chull.add(coord);
        }
    }

    public List<Coordinate> getChull() {
        return chull;
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

    @Override
    public void resetMembers() {
        members = new HashSet<>();
    }
}
