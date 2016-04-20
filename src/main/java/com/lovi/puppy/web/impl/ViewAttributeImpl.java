package com.lovi.puppy.web.impl;

import io.vertx.ext.web.RoutingContext;

import com.lovi.puppy.web.ViewAttribute;

public class ViewAttributeImpl implements ViewAttribute {

	private RoutingContext routingContext;
	
	public ViewAttributeImpl(RoutingContext routingContext) {
		this.routingContext = routingContext;
	}
	
	@Override
	public void put(String key, Object object) {
		routingContext.put(key, object);
	}

	@Override
	public <T> T get(String key) {
		return routingContext.<T>get(key);
	}

}
