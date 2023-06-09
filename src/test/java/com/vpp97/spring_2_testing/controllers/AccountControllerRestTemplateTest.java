package com.vpp97.spring_2_testing.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpp97.spring_2_testing.data.Data;
import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.DataTransferDto;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerRestTemplateTest {
    @Autowired
    private TestRestTemplate client;
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(){
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void test_transfer(){
        DataTransferDto dto = Data.createDataTransferDto();
        Map<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("date", LocalDate.now().toString());
        expectedResponse.put("message", "Transfer done successfully");
        expectedResponse.put("detail", dto);

        ResponseEntity<Map> response
                //= client.postForEntity("/api/accounts/transfer", dto, Map.class);
                = client.postForEntity(String.format("http://localhost:%s/api/accounts/transfer", this.port), dto, Map.class);
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(body.containsKey("detail"));
    }

    @Test
    @Order(2)
    void test_find_by_id(){
        ResponseEntity<Account> response = client.getForEntity("/api/accounts/1", Account.class);
        Account account = response.getBody();
        assertNotNull(account);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Victor", account.getOwnerName());
        assertEquals("990.00", account.getBalance().toPlainString() );
    }

    @Test
    @Order(3)
    void test_find_all() throws Exception {
        ResponseEntity<Account[]> response = client.getForEntity("/api/accounts", Account[].class);
        List<Account> accounts = new ArrayList<>();
        Collections.addAll(accounts, response.getBody());

        assertNotNull(accounts);
        assertEquals(3, accounts.size());
        assertEquals("Victor",accounts.get(0).getOwnerName());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
        assertEquals("Victor", json.get(0).path("ownerName").asText());
    }

    @Test
    @Order(4)
    void test_save_account() {
        Account newAccount = Account.builder()
                .ownerName("Ander")
                .balance(new BigDecimal("4500"))
                .build();

        ResponseEntity<Account> response = client.postForEntity("/api/accounts",newAccount, Account.class);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Account accountResponse = response.getBody();
        assertEquals(4L, accountResponse.getId());
        assertEquals("Ander",accountResponse.getOwnerName());
    }

    @Test
    @Order(5)
    void test_delete_account() {
        ResponseEntity<Account[]> response = client.getForEntity("/api/accounts", Account[].class);
        List<Account> accounts = new ArrayList<>();
        Collections.addAll(accounts, response.getBody());
        assertEquals(4, accounts.size());

        //client.delete("/api/accounts/1");
        //ResponseEntity<Void> exchange = client.exchange("/api/accounts/1", HttpMethod.DELETE, null, Void.class);
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("id", "1");
        ResponseEntity<Void> exchange = client.exchange("/api/accounts/{id}", HttpMethod.DELETE, null, Void.class, pathVars);

        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        ResponseEntity<Account[]> response2 = client.getForEntity("/api/accounts", Account[].class);
        accounts = new ArrayList<>();
        Collections.addAll(accounts, response2.getBody());
        assertEquals(3, accounts.size());

        ResponseEntity<Account> responseFinding = client.getForEntity("/api/accounts/1", Account.class);
        assertEquals(HttpStatus.NOT_FOUND, responseFinding.getStatusCode());
        assertFalse(responseFinding.hasBody());
    }

}