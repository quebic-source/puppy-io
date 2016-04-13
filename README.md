# puppy-io
puppy-io provides easy way to develop reactive microservice applications on the jvm.

### Prerequisities
  * JDK 1.8.X
  * Maven 3.3.X

### Getting Started
 * Download the [puppy-io](https://github.com/loviworld/puppy-io) from GitHub
 * Install puppy-io dependency using **mvn install**
 
### Sample application
 * Download the [user-mgr](https://github.com/loviworld/puppy-io) application from GitHub
 * Build the application using **mvn package**
 * Run the application using **java -jar target/use.jar**

##Starting the application
```java
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        PuppyApp.create(App.class, "user-mgr", args).run(81);
    }
}
```
* ```PuppyApp.run()``` default port is 8080
* ```PuppyApp.run(int webAppPort)```
* ```PuppyApp.runWebApp()``` run only the web application
* ```PuppyApp.runServiceApp()``` run only the service application
* Sample application is based on spring-boot.you also can use puppy-io without spring-boot

##Web App
```java
@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private ServiceCaller serviceCaller;
	
	@ResponseBody
	@RequestMapping(produce="application/json")
	public void findAll(HttpResponseResult responseResult) throws ServiceCallerException{
		
		Result<List<User>> result = Result.create();
		FailResult failResult = FailResult.create();
		
		serviceCaller.call("UserService.findAll", result);
		
		result.process(r->{
			responseResult.complete(new ResponseMessage(1, r));
		}, failResult);
		
		failResult.setHandler(fail->{
			responseResult.complete(new ResponseMessage(-1, fail.getMessage()),500);
		});
	}
	
	@ResponseBody
	@RequestMapping(method=HttpMethod.POST,produce="application/json")
	public void insert(@ModelAttribute User user,HttpResponseResult responseResult) throws ServiceCallerException{
		
		serviceCaller.call("UserService.insert", user);
		responseResult.complete(new ResponseMessage(1, "do insert"),200);
	
	}
	....
}
```
####@Controller
* Use ```com.lovi.puppy.annotation.Controller```
* Implementation of the controllers are similar to the spring-mvc but remember internal architecture of the puppy-io is totally different from spring-mvc

####@RequestMapping
* value = The primary mapping expressed by this annotation
* method = The HTTP request methods
* consumes = The consumable media types of the mapped request
* produce = The producible media types of the mapped request

####HttpResponseResult
* ```HttpResponseResult.complete(Object value)``` set response value
* ```HttpResponseResult.complete(Object value, int statusCode)``` set response value with statusCode

####ServiceCaller
* ServiceCaller is used to call service method
* ```ServiceCaller.call(String serviceMethod,Object... inputParameters)```
* ```ServiceCaller.call(String serviceMethod,Result<U> result,Object... inputParameters)```. if your service method has a return value, get the return value by using ```Result```
* ```ServiceCaller.call(String appName,String serviceMethod,Result<U> result,Object... inputParameters)```. if you want to call serivce method from a another application, you can call with the appName

####Result
* ```Result``` is used to catch the return value of the service method which is called by ```ServiceCaller```

####FailResult 
* ```FailResult``` is used to catch the failure within the service method call which is called by ```ServiceCaller```

##Service App
```java
@Service("userService")
public class UserService{

	@Autowired
	private UserRepository userRepository;
	
	@ServiceFunction
	public void insert(User user){
		userRepository.insert(user);
	}
	
	@ServiceFunction("_findAll")
	public List<User> findAll(){
		return userRepository.findAll();
	}
	....
}
```
####@Service
* Use ```com.lovi.puppy.annotation.Service```
* Class is marked as a service by using ```@Service```

####@ServiceFunction
* use ```com.lovi.puppy.annotation.ServiceFunction```
* Method is marked as a service method by using ```@ServiceFunction```

##UI Service
```java
@UIService
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@UIServiceFunction(listenerAddress="users", delay=1)
	public List<User> pushUsers(){
		return userRepository.findAll();
	}
}
```
