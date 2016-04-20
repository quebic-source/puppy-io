package com.lovi.puppy.web.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovi.puppy.web.Session;

public class SessionImpl implements Session{
		
	private io.vertx.ext.web.Session session;
	
	public SessionImpl(io.vertx.ext.web.Session session) {
		this.session = session;
	}
	
	@Override
	public <T> void put(String key,T object){
		ObjectMapper objectMapper = new ObjectMapper();
		Object o = null;
		try {
			o = objectMapper.writeValueAsString(object);
		} catch (Exception e) {
		}
		session.put(key, o);
	}
	
	@Override
	public <T> T get(String key,Class<T> cls){
		ObjectMapper objectMapper = new ObjectMapper();
		T t = null;
		try{
			t = objectMapper.readValue(session.get(key).toString(), cls);
		}catch(Exception e){
		}
		
		return t;
	}

}
