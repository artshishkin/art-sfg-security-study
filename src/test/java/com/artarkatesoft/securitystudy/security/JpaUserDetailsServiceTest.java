package com.artarkatesoft.securitystudy.security;

import com.artarkatesoft.securitystudy.bootstrap.UserDataLoader;
import com.artarkatesoft.securitystudy.repositories.security.AuthorityRepository;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class JpaUserDetailsServiceTest {

    @Autowired
    UserRepository userRepository;

    JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    AuthorityRepository authorityRepository;

    UserDataLoader userDataLoader;

    @BeforeEach
    void setUp() throws Exception {
        userDataLoader = userDataLoader();
        userDataLoader.run();
        jpaUserDetailsService = new JpaUserDetailsService(userRepository);
    }

    public PasswordEncoder passwordEncoder() {
        return ArtPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    UserDataLoader userDataLoader() {
        return new UserDataLoader(authorityRepository, userRepository, passwordEncoder());
    }

    @ParameterizedTest
    @CsvSource({
            "art,ADMIN",
            "secondUser,USER",
            "scott,CUSTOMER"
    })
    void loadByUsernameWhenPresent(String username, String role) {
        //when
        UserDetails user = jpaUserDetailsService.loadUserByUsername(username);

        //then
        assertThat(user).isNotNull();
        assertThat(user.getAuthorities()).isNotEmpty();
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo(role);
    }

    @Test
    void loadByUsernameWhenAbsent() {
        //given
        String username = "AbsentUser";

        //when ... then
        assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername(username))
                .isExactlyInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User " + username + " not found");
    }
}