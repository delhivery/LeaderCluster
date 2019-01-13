package com.delhivery.clustering.reduction;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.delhivery.clustering.elements.Clusterable;
import com.delhivery.clustering.elements.ClusterableImpl;
import com.delhivery.clustering.elements.Geocode;

public class TestGeocodeReducer {
    private Geocode[]               geocodes;
    private Collection<Clusterable> clusterables;

    @Before
    public void setUp() {
        geocodes = new Geocode[] {
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
    public void testCompressedClusterables() {

        Reducer reducer = new GeocodeReducer(clusterables);

        Collection<Clusterable> compressedClusterables = reducer.compressedClusterables();

        Set<Geocode> uniqueGeocodes = newHashSet(geocodes);

        assertEquals(compressedClusterables.size(), uniqueGeocodes.size());

        assertEquals(compressedClusterables.stream().map(Clusterable::geocode).collect(toSet()), uniqueGeocodes);

    }

    @Test
    public void testDecompressClusterable() {
        Reducer reducer = new GeocodeReducer(clusterables);

        Collection<Clusterable> compressedClusterables = reducer.compressedClusterables();

        Collection<Clusterable> decompressedClusterables = compressedClusterables.stream()
                                                                                 .map(reducer::decompressClusterable)
                                                                                 .flatMap(Collection::stream)
                                                                                 .collect(toList());

        assertEquals(new HashSet<>(decompressedClusterables), new HashSet<>(this.clusterables));
    }

}
