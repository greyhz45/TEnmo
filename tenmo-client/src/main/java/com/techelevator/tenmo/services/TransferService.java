package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TransferService {

    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;
    private String transferPath = "transfers/";
    private String userPath = "users/";

    public TransferService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Transfer[] getTransfersByUserId(Long userId) throws TransferServiceException {

        Transfer[] transfers = null;

        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + transferPath + userPath + userId, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException e) {
            throw new TransferServiceException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new TransferServiceException(e.getMessage());
        } catch (RestClientException e) {
            throw new TransferServiceException(e.getMessage());
        }

        return transfers;
    }

    public Transfer createTransfer(Transfer transfer) throws TransferServiceException {

        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        Transfer newTransfer = null;
        try {
            newTransfer = restTemplate.postForObject(baseUrl + transferPath, entity, Transfer.class);
        } catch (RestClientResponseException e) {
            throw new TransferServiceException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new TransferServiceException(e.getMessage());
        } catch (RestClientException e) {
            throw new TransferServiceException(e.getMessage());
        }

        return newTransfer;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
