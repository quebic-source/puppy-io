package com.lovi.puppy.exceptions;

public class ServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5223674757690207273L;

	public ServiceException(String errorMessage) {
		super(errorMessage);
	}

}
