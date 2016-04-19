package com.lovi.puppy.message;

import com.lovi.puppy.exceptions.ServiceCallerException;

public interface ServiceCaller {
	
	/**
	 * U - Return Type
	 * @param serviceMethod
	 * @param result
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	<U>  void call(String serviceMethod,Result<U> result,Object... inputParameters) throws ServiceCallerException;
	
	/**
	 * U - Return Type
	 * @param appName
	 * @param serviceMethod
	 * @param result
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	<U>  void call(String appName,String serviceMethod,Result<U> result,Object... inputParameters) throws ServiceCallerException;
	
	/**
	 * @param serviceMethod
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	void call(String serviceMethod,Object... inputParameters) throws ServiceCallerException;
	
	/**
	 * @param appName
	 * @param serviceMethod
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	void call(String appName,String serviceMethod,Object... inputParameters) throws ServiceCallerException;

}
