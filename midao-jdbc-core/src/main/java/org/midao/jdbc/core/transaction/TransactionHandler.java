/*
 * Copyright 2013 Zakhar Prykhoda
 *
 *    midao.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.midao.jdbc.core.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Transaction Handler definition.
 * Used by {@link org.midao.jdbc.core.service.QueryRunnerService} to handle Connection retrieval/initialization,
 * Transactions: Commit/Rollback, Modes and Isolation's.
 */
public interface TransactionHandler {

    /**
     * Creates new TransactionHandler instance.
     *
     * Might be removed in future
     *
     * @param conn SQL Connection
     * @return
     */
	public TransactionHandler newInstance(Connection conn);

    /**
     * Creates new TransactionHandler instance.
     *
     * Might be removed in future
     *
     * @param ds SQL DataSource
     * @return
     */
	public TransactionHandler newInstance(DataSource ds);

    /**
     * Allows switching on/off Manual Transaction mode.
     * This allows to group more that one operation into one Transaction
     * and allows commit/rollback transaction manually
     *
     * @param manualMode new Transaction Mode
     */
	public void setManualMode(boolean manualMode);

    /**
     * Returns current Transaction mode
     *
     * @return current Transaction mode
     */
	public boolean getManualMode();

    /**
     * Transaction Isolation level.
     *
     * For general information please look at Java Tutorials (docs.oracle.com/javase/tutorial/index.html)
     * JDBC basics / Using Transactions
     *
     * For detailed information please look at vendor JDBC driver description
     *
     * @param level Transaction Isolation level
     */
	public void setIsolationLevel(Integer level);

    /**
     * Returns current Transaction Isolation level
     *
     * @return current Transaction Isolation level
     */
	public Integer getIsolationLevel();

    /**
     * Returns wrapped(proxy) connection.
     * Proxy protects Connection from being closed as this is managed by Transaction Handler already and might create
     * exception if Connection would be closed outside it.
     *
     * In case you need to perform some unorthodox manipulation with it - it is allowed simply to be casted to
     * Implementation class.
     *
     * @return Proxy of SQL Connection
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public Connection getConnection() throws SQLException;

    /**
     * Informs Transaction Handler that connection is no longer used.
     * Actual close might not be performed as it depends on TransactionHandler mode.
     */
	public void closeConnection();

    /**
     * Commits current Transaction
     *
     * Usable only when {@link #setManualMode(boolean)} set as true
     *
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public void commit() throws SQLException;

    /**
     * Rollbacks current Transaction
     *
     * Usable only when {@link #setManualMode(boolean)} set as true
     *
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public void rollback() throws SQLException;

    /**
     * Creates an unnamed savepoint in the current transaction and returns the new Savepoint object that represents it.
     * {@link java.sql.Connection#setSavepoint()}
     *
     * Usable only when {@link #setManualMode(boolean)} set as true
     *
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public Savepoint setSavepoint() throws SQLException;

    /**
     * Creates a savepoint with the given name in the current transaction and returns the new Savepoint object that represents it.
     * {@link java.sql.Connection#setSavepoint(String)}
     *
     * Usable only when {@link #setManualMode(boolean)} set as true
     *
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public Savepoint setSavepoint(String name) throws SQLException;

    /**
     * Undoes all changes made after the given Savepoint object was set.
     * {@link java.sql.Connection#rollback(java.sql.Savepoint)}
     *
     * Usable only when {@link #setManualMode(boolean)} set as true
     *
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public void rollback(Savepoint savepoint) throws SQLException;

    /**
     *Removes the specified Savepoint and subsequent Savepoint objects from the current transaction.
     * {@link java.sql.Connection#releaseSavepoint(java.sql.Savepoint)}
     *
     * Usable only when {@link #setManualMode(boolean)} set as true
     *
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException;
	
}
