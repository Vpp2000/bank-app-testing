package com.vpp97.spring_2_testing.repositories;

import com.vpp97.spring_2_testing.models.Account;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository {
    List<Account> findAll();
    Account findById(Long accountId);
    void update(Account account);
}
