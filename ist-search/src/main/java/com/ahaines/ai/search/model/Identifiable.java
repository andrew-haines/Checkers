package com.ahaines.ai.search.model;

/**
 * All node states are defined as identifiable for fast lookup in the caching layer if it is used.
 * @author andrewhaines
 *
 */
public interface Identifiable {

	/**
	 * Returns a globally unique identifier for this object
	 * @return
	 */
	public int getId();
}
