package com.artarkatesoft.securitystudy.domain.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String authority;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
