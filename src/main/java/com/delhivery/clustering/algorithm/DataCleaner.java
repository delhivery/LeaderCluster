package com.delhivery.clustering.algorithm;

import java.util.*;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 19/9/17
 *         The function of this class is to remove duplicate Clusterable points
 */
public class DataCleaner<T extends Clusterable> {

    private Map<String, List<T>> allPoints = new TreeMap<>();
    private Collection<T> input;
    private Set<DummyPoint> output;

    public DataCleaner(Collection<T> input) {
        this.input = input;
    }

    public Set<DummyPoint> uniqify(){
        createCoordToPointMap();

        for(List<T> points: allPoints.values()){
            double weight = 0;
            for(T point : points) {
                weight += point.getWeight();
            }
            output.add(new DummyPoint(points.get(0).getCoordinate(), weight));
        }

        return output;
    }

    private void createCoordToPointMap(){
        for(T point: input) {
            String coordinateAsKey = point.getCoordinate().toString();
            if(allPoints.containsKey(coordinateAsKey)){
                allPoints.get(coordinateAsKey).add(point);
            }
            else {
                List<T> pointsList = new LinkedList<>();
                pointsList.add(point);
                allPoints.put(coordinateAsKey, pointsList);
            }
        }
    }
}
