package com.lovi.puppy.message;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import com.lovi.puppy.message.factory.FailResultFactory;
import com.lovi.puppy.message.handler.ResultHandler;

public interface FailResult {

	static FailResult create() {
		return factory.create();
	}
	
	default void setHandler(ResultHandler<Throwable> handler){
		
		getVerxFuture().setHandler(new Handler<AsyncResult<Message<String>>>() {
			
			@Override
			public void handle(AsyncResult<Message<String>> asyncResult) {
				handler.handle(asyncResult.cause());
			}
		});
	}

	
	Future<Message<String>> getVerxFuture();

	static FailResultFactory factory = new FailResultFactory();
}
