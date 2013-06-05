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

package org.midao.core;

import org.midao.core.exception.ExceptionUtils;
import org.midao.core.exception.MidaoException;
import org.midao.core.exception.MidaoRuntimeException;
import org.midao.core.exception.MidaoSQLException;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.OutputHandler;
import org.midao.core.handlers.output.RowCountOutputHandler;
import org.midao.core.handlers.type.TypeHandler;
import org.midao.core.handlers.utils.CallableUtils;
import org.midao.core.metadata.MetadataHandler;
import org.midao.core.service.QueryRunnerService;
import org.midao.core.statement.StatementHandler;
import org.midao.core.transaction.TransactionHandler;
import org.midao.core.utils.MidaoUtils;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

/**
 * Core of QueryRunner implementation.
 */
public abstract class AbstractQueryRunner implements QueryRunnerService {
	private static final String ERROR_SH_INIT_FAILED = "Error! Failed to initialize Statement Handler class. Please make sure there is public constructor which accepts Overrider class";
	private static final String ERROR_TyH_INIT_FAILED = "Error! Failed to initialize Type Handler class. Please make sure there is public constructor which accepts Overrider class";
	
	protected final Overrider overrider;
	
    private TypeHandler typeHandler;
    
    private TransactionHandler transactionHandler;
    
    private StatementHandler statementHandler;
    
    private MetadataHandler metadataHandler;

    /**
     * Creates new AbstractQueryRunner instance
     *
     * @param ds SQL DataSource
     */
    AbstractQueryRunner(DataSource ds) {
    	this(ds, null);
    }

    /**
     * Creates new AbstractQueryRunner instance
     *
     * @param conn SQL Connection
     */
    AbstractQueryRunner(Connection conn) {
    	this(null, conn);
    }

    /**
     * Creates new AbstractQueryRunner instance
     *
     * @param ds SQL DataSource
     * @param conn SQL Connection
     */
    AbstractQueryRunner(DataSource ds, Connection conn) {
    	this(ds, conn, null, null);
    }

