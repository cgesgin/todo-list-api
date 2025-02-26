package com.cgesgin.todo_list_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cgesgin.todo_list_api.model.entity.User;
import com.cgesgin.todo_list_api.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Configuration
public class UserDetailsServiceConfig{

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            Optional<User> user = userRepository.findByUsername(username);
            if (!user.isPresent()) {
                throw new UsernameNotFoundException("User not found");
            }
            return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), new ArrayList<>());
        };
    }
}