package com.artarkatesoft.securitystudy.web.controllers.api;

import com.artarkatesoft.securitystudy.bootstrap.DefaultBreweryLoader;
import com.artarkatesoft.securitystudy.domain.Beer;
import com.artarkatesoft.securitystudy.repositories.BeerRepository;
import com.artarkatesoft.securitystudy.web.controllers.BaseIT;
import com.artarkatesoft.securitystudy.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void findBeers() throws Exception {
        //when
        mockMvc.perform(get("/api/v1/beer"))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findById() throws Exception {
        //given
        UUID beerId = beerRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(get("/api/v1/beer/{beerId}", beerId))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByUpc() throws Exception {
        //when
        mockMvc.perform(get("/api/v1/beerUpc/{upc}", DefaultBreweryLoader.BEER_3_UPC))

                //then
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource
    @DisplayName("Only Authenticated users should HAVE access to Beer Rest Endpoint")
    void findTests(String url, String username, String password, HttpStatus httpStatus) throws Exception {
        //given
        Beer beer = beerRepository.findAll().get(0);
        String urlParameter = url.contains("beerUpc") ? beer.getUpc() : beer.getId().toString();

        //when
        mockMvc.perform(
                get(url, urlParameter)
                        .with(httpBasic(username, password)))
                //then
                .andExpect(status().is(httpStatus.value()));
    }

    static Stream<Arguments> findTests() {
        return Stream
                .of("/api/v1/beer", "/api/v1/beer/{beerId}", "/api/v1/beerUpc/{upc}")
                .flatMap(url -> Stream
                        .of(
                                Arguments.of(url, "art", "123", HttpStatus.OK),
                                Arguments.of(url, "secondUser", "pass222", HttpStatus.OK),
                                Arguments.of(url, "scott", "tiger", HttpStatus.OK),
                                Arguments.of(url, "foo", "buzz", HttpStatus.UNAUTHORIZED)
                        )
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/beer", "/api/v1/beer/{beerId}","/api/v1/beerUpc/{upc}"})
    @DisplayName("Anonymous users are not allowed to access Beer Endpoint")
    void findById_anonymous(String url) throws Exception {
        //given
        Beer beer = beerRepository.findAll().get(0);
        String urlParameter = url.contains("beerUpc") ? beer.getUpc() : beer.getId().toString();

        //when
        mockMvc.perform(get(url, urlParameter).with(anonymous()))
                //then
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Delete tests")
    @Nested
    class DeleteTests {

        private UUID testBeerId;
        Random random = new Random();

        @BeforeEach
        void setUp() {
            String beerTestUpc = String.valueOf(random.nextInt(99999999));

            Beer beerInTest = Beer.builder()
                    .beerName("Delete Beer")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(beerTestUpc)
                    .build();

            testBeerId = beerRepository.save(beerInTest).getId();
        }

        @Test
        @DisplayName("DELETE beer by ID with WRONG credentials in HEADERS (may be met in LEGACY systems)")
        void deleteById_wrongCredentials() throws Exception {
            //when
            mockMvc.perform(
                    delete("/api/v1/beer/{beerID}", testBeerId)
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
                    delete("/api/v1/beer/{beerId}", testBeerId))
                    //then
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE beer by ID with CORRECT credentials in HEADERS (may be met in LEGACY systems)")
        void deleteById_ok() throws Exception {
            //when
            mockMvc.perform(
                    delete("/api/v1/beer/{beerId}", testBeerId)
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
                            delete("/api/v1/beer/{beerId}", testBeerId)
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
                            delete("/api/v1/beer/{beerId}", testBeerId)
                                    .with(httpBasic(username, password)))
                    //then
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE beer by ID with WRONG credentials in PARAMETERS (may be met in LEGACY systems)")
        void deleteById_wrongCredentialsInParametersUsingURL() throws Exception {
            //when
            mockMvc.perform(
                    delete("/api/v1/beer/{beerId}?Api-Key=art&Api-Secret=BLA_BLA_BLA", testBeerId))

                    //then
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE beer by ID with CORRECT credentials in PARAMETERS (may be met in LEGACY systems)")
        void deleteById_keyInParametersUsingURL() throws Exception {
            //when
            mockMvc.perform(
                    delete("/api/v1/beer/{beerId}?Api-Key=art&Api-Secret=123", testBeerId))

                    //then
                    .andExpect(status().isOk());
        }


        @Test
        @DisplayName("DELETE beer by ID with WRONG credentials in PARAMETERS (may be met in LEGACY systems)")
        void deleteById_wrongCredentialsInParameters() throws Exception {
            //when
            mockMvc.perform(
                    delete("/api/v1/beer/{beerId}", testBeerId)
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
                    delete("/api/v1/beer/{beerId}", testBeerId)
                            .param("Api-Key", "art")
                            .param("Api-Secret", "123"))
                    //then
                    .andExpect(status().isOk());
        }

    }


}