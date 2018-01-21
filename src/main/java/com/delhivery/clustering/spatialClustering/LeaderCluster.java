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

import com.delhivery.clustering.algorithm.LeaderClusterAlgorithm;
import com.delhivery.clustering.exceptions.ClusteringException;
import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.utils.DistanceCalculator;
import com.delhivery.clustering.utils.HaversineDistanceCalculator;

import java.util.Collection;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 4/1/17
 */
public class LeaderCluster {

    /**
     * Clusters using Haversine Distance calculator
     * @param data collection of spatial data points to be clustered
     * @param radius cluster radius in meters
     * @return a collections of clusters
     * @throws ClusteringException
     * @throws InvalidDataException
     */
    public static Collection<SpatialCluster> cluster(Collection<SpatialPoint> data, int radius)
            throws ClusteringException, InvalidDataException{

        LeaderClusterAlgorithm<SpatialCluster, SpatialPoint> clusterAlgorithm =
                new LeaderClusterAlgorithm<>(new SpatialGenerator(), data,
                        new HaversineDistanceCalculator(), radius);

        return clusterAlgorithm.cluster();
    }

    /**
     * Clusters using a custom Distance calculator
     * @param data collection of spatial data points to be clustered
     * @param radius cluster radius in meters
     * @param calculator custom distance calculator
     * @return a collections of clusters
     * @throws ClusteringException
     * @throws InvalidDataException
     */
    public static Collection<SpatialCluster> cluster(Collection<SpatialPoint> data, int radius,
                                                     DistanceCalculator calculator)
            throws ClusteringException, InvalidDataException{

        LeaderClusterAlgorithm<SpatialCluster, SpatialPoint> clusterAlgorithm =
                new LeaderClusterAlgorithm<>(new SpatialGenerator(), data, calculator, radius);

        return clusterAlgorithm.cluster();
    }

}
