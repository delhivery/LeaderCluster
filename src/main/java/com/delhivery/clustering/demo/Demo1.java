package com.delhivery.clustering.demo;

import static com.delhivery.clustering.distances.DistanceMeasureFactory.HAVERSINE;
import static java.lang.Double.parseDouble;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;

import org.slf4j.Logger;

import com.delhivery.clustering.Cluster;
import com.delhivery.clustering.Clusterable;
import com.delhivery.clustering.ClusterableImpl;
import com.delhivery.clustering.Clusterer;
import com.delhivery.clustering.Geocode;
import com.delhivery.clustering.LC.LCBuilder;
import com.delhivery.clustering.distances.DistanceMeasure;
import com.opencsv.CSVReader;

public class Demo1 {
    private static final Logger LOGGER = getLogger(Demo1.class);

    public static void main(String[] args) {
        Path path = get(args.length == 0 ? "sampleInput.csv" : args[0]);

        LOGGER.info("CSV File path to create clusterable points: {}", path);

        Collection<Clusterable> points = createClusterables(path);

        DistanceMeasure distanceMeasure = HAVERSINE;

        Clusterer algorithm = LCBuilder.newInstance()
                                       .distanceConstraint(500, distanceMeasure)
                                       .enableLcOnCompressedClusterables()
                                       .refineAssignToClosestCluster(3, distanceMeasure)
                                       .build();

        Collection<Cluster> clusters = algorithm.cluster(points);

        System.out.println(clusters.size());
    }

    private static Collection<Clusterable> createClusterables(Path filePath) {

        try (CSVReader csvReader = new CSVReader(newBufferedReader(filePath))) {
            csvReader.readNext();

            return csvReader.readAll().stream().map(ClusterableRow::new).collect(toList());

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class ClusterableRow extends ClusterableImpl {
        private final String id;

        public ClusterableRow(String[] row) {

            super(new Geocode(parseDouble(row[1]), parseDouble(row[2])), parseDouble(row[3]));
            this.id = row[0];
        }

        @Override
        public String toString() {

            return id;
        }

    }

}
