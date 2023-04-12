package com.vpp97.spring_2_testing.services;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.Bank;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import com.vpp97.spring_2_testing.repositories.BankRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService{
    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    private  AccountRepository accountRepository;
    private  BankRepository bankRepository;

    @Override
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public int checkTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId);
        return bank.getTotalTransfers();
    }

    @Override
    public BigDecimal checkBalance(Long accountId) {
        Account account = accountRepository.findById(accountId);
        return account.getBalance();
    }

    @Override
    public void transfer(Long bankId, Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        Account sourceAccount = accountRepository.findById(sourceAccountId);
        sourceAccount.debit(amount);
        accountRepository.update(sourceAccount);

        Account destinationAccount = accountRepository.findById(destinationAccountId);
        destinationAccount.credit(amount);
        accountRepository.update(destinationAccount);

        Bank bank = bankRepository.findById(bankId);
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.update(bank);

    }
}
