package com.artarkatesoft.securitystudy.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BreweryControllerIT extends BaseIT{

    @ParameterizedTest
    @MethodSource("usersUrlSourceStream")
    @DisplayName("Customer HAS authority, but User, Admin and Anon has NO authority to Brewery endpoint")
    void brewerySecurityTest_differentRoles(String url, String username, String password, HttpStatus httpStatus) throws Exception {
        mockMvc
                .perform(
                        get(url)
                                .with(httpBasic(username, password)))
                .andExpect(status().is(httpStatus.value()));
    }

    static Stream<Arguments> usersUrlSourceStream() {
        return Stream
                .of("/breweries", "/breweries/index", "/breweries/index.html", "/breweries.html", "/api/v1/breweries")
                .flatMap(url -> Stream
                        .of(
                                Arguments.of(url, "art", "123", HttpStatus.OK),
                                Arguments.of(url, "secondUser", "pass222", HttpStatus.FORBIDDEN),
                                Arguments.of(url, "scott", "tiger", HttpStatus.OK),
                                Arguments.of(url, "foo", "buzz", HttpStatus.UNAUTHORIZED)
                        )
                );
    }


}