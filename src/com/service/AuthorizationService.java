package com.service;

import com.model.Role;
import com.model.User;

public class AuthorizationService {

    public boolean isAdmin(User user) {

        return user.getRole() == Role.ADMIN;
    }

    public boolean isInvestigator(User user) {

        return user.getRole() == Role.INVESTIGATOR;
    }

    public boolean isAuditor(User user) {

        return user.getRole() == Role.AUDITOR;
    }
}