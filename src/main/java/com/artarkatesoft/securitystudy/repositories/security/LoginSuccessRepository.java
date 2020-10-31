package com.artarkatesoft.securitystudy.repositories.security;

import com.artarkatesoft.securitystudy.domain.security.LoginSuccess;
import com.artarkatesoft.securitystudy.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Integer> {
    List<LoginSuccess> findAllByUser(User user);
}
