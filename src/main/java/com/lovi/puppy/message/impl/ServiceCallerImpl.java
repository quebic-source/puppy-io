package com.lovi.puppy.message.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.exceptions.ServiceCallerException;
import com.lovi.puppy.exceptions.message.ErrorMessage;
import com.lovi.puppy.message.MessageBody;
import com.lovi.puppy.message.Result;
import com.lovi.puppy.message.ServiceCaller;

@Component
public class ServiceCallerImpl implements ServiceCaller{

	@Autowired
	private AppConfig appConfig;
	
	/**
	 * U - Return Type
	 * @param serviceMethod
	 * @param result
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	@Override
	public <U>  void call(String serviceMethod,Result<U> result,Object... inputParameters) throws ServiceCallerException{
		try {
			String address = appConfig.getAppName() + "." + serviceMethod;
			MessageBody messageBody = new MessageBody();
			messageBody.setStatus(1);
			messageBody.setValues(inputParameters);
			appConfig.getVertx().eventBus().<MessageBody>send(address, messageBody, result.getHandler());
			
		} catch (Exception e) {
			throw new ServiceCallerException(ErrorMessage.SERVICE_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}
	
	/**
	 * U - Return Type
	 * @param appName
	 * @param serviceMethod
	 * @param result
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	@Override
	public <U>  void call(String appName,String serviceMethod,Result<U> result,Object... inputParameters) throws ServiceCallerException{
		try {
			String address = appName + "." + serviceMethod;
			MessageBody messageBody = new MessageBody();
			messageBody.setStatus(1);
			messageBody.setValues(inputParameters);
			appConfig.getVertx().eventBus().<MessageBody>send(address, messageBody, result.getHandler());
			
		} catch (Exception e) {
			throw new ServiceCallerException(ErrorMessage.SERVICE_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}
	
	/**
	 * @param serviceMethod
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	@Override
	public void call(String serviceMethod,Object... inputParameters) throws ServiceCallerException{
		try {
			String address = appConfig.getAppName() + "." + serviceMethod;
			MessageBody messageBody = new MessageBody();
			messageBody.setStatus(1);
			messageBody.setValues(inputParameters);
			appConfig.getVertx().eventBus().send(address, messageBody);
			
		} catch (Exception e) {
			throw new ServiceCallerException(ErrorMessage.SERVICE_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}
	
	/**
	 * @param appName
	 * @param serviceMethod
	 * @param inputParameters Object...
	 * @throws ServiceCallerException
	 */
	@Override
	public void call(String appName,String serviceMethod,Object... inputParameters) throws ServiceCallerException{
		try {
			String address = appName + "." + serviceMethod;
			MessageBody messageBody = new MessageBody();
			messageBody.setStatus(1);
			messageBody.setValues(inputParameters);
			appConfig.getVertx().eventBus().send(address, messageBody);
			
		} catch (Exception e) {
			throw new ServiceCallerException(ErrorMessage.SERVICE_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}
}
