package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private List<Transfer> transfers = null;

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransfer(Long transferId) {

        Transfer transfer = null;
        String sql = "SELECT * FROM transfers " +
                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        if (rowSet.next()) {
            transfer = mapRowToTransfer(rowSet);
        }

        return transfer;
    }

    @Override
    public List<Transfer> listAllRelatedTransfers(Long userId) {

        Long accountId = getAccountId(userId);
        if (accountId != 0) {
            listTransfersByAccountTo(accountId);
            listTransfersByAccountFrom(accountId);
        }

        return transfers;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {

        Transfer newTransfer = null;
        newTransfer.setTransferTypeId(transfer.getTransferTypeId());
        newTransfer.setTransferStatusId(transfer.getTransferStatusId());
        newTransfer.setAccountFrom(transfer.getAccountFrom());
        newTransfer.setAccountTo(transfer.getAccountTo());
        newTransfer.setAmount(transfer.getAmount());
        String insertSql = "INSERT INTO transfers " +
                "(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "RETURNING transfer_id;";
        Long transferId = jdbcTemplate.queryForObject(insertSql, Long.class, newTransfer.getTransferTypeId(), newTransfer.getTransferStatusId(), newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());

        if (transferId != 0) {
            newTransfer.setTransferId(transferId);
        }

        return newTransfer;
    }

    @Override
    public void updateTransfer(Transfer transfer) {

    }

    @Override
    public void deleteTransfer(Long transferId) {

    }

    private void listTransfersByAccountFrom(Long accountFrom) {

        String sql = "SELECT * FROM transfers " +
                "WHERE account_from = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountFrom);
        while (rowSet.next()) {
            transfers.add(mapRowToTransfer(rowSet));
        }
    }

    private void listTransfersByAccountTo(Long accountTo) {

        String sql = "SELECT * FROM transfers " +
                "WHERE account_to = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountTo);
        while (rowSet.next()) {
            transfers.add(mapRowToTransfer(rowSet));
        }
    }

    private Long getAccountId(Long userId) {

        String sql = "SELECT account_id FROM accounts " +
                "WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(sql, Long.class, userId);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {

        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setTransferTypeId(rs.getLong("transfer_type_id"));
        transfer.setTransferStatusId(rs.getLong("transfer_status_id"));
        transfer.setAccountFrom(rs.getLong("account_from"));
        transfer.setAccountTo(rs.getLong("account_to"));
        transfer.setAmount(rs.getDouble("amount"));
        return transfer;
    }
}
