package com.lovi.puppy;

public class PuppyApp {
	private static PuppyApp app;
	private static PuppyAppLauncher launcher;
	
	private PuppyApp(Class<?> appClass,String appName,String... args){
		launcher = new PuppyAppLauncher(appClass,appName,args);
	}
	
	public synchronized static PuppyApp create(Class<?> appClass,String appName,String... args){
		if(app == null)
			app = new PuppyApp(appClass,appName,args);
		return app;
	}
	
	/**
	 * run web-app and service-app
	 * @param webAppPort
	 */
	public void run(int webAppPort){
		launcher.run(webAppPort);
	}
	
	/**
	 * run web-app and service-app.default webAppPort 8080
	 */
	public void run(){
		launcher.run();
	}
	
	/**
	 * run web-app
	 * @param webAppPort
	 */
	public void runWebApp(int webAppPort){
		launcher.runWebApp(webAppPort);
	}
	
	/**
	 * run web-app.default webAppPort 8080
	 */
	public void runWebApp(){
		launcher.runWebApp();
	}
	
	/**
	 * run service-app
	 */
	public void runServiceApp(){
		launcher.runServiceApp();
	}

}
