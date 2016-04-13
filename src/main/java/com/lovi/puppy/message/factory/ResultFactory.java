package com.lovi.puppy.message.factory;

import com.lovi.puppy.message.Result;
import com.lovi.puppy.message.impl.ResultImpl;

public class ResultFactory{

	public <T> Result<T> create(){
		return new ResultImpl<>();
	}
}
