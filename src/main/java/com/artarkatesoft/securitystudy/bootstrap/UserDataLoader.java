package com.artarkatesoft.securitystudy.bootstrap;

import com.artarkatesoft.securitystudy.domain.security.Authority;
import com.artarkatesoft.securitystudy.domain.security.Role;
import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.repositories.security.AuthorityRepository;
import com.artarkatesoft.securitystudy.repositories.security.RoleRepository;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;
        loadSecurityData();
    }

    private void loadSecurityData() {
        Map<String, Authority> auth = Stream.of(
                "beer.create", "beer.read", "beer.update", "beer.delete",
                "customer.create", "customer.read", "customer.update", "customer.delete",
                "brewery.create", "brewery.read", "brewery.update", "brewery.delete"
        )
                .map(permission -> Authority.builder().authority(permission).build())
//                .map(authorityRepository::save)
                .collect(Collectors.toMap(Authority::getAuthority, authority -> authority));

        Role adminRole = Role.builder().name("ADMIN").authorities(auth.values()).build();
        Role customerRole = Role.builder().name("CUSTOMER")
                .authority(auth.get("beer.read"))
                .authority(auth.get("customer.read"))
                .authority(auth.get("brewery.read"))
                .build();
        Role userRole = Role.builder().name("USER")
                .authority(auth.get("beer.read"))
                .build();

        User user1 = User.builder()
                .username("art")
                .password(passwordEncoder.encode("123"))
                .role(adminRole)
                .build();
        User user2 = User.builder()
                .username("secondUser")
                .password(passwordEncoder.encode("pass222"))
                .role(userRole)
                .build();
        User user3 = User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build();

        userRepository.saveAll(List.of(user1, user2, user3));

        log.debug("Users loaded: {}", userRepository.count());
    }

}
