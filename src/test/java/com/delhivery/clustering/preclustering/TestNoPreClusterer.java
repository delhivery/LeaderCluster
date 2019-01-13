package com.delhivery.clustering.preclustering;

import static com.delhivery.clustering.preclustering.PreClusteringFactory.NO_PRECLUSTERING;
import static com.delhivery.clustering.utils.Utils.iDCreator;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.ClusterableImpl;
import com.delhivery.clustering.elements.Geocode;

public class TestNoPreClusterer {
    private Collection<Clusterable> clusterables;

    @Before
    public void setUp() {
        Geocode[] geocodes = new Geocode[] {
            new Geocode(0, 0),
            new Geocode(1, 0),
            new Geocode(0, 1),
            new Geocode(0, 0)
        };

        this.clusterables = asList(
                new ClusterableImpl("1", geocodes[0], 1),
                new ClusterableImpl("2", geocodes[1], 1),
                new ClusterableImpl("3", geocodes[2], 1),
                new ClusterableImpl("4", geocodes[3], 1));
    }

    @Test
    public void testPreclusters() {

        PreClusteringFactory preClusteringFactory = NO_PRECLUSTERING;

        PreClustering preClustering = preClusteringFactory.createPreClusterer(clusterables);
        
        assertTrue(preClustering.preclusters(iDCreator()).isEmpty());
    }

    @Test
    public void testUnclusteredPoints() {
        PreClusteringFactory preClusteringFactory = NO_PRECLUSTERING;

        PreClustering preClustering = preClusteringFactory.createPreClusterer(clusterables);

        assertEquals(new HashSet<>(preClustering.unclusteredPoints()), new HashSet<>(clusterables));
    }

}
