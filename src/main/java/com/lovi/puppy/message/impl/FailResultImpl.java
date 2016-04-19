package com.lovi.puppy.message.impl;

import com.lovi.puppy.message.FailResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

public class FailResultImpl implements FailResult{

	private Future<Message<String>> future;
	
	public FailResultImpl() {
		this.future = Future.future();
	}

	@Override
	public Future<Message<String>> getVerxFuture() {
		return future;
	}

	
}
