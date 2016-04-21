package com.lovi.puppy.message;

import com.lovi.puppy.exceptions.UICallerException;

public interface UICaller {
	
	/**
	 * listener address format -> {appName}.{serviceClassName}.{methodName}
	 * @param address
	 * @param message
	 * @throws ServiceCallerException
	 */
	void call(String listenerAddress,Object message) throws UICallerException;

}
