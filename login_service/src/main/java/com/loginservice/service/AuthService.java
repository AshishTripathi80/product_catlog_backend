package com.loginservice.service;


import com.loginservice.entity.UserCredential;
import com.loginservice.exception.DuplicateEmailException;
import com.loginservice.exception.InvalidTokenException;
import com.loginservice.exception.InvalidUserDataException;
import com.loginservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private JwtService jwtService;

    public UserCredential addUser(UserCredential credential, BindingResult bindingResult) {

        validationError(bindingResult);

        // Check if email already exists
        if (userCredentialRepository.existsByEmail(credential.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + credential.getEmail());
        }

        return userCredentialRepository.save(credential);
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        try {
            jwtService.validateToken(token);
        } catch (InvalidTokenException ex) {
            throw new InvalidTokenException("Invalid token: " + token, ex);
        }
    }




    /**
     * Handle validation errors.
     *
     * @param bindingResult The BindingResult object containing validation errors.
     * @throws InvalidUserDataException if validation errors are present.
     */
    public void validationError(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            List<String> errors = new ArrayList<>();

            // Convert each field error to a string representation
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }

            // Throw an exception with the validation errors
            throw new InvalidUserDataException("Validation Failed!", LocalDateTime.now(), errors);
        }
    }


    public List<UserCredential> getUser() {
        return userCredentialRepository.findAll();
    }
}
