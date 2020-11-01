package com.artarkatesoft.securitystudy.web.controllers;

import com.artarkatesoft.securitystudy.domain.security.User;
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

    @GetMapping("register2fa")
    public String register2fa(Model model) {

        User user = getUser();
        GoogleAuthenticatorKey googleAuthKey = googleAuthenticator.createCredentials(user.getUsername());
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("ArtArKateSoft", user.getUsername(), googleAuthKey );

        log.debug("Google QR Code URL: {}", url);

        model.addAttribute("googleurl", url);
        return "user/register2fa";
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("register2fa")
    public String confirm2fa(@RequestParam Integer verifyCode) {
        log.debug("Verify Code {}", verifyCode);
        return "index";
    }
}
