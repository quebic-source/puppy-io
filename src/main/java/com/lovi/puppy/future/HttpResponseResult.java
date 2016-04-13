package com.lovi.puppy.future;

import com.lovi.puppy.future.factory.HttpResponseResultFactory;
import com.lovi.puppy.future.handler.HttpResponseHandler;

public interface HttpResponseResult {
	
	static HttpResponseResult create(){
		return factory.create();
	}
	
	default void complete(Object value){
		getResultHandler().handle(value, 200);
	}
	
	default void complete(Object value, int statusCode){
		getResultHandler().handle(value, statusCode);
	}
	
	
	
	HttpResponseHandler getResultHandler();
	
	void setResultHandler(HttpResponseHandler responseHandler);

	static HttpResponseResultFactory factory = new HttpResponseResultFactory();
}
