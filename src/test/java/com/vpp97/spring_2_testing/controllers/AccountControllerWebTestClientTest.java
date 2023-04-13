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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

}