package com.lovi.puppy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lovi.puppy.annotation.enums.HttpMethod;

/**
 * Annotation for mapping web requests onto specific handler classes and/or handler methods
 * </br>
 * </br>
 * value => the path mapping URIs.(e.g. "/testPath")
 * </br>
 * </br>
 * method => The HTTP request methods to map to, narrowing the primary mapping. default HttpMethod.GET (HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH)
 * </br>
 * </br>
 * consumes => The consumable media types of the mapped request.
 * </br>
 * </br>
 * produce => The producible media types of the mapped request.
 * </br>
 * </br>
 * @author Tharanga Thennakoon
 *
 */

@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

	String value() default "/";
	HttpMethod method() default HttpMethod.GET;
	String consumes() default "";
	String produce() default "";
	
}
