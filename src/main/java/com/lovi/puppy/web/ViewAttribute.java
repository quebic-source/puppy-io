package com.lovi.puppy.web;

public interface ViewAttribute {

	void put(String key,Object object);
	
	<T> T get(String key);
}
