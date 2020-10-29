package com.artarkatesoft.securitystudy.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.update') OR " +
        "hasAuthority('customer.order.update') AND @beerOrderAuthenticationManager.customerIdMatches(authentication,#customerId)")
public @interface BeerOrderUpdatePermission {
}
