package com.vpp97.spring_2_testing.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpp97.spring_2_testing.data.Data;
import com.vpp97.spring_2_testing.models.DataTransferDto;
import com.vpp97.spring_2_testing.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }


    @Test
    void test_detail() throws Exception {
        // GIVEN
        when(accountService.findById(1L)).thenReturn(Data.createAccount001().orElseThrow());

        //WHEN
        mockMvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerName").value("Victor"));
    }

    @Test
    void test_transfer_controller() throws Exception {
        DataTransferDto dataTransferDto = Data.createDataTransferDto();

        Map<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("date", LocalDate.now().toString());
        expectedResponse.put("message", "Transfer done successfully");
        expectedResponse.put("detail", dataTransferDto);


        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataTransferDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transfer done successfully"))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }
}