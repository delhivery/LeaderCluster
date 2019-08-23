package com.delhivery.clustering.reduction;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.slf4j.Logger;

import com.delhivery.clustering.elements.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */
public abstract class Hasher<T>extends Reducer {
	private static final Logger                   LOGGER = getLogger(Hasher.class);
	/**
	 * Hashes Clusterable on given function and produces
	 * a representable clusterable point.
	 * After clustering, clusters can be expanded with individual clusterable points
	 * using "decompressClusterable" methods.
	 */

	private final Map<T, Collection<Clusterable>> mapper;
	private final Function<Clusterable, T>        hasher;

	protected Hasher(Collection<? extends Clusterable> clusterables, Function<Clusterable, T> hasher) {
		this.mapper = clusterables.stream().collect(groupingBy(hasher, toCollection(LinkedList::new)));
		this.hasher = hasher;

		LOGGER.info("Number of distinct hashed Point in Hasher={}", this.mapper.size());
	}

	/**
	 * @return clusterables which is distinct on hash given by "hasher" function.
	 */
	@Override
	public Collection<Clusterable> compressedClusterables() {
		return this.mapper.entrySet()
		                  .stream()
		                  .map(this::createCompressedClusterable)
		                  .collect(toList());
	}

	/**
	 * @param clusterable
	 * @return clusterables point which share same hash as that of "clusterable"
	 */

	@Override
	public Collection<Clusterable> decompressClusterable(Clusterable clusterable) {
		Collection<Clusterable> points = mapper.get(hasher.apply(clusterable));

		if (isNull(points))
			throw new IllegalArgumentException("Invalid Clusterable " + clusterable);

		return unmodifiableCollection(points);

	}

	public abstract Clusterable createCompressedClusterable(Entry<T, Collection<Clusterable>> e);

}
