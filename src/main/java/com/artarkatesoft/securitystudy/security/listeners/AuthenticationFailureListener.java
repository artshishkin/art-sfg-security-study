package com.artarkatesoft.securitystudy.security.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFailureListener {

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        StringBuilder builder = new StringBuilder("Login FAILURE");
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            if (token.getPrincipal() instanceof String)
                builder.append("\nAttempted Username: ").append((String) token.getPrincipal());
            if (token.getCredentials() instanceof String)
                builder.append("\nAttempted password: ").append((String) token.getCredentials());
            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) token.getDetails();
                builder.append("\nRemote IP: ").append(webAuthenticationDetails.getRemoteAddress());
                builder.append("\nSession ID: ").append(webAuthenticationDetails.getSessionId());
            }
        }
        log.debug(builder.toString());
    }
}
