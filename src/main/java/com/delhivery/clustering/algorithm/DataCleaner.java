package com.delhivery.clustering.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 19/9/17
 *         The function of this class is to remove duplicate Clusterable points
 */
public class DataCleaner<T extends Cluster<T,V>, V extends Clusterable> {

    private static Logger logger = LoggerFactory.getLogger(DataCleaner.class);

    private Map<String, List<V>> allPoints = new TreeMap<>();
    private Collection<V> input;
    private Set<V> output = new HashSet<>();
    private Generator<T, V> factory;

    public DataCleaner(Collection<V> input, Generator<T, V> factory) {
        this.input = input;
        this.factory = factory;
    }

    /**
     * Removes duplicates and tracks them for expanding later
     */
    public void uniqify(){
        createCoordToPointMap();

        for(List<V> points: allPoints.values()){
            double weight = 0;
            for(V point : points) {
                weight += point.getWeight();
            }

            output.add(factory.createClusterable(points.get(0).getCoordinate(), weight));
        }

        logger.info("reduced size from:{} to:{}", input.size(), output.size());
    }

    /**
     * Creates a mapping of coord string to coordinates
     */
    private void createCoordToPointMap(){
        for(V point: input) {
            String coordinateAsKey = point.getCoordinate().toString();
            if(allPoints.containsKey(coordinateAsKey)){
                allPoints.get(coordinateAsKey).add(point);
            }
            else {
                List<V> pointsList = new LinkedList<>();
                pointsList.add(point);
                allPoints.put(coordinateAsKey, pointsList);
            }
        }
    }

    /**
     * expnads teh leader cluster output by appending duplicates
     * that were removed during the uniqify process
     * @param clusters
     * @return
     */
    public Collection<T> expandClusters(Collection<T> clusters) {

        for (T cluster : clusters) {
            Collection<V> members = cluster.getMembers();
            cluster.resetMembers();
            for (V member : members) {
                String coordinateAsKey = member.getCoordinate().toString();
                allPoints.get(coordinateAsKey).forEach(cluster::addMember);
            }
        }
        return clusters;
    }

    public Set<V> getOutput() {
        return output;
    }
}
