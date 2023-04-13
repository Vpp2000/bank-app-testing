package com.vpp97.spring_2_testing.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpp97.spring_2_testing.data.Data;
import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.DataTransferDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerWebTestClientTest {
    @Autowired
    private WebTestClient webTestClient;

    private ObjectMapper mapper;


    @BeforeEach
    void setup() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void test_transfer() throws JsonProcessingException {
        DataTransferDto dto = Data.createDataTransferDto();
        Map<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("date", LocalDate.now().toString());
        expectedResponse.put("message", "Transfer done successfully");
        expectedResponse.put("detail", dto);


        webTestClient.post()
                .uri("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody() // Por defecto nos da en un genérico un array de bytes
                .consumeWith(res -> {
                    try {
                        System.out.println(res.getResponseBody().getClass());
                        JsonNode json = mapper.readTree(res.getResponseBody());  // para manipular el array de bytes
                        assertEquals("Transfer done successfully", json.path("message").asText());
                        assertEquals(1L, json.path("detail").path("sourceAccountId").asLong());
                        assertEquals("10", json.path("detail").path("amount").asText());
                        assertNotNull(json.path("date").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transfer done successfully"))
                .jsonPath("$.message").value(value -> {
                    assertEquals("Transfer done successfully", value);
                })
                .jsonPath("$.detail.sourceAccountId").isEqualTo(dto.getSourceAccountId())
                .json(mapper.writeValueAsString(expectedResponse));

    }


    @Test
    @Order(2)
    void test_find_account_by_id(){
        webTestClient.get()
                .uri("/api/accounts/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith((res) -> {
                    Account account = res.getResponseBody();
                    assertEquals("Victor", account.getOwnerName());
                    assertEquals("990.00", account.getBalance().toPlainString());
                });
    }

    @Test
    @Order(3)
    void test_find_all_accounts(){
        webTestClient.get().uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].ownerName").isEqualTo("Victor")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$").value(hasSize(3));
    }

    @Test
    @Order(4)
    void test_find_all_accounts_another_way(){
        webTestClient.get().uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith(res -> {
                    List<Account> accounts = res.getResponseBody();
                    assertEquals(3, accounts.size());
                    assertEquals("Victor", accounts.get(0).getOwnerName());
                    assertEquals(990, accounts.get(0).getBalance().intValue());
                })
                .hasSize(3);
    }

    @Test
    @Order(5)
    void test_save_account(){
        // given
        Account acount = Account.builder()
                .ownerName("Karim")
                .balance(new BigDecimal("1600"))
                .build();

        // when
        webTestClient.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(acount)
                .exchange()

        // then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(4)
                .jsonPath("$.ownerName").value(is("Karim"))
                .jsonPath("$.ownerName").isEqualTo("Karim");
    }


    @Test
    @Order(6)
    void test_save_account_with_consumer(){
        // given
        Account account = Account.builder()
                .ownerName("Sandro")
                .balance(new BigDecimal("1600"))
                .build();

        // when
        webTestClient.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()

                // then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(res -> {
                    Account acc = res.getResponseBody();
                    assertNotNull(acc);
                    assertEquals(5L, acc.getId());
                    assertEquals("Sandro", acc.getOwnerName());
                });
    }

    @Test
    @Order(7)
    void test_delete_account(){
        webTestClient.get()
                .uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Account.class).hasSize(5);

        webTestClient.delete()
                        .uri("/api/accounts/5")
                        .exchange()
                        .expectStatus().isNoContent()
                        .expectBody().isEmpty();

        webTestClient.get()
                .uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Account.class).hasSize(4);

        webTestClient.get()
                .uri("/api/accounts/5")
                .exchange()
                //.expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}