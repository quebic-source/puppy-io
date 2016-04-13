package com.lovi.puppy.message.handler;

@FunctionalInterface
public interface ResultHandler<T> {
	void handle(T value);
}
