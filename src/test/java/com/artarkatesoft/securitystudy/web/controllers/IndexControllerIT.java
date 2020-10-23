package com.artarkatesoft.securitystudy.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
class IndexControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testGetIndexSlash() throws Exception {
        //when
        mockMvc.perform(get("/"))

                //then
                .andExpect(status().isOk());
    }
}