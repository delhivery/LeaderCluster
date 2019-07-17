package com.delhivery.clustering.elements;

import java.io.Serializable;

/**
 * @author Shiv Krishna Jaiswal
 */
public interface Clusterable extends Serializable {

	String id();

	/**
	 * @return geocode of this point.
	 */
	Geocode geocode();

	/**
	 * 
	 * @return importance of this clusterable point.
	 */
	double weight();

	/**
	 * It returns the stored data and if no data is stored 
	 * then returning null.
	 * 
	 * By default, invocation of this method throws
	 * unsupported operation exception.
	 *  
	 * This methods needs to be overridden by class subclassing it.
	 * 
	 * @return data stored, null if no data is stored
	 */
	default <T> T getUserData() {
		throw new UnsupportedOperationException("Class="
		+ getClass().getCanonicalName()
		+ " does not override this method");
	}

}
