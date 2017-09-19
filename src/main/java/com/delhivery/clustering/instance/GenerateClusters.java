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

package com.delhivery.clustering.instance;

import com.delhivery.clustering.exceptions.ClusteringException;
import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.spatialClustering.LeaderCluster;
import com.delhivery.clustering.spatialClustering.SpatialCluster;
import com.delhivery.clustering.spatialClustering.SpatialPoint;
import com.delhivery.clustering.utils.DistanceCalculatorFactory;

import java.util.Collection;

import static com.delhivery.clustering.utils.DistanceCalculatorFactory.DistanceType.HAVERSINE;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 5/1/17
 */
public class GenerateClusters {

    public static void main(String[] args) throws InvalidDataException, ClusteringException{
        String inputCsv = args[0];
        int radius = Integer.parseInt(args[1]);

        DistanceCalculatorFactory.DistanceType distanceType;

        if(args.length == 3)
            distanceType = DistanceCalculatorFactory.DistanceType.valueOf(args[2].toUpperCase());
        else
            distanceType = HAVERSINE;

        CsvHandler handler = new CsvHandler();
        Collection<SpatialPoint> data = handler.readInput(inputCsv);
        Collection<SpatialCluster> output = LeaderCluster.cluster(data, radius, DistanceCalculatorFactory
                .getCalculator(distanceType, radius));

        for(SpatialCluster cluster: output)
            cluster.generateConvexHull();

        String outputFile = inputCsv.split(".")[0] + "_output.csv";

        handler.writeOutput(outputFile, output);
    }

}
