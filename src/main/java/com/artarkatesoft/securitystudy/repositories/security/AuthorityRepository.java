package com.artarkatesoft.securitystudy.repositories.security;

import com.artarkatesoft.securitystudy.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority,Integer> {
}
