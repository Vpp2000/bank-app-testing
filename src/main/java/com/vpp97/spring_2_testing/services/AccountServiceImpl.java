package com.vpp97.spring_2_testing.services;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.Bank;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import com.vpp97.spring_2_testing.repositories.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new NoSuchElementException(String.format("Account with id %s not found", accountId)));
    }

    @Override
    @Transactional
    public void deleteByID(Long accountId) {
        accountRepository.deleteById(accountId);
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
        System.out.println(sourceAccount);
        sourceAccount.debit(amount);
        accountRepository.save(sourceAccount);
        System.out.println(sourceAccount);

        Account destinationAccount = accountRepository.findById(destinationAccountId).orElseThrow();
        destinationAccount.credit(amount);
        accountRepository.save(destinationAccount);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.save(bank);
    }


}
