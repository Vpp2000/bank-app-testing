package com.vpp97.spring_2_testing;

import com.vpp97.spring_2_testing.data.Data;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import com.vpp97.spring_2_testing.repositories.BankRepository;
import com.vpp97.spring_2_testing.services.AccountService;
import com.vpp97.spring_2_testing.services.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class Spring2TestingApplicationTests {


    AccountRepository accountRepository;
    BankRepository bankRepository;
    AccountService accountService;

    @BeforeEach
    void setup_dependencies(){
        accountRepository = Mockito.mock(AccountRepository.class);
        bankRepository  = Mockito.mock(BankRepository.class);
        accountService = new AccountServiceImpl(accountRepository, bankRepository);
    }

    @Test
    @DisplayName("Test money transfer")
    void test_money_transfer() {
        when(accountRepository.findById(1L)).thenReturn(Data.ACCOUNT_001);
        when(accountRepository.findById(2L)).thenReturn(Data.ACCOUNT_002);
        when(bankRepository.findById(1L)).thenReturn(Data.BANK);

        BigDecimal sourceBalance = accountService.checkBalance(1L);
        BigDecimal targetBalance = accountService.checkBalance(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        accountService.transfer(1L, 1L, 2L, new BigDecimal("300"));

        sourceBalance = accountService.checkBalance(1L);
        targetBalance = accountService.checkBalance(2L);

        assertEquals("700", sourceBalance.toPlainString());
        assertEquals("2300", targetBalance.toPlainString());

    }
}
