package com.artarkatesoft.securitystudy.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("register2fa")
    public String register2fa(Model model) {
        model.addAttribute("googleurl", "todo");
        return "user/register2fa";
    }

    @PostMapping("register2fa")
    public String confirm2fa(@RequestParam Integer verifyCode) {
        log.debug("Verify Code {}",verifyCode);
        return "index";
    }
}
