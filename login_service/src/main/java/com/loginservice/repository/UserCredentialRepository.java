package com.loginservice.repository;


import com.loginservice.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository  extends JpaRepository<UserCredential,Integer> {


    UserCredential findByEmail(String email);

    boolean existsByEmail(String email);
}
