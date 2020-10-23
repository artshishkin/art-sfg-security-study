package com.artarkatesoft.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizedUrl ->
                        authorizedUrl
                                .antMatchers("/", "/webjars/**", "/resources/**").permitAll()
                                .antMatchers("/beers/find", "/beers").permitAll()
                                .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                                .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
                )

                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

                .formLogin().and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
//                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .withUser("art")
//                .password("{noop}123")
                .password("{SSHA}2aZ2MSg55bVH2sgFhNYw0tcy1+rl5JHsLXXNBg==")
                .roles("ADMIN")
                .and()
                .withUser("secondUser")
//                .password("{noop}pass222")
                .password("{SSHA}WWyAjJSuxdmicnwiM008pFMGQlEUYQwb5y+HIQ==")
                .roles("USER");
        auth.inMemoryAuthentication()
                .withUser("scott")
//                .password("{noop}tiger")
                .password("{SSHA}hZBa7iNMzmAwCueu9Av3KpSxOY07YhaH3Zh05w==")
                .roles("CUSTOMER");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new LdapShaPasswordEncoder();
    }

//    @Bean
//    @Override
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("art")
//                .password("123")
////                .roles("ADMIN", "USER")
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("secondUser")
//                .password("pass222")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
