# puppy-io
puppy-io provides easy way to develop reactive microservice applications on the jvm.

### Prerequisities
  * JDK 1.8.X
  * Maven 3.3.X

### Getting Started
 * Download the [puppy-io](https://github.com/loviworld/puppy-io) from GitHub
 * Install puppy-io dependency using **mvn install**
 
### Running the sample application
 * Download the [user-mgr](https://github.com/loviworld/puppy-io) application from GitHub
 * Build the application using **mvn package**
 * Run teh application using **java -jar target/use.jar**

#How to use puppy-io
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
* ```PuppyApp.run()``` default webAppPort is 8080
* ```PuppyApp.run(int webAppPort)```
* sample application is based on spring-boot.if you want you can use puppy-io without spring-boot


