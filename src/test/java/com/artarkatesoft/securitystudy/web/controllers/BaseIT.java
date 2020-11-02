package com.artarkatesoft.securitystudy.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestPropertySource(properties = {
        "app.security.max-attempts-to-lock-user=300"
})
public abstract class BaseIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
//                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }
}
