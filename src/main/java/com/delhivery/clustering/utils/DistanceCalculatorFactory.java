package com.delhivery.clustering.utils;

import com.delhivery.clustering.exceptions.InvalidDataException;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 8/2/17
 */
public class DistanceCalculatorFactory {

    /**
     * Lists the different distance calculator types that are available
     */
    public enum DistanceType { HAVERSINE, OSRM, GOOGLE}

    /**
     * Returns a distance calculator
     * @param type type of distance
     * @return distance calculator object
     */
    public static DistanceCalculator getCalculator(DistanceType type){

        DistanceCalculator calculator = null;

        switch(type){
            case HAVERSINE: calculator = new HaversineDistanceCalculator();
                break;
            case OSRM: calculator = new OsrmDistanceCalculator();
                break;
            case GOOGLE: calculator = new GoogleDistanceCalculator();
                break;
        }

        return calculator;
    }
}
