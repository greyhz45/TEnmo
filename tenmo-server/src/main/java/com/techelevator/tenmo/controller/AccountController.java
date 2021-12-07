package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping("/u/{id}")// /u/ is unique endpoint to signal it is from a userId
    public Account getAccountByUserId(@PathVariable Long id) {

        return accountDao.getAccountByUserId(id);
    }

    @PostMapping("")
    public Account createAccount(@RequestBody Account account) {

        return accountDao.createAccount(account);
    }

    @PutMapping("")
    public void updateAccount(@RequestBody Account account) {

        accountDao.updateAccount(account);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long userId) {

        accountDao.deleteAccount(userId);
    }
}
