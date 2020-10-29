package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.domain.Beer;
import com.artarkatesoft.securitystudy.domain.BeerOrder;
import com.artarkatesoft.securitystudy.domain.Customer;
import com.artarkatesoft.securitystudy.repositories.BeerRepository;
import com.artarkatesoft.securitystudy.repositories.CustomerRepository;
import com.artarkatesoft.securitystudy.web.controllers.BaseIT;
import com.artarkatesoft.securitystudy.web.model.BeerOrderDto;
import com.artarkatesoft.securitystudy.web.model.BeerOrderLineDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.artarkatesoft.securitystudy.bootstrap.DefaultBreweryLoader.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerIT extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/{customerId}";

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

    @Test
    void createOrderNotAuth() throws Exception {
        //given
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        //when
        mockMvc
                .perform(
                        post(API_ROOT + "/orders", stPeteCustomer.getId())
                                .accept(APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderDto)))
                //then
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("art")
    @Test
    void createOrderUserAdmin() throws Exception {
        //given
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        //when
        mockMvc
                .perform(
                        post(API_ROOT + "/orders", stPeteCustomer.getId())
                                .accept(APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderDto)))
                //then
                .andExpect(status().isCreated());
    }

    @WithUserDetails(ST_PETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        //given
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        //when
        mockMvc
                .perform(
                        post(API_ROOT + "/orders", stPeteCustomer.getId())
                                .accept(APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderDto)))
                //then
                .andExpect(status().isCreated());
    }

    @WithUserDetails(KEY_WEST_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception {
        //given
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        //when
        mockMvc
                .perform(
                        post(API_ROOT + "/orders", stPeteCustomer.getId())
                                .accept(APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderDto)))
                //then
                .andExpect(status().isForbidden());
    }

    static Stream<Arguments> urlTemplateStream() {
        return Stream.of(
                Arguments.of(API_ROOT + "/orders"),
                Arguments.of(API_ROOT + "/orders/{orderId}")
        );
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Not authenticated user should NOT have access to All Order List and Order by Id")
    void getOrdersOrByIdNotAuth(String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        get(endpointUrlTemplate, stPeteCustomer.getId(), testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("art")
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Admin user should have access to All Order List and Order by Id")
    void getOrdersOrByIdUserAdmin(String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        get(endpointUrlTemplate, stPeteCustomer.getId(), testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Transactional
    @WithUserDetails(ST_PETE_USER)
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Customer user should have access ONLY to OWN Order List and Order by Id")
    void getOrdersOrByIdUserAuthCustomer(String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        get(endpointUrlTemplate, stPeteCustomer.getId(), testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Transactional
    @WithUserDetails(KEY_WEST_USER)
    @ParameterizedTest
    @MethodSource("urlTemplateStream")
    @DisplayName("Customer user should NOT have access to ANOTHER CUSTOMERS' Order List and Order by Id")
    void getOrdersOrByIdUserNotAuthCustomer(String endpointUrlTemplate) throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        UUID testBeerOrderId = beerOrder.getId();
        //when
        mockMvc
                .perform(
                        get(endpointUrlTemplate, stPeteCustomer.getId(), testBeerOrderId)
                                .accept(APPLICATION_JSON))
                //then
                .andExpect(status().isForbidden());
    }

    @Test
    @Disabled("Not implemented yet")
    void pickupOrderNotAuth() {
    }

    @Test
    @Disabled("Not implemented yet")
    void pickupOrderUserAdmin() {
    }

    @Test
    @Disabled("Not implemented yet")
    void pickupOrderUserAuthCustomer() {
    }

    @Test
    @Disabled("Not implemented yet")
    void pickupOrderUserNOTAuthCustomer() {
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());
        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://www.example.com")
                .beerOrderLines(orderLines)
                .build();
    }
}