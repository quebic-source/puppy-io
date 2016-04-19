package com.lovi.puppy.context;

import io.vertx.core.Vertx;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	private Vertx vertx;
	private Class<?> appClass;
	private String appName;
	
	public Vertx getVertx() {
		return vertx;
	}
	public void setVertx(Vertx vertx) {
		this.vertx = vertx;
	}
	public Class<?> getAppClass() {
		return appClass;
	}
	public void setAppClass(Class<?> appClass) {
		this.appClass = appClass;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
}
