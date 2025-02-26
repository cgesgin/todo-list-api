package com.cgesgin.todo_list_api.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cgesgin.todo_list_api.model.entity.User;
import com.cgesgin.todo_list_api.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        if (user.getId()==null || user.getId()>0) {
            user.setId(null);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var savedUser = userRepository.save(user);

        savedUser.setPassword("********");
        return savedUser;
    }

    public boolean findByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if(byUsername.isPresent()) {
            return true;
        }
        return false;
    }
}
