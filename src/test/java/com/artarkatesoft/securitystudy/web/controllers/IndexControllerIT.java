package com.artarkatesoft.securitystudy.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class IndexControllerIT extends BaseIT{

    @Test
    void testGetIndexSlash() throws Exception {
        //when
        mockMvc.perform(get("/"))

                //then
                .andExpect(status().isOk());
    }
}