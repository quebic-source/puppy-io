package com.lovi.puppy.async;

import com.lovi.puppy.async.handlers.AsyncHandler;
import com.lovi.puppy.async.handlers.Handler;
import com.lovi.puppy.async.impl.AsyncExecutorImpl;

/**
 * 
 * @author Tharanga Thennakoon
 *
 */
public interface AsyncExecutor<T>{
	
	static <T> AsyncExecutor<T> create(){
		return new AsyncExecutorImpl<T>();
	}
	void run(AsyncHandler<T> handler, Handler<T> successHandler, Handler<Throwable> failureHandler);
}
