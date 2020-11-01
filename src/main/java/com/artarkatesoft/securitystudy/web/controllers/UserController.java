package com.artarkatesoft.securitystudy.web.controllers;

import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("user")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final GoogleAuthenticator googleAuthenticator;
    private final UserRepository userRepository;

    @GetMapping("register2fa")
    public String register2fa(Model model) {

        User user = getUser();
        GoogleAuthenticatorKey googleAuthKey = googleAuthenticator.createCredentials(user.getUsername());
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("ArtArKateSoft", user.getUsername(), googleAuthKey);

        log.debug("Google QR Code URL: {}", url);

        model.addAttribute("googleurl", url);
        return "user/register2fa";
    }

    @PostMapping("register2fa")
    public String confirm2fa(@RequestParam Integer verifyCode) {

        log.debug("Entered Code is {}", verifyCode);

        User user = getUser();
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            user.setUserGoogle2fa(true);
            userRepository.save(user);
            return "/index";
        } else {
            //wrong code
            return "user/register2fa";
        }
    }

    @GetMapping("verify2fa")
    public String verify2fa(Model model) {
        return "user/verify2fa";
    }

    @PostMapping("verify2fa")
    public String verify2fa(@RequestParam Integer verifyCode) {

        log.debug("Entered Code is {}", verifyCode);

        User user = getUser();
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
//            ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2faRequired(false);
            user.setGoogle2faRequired(false);
            return "index";
        } else {
            //wrong code
            return "user/verify2fa";
        }
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
