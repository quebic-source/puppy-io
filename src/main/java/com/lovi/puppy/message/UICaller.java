package com.lovi.puppy.message;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovi.puppy.exceptions.ServiceCallerException;
import com.lovi.puppy.exceptions.message.ErrorMessage;

import io.vertx.core.Vertx;

@Component
public class UICaller {
	
	private Vertx vertx;
	
	public void setVertx(Vertx vertx) {
		this.vertx = vertx;
	}

	/**
	 * listener address format -> ui.{appName}.{methodName}
	 * @param address
	 * @param message
	 * @throws ServiceCallerException
	 */
	public void call(String listenerAddress,Object message) throws ServiceCallerException{
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			vertx.eventBus().publish(listenerAddress, objectMapper.writeValueAsString(message));
			
		} catch (Exception e) {
			throw new ServiceCallerException(ErrorMessage.UI_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}

}
