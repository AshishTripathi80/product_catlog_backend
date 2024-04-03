package com.loginservice.controller;


import com.loginservice.entity.UserCredential;
import com.loginservice.exception.UserNotFoundException;
import com.loginservice.repository.UserCredentialRepository;
import com.loginservice.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @PostMapping
    public ResponseEntity<UserCredential> addUser(@Valid @RequestBody UserCredential user, BindingResult bindingResult) {
        logger.info("Received addUser request for email: {}", user.getEmail());
        UserCredential newUser = authService.addUser(user, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    @GetMapping
    public List<UserCredential> getUser(){
        return authService.getUser();
    }

    @PostMapping("/login")
    public ResponseEntity<UserCredential> login(@RequestBody UserCredential userCredential) {
        logger.info("Received login request for email: {}", userCredential.getEmail());
        String email = userCredential.getEmail();
        UserCredential user = userCredentialRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("No user found with email: " + email);
        }

        if (user.getEmail().equals(userCredential.getEmail()) && user.getPassword().equals(userCredential.getPassword())) {
            String token = authService.generateToken(userCredential.getEmail());
            user.setToken(token);
            return ResponseEntity.ok(user);
        } else {
            throw new RuntimeException("Invalid access");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        logger.info("Received validateToken request for token: {}", token);
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}
