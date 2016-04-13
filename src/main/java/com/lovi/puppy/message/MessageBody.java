package com.lovi.puppy.message;

import java.io.Serializable;
import java.util.Arrays;

public class MessageBody implements Serializable{
	private static final long serialVersionUID = -5505373268972638592L;
	private int status;
	private Object[] values;
	private Throwable throwable;
	
	public MessageBody() {
	}
	
	public MessageBody(int status,Throwable throwable,Object... values) {
		this.status = status;
		this.values = values;
		this.throwable = throwable;
	}
	
	public MessageBody(int status, Object... values) {
		this.status = status;
		this.values = values;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "MessageBody [status=" + status + ", inputParameters="
				+ Arrays.toString(values) + ", throwable=" + throwable
				+ "]";
	}

	
}
