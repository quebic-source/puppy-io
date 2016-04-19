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
import com.lovi.puppy.annotation.Service;
import com.lovi.puppy.annotation.ServiceFunction;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.context.HazelCastContext;
import com.lovi.puppy.exceptions.message.ErrorMessage;
import com.lovi.puppy.message.MessageBody;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

@Component
public class ServiceVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private HazelCastContext hazelCastContext;
	
	@Autowired
	private AppConfig appConfig;

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		// process @Service
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

		for (BeanDefinition bd : scanner.findCandidateComponents(appConfig.getAppClass().getPackage().getName())) {

			try {
				Class<?> classService = Class.forName(bd.getBeanClassName());

				// processing annotations for class
				// process @Service
				Service serviceAnnotation = (Service) classService.getAnnotation(Service.class);
				String serviceName = serviceAnnotation.value();

				if (serviceName.equals(""))
					serviceName = classService.getSimpleName();

				Object serviceObject = applicationContext.getBean(classService);

				// process methods
				for (Method method : classService.getDeclaredMethods()) {
					Annotation serviceFunctionMethodAnnotation = method.getAnnotation(ServiceFunction.class);

					// process @ServiceFunction
					if (serviceFunctionMethodAnnotation != null) {

						ServiceFunction serviceFunctionAnnotation = (ServiceFunction) serviceFunctionMethodAnnotation;

						String serviceFunctionName = serviceFunctionAnnotation.value();

						if (serviceFunctionName.equals(""))
							serviceFunctionName = method.getName();

						String serviceAddress = appConfig.getAppName() + "." + serviceName + "." + serviceFunctionName;

						if(registerService(serviceAddress))
							deployService(method, serviceAddress, serviceObject);
						
					}
				}

			} catch (Exception e) {
				logger.error(ErrorMessage.SERVICE_ERROR.getMessage() + e.getMessage());
			}
		}
	}

	private void deployService(Method method, String serviceAddress, Object serviceObject) {
		logger.info("service -> {}", serviceAddress);

		vertx.eventBus().consumer(serviceAddress, new Handler<Message<MessageBody>>() {

			@Override
			public void handle(Message<MessageBody> message) {

				vertx.executeBlocking(new Handler<Future<MessageBody>>() {

					@Override
					public void handle(Future<MessageBody> future) {

						try {
							Object[] consumeInputparameters = message.body().getValues();
							Object[] inputParameters = new Object[method.getParameterCount()];

							for (int i = 0; i < inputParameters.length; i++) {
								try {
									inputParameters[i] = consumeInputparameters[i];
								} catch (Exception e) {
									throw new IllegalArgumentException();
								}
							}

							Object returnValue = method.invoke(serviceObject, inputParameters);
							future.complete(new MessageBody(1, returnValue));

						} catch (Exception e) {
							future.fail(e.getCause().getMessage());
						}

					}
				}, false, new Handler<AsyncResult<MessageBody>>() {

					@Override
					public void handle(AsyncResult<MessageBody> asyncResult) {
						if (asyncResult.succeeded())
							message.reply(asyncResult.result());
						else
							message.fail(-1, asyncResult.cause().getMessage());
					}
				});

			}

		});
	}
	
	
	private boolean registerService(String service){
		return hazelCastContext.addService(service);
	}

	

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
	}

}
