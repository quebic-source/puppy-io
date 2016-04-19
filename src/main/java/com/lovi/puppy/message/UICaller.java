package com.lovi.puppy.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.exceptions.ServiceCallerException;
import com.lovi.puppy.exceptions.message.ErrorMessage;

@Component
public class UICaller {
	
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * listener address format -> ui.{appName}.{methodName}
	 * @param address
	 * @param message
	 * @throws ServiceCallerException
	 */
	public void call(String listenerAddress,Object message) throws ServiceCallerException{
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			appConfig.getVertx().eventBus().publish(listenerAddress, objectMapper.writeValueAsString(message));
			
		} catch (Exception e) {
			throw new ServiceCallerException(ErrorMessage.UI_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}

}
