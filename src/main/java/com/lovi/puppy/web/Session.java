package com.lovi.puppy.web;

public interface Session{

	<T> void put(String key,T object);
	
	<T> T get(String key,Class<T> cls);
	
}
