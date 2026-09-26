package com.dao;

import java.util.ArrayList;
import java.util.List;

import com.model.User;

public class UserDAO implements DAO<User> {

    private List<User> users = new ArrayList<>();

    @Override
    public void add(User user) {
        users.add(user);
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User findBy(int id) {

        for (User user : users) {

            if (user.getUserId() == id) {
                return user;
            }
        }

        return null;
    }

    @Override
    public void delete(int id) {

        User user = findBy(id);

        if (user != null) {
            users.remove(user);
        }
    }
    
    public User findByUsername(String userName) {

        for (User user : users) {

            if (user.getUserName().equals(userName)) {
                return user;
            }
        }

        return null;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
}