package com.lovi.puppy.verticle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovi.puppy.annotation.Controller;
import com.lovi.puppy.annotation.ModelAttribute;
import com.lovi.puppy.annotation.PathVariable;
import com.lovi.puppy.annotation.RequestHeader;
import com.lovi.puppy.annotation.RequestMapping;
import com.lovi.puppy.annotation.RequestParm;
import com.lovi.puppy.annotation.ResponseBody;
import com.lovi.puppy.context.AppConfig;
import com.lovi.puppy.exceptions.InternalServerException;
import com.lovi.puppy.exceptions.RequestProcessingException;
import com.lovi.puppy.exceptions.message.ErrorMessage;
import com.lovi.puppy.future.HttpResponseResult;
import com.lovi.puppy.future.handler.HttpResponseHandler;
import com.lovi.puppy.web.Session;
import com.lovi.puppy.web.ViewAttribute;
import com.lovi.puppy.web.impl.SessionImpl;
import com.lovi.puppy.web.impl.ViewAttributeImpl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.templ.TemplateEngine;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

@Component
public class ServerVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ServerVerticle.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private AppConfig appConfig;
	
	private SessionHandler sessionHandler;

	private int port;

	private String resourcesFolder = "src/main/resources/web";

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		Router router = Router.router(vertx);
		router.route("/*").handler(BodyHandler.create());// this is for access json request body
		
		//static resources
		router.get("/" + appConfig.getAppName() +"/static/*").handler(StaticHandler.create(resourcesFolder + "/static").setCachingEnabled(false));
		
		//templates
		TemplateEngine engine = ThymeleafTemplateEngine.create();
		TemplateHandler htmlHandler = TemplateHandler.create(engine,resourcesFolder + "/templates","text/html;charset=utf-8");
		router.get("/" + appConfig.getAppName() + "/templates/*").handler(htmlHandler)
		.failureHandler(failureHandler->{
			prepareFailureResponse(failureHandler, 404, "unable to found template - " + failureHandler.request().path());
		});
		
		//ui - socket.js hander
		SockJSHandler sockJSHandler = socketJSRegister();
		router.route("/ui." + appConfig.getAppName() + "/*").handler(sockJSHandler);
		
		//cookie handler
		router.route().handler(CookieHandler.create());
		//session handler
		router.route().handler(sessionHandler);
		
		//process @Controller 
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

		scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

		
		for (BeanDefinition bd : scanner.findCandidateComponents(appConfig.getAppClass().getPackage().getName())) {
			try {
				Class<?> classController = Class.forName(bd.getBeanClassName());

				String controllerBaseUrl = "";

				// processing annotations for class
				// process @RequestMapping
				Annotation requestMappingClassAnnonation = classController.getAnnotation(RequestMapping.class);
				if (requestMappingClassAnnonation != null) {
					RequestMapping requestMappingAnnotation = (RequestMapping) requestMappingClassAnnonation;
					controllerBaseUrl = requestMappingAnnotation.value();
				}
				
				Object controllerObject = applicationContext.getBean(classController);

				// process methods
				for (Method method : classController.getDeclaredMethods()) {

					Annotation requestMappingMethodAnnotation = method.getAnnotation(RequestMapping.class);
					
					// process @RequestMapping
					if (requestMappingMethodAnnotation != null) {
						RequestMapping requestMappingAnnotation = (RequestMapping) requestMappingMethodAnnotation;

						String requestUrl = getAppNameBaseUrl() + controllerBaseUrl + requestMappingAnnotation.value();
						String consumes = requestMappingAnnotation.consumes();
						String produce = requestMappingAnnotation.produce();
						
						logger.info("web request mapping -> {} | {}", requestUrl, requestMappingAnnotation.method());

						switch (requestMappingAnnotation.method()) {
							case GET:
								if(consumes.equals(""))
									router.get(requestUrl).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								else
									router.get(requestUrl).consumes(consumes).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								break;
							case POST:
								if(consumes.equals(""))
									router.post(requestUrl).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								else
									router.post(requestUrl).consumes(consumes).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								break;
							case PUT:
								if(consumes.equals(""))
									router.put(requestUrl).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								else
									router.put(requestUrl).consumes(consumes).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								break;
							case DELETE:
								if(consumes.equals(""))
									router.delete(requestUrl).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								else
									router.delete(requestUrl).consumes(consumes).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								break;
							default:
								if(consumes.equals(""))
									router.patch(requestUrl).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								else
									router.patch(requestUrl).consumes(consumes).handler(routingContext -> {
										processRequest(routingContext, method, controllerObject, consumes, produce);
									});
								break;
						}
					}

				}

			} catch (Exception e) {
				throw new InternalServerException(e.getMessage());
			}
		}
		
		
		
		//404 handler
		router.route().handler(failureRoutingContext->{
			prepareFailureResponse(failureRoutingContext, 404, "unable to found requested path [ " + failureRoutingContext.request().path() + " ] method [ " + failureRoutingContext.request().method() + " ]");
		});
		
		// vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		vertx.createHttpServer().requestHandler(request -> router.accept(request)).listen(port,handle->logger.info("web app listen : {}" ,port));
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
	}

	private void processRequest(RoutingContext routingContext, Method method, Object controllerObject, String consumes,
			String produce) {
		try {

			HttpServerRequest httpServerRequest = routingContext.request();
			HttpResponseResult httpResponseFuture = HttpResponseResult.create();
			
			//-------------process response-------------------------------------
			//@ResponseBody
			Annotation responseBodyAnnonation = method.getAnnotation(ResponseBody.class);
			
			//@ResponseBody response
			if(responseBodyAnnonation != null){
				
				ResponseBody responseBody = (ResponseBody)responseBodyAnnonation;
				responseBody.hashCode();//no need
				
				httpResponseFuture.setResultHandler(new HttpResponseHandler() {
					
					@Override
					public void handle(Object value,int statusCode) {
						
						//check return type is String
						//if String just print
						if(value instanceof String){
							HttpServerResponse response = routingContext.response();
							response.setStatusCode(statusCode);
							response.putHeader("content-type", produce);
							response.end((String)value);
						}else{
							try{
								String responseValue = "";
								
								//skip XML or JSON checking
								/*switch (responseBody.value()) {
									case XML:
										//XML Parsing
										break;
									default:
										ObjectMapper objectMapper = new ObjectMapper();
										responseValue = objectMapper.writeValueAsString(methodInvokeResult);
										break;
								}*/
								
								ObjectMapper objectMapper = new ObjectMapper();
								responseValue = objectMapper.writeValueAsString(value);
								
								HttpServerResponse response = routingContext.response();
								response.setStatusCode(statusCode);
								response.putHeader("content-type", produce);
								response.end(responseValue);
								
							}catch(Exception e){
								try {
									throw new InternalServerException(ErrorMessage.UNABLE_TO_PARSE_METHOD_RETURN_TYPE_VALUE.getMessage() + method.getName());
								} catch (InternalServerException e1) {
									prepareFailureResponse(routingContext,400,e.getMessage());
								}
							}
						}
					}
				});
			}
			
			//Redirect to View
			else{
				
				httpResponseFuture.setResultHandler(new HttpResponseHandler() {
					
					@Override
					public void handle(Object value, int statusCode) {
						if(value instanceof String){
							
							String path = (String) value;
							
							//check for redirect to another route
							String pattern = "/\\.*";
					    	Pattern r = Pattern.compile(pattern);
					    	Matcher m = r.matcher(path);
							if (m.find()) {
								path = path.substring(m.end());
								routingContext.reroute(HttpMethod.GET, "/" + appConfig.getAppName() + path);
							}else{
								routingContext.reroute(HttpMethod.GET, "/" + appConfig.getAppName() + "/templates/" + (String) value + ".html");
							}
							
						}else{
							try {
								throw new Exception(ErrorMessage.UNABLE_TO_REDIRECT_RESPONSE_TO_TEMPLATE.getMessage());
							} catch (Exception e) {
								prepareFailureResponse(routingContext,400,e.getMessage());
							}
						}
					}
				});
			}
			//-------------end process response-------------------------------------
			
			
			int methodtParameterCount = method.getParameterCount();
			Object[] inputParms = new Object[methodtParameterCount];
			int paramterCount = 0;

			// process through input parameters
			for (Parameter paramater : method.getParameters()) {

				String paramaterType = paramater.getType().getName();

				//check primitive type parameter
				if (paramaterType.equals(String.class.getName()) 
						|| paramaterType.equals(Integer.class.getName())
						|| paramaterType.equals(Double.class.getName())
						|| paramaterType.equals(Float.class.getName()) 
						|| paramaterType.equals(Long.class.getName())
						|| paramaterType.equals(Short.class.getName())
						|| paramaterType.equals(Boolean.class.getName())) {

					//@RequestParm
					Annotation requestParmAnnonation = paramater.getAnnotation(RequestParm.class);
					
					//@PathVariable
					Annotation pathVariableAnnonation = paramater.getAnnotation(PathVariable.class);
					
					//@RequestHeader
					Annotation requestHeaderAnnonation = paramater.getAnnotation(RequestHeader.class);
					
					//process @RequestParm
					if (requestParmAnnonation != null) {
						
						RequestParm requestParm = (RequestParm) requestParmAnnonation;
						String requestParmValue = requestParm.value();

						if (requestParmValue.equals(""))
							throw new InternalServerException(ErrorMessage.REQUEST_PARAM_ANNOTATION_VALUE_CAN_NOT_BE_EMPTY.getMessage());
						else {
							String requestValue = httpServerRequest.getParam(requestParmValue);

							if (requestValue == null)
								throw new RequestProcessingException(ErrorMessage.REQUEST_PARAM_NOT_FOUND.getMessage() + requestParmValue);

							try{
								if (paramaterType.equals(Integer.class.getName())) {
									inputParms[paramterCount++] = Integer.parseInt(requestValue);
								} else if (paramaterType.equals(Double.class.getName())) {
									inputParms[paramterCount++] = Double.parseDouble(requestValue);
								} else if (paramaterType.equals(Float.class.getName())) {
									inputParms[paramterCount++] = Float.parseFloat(requestValue);
								} else if (paramaterType.equals(Long.class.getName())) {
									inputParms[paramterCount++] = Long.parseLong(requestValue);
								} else if (paramaterType.equals(Short.class.getName())) {
									inputParms[paramterCount++] = Short.parseShort(requestValue);
								} else if (paramaterType.equals(Boolean.class.getName())) {
									inputParms[paramterCount++] = Boolean.parseBoolean(requestValue);
								} else {
									inputParms[paramterCount++] = requestValue;
								}
							}catch(Exception e){
								throw new RequestProcessingException(ErrorMessage.UNABLE_TO_PARSE_REQUEST_PARM.getMessage() + requestValue);
							}
							
						}
					}
					
					//process @PathVariable
					else if(pathVariableAnnonation != null){
						PathVariable pathVariable = (PathVariable) pathVariableAnnonation;
						String pathVariableValue = pathVariable.value();

						if (pathVariableValue.equals(""))
							throw new InternalServerException(ErrorMessage.PATH_PARAM_ANNOTATION_VALUE_CAN_NOT_BE_EMPTY.getMessage());
						else {
							String requestValue = httpServerRequest.getParam(pathVariableValue);

							if (requestValue == null)
								throw new RequestProcessingException(ErrorMessage.PATH_PARAM_NOT_FOUND.getMessage() + pathVariableValue);

							try{
								if (paramaterType.equals(Integer.class.getName())) {
									inputParms[paramterCount++] = Integer.parseInt(requestValue);
								} else if (paramaterType.equals(Double.class.getName())) {
									inputParms[paramterCount++] = Double.parseDouble(requestValue);
								} else if (paramaterType.equals(Float.class.getName())) {
									inputParms[paramterCount++] = Float.parseFloat(requestValue);
								} else if (paramaterType.equals(Long.class.getName())) {
									inputParms[paramterCount++] = Long.parseLong(requestValue);
								} else if (paramaterType.equals(Short.class.getName())) {
									inputParms[paramterCount++] = Short.parseShort(requestValue);
								} else if (paramaterType.equals(Boolean.class.getName())) {
									inputParms[paramterCount++] = Boolean.parseBoolean(requestValue);
								} else {
									inputParms[paramterCount++] = requestValue;
								}
							}catch(Exception e){
								throw new RequestProcessingException(ErrorMessage.UNABLE_TO_PARSE_PATH_PARM.getMessage() + requestValue);
							}
							
						}
					}
					
					//process @RequestHeader
					else if(requestHeaderAnnonation != null){
						RequestHeader requestHeader = (RequestHeader) requestHeaderAnnonation;
						String requestHeaderValue = requestHeader.value();
						inputParms[paramterCount++] = httpServerRequest.getHeader(requestHeaderValue);
					}
					
					else{
						inputParms[paramterCount++] = null;
					}

				}

				else if (paramaterType.equals(RoutingContext.class.getName())) {
					// check RoutingContext type parameter
					inputParms[paramterCount++] = routingContext;
				}

				else if (paramaterType.equals(HttpServerRequest.class.getName())) {
					// check HttpServerRequest type parameter
					inputParms[paramterCount++] = httpServerRequest;
				}
				
				else if (paramaterType.equals(HttpResponseResult.class.getName())) {
					// check HttpServerRequest type parameter
					inputParms[paramterCount++] = httpResponseFuture;
				}
				
				else if (paramaterType.equals(Session.class.getName())) {
					// check Session type parameter
					io.vertx.ext.web.Session session = routingContext.session();
					Session customSession = new SessionImpl(session);
					inputParms[paramterCount++] = customSession;
				}
				
				else if (paramaterType.equals(ViewAttribute.class.getName())) {
					// check ViewAttribute type parameter
					ViewAttribute viewAttribute = new ViewAttributeImpl(routingContext);
					inputParms[paramterCount++] = viewAttribute;
				}
				
				else {
					// process @ModelAttribute
					Annotation modelAttributeAnnonation = paramater.getAnnotation(ModelAttribute.class);

					if (modelAttributeAnnonation != null) {

						ModelAttribute modelAttribute = (ModelAttribute) modelAttributeAnnonation;
						String modelAttributeValue = modelAttribute.value();

						Object parameterObject = paramater.getType().newInstance();

						// Turn on:
						// - auto null reference initialization
						// - auto collection growing
						SpelParserConfiguration config = new SpelParserConfiguration(true, true);
						ExpressionParser parser = new SpelExpressionParser(config);
						EvaluationContext context = new StandardEvaluationContext(parameterObject);

						
						
						if (modelAttributeValue.equals("")) {

							MultiMap parms = httpServerRequest.params();
							for (Entry<String, String> entry : parms) {
								String key = entry.getKey();
								String value = entry.getValue();
								Expression exp = parser.parseExpression(key);
								
								try{
									exp.setValue(context, value);
								}catch(Exception e){
									//throw new Exception("ModelAttribute parse exception - unable to found parm - " + key);
								}
								
							}

						} else {

							MultiMap parms = httpServerRequest.params();

							for (Entry<String, String> entry : parms) {

								// entry.getKey() => vehicle.owner.name
								// pattern => \\bvehicle\\.\\b
								// check for => vehicle.
								String checkStr = entry.getKey();
								String pattern = "\\b" + modelAttributeValue + "\\.\\b";

								Pattern r = Pattern.compile(pattern);

								Matcher m = r.matcher(checkStr);
								if (m.find()) {
									String key = checkStr.substring(m.end());
									String value = entry.getValue();
									Expression exp = parser.parseExpression(key);
									
									try{
										exp.setValue(context, value);
									}catch(Exception e){
										//throw new Exception("ModelAttribute parse exception - unable to found parm - " + key);
									}
								}
							}

						}

						inputParms[paramterCount++] = parameterObject;
					}else{
						throw new InternalServerException(ErrorMessage.UNABLE_TO_PROCESS_METHOD_INPUT_PARM.getMessage() + method.getName());
					}

				}
			}

			method.invoke(controllerObject, inputParms);

		}catch (InstantiationException e) {
			prepareFailureResponse(routingContext,400,e.getCause().toString());
		}catch (IllegalAccessException e) {
			prepareFailureResponse(routingContext,400,e.getCause().toString());
		}catch (Exception e) {
			prepareFailureResponse(routingContext,400,e.getMessage());
		}
	}
	
	private void prepareFailureResponse(RoutingContext failureRoutingContext,int statusCode,String message){
		HttpServerResponse response = failureRoutingContext.response();
		
		response.setStatusCode(statusCode);
		
		response.putHeader("content-type", "text/html");
		
		StringBuilder responseStrBuilder = new StringBuilder();
		responseStrBuilder.append("<h1 style='background-color:#D50000;color:#FFF'>HTTP Status - " + statusCode + "</h1>");
		responseStrBuilder.append("<h3 style='color:#D50000'>message : " + message + "</h3>");
		responseStrBuilder.append("<h3 style='color:#D50000'>puppy-io [web]</h3>");
		
		response.end(responseStrBuilder.toString());
	}
	
	private SockJSHandler socketJSRegister() {
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
		BridgeOptions options = new BridgeOptions();
		//ui.[appName].[method]
		options.addOutboundPermitted(new PermittedOptions().setAddressRegex("ui." + appConfig.getAppName() + "..*"));
		sockJSHandler.bridge(options, event -> {
			if (event.type() == BridgeEventType.SOCKET_CREATED) {
			}
			event.complete(true);
		});
		return sockJSHandler;

	}
	
	public void setSessionHandler(SessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}

	private String getAppNameBaseUrl(){
		return "/" + appConfig.getAppName();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
