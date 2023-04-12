package com.vpp97.spring_2_testing.repositories;

import com.vpp97.spring_2_testing.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    //List<Bank> findAll();
    //Optional<Bank> findById(Long bankId);
    //void update(Bank bank);
}
