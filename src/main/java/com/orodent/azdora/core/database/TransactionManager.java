package com.orodent.azdora.core.database;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private final Connection connection;

    public TransactionManager(Connection connection) {
        this.connection = connection;
    }

    public void inTransaction(Runnable work) {
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            work.run();

            connection.commit();

        } catch (Exception ex) {
            try { connection.rollback(); } catch (SQLException rollbackEx) { ex.addSuppressed(rollbackEx); }
            throw new RuntimeException("Errore transazione", ex);

        } finally {
            try { connection.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) {}
        }
    }
}
