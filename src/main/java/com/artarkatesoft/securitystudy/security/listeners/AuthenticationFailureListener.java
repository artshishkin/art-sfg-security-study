package com.artarkatesoft.securitystudy.security.listeners;

import com.artarkatesoft.securitystudy.domain.security.LoginFailure;
import com.artarkatesoft.securitystudy.repositories.security.LoginFailureRepository;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        StringBuilder builder = new StringBuilder("Login FAILURE");
        LoginFailure.LoginFailureBuilder loginFailureBuilder = LoginFailure.builder();
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof String) {
                String username = (String) token.getPrincipal();
                userRepository.findByUsername(username).ifPresent(loginFailureBuilder::user);
                loginFailureBuilder.username(username);
                builder.append("\nAttempted Username: ").append(username);
            }

            if (token.getCredentials() instanceof String)
                builder.append("\nAttempted password: ").append((String) token.getCredentials());

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) token.getDetails();
                String remoteAddress = webAuthenticationDetails.getRemoteAddress();
                builder.append("\nRemote IP: ").append(remoteAddress);
                loginFailureBuilder.sourceIp(remoteAddress);
                builder.append("\nSession ID: ").append(webAuthenticationDetails.getSessionId());
            }
            LoginFailure loginFailure = loginFailureRepository.save(loginFailureBuilder.build());
            builder.append("\nLogin Failure saved with id: ").append(loginFailure.getId());
        }
        log.debug(builder.toString());
    }
}
