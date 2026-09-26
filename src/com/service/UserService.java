package com.service;

import com.dao.UserDAO;
import com.exception.DuplicateUserException;
import com.model.User;

public class UserService {

	private UserDAO userDAO;
	
	public UserService(UserDAO userDAO) //constructor dependency injection
	{
		this.userDAO = userDAO;
	}
	
	public void registerUser(User user)
            throws DuplicateUserException {

        User existingUser =
                userDAO.findByUsername(user.getUserName());

        if (existingUser != null) {

            throw new DuplicateUserException(
                    "Username already exists: "
                    + user.getUserName()
            );
        }

        userDAO.add(user);

        System.out.println(
                "User registered successfully."
        );
    }
}