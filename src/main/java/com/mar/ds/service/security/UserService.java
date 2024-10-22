package com.mar.ds.service.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public List<UserDetails> findAll() {
        return List.of(
                User.withUsername("marolok")
                        .password("{noop}123")
                        .roles("USER")
                        .build(),
                User.withUsername("mar")
                        .password("{noop}111")
                        .roles("USER")
                        .build()
        );
    }

    public UserDetails findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

}
