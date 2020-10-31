package com.artarkatesoft.securitystudy.security.listeners;

import com.artarkatesoft.securitystudy.domain.security.LoginSuccess;
import com.artarkatesoft.securitystudy.domain.security.Role;
import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.repositories.security.LoginSuccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {

    private final LoginSuccessRepository loginSuccessRepository;

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {
        StringBuilder builder = new StringBuilder("User Logged In Okay");
//        User user = (User) event.getAuthentication().getPrincipal();
        LoginSuccess.LoginSuccessBuilder loginSuccessBuilder = LoginSuccess.builder();
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            if (token.getPrincipal() instanceof User) {
                User user = (User) token.getPrincipal();
                loginSuccessBuilder.user(user);
                builder.append("\nUsername is ").append(user.getUsername());
                builder.append("\nUser ROLEs are ");
                builder.append(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")));
            }
            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) token.getDetails();
                String remoteAddress = webAuthenticationDetails.getRemoteAddress();
                builder.append("\nRemote IP: ").append(remoteAddress);
                builder.append("\nSession ID: ").append(webAuthenticationDetails.getSessionId());
                loginSuccessBuilder.sourceIp(remoteAddress);
            }
            LoginSuccess loginSuccess = loginSuccessRepository.save(loginSuccessBuilder.build());
            builder.append("\nLogin success saved with id: ").append(loginSuccess.getId());
        }
        log.debug(builder.toString());

    }
}
