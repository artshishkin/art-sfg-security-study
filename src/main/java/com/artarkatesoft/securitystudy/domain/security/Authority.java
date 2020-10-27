package com.artarkatesoft.securitystudy.domain.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String authority;

    @ManyToMany(mappedBy = "authorities")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Role> roles;
}
