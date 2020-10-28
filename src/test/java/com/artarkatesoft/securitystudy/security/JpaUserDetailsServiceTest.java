package com.artarkatesoft.securitystudy.security;

import com.artarkatesoft.securitystudy.bootstrap.UserDataLoader;
import com.artarkatesoft.securitystudy.repositories.security.AuthorityRepository;
import com.artarkatesoft.securitystudy.repositories.security.RoleRepository;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.GrantedAuthority;
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

    @Autowired
    RoleRepository roleRepository;

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
        return new UserDataLoader(authorityRepository, roleRepository, userRepository, passwordEncoder());
    }

    @ParameterizedTest
    @CsvSource({
            "art,beer.create,true",
            "art,beer.delete,true",
            "art,customer.create,true",
            "art,brewery.create,true",
            "secondUser,beer.read,true",
            "secondUser,beer.update,false",
            "secondUser,beer.create,false",
            "secondUser,brewery.delete,false",
            "secondUser,customer.delete,false",
            "scott,beer.read,true",
            "scott,beer.update,false",
            "scott,beer.delete,false",
            "scott,brewery.update,false",
            "scott,brewery.delete,false"
    })
    void loadByUsernameWhenPresent(String username, String permission, boolean isAllowed) {
        //when
        UserDetails user = jpaUserDetailsService.loadUserByUsername(username);

        //then
        assertThat(user).isNotNull();
        assertThat(user.getAuthorities()).isNotEmpty();
        if (isAllowed)
            assertThat(user.getAuthorities().stream().map(GrantedAuthority::getAuthority)).contains(permission);
        else
            assertThat(user.getAuthorities().stream().map(GrantedAuthority::getAuthority)).doesNotContain(permission);
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