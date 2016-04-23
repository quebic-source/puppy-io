package com.lovi.puppy.message;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import com.lovi.puppy.message.factory.FailResultFactory;
import com.lovi.puppy.message.handler.ResultHandler;

/**
 * The class is used to handle the failure occur within the service method execution which is called by ServiceCaller.
 * @see ServiceCaller
 * @see Result
 * @author Tharanga Thennakoon
 *
 */
public interface FailResult {

	/**
	 * Create new instance from FailResult class
	 * @return
	 */
	static FailResult create() {
		return factory.create();
	}
	
	/**
	 * Set a handler for the FailResult.
	 * @param handler The Handler that will be called after the failure occur.
	 */
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
