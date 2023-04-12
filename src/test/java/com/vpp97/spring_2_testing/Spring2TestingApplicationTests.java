package com.vpp97.spring_2_testing;

import com.vpp97.spring_2_testing.data.Data;
import com.vpp97.spring_2_testing.exceptions.NotEnoughMoneyException;
import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.Bank;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import com.vpp97.spring_2_testing.repositories.BankRepository;
import com.vpp97.spring_2_testing.services.AccountService;
import com.vpp97.spring_2_testing.services.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class Spring2TestingApplicationTests {
    @MockBean
    AccountRepository accountRepository;
    @MockBean
    BankRepository bankRepository;
    @Autowired
    AccountService accountService;
    //    AccountServiceImpl accountService;

    @BeforeEach
    void setup_dependencies(){
    }

    @Test
    @DisplayName("Test money transfer")
    void test_money_transfer() {
        when(accountRepository.findById(1L)).thenReturn(Data.createAccount001());
        when(accountRepository.findById(2L)).thenReturn(Data.createAccount002());
        when(bankRepository.findById(1L)).thenReturn(Data.createBank());

        BigDecimal sourceBalance = accountService.checkBalance(1L);
        BigDecimal targetBalance = accountService.checkBalance(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        accountService.transfer(1L, 1L, 2L, new BigDecimal("300"));

        sourceBalance = accountService.checkBalance(1L);
        targetBalance = accountService.checkBalance(2L);

        assertEquals("700", sourceBalance.toPlainString());
        assertEquals("2300", targetBalance.toPlainString());

        int totalTransfers = accountService.checkTotalTransfers(1L);
        assertEquals(1, totalTransfers);

        verify(accountRepository, times(3)).findById(1L);
        verify(accountRepository, times(3)).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));

        verify(bankRepository, times(2)).findById(1L);
        verify(bankRepository).save(any(Bank.class));

        verify(accountRepository, times(6)).findById(anyLong());
        verify(accountRepository, never()).findAll();
    }

    @Test
    @DisplayName("Test when there is no enough money")
    void test_when_money_isnt_enough(){
        when(accountRepository.findById(1L)).thenReturn(Data.createAccount001());
        when(accountRepository.findById(2L)).thenReturn(Data.createAccount002());
        when(bankRepository.findById(1L)).thenReturn(Data.createBank());

        BigDecimal sourceBalance = accountService.checkBalance(1L);
        BigDecimal targetBalance = accountService.checkBalance(2L);

        assertEquals("1000", sourceBalance.toPlainString());
        assertEquals("2000", targetBalance.toPlainString());

        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
            accountService.transfer(1L, 1L, 2L, new BigDecimal("3000"));
        });

        String message = exception.getMessage();

        assertEquals("Not enough money on this account", message);

        verify(bankRepository, never()).save(any(Bank.class));
        verify(bankRepository, never()).findById(1L);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Test to use assert Same")
    void test_with_assert_same() {
        when(accountRepository.findById(1L)).thenReturn(Data.createAccount001());
        Account c1 = accountService.findById(1L);
        Account c2 = accountService.findById(1L);

        assertSame(c1, c2);
        assertTrue(c1 == c2);
        assertEquals("Victor", c1.getOwnerName());
        assertEquals("Victor", c2.getOwnerName());

        verify(accountRepository, times(2)).findById(1L);

    }

    @Test
    @DisplayName("Test find all service")
    void test_find_all_service() {
        List<Account> accountsMocked = Arrays.asList(
                Data.createAccount001().orElseThrow(),
                Data.createAccount002().orElseThrow()
        );

        when(accountRepository.findAll()).thenReturn(accountsMocked);

        List<Account> accounts = accountService.findAll();

        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());

        verify(accountRepository).findAll();
    }


}
