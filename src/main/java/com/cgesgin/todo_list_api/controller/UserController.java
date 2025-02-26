package com.cgesgin.todo_list_api.controller;

import java.util.ArrayList; 
import java.util.List; 
import java.util.Objects;

import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.cgesgin.todo_list_api.config.security.JwtUtil;
import com.cgesgin.todo_list_api.model.dto.DataResponse;
import com.cgesgin.todo_list_api.model.dto.TokenResponse;
import com.cgesgin.todo_list_api.model.entity.User;
import com.cgesgin.todo_list_api.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "User", description = "API for User operations")
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;
    
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<DataResponse<User>> save(@RequestBody User user) {

        DataResponse<User> response = new DataResponse<>();
        if (Objects.isNull(user)) {
            response.setMessage("Payload cannot be Null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (userService.findByUsername(user.getUsername())) {
            response.setMessage("Username is already taken");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.setData(userService.save(user));
        response.setMessage(HttpStatus.CREATED.toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<List<TokenResponse>>> createAuthenticationToken(@RequestBody User user) throws BadCredentialsException {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        List<TokenResponse> tokens = new ArrayList<>();
        tokens.add(new TokenResponse(jwt));

        DataResponse<List<TokenResponse>> response = new DataResponse<>();
        response.setData(tokens);
        response.setMessage(HttpStatus.OK.toString());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<DataResponse<List<TokenResponse>>> refreshToken(@RequestBody TokenResponse token) {

        DataResponse<List<TokenResponse>> response = new DataResponse<>();
        List<TokenResponse> tokens = new ArrayList<>();
        
        if (token == null) {
            
            response.setMessage("Token is missing or invalid");
            return ResponseEntity.badRequest().body(response);
        }

        String username = jwtUtil.extractUsernameFromToken(token.getToken());

        if(!this.userService.findByUsername(username)){
            
            response.setMessage("User Not Found");
            return ResponseEntity.status(403).body(response);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String newAccessToken = jwtUtil.generateToken(userDetails);

        tokens.add(new TokenResponse(newAccessToken));
        
        response.setData(tokens);
        response.setMessage(HttpStatus.CREATED.toString());
        
        return ResponseEntity.ok(response);
    }
}
