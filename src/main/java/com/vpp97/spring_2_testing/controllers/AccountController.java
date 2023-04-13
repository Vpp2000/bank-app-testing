package com.vpp97.spring_2_testing.controllers;

import com.vpp97.spring_2_testing.models.Account;
import com.vpp97.spring_2_testing.models.DataTransferDto;
import com.vpp97.spring_2_testing.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("{accountId}")
    public ResponseEntity<Account> findById(@PathVariable("accountId") Long accountId){
        try {
            Account account = accountService.findById(accountId);
            return ResponseEntity.ok(account);
        } catch (NoSuchElementException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("transfer")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody DataTransferDto dto){
        accountService.transfer(
                dto.getBankId(),
                dto.getSourceAccountId(),
                dto.getDestinationAccountId(),
                dto.getAmount());
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("date", LocalDate.now().toString());
        response.put("message", "Transfer done successfully");
        response.put("detail", dto);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Account> findAll(){
        return accountService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account save(@RequestBody Account account){
        return accountService.save(account);
    }

    @DeleteMapping("{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("accountId") Long accountId){
        accountService.deleteByID(accountId);
    }

}
