package com.lovi.puppy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lovi.puppy.annotation.enums.RequestBodyFormat;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
	RequestBodyFormat format() default RequestBodyFormat.STRING;
	String encode() default "";
}
