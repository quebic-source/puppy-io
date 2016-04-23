package com.lovi.puppy.message;

import com.lovi.puppy.exceptions.ServiceCallerException;

/**
 * ServiceCaller is used to call service method.
 * @author Tharanga Thennakoon
 * @see Result
 */
public interface ServiceCaller {
	
	/**
	 * Call service method.
	 * @param serviceMethod Format {servlce_name.service_method_name}
	 * @param result Handler for perform result of service method
	 * @param inputParameters Input parameters list of service method
	 * @param <U> Data type of the return value of service method
	 * @throws ServiceCallerException
	 */
	<U>  void call(String serviceMethod,Result<U> result,Object... inputParameters) throws ServiceCallerException;
	
	/**
	 * Call service method.This method is used for calling service method from external application.
	 * @param appName Name of the application
	 * @param serviceMethod Format {servlce_name.service_method_name}
	 * @param result Handler for perform result of service method
	 * @param inputParameters Input parameters list of service method
	 * @param <U> Data type of the return value of service method
	 * @throws ServiceCallerException
	 */
	<U>  void call(String appName,String serviceMethod,Result<U> result,Object... inputParameters) throws ServiceCallerException;
	
	/**
	 * Call service method.This method is used for calling service methods which don't have return value.
	 * @param serviceMethod Format {servlce_name.service_method_name}
	 * @param inputParameters Input parameters list of service method
	 * @throws ServiceCallerException
	 */
	void call(String serviceMethod,Object... inputParameters) throws ServiceCallerException;
	
	/**
	 * Call service method.This method is used for calling service methods which don't have return value and for calling service method from external application.
	 * @param appName Name of the application
	 * @param serviceMethod Format {servlce_name.service_method_name}
	 * @param inputParameters Input parameters list of service method
	 * @throws ServiceCallerException
	 */
	void call(String appName,String serviceMethod,Object... inputParameters) throws ServiceCallerException;

}
