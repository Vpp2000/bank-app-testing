package com.vpp97.spring_2_testing.services;

import com.vpp97.spring_2_testing.models.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AccountService {
    Account findById(Long accountId);
    int checkTotalTransfers(Long bankId);
    BigDecimal checkBalance(Long accountId);
    void transfer(Long bankId, Long sourceAccountId, Long destinationAccountId, BigDecimal amount);
}
