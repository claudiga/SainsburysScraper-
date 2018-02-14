package com.sainsburys.exceptions;

public class UnableToGetItemException extends RuntimeException {
	
	public UnableToGetItemException(String Message) {
		super(Message);
		
	}
	
	public UnableToGetItemException(String Message, Throwable cause) {
		super(Message,cause);
		
	}
	
	

}
