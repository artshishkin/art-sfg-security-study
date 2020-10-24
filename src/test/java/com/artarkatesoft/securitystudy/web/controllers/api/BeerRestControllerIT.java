package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.services.BeerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerRestController.class)
class BeerRestControllerIT {

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void findBeers() throws Exception {
        //when
        mockMvc.perform(get("/api/v1/beer"))

                //then
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        //when
        mockMvc.perform(get("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f"))

                //then
                .andExpect(status().isOk());
    }

    @Test
    void findByUpc() throws Exception {
        //when
        mockMvc.perform(get("/api/v1/beerUpc/0123456789"))

                //then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When delete beer by ID with WRONG credentials (may be met in LEGACY systems)")
    void deleteById_fail() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .header("Api-Key", "art")
                        .header("Api-Secret", "1234"))
                //then
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("When delete beer by ID with CORRECT credentials (may be met in LEGACY systems)")
    void deleteById_ok() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .header("Api-Key", "art")
                        .header("Api-Secret", "123"))
                //then
                .andExpect(status().isOk());
    }
}