package com.vpp97.spring_2_testing.data;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.Bank;

import java.math.BigDecimal;
import java.util.Optional;

public class Data {
    public static Optional<Account> createAccount001() {
        return Optional.of(Account.builder()
                .id(1L)
                .balance(new BigDecimal("1000"))
                .ownerName("Victor")
                .build());
    }

    public static Optional<Account> createAccount002() {
        return Optional.of(Account.builder()
                .id(2L)
                .balance(new BigDecimal("2000"))
                .ownerName("Andres")
                .build());
    }

    public static Optional<Bank> createBank() {
        return Optional.of(Bank.builder()
                .id(1L)
                .name("Banco Financiero")
                .totalTransfers(0)
                .build());
    }
}
