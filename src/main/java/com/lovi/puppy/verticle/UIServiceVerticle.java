package com.lovi.puppy.verticle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import com.lovi.puppy.annotation.UIService;
import com.lovi.puppy.annotation.UIServiceFunction;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.context.HazelCastContext;
import com.lovi.puppy.exceptions.message.ErrorMessage;
import com.lovi.puppy.message.UICaller;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@Component
public class UIServiceVerticle extends AbstractVerticle{

	private static final Logger logger = LoggerFactory.getLogger(UIServiceVerticle.class);

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private HazelCastContext hazelCastContext;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private UICaller uiCaller;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		// process @UIService
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(UIService.class));

		for (BeanDefinition bd : scanner.findCandidateComponents(appConfig.getAppClass().getPackage().getName())) {

			try {
				Class<?> classService = Class.forName(bd.getBeanClassName());
				
				//@UIService value
				UIService uiServiceAnnotation = classService.getAnnotation(UIService.class);
				String uiServiceAnnotationValue = uiServiceAnnotation.value();
				String uiServiceClass =	((uiServiceAnnotationValue.equals(""))?classService.getSimpleName():uiServiceAnnotationValue);
						
				// processing annotations for class
				Object serviceObject = applicationContext.getBean(classService);

				// process methods
				for (Method method : classService.getDeclaredMethods()) {
					Annotation serviceFunctionMethodAnnotation = method.getAnnotation(UIServiceFunction.class);

					// process @UIServiceFunction
					if (serviceFunctionMethodAnnotation != null) {

						UIServiceFunction serviceFunctionAnnotation = (UIServiceFunction) serviceFunctionMethodAnnotation;

						//listener address format -> ui.{appName}.{methodName}
						String uiServiceFunctionAnnotationValue = serviceFunctionAnnotation.value();
						String address = "ui." + appConfig.getAppName() + "." + uiServiceClass + "." + ((uiServiceFunctionAnnotationValue.equals(""))?method.getName():uiServiceFunctionAnnotationValue);
						int delay = serviceFunctionAnnotation.delay();

						if(registerService(address))
							deployServices(method,address,delay,serviceObject);
						
					}
				}

			} catch (Exception e) {
				logger.error(ErrorMessage.SERVICE_ERROR.getMessage() + e.getMessage());
			}
		}
	}

	private void deployServices(Method method, String listenerAddress,int delay,Object serviceObject) {
		logger.info("ui service -> {}", listenerAddress);
	
		vertx.deployVerticle(new AbstractVerticle() {
			
			@Override
			public void start(Future<Void> startFuture) throws Exception {
				vertx.setPeriodic(delay * 1000, new Handler<Long>() {
					
					@Override
					public void handle(Long event) {
						try {
							Object message = method.invoke(serviceObject);
							uiCaller.call(listenerAddress, message);
							
						} catch (IllegalArgumentException e) {
							logger.error(ErrorMessage.UI_SERVICE_FUNCTION_INPUT_PARAMETERS_FOUND.getMessage());
						} catch (Exception e) {
							logger.error(ErrorMessage.UI_SERVICE_FUNCTION_UNABLE_TO_PROCESS.getMessage() + " - " + e.getMessage());
						}
					}
				});
			}
			
			@Override
			public void stop(Future<Void> stopFuture) throws Exception {
				
			}
		}
		,new DeploymentOptions().setWorker(false));
		
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
	}
	
	private boolean registerService(String uiService){
		return hazelCastContext.addUIService(uiService);
	}


}
