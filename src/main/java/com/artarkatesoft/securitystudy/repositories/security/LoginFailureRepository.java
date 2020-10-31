package com.artarkatesoft.securitystudy.repositories.security;

import com.artarkatesoft.securitystudy.domain.security.LoginFailure;
import com.artarkatesoft.securitystudy.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
    long countAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}
