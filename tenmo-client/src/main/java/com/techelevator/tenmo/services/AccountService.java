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

public class AccountService {

    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Double getBalance(Long id) throws AccountServiceException {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "accounts/" + id, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException e) {
            //System.out.println("Error encountered.");
            throw new AccountServiceException(e.getMessage());
        } catch (ResourceAccessException e) {
            //String message = createLoginExceptionMessage(ex);
            throw new AccountServiceException(e.getMessage());
        } catch (RestClientException e) {
            //String message = createLoginExceptionMessage(ex);
            throw new AccountServiceException(e.getMessage());
        }
        return account.getBalance();
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
