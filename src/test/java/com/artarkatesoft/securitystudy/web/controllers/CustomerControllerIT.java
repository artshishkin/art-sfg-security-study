package com.artarkatesoft.securitystudy.web.controllers;

import com.artarkatesoft.securitystudy.domain.Customer;
import com.artarkatesoft.securitystudy.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CustomerControllerIT extends BaseIT {

    @Autowired
    CustomerRepository customerRepository;

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

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("urlUsersStream")
    @DisplayName("Only ADMIN or CUSTOMER users should HAVE access to Customers Endpoint")
    void securityAccessTest(String url, String username, String password, HttpStatus httpStatus) throws Exception {
        //given
        Customer customer = customerRepository.findAll().get(0);
        String urlParameter = customer.getId().toString();

        //when
        mockMvc.perform(
                get(url, urlParameter)
                        .with("".equals(username) ? anonymous() : httpBasic(username, password)))
                //then
                .andExpect(status().is(httpStatus.value()));
    }


    static Stream<Arguments> urlUsersStream() {
        return Stream
//                .of("/customers", "/customers/find", "/customers/{customerId}", "/customers/new","/customers/{customerId}/edit")
                .of("/customers")
                .flatMap(url -> Stream
                        .of(
                                Arguments.of(url, "art", "123", HttpStatus.OK),
                                Arguments.of(url, "secondUser", "pass222", HttpStatus.FORBIDDEN),
                                Arguments.of(url, "scott", "tiger", HttpStatus.OK),
                                Arguments.of(url, "foo", "buzz", HttpStatus.UNAUTHORIZED),
                                Arguments.of(url, "", "", HttpStatus.UNAUTHORIZED)
                        )
                );
    }

    @Nested
    @DisplayName("Add Customers")
    class AddCustomers {

        @Rollback
        @ParameterizedTest
        @MethodSource("com.artarkatesoft.securitystudy.web.controllers.CustomerControllerIT#addCustomersStream")
        @DisplayName("New Customer Creation is allowed to ADMIN only")
        void processCreationForm(String url, String username, String password, HttpStatus httpStatus) throws Exception {
            //given
            Customer customer = customerRepository.findAll().get(0);
            String urlParameter = customer.getId().toString();

            //when
            mockMvc.perform(
                    post(url, urlParameter)
                            .param("customerName", "Foo Customer")
                            .with("".equals(username) ? anonymous() : httpBasic(username, password)))
                    //then
                    .andExpect(status().is(httpStatus.value()));
        }
    }

    static Stream<Arguments> addCustomersStream() {
        return Stream
//                .of("/customers", "/customers/find", "/customers/{customerId}", "/customers/new","/customers/{customerId}/edit")
                .of("/customers/new")
                .flatMap(url -> Stream
                        .of(
                                Arguments.of(url, "art", "123", HttpStatus.FOUND),
                                Arguments.of(url, "secondUser", "pass222", HttpStatus.FORBIDDEN),
                                Arguments.of(url, "scott", "tiger", HttpStatus.FORBIDDEN),
                                Arguments.of(url, "foo", "buzz", HttpStatus.UNAUTHORIZED),
                                Arguments.of(url, "", "", HttpStatus.UNAUTHORIZED)
                        )
                );
    }

}