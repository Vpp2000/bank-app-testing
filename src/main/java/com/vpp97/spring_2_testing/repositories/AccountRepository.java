package com.vpp97.spring_2_testing.repositories;

import com.vpp97.spring_2_testing.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    //List<Account> findAll();
    //Optional<Account> findById(Long accountId);
    //void update(Account account);
}
