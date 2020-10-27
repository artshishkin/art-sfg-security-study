package com.artarkatesoft.securitystudy.config;

import com.artarkatesoft.securitystudy.security.ArtPasswordEncoderFactories;
import com.artarkatesoft.securitystudy.security.RestHeaderAuthFilter;
import com.artarkatesoft.securitystudy.security.RestUrlAuthFilter;
import com.artarkatesoft.securitystudy.security.SfgRestHeaderAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    JpaUserDetailsService jpaUserDetailsService;

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
                .addFilterBefore(artRestHeaderAuthFilter(this.authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(artRestRequestParamAuthFilter(this.authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http
                .authorizeRequests(authorize ->
                        authorize
                                .antMatchers("/h2-console/**").permitAll()  //do not use in production!!!
                                .antMatchers("/", "/webjars/**", "/resources/**").permitAll()
                                .antMatchers("/beers/find", "/beers/**")
                                    .hasAnyRole("ADMIN","USER","CUSTOMER")
                                .antMatchers(HttpMethod.GET, "/api/v1/beer/**")
                                    .hasAnyRole("ADMIN","USER","CUSTOMER")
                                .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}")
                                    .authenticated()
                                .mvcMatchers(HttpMethod.DELETE, "/api/v1/beer/**")
                                    .hasRole("ADMIN")
                                .mvcMatchers("/breweries/**", "/breweries.html", "/api/v1/breweries")
                                    .hasAnyRole("ADMIN", "CUSTOMER")
                )

                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

                .formLogin().and()
                .httpBasic();

//        h2 console config
        http.headers().frameOptions().sameOrigin();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return ArtPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
