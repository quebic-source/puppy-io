package com.lovi.puppy.message.impl;

import com.lovi.puppy.message.MessageBody;
import com.lovi.puppy.message.Result;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public class ResultImpl<T> implements Result<T>{
	
	private Future<Message<MessageBody>> future;
	
	public ResultImpl() {
		future = Future.future();
	}
	
	@Override
	public Handler<AsyncResult<Message<MessageBody>>> getHandler(){
		return future.completer();
	}

	@Override
	public Future<Message<MessageBody>> getVerxFuture() {
		return this.future;
	}

}
