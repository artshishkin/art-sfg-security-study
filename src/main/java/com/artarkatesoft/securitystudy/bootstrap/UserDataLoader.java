package com.artarkatesoft.securitystudy.bootstrap;

import com.artarkatesoft.securitystudy.domain.security.Authority;
import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.repositories.security.AuthorityRepository;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;
        loadSecurityData();
    }

    private void loadSecurityData() {

        List<Authority> authorities = Stream.of("ADMIN", "USER", "CUSTOMER")
                .map(authority -> Authority.builder().authority(authority).build())
                .map(authorityRepository::save)
                .collect(Collectors.toList());

        Authority admin = authorities.get(0);
        Authority user = authorities.get(1);
        Authority customer = authorities.get(2);


        User user1 = User.builder()
                .username("art")
                .password(passwordEncoder.encode("123"))
                .authority(admin)
                .build();
        User user2 = User.builder()
                .username("secondUser")
                .password(passwordEncoder.encode("pass222"))
                .authority(user)
                .build();
        User user3 = User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .authority(customer)
                .build();

        userRepository.saveAll(List.of(user1, user2, user3));

        log.debug("Users loaded: {}", userRepository.count());
    }

}
