package com.service;

import com.dao.UserDAO;
import com.exception.InvalidLoginException;
import com.model.User;

public class AuthenticationService {

	private UserDAO userDAO;

	public AuthenticationService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	  public User login(String userName, String password)
	            throws InvalidLoginException {

	        User user = userDAO.findByUsername(userName);

	        if (user == null) {

	            throw new InvalidLoginException(
	                    "Invalid username or password"
	            );
	        }

	        if (!user.getPassword().equals(password)) {

	            throw new InvalidLoginException(
	                    "Invalid username or password"
	            );
	        }

	        return user;
	    }
	}
