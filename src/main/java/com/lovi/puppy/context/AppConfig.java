package com.lovi.puppy.context;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	private String appName;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
}
