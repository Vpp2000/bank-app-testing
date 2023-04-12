package com.vpp97.spring_2_testing.services;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.Bank;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import com.vpp97.spring_2_testing.repositories.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public int checkTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();
        return bank.getTotalTransfers();
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal checkBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getBalance();
    }

    @Transactional
    @Override
    public void transfer(Long bankId, Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        Account sourceAccount = accountRepository.findById(sourceAccountId).orElseThrow();
        sourceAccount.debit(amount);
        accountRepository.save(sourceAccount);

        Account destinationAccount = accountRepository.findById(destinationAccountId).orElseThrow();
        destinationAccount.credit(amount);
        accountRepository.save(destinationAccount);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.save(bank);

    }
}
