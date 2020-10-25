package com.artarkatesoft.securitystudy.security;


import javax.servlet.http.HttpServletRequest;

public class RestUrlAuthFilter extends AbstractRestAuthFilter {
    public RestUrlAuthFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("Api-Key");
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter("Api-Secret");
    }
}
