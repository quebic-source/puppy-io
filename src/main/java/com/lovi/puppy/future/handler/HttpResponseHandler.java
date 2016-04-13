package com.lovi.puppy.future.handler;

@FunctionalInterface
public interface HttpResponseHandler{
	void handle(Object value,int statusCode);
}
