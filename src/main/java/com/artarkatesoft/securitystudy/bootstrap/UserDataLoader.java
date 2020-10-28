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
import java.util.Set;
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
        //Beer auth
        List<Authority> authorities = Stream.of("beer.create", "beer.read", "beer.update", "beer.delete")
                .map(permission -> Authority.builder().authority(permission).build())
//                .map(authorityRepository::save)
                .collect(Collectors.toList());

        Authority createBeer = authorities.get(0);
        Authority readBeer = authorities.get(1);
        Authority updateBeer = authorities.get(2);
        Authority deleteBeer = authorities.get(3);


//        Role adminRole = roleRepository.save(
//                Role.builder().name("ADMIN").authorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer)).build());
//        Role customerRole = roleRepository.save(
//                Role.builder().name("CUSTOMER").authority(readBeer).build());
//        Role userRole = roleRepository.save(
//                Role.builder().name("USER").authority(readBeer).build());
        Role adminRole = Role.builder().name("ADMIN").authorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer)).build();
        Role customerRole = Role.builder().name("CUSTOMER").authority(readBeer).build();
        Role userRole = Role.builder().name("USER").authority(readBeer).build();

//        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
//        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
//        Role userRole = roleRepository.save(Role.builder().name("USER").build());
//
//        adminRole.setAuthorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer));
//        customerRole.setAuthorities(Set.of(readBeer));
//        userRole.setAuthorities(Set.of(readBeer));

//        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));
//        adminRole = roleRepository.save(adminRole);
//        customerRole = roleRepository.save(customerRole);
//        userRole = roleRepository.save(userRole);

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

//        userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
        userRepository.saveAll(List.of(user1, user2, user3));

        log.debug("Users loaded: {}", userRepository.count());
    }

}
