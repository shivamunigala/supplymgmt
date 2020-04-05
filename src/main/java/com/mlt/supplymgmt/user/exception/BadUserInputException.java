package com.mlt.supplymgmt.user.exception;

public class BadUserInputException extends Exception {

	private static final long serialVersionUID = 1L;

	public String message;
	
	public BadUserInputException (String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
