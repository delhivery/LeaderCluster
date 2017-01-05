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

import com.delhivery.clustering.exceptions.InvalidDataException;
import com.delhivery.clustering.spatialClustering.SpatialCluster;
import com.delhivery.clustering.spatialClustering.SpatialPoint;
import com.delhivery.clustering.utils.Coordinate;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Anurag Paul(anurag.paul@delhivery.com)
 *         Date: 5/1/17
 */
public class CsvHandler {

    private static final Logger logger = LoggerFactory.getLogger(CsvHandler.class);

    /**
     * Reads the specified csv file and returns the input spatial points
     * @param fileName input file
     * @return a Set of input points
     * @throws InvalidDataException
     */
    public Set<SpatialPoint> readInput(String fileName) throws InvalidDataException {
        Set<SpatialPoint> inputData = new HashSet<>();
        CSVReader reader = null;
        String[] record;

        try {

            reader = new CSVReader(new FileReader(fileName));
            reader.readNext();

            while ((record = reader.readNext()) != null) {
                String id = record[0];
                double lat = Double.parseDouble(record[1]);
                double lng = Double.parseDouble(record[2]);

                Coordinate coords = new Coordinate(lat, lng);

                double weight = Double.parseDouble(record[3]);
                inputData.add(new SpatialPoint(id, coords, weight));
            }
        } catch (IOException exception) {
            File f = new File(fileName);
            if (!f.exists() || f.isDirectory()) {
                logger.error("FLP> Client Data File does not exist");
            } else
                logger.error("IOException: ", exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException exception) {
                    logger.error("FLP> Client Data File stream could not be closed");
                }
            }
        }

        return inputData;
    }

    /**
     * Writes the output to a file
     * @param clusters Spatial clusters
     */
    public void writeOutput(Collection<SpatialCluster> clusters){
        CSVWriter writer = null;
        String fileName = "output.csv";

        try {
            writer = new CSVWriter(new FileWriter(fileName));
            String[] record = {"Centroid Latitude", "Centroid Longitude", "Weight", "Members"};
            writer.writeNext(record);

            for (SpatialCluster cluster : clusters) {

                record[0] = Double.toString(cluster.getCoordinate().lat);
                record[1] = Double.toString(cluster.getCoordinate().lng);
                record[2] = Double.toString(cluster.getWeight());
                record[3] = "";

                for(SpatialPoint member : cluster.getMembers())
                    record[3] += member.getId() + ",";

                record[3] = record[3].substring(0, record[3].length()-1);

                writer.writeNext(record, false);
            }

        } catch (IOException exception) {

            logger.error("IOException: ", exception);

        } finally {

            if (writer != null) {

                try {
                    writer.close();
                } catch (IOException exception) {

                    logger.error("FLP> Facility Data File stream could not be closed", exception);
                }
            }
        }

    }
}
