package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer getTransfer(Long transferId);

    List<Transfer> listAllRelatedTransfers(Long userId);

    Transfer createTransfer(Transfer transfer);

    void updateTransfer(Transfer transfer);

    void deleteTransfer(Long transferId);
}
