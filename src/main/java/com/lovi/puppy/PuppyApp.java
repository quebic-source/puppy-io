package com.lovi.puppy;

/**
 * The class is used to run the application (Web Application, Service Application)
 * @author Tharanga Thennakoon
 *
 */
public class PuppyApp {
	private static PuppyApp app;
	private static PuppyAppLauncher launcher;
	
	private PuppyApp(Class<?> appClass,String appName,String... args){
		launcher = new PuppyAppLauncher(appClass,appName,args);
	}
	
	/**
	 * Create a singleton instance from PuppyApp class.
	 * @param appClass
	 * @param appName
	 * @param args
	 * @return
	 */
	public synchronized static PuppyApp create(Class<?> appClass,String appName,String... args){
		if(app == null)
			app = new PuppyApp(appClass,appName,args);
		return app;
	}
	
	/**
	 * Run web application and service application.
	 * @param webAppPort web server port
	 */
	public void run(int webAppPort){
		launcher.run(webAppPort);
	}
	
	/**
	 * Run web application and service application. default webAppPort is 8080.
	 */
	public void run(){
		launcher.run();
	}
	
	/**
	 * Run only web application.
	 * @param webAppPort web server port
	 */
	public void runWebApp(int webAppPort){
		launcher.runWebApp(webAppPort);
	}
	
	/**
	 * Run only web application. default webAppPort is 8080
	 */
	public void runWebApp(){
		launcher.runWebApp();
	}
	
	/**
	 * Run only service application.
	 */
	public void runServiceApp(){
		launcher.runServiceApp();
	}

}
