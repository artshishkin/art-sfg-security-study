package com.artarkatesoft.securitystudy.security.listeners;

import com.artarkatesoft.securitystudy.domain.security.LoginFailure;
import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.repositories.security.LoginFailureRepository;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @Value("${app.security.max-attempts-to-lock-user:5}")
    private Integer maxAttemptsToLockUser;

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

            if (loginFailure.getUser() != null)
                lockoutUser(loginFailure.getUser());
        }
        log.debug(builder.toString());
    }

    private void lockoutUser(User user) {
        long failures = loginFailureRepository.countAllByUserAndCreatedDateIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        if (failures >= maxAttemptsToLockUser) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
            log.debug("User with id {} is locked out after {} failure attempts", user.getId(), failures);
        }
    }
}
