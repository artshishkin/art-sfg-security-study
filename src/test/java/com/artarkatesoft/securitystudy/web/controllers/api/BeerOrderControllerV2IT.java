package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.domain.Beer;
import com.artarkatesoft.securitystudy.domain.BeerOrder;
import com.artarkatesoft.securitystudy.domain.Customer;
import com.artarkatesoft.securitystudy.repositories.BeerRepository;
import com.artarkatesoft.securitystudy.repositories.CustomerRepository;
import com.artarkatesoft.securitystudy.web.controllers.BaseIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.artarkatesoft.securitystudy.bootstrap.DefaultBreweryLoader.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerV2IT extends BaseIT {

    public static final String API_ROOT = "/api/v2/orders";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Customer stPeteCustomer;
    private Customer dunedinCustomer;
    private Customer keyWestCustomer;
    private List<Beer> loadedBeers;


    @BeforeEach
    void setUp() {
        stPeteCustomer = customerRepository.findByCustomerName(ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findByCustomerName(DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findByCustomerName(KEY_WEST_DISTRIBUTORS).orElseThrow();

        loadedBeers = beerRepository.findAll();


    }

    static Stream<Arguments> urlTemplateStream() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, API_ROOT)
//                Arguments.of(HttpMethod.GET, API_ROOT + "/orders/{orderId}"),
//                Arguments.of(HttpMethod.PUT, API_ROOT + "/orders/{orderId}/pickup")
        );
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Not authenticated user should NOT have access to All Order List and Order by Id")
    void getOrPut_OrdersOrByIdNotAuth(HttpMethod httpMethod, String endpointUrlTemplate) throws Exception {
        //given
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();

        //when
        mockMvc
                .perform(
                        request(httpMethod, endpointUrlTemplate, testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("art")
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Admin user should have access to All Order List and Order by Id")
    void getOrPut_OrdersOrByIdUserAdmin(HttpMethod httpMethod, String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        request(httpMethod, endpointUrlTemplate, testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(ST_PETE_USER)
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Customer user should have access ONLY to OWN Order List and Order by Id")
    void getOrPut_OrdersOrByIdUserAuthCustomer(HttpMethod httpMethod, String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        request(httpMethod, endpointUrlTemplate, testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(KEY_WEST_USER)
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Customer user should have access ONLY to OWN Order List and Order by Id")
    void getOrPut_OrdersOrByIdUserAuthCustomer2(HttpMethod httpMethod, String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        request(httpMethod, endpointUrlTemplate, testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }
}