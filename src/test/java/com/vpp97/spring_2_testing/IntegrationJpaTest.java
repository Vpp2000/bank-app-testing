package com.vpp97.spring_2_testing;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    void test_find_account_by_id(){
        Optional<Account> accountOptional = this.accountRepository.findById(1L);
        assertTrue(accountOptional.isPresent());
        assertEquals("Victor", accountOptional.get().getOwnerName());
    }

    @Test
    void test_find_account_by_owner_name(){
        Optional<Account> accountOptional = this.accountRepository.findByOwner("Andres");
        assertTrue(accountOptional.isPresent());
        assertEquals("Andres", accountOptional.get().getOwnerName());
    }

    @Test
    void test_find_account_by_owner_name_throws_exception(){
        Optional<Account> accountOptional = this.accountRepository.findByOwner("Samuel");
        assertThrows(NoSuchElementException.class, accountOptional::orElseThrow);
        assertFalse(accountOptional.isPresent());
    }

    @Test
    void test_find_all(){
        List<Account> accounts = this.accountRepository.findAll();
        assertFalse(accounts.isEmpty());
        assertTrue(accounts.size() == 3);
    }
}
