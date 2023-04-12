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
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        verify(accountRepository, times(2)).update(any(Account.class));

        verify(bankRepository, times(2)).findById(1L);
        verify(bankRepository).update(any(Bank.class));

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

        verify(bankRepository, never()).update(any(Bank.class));
        verify(bankRepository, never()).findById(1L);

        verify(accountRepository, never()).update(any(Account.class));
    }
}
