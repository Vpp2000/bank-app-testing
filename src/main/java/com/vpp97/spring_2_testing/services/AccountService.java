package com.vpp97.spring_2_testing.services;

import com.vpp97.spring_2_testing.models.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> findAll();
    Account save(Account account);
    Account findById(Long accountId);
    int checkTotalTransfers(Long bankId);
    BigDecimal checkBalance(Long accountId);
    void transfer(Long bankId, Long sourceAccountId, Long destinationAccountId, BigDecimal amount);
}
