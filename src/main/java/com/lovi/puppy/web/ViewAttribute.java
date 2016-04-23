package com.lovi.puppy.web;

/**
 * ViewAttribute is used to share data between controllers and views
 * @author Tharanga Thennakoon
 *
 */
public interface ViewAttribute {

	/**
	 * Put context data with key
	 * @param key
	 * @param object
	 */
	void put(String key,Object object);
	
	/**
	 * Get context data for the key
	 * @param key
	 * @param <T> Data type of the context data
	 * @return
	 */
	<T> T get(String key);
}