    /**
     * Creates new AbstractQueryRunner instance
     *
     * @param ds SQL DataSource
     * @param conn SQL Connection
     * @param typeHandlerClazz TypeHandler implementation class (from which new TypeHandler instance would be created)
     * @param statementHandlerClazz StatementHandler implementation class (from which new StatementHandler instance would be created)
     */
    AbstractQueryRunner(DataSource ds, Connection conn, Class<? extends TypeHandler> typeHandlerClazz, Class<? extends StatementHandler> statementHandlerClazz) {
    	this.overrider = MidaoConfig.getDefaultOverrider();
    	
    	if (typeHandlerClazz != null) {
    		this.typeHandler = getTypeHandler(typeHandlerClazz);
    	} else {
    		this.typeHandler = MidaoConfig.getDefaultTypeHandler(overrider);
    	}
    	
    	if (statementHandlerClazz != null) {
    		this.statementHandler = getStatementHandler(statementHandlerClazz);
    	} else {
    		this.statementHandler = MidaoConfig.getDefaultStatementHandler(overrider);
    	}
        
        if (ds != null) {
        	this.transactionHandler = MidaoConfig.getDefaultTransactionHandler(ds);
        } else if (conn != null) {
        	this.transactionHandler = MidaoConfig.getDefaultTransactionHandler(conn);
        } else {
        	throw new MidaoRuntimeException("Either DataSource or Connection should be specified");
        }
        
        if (ds != null) {
        	this.metadataHandler = MidaoConfig.getDefaultMetadataHandler(ds);
        } else if (conn != null) {
        	this.metadataHandler = MidaoConfig.getDefaultMetadataHandler(conn);
        } else {
        	throw new MidaoRuntimeException("Either DataSource or Connection should be specified");
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTransactionManualMode() {
        return this.transactionHandler.getManualMode();
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionManualMode(boolean manualMode) {
        this.transactionHandler.setManualMode(manualMode);
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionIsolationLevel(Integer level) {
        this.transactionHandler.setIsolationLevel(level);
    }

    /**
     * {@inheritDoc}
     */
    public Integer getTransactionIsolationLevel() {
        return this.transactionHandler.getIsolationLevel();
    }

    /**
     * {@inheritDoc}
     */
    public void commit() throws SQLException {
        this.transactionHandler.commit();
    }

    /**
     * {@inheritDoc}
     */
    public void rollback() throws SQLException {
        this.transactionHandler.rollback();
    }

    /**
     * {@inheritDoc}
     */
    public Savepoint setSavepoint() throws SQLException {
        return this.transactionHandler.setSavepoint();
    }

    /**
     * {@inheritDoc}
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.transactionHandler.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        this.transactionHandler.rollback(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.transactionHandler.releaseSavepoint(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    public QueryRunnerService overrideOnce(String operation, Object value) {
        this.overrider.overrideOnce(operation, value);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public QueryRunnerService override(String operation, Object value) {
        this.overrider.override(operation, value);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public QueryRunnerService removeOverride(String operation) {
        this.overrider.removeOverride(operation);

        return this;
    }

    /**
     * Returns {@link TypeHandler} implementation assigned to this {@link QueryRunnerService} instance
     *
     * @return {@link TypeHandler} implementation assigned to this {@link QueryRunnerService} instance
     */
    public TypeHandler getTypeHandler() {
        return typeHandler;
    }

    /**
     * Assigns {@link TypeHandler} implementation to this {@link QueryRunnerService} instance
     * Please be aware that input {@link TypeHandler} should be share same {@link Overrider} instance:
     *
     * Example: QueryRunner.setTypeHandler(new BaseTypeHandler(queryRunner.getOverrider()));
     *
     * @param typeHandler {@link TypeHandler} implementation
     */
    public void setTypeHandler(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
    }

    /**
     * Returns {@link TransactionHandler} implementation assigned to this {@link QueryRunnerService} instance
     *
     * @return {@link TransactionHandler} implementation assigned to this {@link QueryRunnerService} instance
     */
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    /**
     * Assigns {@link TransactionHandler} implementation to this {@link QueryRunnerService} instance
     * Please be aware that input {@link TransactionHandler} should be share same {@link Overrider} instance:
     *
     * Example: QueryRunner.setTransactionHandler(new BaseTransactionHandler(queryRunner.getOverrider()));
     *
     * @param transactionHandler {@link TransactionHandler} implementation
     */
    public void setTransactionHandler(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    /**
     * Returns {@link StatementHandler} implementation assigned to this {@link QueryRunnerService} instance
     *
     * @return {@link StatementHandler} implementation assigned to this {@link QueryRunnerService} instance
     */
    public StatementHandler getStatementHandler() {
        return statementHandler;
    }

    /**
     * Assigns {@link StatementHandler} implementation to this {@link QueryRunnerService} instance
     * Please be aware that input {@link StatementHandler} should be share same {@link Overrider} instance:
     *
     * Example: QueryRunner.setStatementHandler(new BaseStatementHandler(queryRunner.getOverrider()));
     *
     * @param statementHandler {@link StatementHandler} implementation
     */
    public void setStatementHandler(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
    }

    /**
     * Returns {@link MetadataHandler} implementation assigned to this {@link QueryRunnerService} instance
     *
     * @return {@link MetadataHandler} implementation assigned to this {@link QueryRunnerService} instance
     */
    public MetadataHandler getMetadataHandler() {
        return metadataHandler;
    }

    /**
     * Assigns {@link MetadataHandler} implementation to this {@link QueryRunnerService} instance
     * Please be aware that input {@link MetadataHandler} should be share same {@link Overrider} instance:
     *
     * Example: QueryRunner.setMetadataHandler(new BaseMetadataHandler(queryRunner.getOverrider()));
     *
     * @param metadataHandler {@link MetadataHandler} implementation
     */
    public void setMetadataHandler(MetadataHandler metadataHandler) {
        this.metadataHandler = metadataHandler;
    }

    /**
     * Returns {@link Overrider} instance assigned to this {@link QueryRunnerService} instance
     *
     * @return {@link Overrider} instance assigned to this {@link QueryRunnerService} instance
     */
    public Overrider getOverrider() {
        return this.overrider;
    }

    /**
     * Creates new {@link Statement} instance
     *
     * @param conn SQL Connection
     * @param sql SQL Query string
     * @return new {@link Statement} instance
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected Statement createStatement(Connection conn, String sql)
            throws SQLException {

        return conn.createStatement();
    }

    /**
     * Creates new {@link PreparedStatement} instance
     *
     * @param conn SQL Connection
     * @param sql SQL Query string
     * @param getGeneratedKeys specifies if generated keys should be returned
     * @return new {@link PreparedStatement} instance
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql, boolean getGeneratedKeys)
    throws SQLException {
    	PreparedStatement result = null;
    	String[] overrideGeneratedKeysArr = null;

    	if (getGeneratedKeys == true || this.overrider.hasOverride(MidaoConstants.OVERRIDE_INT_GET_GENERATED_KEYS) == true) {
    		if (this.overrider.hasOverride(MidaoConstants.OVERRIDE_GENERATED_COLUMN_NAMES) == true) {
    			overrideGeneratedKeysArr = (String []) this.overrider.getOverride(MidaoConstants.OVERRIDE_GENERATED_COLUMN_NAMES);
    			result = conn.prepareStatement(sql, overrideGeneratedKeysArr);
    		} else {
    			result = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    		}
    		
    		if (this.overrider.hasOverride(MidaoConstants.OVERRIDE_INT_GET_GENERATED_KEYS) == false) {
    			this.overrider.overrideOnce(MidaoConstants.OVERRIDE_INT_GET_GENERATED_KEYS, true);
    		}
    	} else {
    		result = conn.prepareStatement(sql);
    	}
    	
    	return result;
    }

    /**
     * Creates new {@link CallableStatement} instance
     *
     * @param conn SQL Connection
     * @param sql SQL Query string
     * @return new {@link CallableStatement} instance
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected CallableStatement prepareCall(Connection conn, String sql)
    throws SQLException {

    	return conn.prepareCall(sql);
    }

    /**
     * Returns new {@link SQLException} instance.
     * Used to throw exception during input arguments null check
     *
     * @return new {@link SQLException} instance
     */
    protected SQLException nullException() {
    	return new SQLException("Error! Value cannot be null");
    }

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     *
     * @param stmtHandler {@link StatementHandler} implementation
     * @param sql The SQL query to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values.
     * @return array of row affected
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected int[] batch(StatementHandler stmtHandler, String sql, QueryParameters[] params) throws SQLException {
    	Connection conn = this.transactionHandler.getConnection();
    	
        if (sql == null) {
        	this.transactionHandler.rollback();
        	this.transactionHandler.closeConnection();
        	
            throw new SQLException("Null SQL statement");
        }

        if (params == null) {
        	this.transactionHandler.rollback();
        	this.transactionHandler.closeConnection();
        	
            throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
        }
        
        PreparedStatement stmt = null;
        int[] rows = null;
        QueryParameters[] processedParams = new QueryParameters[params.length];
        
        try {
        	
            stmt = this.prepareStatement(conn, sql, false);

            for (int i = 0; i < params.length; i++) {
            	processedParams[i] = typeHandler.processInput(stmt, params[i]);
            	
            	stmtHandler.setStatement(stmt, processedParams[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();
            
            for (int i = 0; i < params.length; i++) {
            	typeHandler.afterExecute(stmt, processedParams[i], params[i]);
            }
            
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.commit();
            }
            
        } catch (SQLException e) {
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.rollback();
            }
        	
            ExceptionUtils.rethrow(conn, e, sql, (Object[])params);
        } finally {
        	
        	stmtHandler.beforeClose();
        	MidaoUtils.closeQuietly(stmt);
            stmtHandler.afterClose();
            
            this.transactionHandler.closeConnection();
        }

        return rows;
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     *
     * @param <T> The type of object that the handler returns
     * @param stmtHandler {@link StatementHandler} implementation
     * @param sql The SQL query to execute.
     * @param outputHandler {@link OutputHandler} implementation
     * @param params parameter values
     * @return converted result
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected <T> T query(StatementHandler stmtHandler, String sql, OutputHandler<T> outputHandler, QueryParameters params)
            throws SQLException {
    	Connection conn = this.transactionHandler.getConnection();
    	
        if (sql == null) {
        	this.transactionHandler.rollback();
        	this.transactionHandler.closeConnection();
        	
            throw new SQLException("Null SQL statement");
        }

        if (outputHandler == null) {
        	this.transactionHandler.rollback();
        	this.transactionHandler.closeConnection();
        	
            throw new SQLException("Null OutputHandler");
        }

        Statement stmt = null;
        PreparedStatement pstmt = null;
        List<QueryParameters> paramsList = null;
        T result = null;
        QueryParameters processedParams = null;
        
        try {
        	if (params.size() > 0) {
        		stmt = this.prepareStatement(conn, sql, false);
        	} else {
        		stmt = this.createStatement(conn, sql);
        	}
            
            // Input/Output is present only for PreparedStatement and CallableStatement
        	if (stmt instanceof PreparedStatement) {
        		processedParams = typeHandler.processInput(stmt, params);
        		stmtHandler.setStatement(stmt, processedParams);
        	}
            
        	if (stmt instanceof PreparedStatement) {
        		pstmt = (PreparedStatement) stmt;
        		pstmt.execute();
        	} else {
        		stmt.execute(sql);
        	}

            if (stmt instanceof PreparedStatement) {
                typeHandler.afterExecute(stmt, processedParams, params);
            }
            
            paramsList = stmtHandler.wrap(stmt);
            
            // Input/Output is present only for PreparedStatement and CallableStatement
            if (stmt instanceof PreparedStatement) {
            	paramsList = typeHandler.processOutput(stmt, paramsList);
            }
            
            result = outputHandler.handle(paramsList);
            
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.commit();
            }

        } catch (SQLException ex) {
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.rollback();
            }
        	
            ExceptionUtils.rethrow(conn, ex, sql, params);
        } catch (MidaoException ex) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            ExceptionUtils.rethrow(ex);
        } finally {
            	
			stmtHandler.beforeClose();
			MidaoUtils.closeQuietly(stmt);
			stmtHandler.afterClose();

			this.transactionHandler.closeConnection();
        }

        return result;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement without
     * any replacement parameters.
     *
     * @param <T> The type of object that the handler returns
     * @param stmtHandler {@link StatementHandler} implementation
     * @param sql The SQL query to execute.
     * @param outputHandler {@link OutputHandler} implementation
     * @param params parameter values
     * @return converted result
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected <T> T update(StatementHandler stmtHandler, String sql, OutputHandler<T> outputHandler, QueryParameters params) throws SQLException {
    	Connection conn = this.transactionHandler.getConnection();

        if (sql == null) {
        	this.transactionHandler.rollback();
        	this.transactionHandler.closeConnection();
        	
            throw new SQLException("Null SQL statement");
        }

        if (outputHandler == null) {
            this.transactionHandler.rollback();
            this.transactionHandler.closeConnection();

            throw new SQLException("Null OutputHandler");
        }

        Statement stmt = null;
        PreparedStatement pstmt = null;
        List<QueryParameters> paramsList = null;
        T result = null;
        QueryParameters processedParams = null;
        
        try {
        	
        	// getting generated keys if handler is not RowCountHandler
        	if (outputHandler instanceof RowCountOutputHandler) {
        		if (params.size() == 0) {
        			stmt = this.createStatement(conn, sql);
        		} else {
        			stmt = this.prepareStatement(conn, sql, false);
        		}
        	} else {
        		stmt = this.prepareStatement(conn, sql, true);
        	}
        	
        	// Input/Output is present only for PreparedStatement and CallableStatement
        	if (stmt instanceof PreparedStatement) {
        		processedParams = typeHandler.processInput(stmt, params);
        		stmtHandler.setStatement(stmt, processedParams);
        	}
            
            if (stmt instanceof PreparedStatement) {
            	pstmt = (PreparedStatement) stmt;
            	pstmt.executeUpdate();
            } else {
            	stmt.execute(sql);
            }

            if (stmt instanceof PreparedStatement) {
                typeHandler.afterExecute(stmt, processedParams, params);
            }
            
            paramsList = stmtHandler.wrap(stmt);
            
        	// Input/Output is present only for PreparedStatement and CallableStatement
        	if (stmt instanceof PreparedStatement) {
        		paramsList = typeHandler.processOutput(stmt, paramsList);
        	}
            
            result = outputHandler.handle(paramsList);
            
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.commit();
            }
            
        } catch (SQLException e) {
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.rollback();
            }
        	
            ExceptionUtils.rethrow(conn, e, sql, params);
        } catch (MidaoException ex) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            ExceptionUtils.rethrow(ex);
        } finally {
        	
        	stmtHandler.beforeClose();
        	MidaoUtils.closeQuietly(stmt);
            stmtHandler.afterClose();
            
            this.transactionHandler.closeConnection();
        }

        return result;
    }

    /**
     * Executes the given CALL SQL statement.
     * Allows execution of Stored Procedures/Functions
     *
     *
     * @param stmtHandler {@link StatementHandler} implementation
     * @param sql The SQL query to execute.
     * @param params parameter values
     * @return Query Output. All input parameters are updated from OUT parameters. Stored Function return is stored there
     * as well. Can be received by invoking {@link org.midao.core.handlers.model.QueryParameters#getReturn()}
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected QueryParameters call(StatementHandler stmtHandler, String sql, QueryParameters params)
            throws SQLException {
    	Connection conn = this.transactionHandler.getConnection();
    	
    	QueryParameters resultParams = new QueryParameters(params);
    	
        if (sql == null) {
        	this.transactionHandler.rollback();
        	this.transactionHandler.closeConnection();
        	
            throw new SQLException("Null SQL statement");
        }

        CallableStatement stmt = null;
        List<QueryParameters> paramsList = null;
        Object[] updatedValues = null;
        QueryParameters processedParams = null;
        
        try {
        	
      		stmt = this.prepareCall(conn, sql);

            if (params.size() > 0) {
      		    processedParams = typeHandler.processInput(stmt, params);
            } else {
                processedParams = params;
            }

      		stmtHandler.setStatement(stmt, processedParams);
            
            stmt.execute();

            if (params.size() > 0) {
                typeHandler.afterExecute(stmt, processedParams, params);
            }
            
            updatedValues = stmtHandler.readStatement(stmt, params);
            resultParams.update(updatedValues, true);
            
            paramsList = stmtHandler.wrap(stmt);

            if (params.size() > 0) {
                paramsList = typeHandler.processOutput(stmt, paramsList);
            }
            
            resultParams.setReturn(paramsList);

            if (params.size() > 0) {
                resultParams = typeHandler.processOutput(stmt, resultParams);
            }
            
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.commit();
            }
            
        } catch (SQLException e) {
            if (this.isTransactionManualMode() == false) {
            	this.transactionHandler.rollback();
            }
        	
            ExceptionUtils.rethrow(conn, e, sql, params);
        } finally {

			stmtHandler.beforeClose();
			MidaoUtils.closeQuietly(stmt);
			stmtHandler.afterClose();

			this.transactionHandler.closeConnection();
        }

        return resultParams;
    }

    /**
     * Reads array of {@link InputHandler} and checks if they have one query string.
     * Used during batch invocation
     *
     * @param inputHandlers array of {@link InputHandler}
     * @return SQL query string
     * @throws SQLException if SQL Query string is not the same among whole array of {@link InputHandler}
     */
    protected String getSqlQuery(InputHandler[] inputHandlers) throws SQLException {
    	String sql = null;
    	
        for (InputHandler inputHandler : inputHandlers) {
        	
        	if (sql == null) {
        		sql = inputHandler.getQueryString();
        	} else {
        		if (sql.equals(inputHandler.getQueryString()) == false) {
        			throw new SQLException("All input handlers should share the same SQL query and the same parameters set");
        		}
        	}
        }
        
        return sql;
    }

    /**
     * Reads array of {@link InputHandler} and returns array of parameter values
     * Used during batch invocation
     *
     * @param inputHandlers array of {@link InputHandler}
     * @return array of query parameter values
     */
    protected QueryParameters[] getQueryParams(InputHandler[] inputHandlers) {
    	QueryParameters[] result = new QueryParameters[inputHandlers.length];

    	for (int i = 0; i < inputHandlers.length; i++) {
    		result[i] = inputHandlers[i].getQueryParameters();
    	}
    	
    	return result;
    }

    /**
     * Converts array of parameter values (as array) and converts it into
     * array of {@link QueryParameters}
     * Used during batch invocation
     *
     * @param params
     * @return
     */
    protected QueryParameters[] getQueryParams(Object[][] params) {
    	QueryParameters[] result = new QueryParameters[params.length];

    	for (int i = 0; i < params.length; i++) {
    		result[i] = new QueryParameters(params[i]);
    	}
    	
    	return result;
    }

    /**
     * Uses {@link MetadataHandler} to read Stored Procedure/Function parameters and creates new
     * {@link QueryInputHandler} instance with parameter values from @inputHandler
     *
     * @param inputHandler {@link AbstractNamedInputHandler} which used as source for {@link QueryInputHandler}
     *                                                      parameter values
     * @param catalog Database Catalog
     * @param schema Database Schema
     * @param useCache specifies if {@link MetadataHandler} should use cache
     * @return new filled {@link QueryInputHandler} instance with values from @inputHandler
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected QueryInputHandler convertToQueryInputHandler(AbstractNamedInputHandler inputHandler, String catalog, String schema, boolean useCache) throws SQLException {
    	QueryInputHandler result = null;
    	
    	String shortProcedureName = CallableUtils.getStoredProcedureShortNameFromSql(inputHandler.getEncodedQueryString());
        boolean expectedReturn = CallableUtils.isFunctionCall(inputHandler.getEncodedQueryString());
    	
    	Connection conn = this.transactionHandler.getConnection();
    	
    	try {
    	
    		//QueryParameters procedureParams = SimpleMetaDataFactory.getProcedureParameters(conn, catalog, schema, shortProcedureName, useCache);
    		QueryParameters procedureParams = this.metadataHandler.getProcedureParameters(conn, catalog, schema, shortProcedureName, useCache);

    		QueryParameters inputParams = inputHandler.getQueryParameters();
    		String encodedSql = inputHandler.getEncodedQueryString();

            // trying to detect if user omitted return, but database returned it.
            if (expectedReturn == false && (procedureParams.size() == inputParams.size() + 1)) {
                for (String parameterName : procedureParams.keySet()) {
                    if (procedureParams.getDirection(parameterName) == QueryParameters.Direction.RETURN) {
                        procedureParams.remove(parameterName);
                        break;
                    }
                }
            }

            if (procedureParams.size() != inputParams.size()) {
                throw new MidaoSQLException(String.format("Database reported %d parameters, but only %d were supplied.", procedureParams.size(), inputParams.size()));
            }
    	
    		inputParams = CallableUtils.updateDirections(inputParams, procedureParams);
    		inputParams = CallableUtils.updateTypes(inputParams, procedureParams);

    		result = new QueryInputHandler(encodedSql, inputParams);
    	} finally {
    		this.transactionHandler.closeConnection();
    	}
    	
    	return result;
    }

    /**
     * Creates new TypeHandler instance
     *
     * @param typeHandlerClazz TypeHandler implementation class
     * @return new TypeHandler implementation instance
     */
    private TypeHandler getTypeHandler(Class<? extends TypeHandler> typeHandlerClazz) {
        TypeHandler result = null;
        Constructor<? extends TypeHandler> constructor = null;

        Class<? extends TypeHandler> clazz = typeHandlerClazz;

        try {
            constructor = clazz.getConstructor(Overrider.class);
            result = constructor.newInstance(overrider);
        } catch (SecurityException e) {
            throw new MidaoRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MidaoRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MidaoRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MidaoRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MidaoRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MidaoRuntimeException(ERROR_TyH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Creates new StatementHandler instance
     *
     * @param statementHandlerClazz StatementHandler implementation class
     * @return new StatementHandler implementation instance
     */
    private StatementHandler getStatementHandler(Class<? extends StatementHandler> statementHandlerClazz) {
        StatementHandler result = null;
        Constructor<? extends StatementHandler> constructor = null;

        Class<? extends StatementHandler> clazz = statementHandlerClazz;

        try {
            constructor = clazz.getConstructor(Overrider.class);
            result = constructor.newInstance(overrider);
        } catch (SecurityException e) {
            throw new MidaoRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MidaoRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MidaoRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MidaoRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MidaoRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MidaoRuntimeException(ERROR_SH_INIT_FAILED, e);
        }

        return result;
    }

}
