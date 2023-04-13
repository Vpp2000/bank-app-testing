package com.vpp97.spring_2_testing;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.repositories.AccountRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Tag("integration_jpa")
public class IntegrationJpaTest {
    @Autowired
    AccountRepository accountRepository;

    @Nested
    class FindMethods {
        @Test
        void test_find_account_by_id(){
            Optional<Account> accountOptional = accountRepository.findById(1L);
            assertTrue(accountOptional.isPresent());
            assertEquals("Victor", accountOptional.get().getOwnerName());
        }

        @Test
        void test_find_account_by_owner_name(){
            Optional<Account> accountOptional = accountRepository.findByOwner("Andres");
            assertTrue(accountOptional.isPresent());
            assertEquals("Andres", accountOptional.get().getOwnerName());
        }

        @Test
        void test_find_account_by_owner_name_throws_exception(){
            Optional<Account> accountOptional = accountRepository.findByOwner("Samuel");
            assertThrows(NoSuchElementException.class, accountOptional::orElseThrow);
            assertFalse(accountOptional.isPresent());
        }

        @Test
        void test_find_all(){
            List<Account> accounts = accountRepository.findAll();
            assertFalse(accounts.isEmpty());
            assertTrue(accounts.size() == 3);
        }
    }

    @Nested
    class AlterMethodsTests {
        @Test
        void test_save_method(){
            Account account = Account.builder()
                    .ownerName("Andy")
                    .balance(new BigDecimal("1500"))
                    .build();

            Account accountCreated = accountRepository.save(account);

            List<Account> accounts = accountRepository.findAll();
            System.out.println("accounts size: " + accounts.size());

            assertEquals("Andy", accountCreated.getOwnerName());
            assertNotNull(accountCreated.getId());
        }

        @Test
        void test_update(){
            Account accountPepe = new Account(null, "Pepe", new BigDecimal("3000"));

            // When
            Account account = accountRepository.save(accountPepe);

            // Then
            assertEquals("Pepe", account.getOwnerName());
            assertEquals("3000", account.getBalance().toPlainString());

            // When
            account.setBalance(new BigDecimal("3800"));
            Account updatedAccount = accountRepository.save(account);

            // Then
            assertEquals("Pepe", updatedAccount.getOwnerName());
            assertEquals("3800", updatedAccount.getBalance().toPlainString());

        }

        @Test
        void test_delete_method(){
            Account account = accountRepository.findByOwner("Victor").orElseThrow();
            accountRepository.delete(account);

            List<Account> accounts = accountRepository.findAll();
            System.out.println("accounts size: " + accounts.size());

            assertThrows(NoSuchElementException.class, () -> accountRepository.findByOwner("Victor").orElseThrow());
        }
    }


}
