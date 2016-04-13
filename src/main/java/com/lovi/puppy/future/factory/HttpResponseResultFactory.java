package com.lovi.puppy.future.factory;

import com.lovi.puppy.future.HttpResponseResult;
import com.lovi.puppy.future.impl.HttpResponseResultImpl;

public class HttpResponseResultFactory {
	
	public HttpResponseResult create(){
		return new HttpResponseResultImpl();
	}
}
