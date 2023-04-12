package com.vpp97.spring_2_testing.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpp97.spring_2_testing.data.Data;
import com.vpp97.spring_2_testing.models.DataTransferDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;


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
    void test_transfer() throws JsonProcessingException {
        DataTransferDto dto = Data.createDataTransferDto();
        Map<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("date", LocalDate.now().toString());
        expectedResponse.put("message", "Transfer done successfully");
        expectedResponse.put("detail", dto);


        webTestClient.post()
                .uri("http://localhost:9000/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transfer done successfully"))
                .jsonPath("$.message").value(value -> {
                    assertEquals("Transfer done successfully", value);
                })
                .jsonPath("$.detail.sourceAccountId").isEqualTo(dto.getSourceAccountId())
                .json(mapper.writeValueAsString(expectedResponse));

    }
}