package com.delhivery.clustering.reduction;

import java.util.Collection;

import com.delhivery.clustering.elements.Clusterable;

/**
 * @author Shiv Krishna Jaiswal
 */
public abstract class Reducer {

	/**
	 * @return clusterables which is distinct, for example, on geocode.
	 */
	public abstract Collection<Clusterable> compressedClusterables();

	/**
	 * @param compressedClusterable
	 * @return all clusterables point corresponding to this "compressedClusterable" clusterable.
	 */

	public abstract Collection<Clusterable> decompressClusterable(Clusterable compressedClusterable);

}
