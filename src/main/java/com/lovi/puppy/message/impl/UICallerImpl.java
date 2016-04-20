package com.lovi.puppy.message.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.exceptions.UICallerException;
import com.lovi.puppy.exceptions.message.ErrorMessage;
import com.lovi.puppy.message.UICaller;

@Component
public class UICallerImpl implements UICaller{

	@Autowired
	private AppConfig appConfig;
	
	@Override
	public void call(String listenerAddress,Object message) throws UICallerException{
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			appConfig.getVertx().eventBus().publish(listenerAddress, objectMapper.writeValueAsString(message));
			
		} catch (Exception e) {
			throw new UICallerException(ErrorMessage.UI_CALL_UNABLE_TO_PROCESS.getMessage());
		}
		
	}
}
