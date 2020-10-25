package com.artarkatesoft.securitystudy.security;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RestHeaderAuthFilter extends AbstractRestAuthFilter {

    public RestHeaderAuthFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String key = request.getHeader("Api-Key");
        return key == null ? "" : key;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String secret = request.getHeader("Api-Secret");
        return secret == null ? "" : secret;
    }
}
