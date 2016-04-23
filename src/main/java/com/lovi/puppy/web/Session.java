package com.lovi.puppy.web;

/**
 * Session is used to represents session
 * @author Tharanga
 *
 */
public interface Session{

	/**
	 * Put data into session with key
	 * @param key
	 * @param object
	 * @param <T> data type of the input value
	 */
	<T> void put(String key,T object);
	
	/**
	 * Get session data for the key
	 * @param key
	 * @param cls
	 * @param <T> data type of the session value
	 * @return
	 */
	<T> T get(String key,Class<T> cls);
	
}
