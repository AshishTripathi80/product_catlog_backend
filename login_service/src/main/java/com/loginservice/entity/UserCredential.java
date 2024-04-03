package com.loginservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true) // Ensures uniqueness of email
    private String email;

    @Size(min = 2, max = 10, message = "firstname must be between 2 and 10 characters")
    private String firstName;

    @Size(min = 2, max = 10, message = "lastname must be between 2 and 10 characters")
    private String lastName;

    @Size(min = 5,message = "Password name must be more than 5 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Size(min = 5,message = "Password name must be more than 5 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    private String token;

}
