package com.lovi.puppy.message;

import com.lovi.puppy.exceptions.UICallerException;

/**
 * UICaller is used to makes call ui listener.
 * @author Tharanga Thennakoon
 *
 */
public interface UICaller {
	
	/**
	 * send message to ui listener
	 * @param listenerAddress Address of the listener
	 * @param message 
	 * @throws UICallerException
	 */
	void call(String listenerAddress,Object message) throws UICallerException;

}
