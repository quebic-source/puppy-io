package com.lovi.puppy.context;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Component
public class HazelCastContext {

	private static final Logger logger = LoggerFactory.getLogger(HazelCastContext.class);
	
	private HazelcastInstance instance;
	
	public enum KEYS{
		APP_NAMES,
		WEB_PORTS,
		SERVICES,
		UI_SERVICES
	}
	
	public HazelCastContext() {
		Config cfg = new Config();
		instance = Hazelcast.newHazelcastInstance(cfg);
	}
		
	public HazelcastInstance getInstance() {
		return instance;
	}

	public boolean addAppName(String appName){
		
		Set<String> appNames = instance.getSet(KEYS.APP_NAMES.toString());
		
		if(appNames.contains(appName)){
			logger.error("app [ {} ] is already deployed",appName);
			//System.err.println("app [ " + appName + " ] is already deployed");
			return false;
		}
		
		appNames.add(appName);
		return true;
		
	}
	
	public boolean addWebPort(Integer webPort){
		
		Set<Integer> webPorts = instance.getSet(KEYS.WEB_PORTS.toString());
		if(webPorts.contains(webPort)){
			logger.error("webPort [ {} ] is already bind",webPort);
			//System.err.println("webPort [ "+ webPort + " ] is already bind");
			return false;
		}
		
		webPorts.add(webPort);
		return true;
	}
	
	public boolean addService(String service){
		
		Set<String> services = instance.getSet(KEYS.SERVICES.toString());
		if(services.contains(service)){
			logger.error("service [ {} ] is already deploy",service);
			//System.err.println("service [ "+ service + " ] is already deploy");
			return false;
		}
		
		services.add(service);
		return true;
	}
	
	public boolean addUIService(String uiService){
		
		Set<String> uiServices = instance.getSet(KEYS.UI_SERVICES.toString());
		if(uiServices.contains(uiService)){
			logger.error("ui service [ {} ] is already deploy",uiService);
			//System.err.println("ui service [ "+ uiService + " ] is already deploy");
			return false;
		}
		
		uiServices.add(uiService);
		return true;
	}
	
	public Set<String> getAppNames(){
		return instance.<String>getSet(KEYS.APP_NAMES.toString());
	}
	
	public Set<Integer> getWebPort(){
		return instance.<Integer>getSet(KEYS.WEB_PORTS.toString());
	}
	
	public Set<String> getServices(){
		return instance.<String>getSet(KEYS.SERVICES.toString());
	}
	
	public Set<String> getUIServices(){
		return instance.<String>getSet(KEYS.UI_SERVICES.toString());
	}
}
