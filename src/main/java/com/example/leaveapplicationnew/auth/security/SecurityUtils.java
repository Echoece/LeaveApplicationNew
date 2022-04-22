package com.example.leaveapplicationnew.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


public class SecurityUtils {

    public static Optional<String> getCurrentLoggedInUserName(){
        final Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return Optional.empty();

        String username = authentication.getName();

        return Optional.ofNullable(username);
    }


}
