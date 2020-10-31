package com.artarkatesoft.securitystudy.repositories.security;

import com.artarkatesoft.securitystudy.domain.security.LoginFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
}
