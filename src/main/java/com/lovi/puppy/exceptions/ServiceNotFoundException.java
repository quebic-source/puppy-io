package com.lovi.puppy.exceptions;

public class ServiceNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1877194882311911342L;

	public ServiceNotFoundException(String errorMessage) {
		super(errorMessage);
	}

}
