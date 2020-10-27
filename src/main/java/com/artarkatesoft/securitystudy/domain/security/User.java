package com.artarkatesoft.securitystudy.domain.security;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String password;
    private String username;
    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = true;

//    @Transient
//    private Set<Authority> authorities;

    @Singular
    @ManyToMany(cascade = {MERGE,PERSIST}, fetch = EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
    private Set<Role> roles;

    @Override
    public Set<Authority> getAuthorities() {
        return roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
