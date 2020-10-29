package com.artarkatesoft.securitystudy.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.delete') OR " +
        "hasAuthority('customer.order.delete') AND @beerOrderAuthenticationManager.customerIdMatches(authentication,#customerId)")
public @interface BeerOrderDeletePermission {
}
