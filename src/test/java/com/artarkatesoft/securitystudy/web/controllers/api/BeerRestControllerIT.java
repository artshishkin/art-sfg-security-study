package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.services.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(BeerRestController.class)
@SpringBootTest
class BeerRestControllerIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    BeerService beerService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

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
    @DisplayName("DELETE beer by ID with WRONG credentials in HEADERS (may be met in LEGACY systems)")
    void deleteById_wrongCredentials() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .header("Api-Key", "art")
                        .header("Api-Secret", "BLA_BLA_BLA"))
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE beer by ID with NO auth")
    void deleteById_fail() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE beer by ID with CORRECT credentials in HEADERS (may be met in LEGACY systems)")
    void deleteById_ok() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .header("Api-Key", "art")
                        .header("Api-Secret", "123"))
                //then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE beer by ID with CORRECT credentials and Basic Auth")
    void deleteById_httpBasic() throws Exception {
        //when
        mockMvc
                .perform(
                        delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                                .with(httpBasic("art", "123")))
                //then
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @ParameterizedTest
    @CsvSource({
            "secondUser,pass222",
            "scott,tiger"
    })
    @DisplayName("DELETE beer by ID with CORRECT credentials and Basic Auth but NOT Allowed by Authority (not ROLE_ADMIN)")
    void deleteById_httpBasic_roleUserOrCustomer(String username, String password) throws Exception {
        //when
        mockMvc
                .perform(
                        delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                                .with(httpBasic(username, password)))
                //then
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE beer by ID with WRONG credentials in PARAMETERS (may be met in LEGACY systems)")
    void deleteById_wrongCredentialsInParametersUsingURL() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f?Api-Key=art&Api-Secret=BLA_BLA_BLA"))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE beer by ID with CORRECT credentials in PARAMETERS (may be met in LEGACY systems)")
    void deleteById_keyInParametersUsingURL() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f?Api-Key=art&Api-Secret=123"))

                //then
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("DELETE beer by ID with WRONG credentials in PARAMETERS (may be met in LEGACY systems)")
    void deleteById_wrongCredentialsInParameters() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .param("Api-Key", "art")
                        .param("Api-Secret", "BLA_BLA_BLA"))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE beer by ID with CORRECT credentials in PARAMETERS (may be met in LEGACY systems)")
    void deleteById_keyInParameters() throws Exception {
        //when
        mockMvc.perform(
                delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .param("Api-Key", "art")
                        .param("Api-Secret", "123"))
                //then
                .andExpect(status().isOk());
    }


}