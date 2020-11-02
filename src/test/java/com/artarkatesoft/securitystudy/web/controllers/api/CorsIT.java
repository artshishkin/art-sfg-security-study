package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CorsIT extends BaseIT {

    public static final String BEER_URL = "/api/v1/beer";

    @WithUserDetails("art")
    @Test
    void findBeersAUTH() throws Exception {
        //when
        mockMvc.perform(get(BEER_URL)
                .header(ORIGIN, "http://artarkatesoft.com"))

                //then
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }

    @ParameterizedTest
    @CsvSource({
            "/api/v1/beer,GET",
            "/api/v1/beer,POST",
            "/api/v1/beer/1234,PUT",
            "/api/v1/beer/1234,DELETE"
    })
    void endpointBeersPREFLIGHT(String url, String httpMethod) throws Exception {
        //when
        mockMvc.perform(options(url)
                .header(ORIGIN, "http://artarkatesoft.com")
                .header(ACCESS_CONTROL_REQUEST_METHOD, httpMethod))

                //then
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }
}
