package com.artarkatesoft.securitystudy.config;

import com.artarkatesoft.securitystudy.security.RestHeaderAuthFilter;
import com.artarkatesoft.securitystudy.security.RestUrlAuthFilter;
import com.artarkatesoft.securitystudy.security.SfgRestHeaderAuthFilter;
import com.artarkatesoft.securitystudy.security.google.Google2faFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    JpaUserDetailsService jpaUserDetailsService;

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository tokenRepository;
    private final Google2faFilter google2faFilter;

    //needed to use with Spring Data JPA SpEL
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    public SfgRestHeaderAuthFilter sfgRestHeaderAuthFilter(AuthenticationManager manager) {
        SfgRestHeaderAuthFilter authFilter = new SfgRestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        authFilter.setAuthenticationManager(manager);
        return authFilter;
    }

    public RestHeaderAuthFilter artRestHeaderAuthFilter(AuthenticationManager manager) {
        RestHeaderAuthFilter authFilter = new RestHeaderAuthFilter("/api/**");
        authFilter.setAuthenticationManager(manager);
        return authFilter;
    }

    public RestUrlAuthFilter artRestRequestParamAuthFilter(AuthenticationManager manager) {
        RestUrlAuthFilter authFilter = new RestUrlAuthFilter("/api/**");
        authFilter.setAuthenticationManager(manager);
        return authFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .addFilterBefore(google2faFilter, SessionManagementFilter.class);

        http
                .addFilterBefore(artRestHeaderAuthFilter(this.authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(artRestRequestParamAuthFilter(this.authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().ignoringAntMatchers("/h2-console/**", "/api/**");

        http
                .authorizeRequests(authorize ->
                        authorize
                                .antMatchers("/h2-console/**").permitAll()  //do not use in production!!!
                                .antMatchers("/", "/webjars/**", "/resources/**").permitAll()
                )

                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

                .formLogin(loginConfigurer -> loginConfigurer
                        .loginProcessingUrl("/login")
                        .loginPage("/")
                        .permitAll()
                        .successForwardUrl("/")
                        .defaultSuccessUrl("/")
                        .failureUrl("/?error"))
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                )
                .httpBasic()
                .and()
                .rememberMe().tokenRepository(tokenRepository).userDetailsService(userDetailsService);
//                .rememberMe().key("art-key").userDetailsService(userDetailsService);

//        h2 console config
        http.headers().frameOptions().sameOrigin();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());
//    }

}
