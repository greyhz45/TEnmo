package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {

    Account getAccount(Long accountId);

    Account getAccountByUserId(Long userId);

    Account createAccount(Account account);

    void updateAccount(Account account, Long accountId);

    void deleteAccount(Long userId);
}
