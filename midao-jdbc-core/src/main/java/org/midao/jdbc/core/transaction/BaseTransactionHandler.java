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

import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.exception.MidaoSQLException;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.transaction.model.DataSourceConnectionConfig;
import org.midao.jdbc.core.utils.MidaoUtils;
import org.midao.jdbc.core.wrappers.ConnectionProxy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Map;
import java.util.Properties;

/**
 * Base TransactionHandler implementation.
 * Is responsible for keeping session configuration and managing
 * connection.
 *
 * Is initialized with DataSource/Connection.
 *
 * Allows retrieval of Connection, but this connection is wrapped in Proxy and {@link java.sql.Connection#close()}
 * won't be invoked on it (unless it would be explicitly cast to Implementation class)
 */
public class BaseTransactionHandler implements TransactionHandler {
	
	private Connection conn;
	private final DataSource dataSource;
	private final DataSourceConnectionConfig dsConnectionConfig = new DataSourceConnectionConfig();
	
	private boolean manualMode = false;
	private Integer isolationLevel = null;

    /**
     * Creates new BaseTransactionHandler instance
     *
     * @param conn SQL Connection
     */
	public BaseTransactionHandler(Connection conn) {
		this.conn = conn;
		this.dataSource = null;
	}

    /**
     * Creates new BaseTransactionHandler instance
     *
     * @param ds SQL DataSource
     */
	public BaseTransactionHandler(DataSource ds) {
		this.conn = null;
		this.dataSource = ds;
	}

    /**
     * {@inheritDoc}
     */
	public TransactionHandler newInstance(Connection conn) {
		return new BaseTransactionHandler(conn);
	}

    /**
     * {@inheritDoc}
     */
	public TransactionHandler newInstance(DataSource ds) {
		return new BaseTransactionHandler(ds);
	}

    /**
     * {@inheritDoc}
     */
	public void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}

    /**
     * {@inheritDoc}
     */
	public boolean getManualMode() {
		return this.manualMode;
	}

    /**
     * {@inheritDoc}
     */
	public void setIsolationLevel(Integer level) {
		this.isolationLevel = level;
	}

    /**
     * {@inheritDoc}
     */
	public Integer getIsolationLevel() {
		return this.isolationLevel;
	}

    /**
     * {@inheritDoc}
     */
	public Connection getConnection() throws SQLException {
		Connection activeConn = null;
		
		if (this.getDataSource() != null) {
			
			// checking if we can reuse last connection
			if (this.conn == null) {
				this.conn = this.getDataSource().getConnection();
			}
			
			if (this.conn != null) {
				initConnection(this.conn);
			}
			
		}
		
		if (this.conn == null) {
			throw new SQLException("Null connection");
		}
		
		if (this.conn.getAutoCommit() != false) {
			this.conn.setAutoCommit(false);
		}
		
		if (this.isolationLevel != null) {
			if (this.conn.getTransactionIsolation() != this.isolationLevel.intValue()) {
				this.conn.setTransactionIsolation(this.isolationLevel);
			}
		}
		
		activeConn = this.conn;
		
		return ConnectionProxy.newInstance(activeConn);
	}

    /**
     * {@inheritDoc}
     */
	public void closeConnection() {
		if (this.manualMode == false && this.dataSource != null) {
			MidaoUtils.closeQuietly(this.conn);
			
			this.conn = null;
		}
	}

    /**
     * {@inheritDoc}
     */
	public void commit() throws SQLException {
		
		if (this.conn != null) {
			this.conn.commit();
		}
		
		if (this.manualMode == true && this.dataSource != null) {
			MidaoUtils.closeQuietly(this.conn);
			
			this.conn = null;
		}
	}

    /**
     * {@inheritDoc}
     */
	public void rollback() throws SQLException {
		
		if (this.conn != null) {
			this.conn.rollback();
		}
		
		if (this.manualMode == true && this.dataSource != null) {
			MidaoUtils.closeQuietly(this.conn);
			
			this.conn = null;
		}
	}

    /**
     * {@inheritDoc}
     */
	public Savepoint setSavepoint() throws SQLException {
		return this.conn.setSavepoint();
	}

    /**
     * {@inheritDoc}
     */
	public Savepoint setSavepoint(String name) throws SQLException {
		return this.conn.setSavepoint(name);
	}

    /**
     * {@inheritDoc}
     */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.conn.releaseSavepoint(savepoint);
	}

    /**
     * {@inheritDoc}
     */
	public void rollback(Savepoint savepoint) throws SQLException {
        if (this.conn != null) {
		    this.conn.rollback(savepoint);
        }

        // unlike usual rollback - connection shouldn't be closed, as this is "partial" rollback
	}

    /**
     * Standard getter
     *
     * @return SQL DataSource instance (null if not assigned)
     */
	private DataSource getDataSource() {
		return dataSource;
	}

    /**
     * Initializes SQL Connection parameters.
     * Invoked everytime new Connection is requested from {@link org.midao.jdbc.core.transaction.BaseTransactionHandler#getConnection()}
     *
     * @param conn SQL Connection
     * @throws SQLException if exception would be thrown by Driver/Database
     */
	private void initConnection(Connection conn) throws SQLException {
		Boolean readOnly = this.dsConnectionConfig.getReadOnly();
		String catalog = this.dsConnectionConfig.getCatalog();
		Map<String, Class<?>> typeMap = this.dsConnectionConfig.getTypeMap();
		Integer holdability = this.dsConnectionConfig.getHoldability();
		Properties clientInfo = this.dsConnectionConfig.getClientInfo();

        try {
            if (readOnly != null) {
                if (conn.isReadOnly() != readOnly.booleanValue()) {
                    MappingUtils.invokeConnectionSetter(conn, "readOnly", readOnly);
                }
            }

            if (catalog != null) {
                if (catalog.equals(conn.getCatalog()) == false) {
                    MappingUtils.invokeConnectionSetter(conn, "catalog", catalog);
                }
            }

            if (typeMap != null) {
                MappingUtils.invokeConnectionSetter(conn, "typeMap", typeMap);
            }

            if (holdability != null) {
                if (conn.getHoldability() != holdability.intValue()) {
                    MappingUtils.invokeConnectionSetter(conn, "holdability", holdability);
                }
            }

            if (clientInfo.size() > 0) {
                MappingUtils.invokeConnectionSetter(conn, "clientInfo", clientInfo);
            }
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }
	}
	
}
