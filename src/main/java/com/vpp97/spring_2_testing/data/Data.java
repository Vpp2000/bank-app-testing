package com.vpp97.spring_2_testing.data;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.Bank;

import java.math.BigDecimal;

public class Data {
    public static final Account ACCOUNT_001 = Account.builder()
            .id(1l)
            .balance(new BigDecimal("1000"))
            .ownerName("Victor")
            .build();

    public static final Account ACCOUNT_002 = Account.builder()
            .id(2L)
            .balance(new BigDecimal("2000"))
            .ownerName("Andres")
            .build();

    public static final Bank BANK = Bank.builder()
            .id(1L)
            .name("Banco Financiero")
            .totalTransfers(0)
            .build();

}
