package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransfer(Long transferId) {

        Transfer transfer = null;
        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, " +
                "t.account_from, t.account_to, t.amount, tt.transfer_type_desc, " +
                "ts.transfer_status_desc FROM transfers t " +
                "JOIN transfer_types tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE t.transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        if (rowSet.next()) {
            transfer = mapRowToTransfer(rowSet);
        }

        return transfer;
    }

    @Override
    public List<Transfer> listAllRelatedTransfers(Long userId) {

        List<Transfer> transfers = new ArrayList<>();
        SqlRowSet rowSet;

        Long accountId = getAccountId(userId);
        if (accountId != 0) {
            rowSet = listTransfersByAccountFrom(accountId);
            while (rowSet.next()) {
                transfers.add(mapRowToTransfer(rowSet));
            }
            rowSet = listTransfersByAccountTo(accountId);
            while (rowSet.next()) {
                transfers.add(mapRowToTransfer(rowSet));
            }
        }

        return transfers;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {

        Transfer newTransfer = new Transfer();
        /*newTransfer.setTransferTypeId(transfer.getTransferTypeId());
        newTransfer.setTransferStatusId(transfer.getTransferStatusId());*/
        newTransfer.setTransferTypeDesc(transfer.getTransferTypeDesc());
        newTransfer.setTransferStatusDesc(transfer.getTransferStatusDesc());
        newTransfer.setAccountFrom(transfer.getAccountFrom());
        newTransfer.setAccountTo(transfer.getAccountTo());
        newTransfer.setAmount(transfer.getAmount());
        String insertSql = "INSERT INTO transfers " +
                "(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES ((SELECT transfer_type_id FROM transfer_types tt WHERE tt.transfer_type_desc = ?), " +
                "(SELECT transfer_status_id FROM transfer_statuses ts WHERE ts.transfer_status_desc = ?), ?, ?, ?) " +
                "RETURNING transfer_id;";
        Long transferId = jdbcTemplate.queryForObject(insertSql, Long.class, newTransfer.getTransferTypeDesc(), newTransfer.getTransferStatusDesc(), newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());

        if (transferId != 0) {
            newTransfer.setTransferId(transferId);
        }

        return newTransfer;
    }

    @Override
    public void updateTransfer(Transfer transfer) {

        String updateSql = "UPDATE transfers " +
                "SET transfer_type_id = ?, " +
                "transfer_status_id = ?, " +
                "account_from = ?, " +
                "account_to = ?, " +
                "amount = ? " +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(updateSql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
    }

    @Override
    public void deleteTransfer(Long transferId) {

        String deleteSql = "DELETE FROM transfers " +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(deleteSql, transferId);
    }

    private SqlRowSet listTransfersByAccountFrom(Long accountFrom) {

        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, " +
                "t.account_from, t.account_to, t.amount, tt.transfer_type_desc, " +
                "ts.transfer_status_desc FROM transfers t " +
                "JOIN transfer_types tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE account_from = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountFrom);

        return rowSet;
    }

    private SqlRowSet listTransfersByAccountTo(Long accountTo) {

        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, " +
                "t.account_from, t.account_to, t.amount, tt.transfer_type_desc, " +
                "ts.transfer_status_desc FROM transfers t " +
                "JOIN transfer_types tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE account_to = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountTo);

        return rowSet;
    }

    private Long getAccountId(Long userId) {

        String sql = "SELECT account_id FROM accounts " +
                "WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(sql, Long.class, userId);
    }

    @Override
    public Transfer createSendTran(TransferDTO transferDTO, Long userId) {

        Transfer transfer = new Transfer();
        //populate new transfer record
        transfer.setTransferTypeDesc("Send");
        transfer.setTransferStatusDesc("Approved");
        transfer.setAccountFrom(getAccountId(userId));
        transfer.setAccountTo(getAccountId(transferDTO.getReceiverId()));
        transfer.setAmount(transferDTO.getAmount());
        return createTransfer(transfer);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {

        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setTransferTypeId(rs.getLong("transfer_type_id"));
        transfer.setTransferStatusId(rs.getLong("transfer_status_id"));
        transfer.setAccountFrom(rs.getLong("account_from"));
        transfer.setAccountTo(rs.getLong("account_to"));
        transfer.setAmount(rs.getDouble("amount"));
        transfer.setTransferTypeDesc(rs.getString("transfer_type_desc"));
        transfer.setTransferStatusDesc(rs.getString("transfer_status_desc"));

        return transfer;
    }
}
