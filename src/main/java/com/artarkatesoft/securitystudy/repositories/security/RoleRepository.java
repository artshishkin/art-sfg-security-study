package com.artarkatesoft.securitystudy.repositories.security;

import com.artarkatesoft.securitystudy.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
