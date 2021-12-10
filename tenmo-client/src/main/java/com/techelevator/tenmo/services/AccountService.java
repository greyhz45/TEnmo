package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;
    private String accountsPath = "accounts/";

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Account getAccount(Long userId) throws AccountServiceException {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "accounts/u/" + userId, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException e) {
            throw new AccountServiceException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AccountServiceException(e.getMessage());
        } catch (RestClientException e) {
            throw new AccountServiceException(e.getMessage());
        }

        return account;
    }

    public Account getAccountByAccountId(Long accountId) throws AccountServiceException {

        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "accounts" + "?account_id=" + accountId, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException e) {
            throw new AccountServiceException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AccountServiceException(e.getMessage());
        } catch (RestClientException e) {
            throw new AccountServiceException(e.getMessage());
        }

        return account;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
