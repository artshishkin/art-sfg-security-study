package com.artarkatesoft.securitystudy.security;

import com.artarkatesoft.securitystudy.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {

    public boolean customerIdMatches(Authentication authentication, UUID customerId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        log.debug("Auth User Customer Id: {} Customer Id: {}", authenticatedUser.getCustomer().getId(), customerId);

        return Objects.equals(authenticatedUser.getCustomer().getId(), customerId);
    }
}
