package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @GetMapping("/{id}")
    public Transfer getTransferById(@PathVariable Long transferId) {
        return transferDao.getTransfer(transferId);
    }

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

}
