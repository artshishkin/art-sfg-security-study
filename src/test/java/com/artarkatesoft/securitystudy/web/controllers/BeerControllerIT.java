/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.artarkatesoft.securitystudy.web.controllers;

import com.artarkatesoft.securitystudy.domain.Beer;
import com.artarkatesoft.securitystudy.repositories.BeerInventoryRepository;
import com.artarkatesoft.securitystudy.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    BeerRepository beerRepository;

    @MockBean
    BeerInventoryRepository beerInventoryRepository;

    List<Beer> beerList;
    UUID uuid;
    Beer beer;

    //    @Autowired
    MockMvc mockMvc;

    Page<Beer> beers;
    Page<Beer> pagedResponse;

    @BeforeEach
    void setUp() {
        beerList = new ArrayList<Beer>();
        beerList.add(Beer.builder().build());
        beerList.add(Beer.builder().build());
        pagedResponse = new PageImpl(beerList);

        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        uuid = UUID.fromString(id);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "art,123",
            "secondUser,pass222",
            "scott,tiger"
    })
    @DisplayName("Entering RIGHT users' credentials should allow access to beers endpoint")
    void initCreationForm(String username, String password) throws Exception {
        mockMvc.perform(
                get("/beers/new")
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @WithMockUser(username = "WTF")
    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
    }

    @Test
    @DisplayName("Entering WRONG username and password should produce 401 Unauthorized status code")
    void findBeersWithHttpBasic_wrongCredentials() throws Exception {
        mockMvc.perform(
                get("/beers/find")
                        .with(httpBasic("userFake", "passwordFake")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Entering CORRECT username and password should allow access")
    void findBeersWithHttpBasic_ok() throws Exception {
        mockMvc.perform(
                get("/beers/find")
                        .with(httpBasic("art", "123")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    @DisplayName("Entering CORRECT credentials through Http Header")
    void findBeersWithHttpBasic_okUsingHeader() throws Exception {
        String credentials = "art:123";
        String credentials64 = Base64.getEncoder().encodeToString(credentials.getBytes());

        mockMvc.perform(
                get("/beers/find")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials64))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    @DisplayName("Entering CORRECT credentials and using HttpHeaders.encodeBasicAuth")
    void findBeersWithHttpBasic_okUsingHeaderAndSpringFeatures() throws Exception {
        mockMvc.perform(
                get("/beers/find")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Basic " + HttpHeaders.encodeBasicAuth("art", "123", StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    @DisplayName("Accessing Find Beer Endpoint when permitAll")
    void findBeers_givenPermitAll() throws Exception {
        mockMvc.perform(get("/beers/find").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }


}