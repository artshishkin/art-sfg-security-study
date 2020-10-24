package com.artarkatesoft.securitystudy.web.controllers;

import com.artarkatesoft.securitystudy.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerIT {

    @MockBean
    CustomerRepository customerRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Entering CORRECT username and password BUT the method of REST endpoint should NOT authorize user")
    void findAll_withRestHeaderAuthFilter() throws Exception {
        //when
        mockMvc.perform(
                get("/customers/find")
                        .with(anonymous())
                        .header("Api-Key", "art")
                        .header("Api-Secret", "123"))
                //then
                .andExpect(status().isUnauthorized());
    }

}