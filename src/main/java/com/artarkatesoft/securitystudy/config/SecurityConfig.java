package com.artarkatesoft.securitystudy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
                .passwordEncoder(new BCryptPasswordEncoder())
                .withUser("art")
//                .password("{noop}123")
                .password("$2a$10$9oSuHCeDq/tcgIA4XMJl1Oe3LsGlqJTYvgj6VnI3EatWRJKMI53uS")
                .roles("ADMIN")
                .and()
                .withUser("secondUser")
//                .password("{noop}pass222")
                .password("$2a$10$HcAo/krFqDFHIi2gtYgi9u4M.ANujT8j5kg3Ma2FCpjlh/5k.ps6u")
                .roles("USER");
        auth.inMemoryAuthentication()
                .withUser("scott")
//                .password("{noop}tiger")
                .password("{bcrypt}$2a$10$3R5K18zZUSynPsLeFK.JGehL6d3fYkIc3UtfgJnyEzVq1E4/ggBBK")
                .roles("CUSTOMER");
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

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
