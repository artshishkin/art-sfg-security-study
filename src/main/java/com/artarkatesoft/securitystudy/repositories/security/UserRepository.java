package com.artarkatesoft.securitystudy.repositories.security;

import com.artarkatesoft.securitystudy.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
