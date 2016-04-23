package com.lovi.puppy.message;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import com.lovi.puppy.message.factory.FailResultFactory;
import com.lovi.puppy.message.handler.ResultHandler;

/**
 * The class is used to catch the failure within the service method call which is called by ServiceCaller.
 * @see ServiceCaller
 * @see Result
 * @author Tharanga Thennakoon
 *
 */
public interface FailResult {

	static FailResult create() {
		return factory.create();
	}
	
	/**
	 * Set a handler for the FailResult. When failure occur within ServiceCaller then the handler will execute. 
	 * @param Handler the Handler that will be called with the failure.
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
