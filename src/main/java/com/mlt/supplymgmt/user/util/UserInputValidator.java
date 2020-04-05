package com.mlt.supplymgmt.user.util;

import com.mlt.supplymgmt.user.exception.BadUserInputException;
import com.mlt.supplymgmt.user.model.User;

public class UserInputValidator {

	public static final void validateCreateUserInput(User user) throws BadUserInputException {
		if(user.getName()==null || user.getName().isEmpty()) {
			throw new BadUserInputException("Name is missing");
		}
		if(user.getUsername()==null || user.getUsername().isEmpty()) {
			throw new BadUserInputException("Username is missing");
		}
		if(user.getPassword()==null || user.getPassword().isEmpty()) {
			throw new BadUserInputException("Password is missing");
		}
		
	}
	public static final void validateUserId(String id) throws BadUserInputException{
		if(id.length()!=36) {
			throw new BadUserInputException("Invalid UserId");
		}
		
	}
}
