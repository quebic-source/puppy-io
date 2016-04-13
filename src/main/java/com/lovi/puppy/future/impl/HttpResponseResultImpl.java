package com.lovi.puppy.future.impl;

import com.lovi.puppy.future.HttpResponseResult;
import com.lovi.puppy.future.handler.HttpResponseHandler;

public class HttpResponseResultImpl implements HttpResponseResult{

	private HttpResponseHandler httpResponseHandler;
	
	@Override
	public HttpResponseHandler getResultHandler() {
		return httpResponseHandler;
	}

	@Override
	public void setResultHandler(HttpResponseHandler httpResponseHandler) {
		this.httpResponseHandler = httpResponseHandler;
	}

	

}
