package com.vpp97.spring_2_testing.repositories;

import com.vpp97.spring_2_testing.models.Bank;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository {
    List<Bank> findAll();
    Bank findById(Long bankId);
    void update(Bank bank);

}
