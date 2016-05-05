package com.lovi.puppy;

import java.io.PrintStream;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.context.HazelCastContext;
import com.lovi.puppy.message.MessageBody;
import com.lovi.puppy.message.MessageBodyCodec;
import com.lovi.puppy.verticle.ServerVerticle;
import com.lovi.puppy.verticle.ServiceVerticle;
import com.lovi.puppy.verticle.UIServiceVerticle;

/**
 * 
 * @author Tharanga Thennakoon
 *
 */
@SpringBootApplication
class PuppyAppLauncher{

	private static final Logger logger = LoggerFactory.getLogger(ServerVerticle.class);
	
	private ApplicationContext context;
	
	private Vertx vertx;
	
	private Class<?> appClass;
	
	private String appName;
	
	public PuppyAppLauncher() {
	}
	
	public PuppyAppLauncher(Class<?> appClass,String appName,String... args) {
		this.appClass = appClass;
		this.appName = appName;
		startSpringBoot(args);
	}
	
	/**
	 * run web-app and service-app
	 * @param webAppPort
	 */
	public void run(int webAppPort){
		
		if(!registerApp(webAppPort))
			return;
		
		if(webAppPort == 0){
			logger.error("Can't bind with this port 0.please try again with another port");
			return;
		}
		initVerticle(handler->{
			startWebApp(webAppPort);
			startServiceApp();
			startUIService();
		});
	}
	
	/**
	 * run web-app and service-app.default webAppPort 8080
	 */
	public void run(){
		int webAppPort = 8080;
		
		if(!registerApp(webAppPort))
			return;
		
		initVerticle(handler->{
			startWebApp(webAppPort);
			startServiceApp();
			startUIService();
		});
	}
	
	/**
	 * run web-app
	 * @param webAppPort
	 */
	public void runWebApp(int webAppPort){
		
		if(!registerApp(webAppPort))
			return;
		
		initVerticle(handler->{
			startWebApp(webAppPort);
		});
	}
	
	/**
	 * run web-app.default webAppPort 8080
	 */
	public void runWebApp(){
		int webAppPort = 8080;
		
		if(!registerApp(webAppPort))
			return;
		
		initVerticle(handler->{
			startWebApp(webAppPort);
		});
	}
	
	/**
	 * run service-app
	 */
	public void runServiceApp(){
		
		if(!registerApp(0))
			return;
		
		initVerticle(handler->{
			startServiceApp();
		});
	}
	
	/**
	 * true - registration is success
	 * false - registration is fail
	 * @return
	 */
	private boolean registerApp(int webPort){
		
		HazelCastContext hazelCastContext = context.getBean(HazelCastContext.class);
		return hazelCastContext.addAppName(appName) && hazelCastContext.addWebPort(webPort);
		
	}
	
	private void startWebApp(int port){
		SessionStore store = LocalSessionStore.create(vertx, appName + ".sessionmap");
		SessionHandler sessionHandler = SessionHandler.create(store);
		
		ServerVerticle serverVerticle = context.getBean(ServerVerticle.class);
    	serverVerticle.setPort(port);
    	serverVerticle.setSessionHandler(sessionHandler);
    	
        vertx.deployVerticle(serverVerticle);
	}
	
	private void startServiceApp(){
		ServiceVerticle serviceVerticle = context.getBean(ServiceVerticle.class);
        vertx.deployVerticle(serviceVerticle);
	}
	
	private void startUIService(){
		UIServiceVerticle uiServiceVerticle = context.getBean(UIServiceVerticle.class);
        vertx.deployVerticle(uiServiceVerticle);
	}
	
	private void startSpringBoot(String... args){
		
		Object[] objects = new Object[2];
		objects[0] = PuppyAppLauncher.class;
		objects[1] = appClass;
		
		SpringApplication app = new SpringApplication(objects);
    	app.setBanner(new Banner() {
			
			@Override
			public void printBanner(Environment arg0, Class<?> arg1, PrintStream printStream) {
				
				StringBuilder stringBuilderDec = new StringBuilder("###########");
				for(int i = 0; i < appName.length(); i++){
					stringBuilderDec.append("#");
				}
				
				printStream.println(stringBuilderDec.toString());
				printStream.println("puppy-io [" + appName +"]");
				printStream.println(stringBuilderDec.toString());
			}
		});
        //app.setBannerMode(Banner.Mode.OFF);
    	context = app.run(args);
	}
	
	private void initVerticle(Handler<Object> handler){
		//start vert.x
    	ClusterManager mgr = new HazelcastClusterManager();
		
    	VertxOptions options = new VertxOptions().setClusterManager(mgr);
		options.setWorkerPoolSize(1000);
		
		Vertx.clusteredVertx(options, res -> {
		  if (res.succeeded()) {
			  
			  vertx = res.result();
			  vertx.eventBus().registerDefaultCodec(MessageBody.class, new MessageBodyCodec());
			  
			  AppConfig appConfig = context.getBean(AppConfig.class);
			  appConfig.setVertx(vertx);
			  appConfig.setAppClass(appClass);
			  appConfig.setAppName(appName);
			
			  handler.handle(1);
		  } else {
			  logger.error("fail clusteredVertx");
		  }
		});
	}
	
	public static void main(String[] args){
	}
}
