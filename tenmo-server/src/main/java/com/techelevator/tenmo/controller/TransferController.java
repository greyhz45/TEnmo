package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    private AccountDao accountDao;
    public TransferController(TransferDao transferDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }

    /*@GetMapping("/{id}")
    public Transfer getTransferById(@PathVariable Long transferId) {
        return transferDao.getTransfer(transferId);
    }*/

    @GetMapping("/{transferId}")
    public Transfer listTransferByTransferId(@PathVariable Long transferId) {

        return transferDao.getTransfer(transferId);
    }

    @GetMapping("/users/{userId}")
    public List<Transfer> getTransfersByUserId(@PathVariable Long userId) {

        return transferDao.listAllRelatedTransfers(userId);
    }

    @PostMapping("")
    public Transfer createNewTransfer(@RequestBody Transfer transfer) {

        return transferDao.createTransfer(transfer);
    }

    @PutMapping("/")
    public void updateTransfer(@RequestBody Transfer transfer) {

        transferDao.updateTransfer(transfer);
    }

    @DeleteMapping("/{transferId}")
    public void deleteTransfer(@PathVariable Long transferId) {

        transferDao.deleteTransfer(transferId);
    }

    @RequestMapping(value = "/send/{userId}", method = {RequestMethod.PUT, RequestMethod.PUT, RequestMethod.POST})
    public Transfer createSendTransfer(@PathVariable Long userId, @RequestBody TransferDTO transferDTO) {

        //update account balances for sender and receiver
        accountDao.updateSenderForSendTran(userId, transferDTO.getAmount());
        accountDao.updateReceiverForSendTran(transferDTO.getReceiverId(), transferDTO.getAmount());
        //create new transfer record
        return transferDao.createSendTran(transferDTO, userId);
    }

}
