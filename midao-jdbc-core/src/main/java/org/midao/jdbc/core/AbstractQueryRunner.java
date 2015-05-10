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

package org.midao.jdbc.core;

import org.midao.jdbc.core.exception.*;
import org.midao.jdbc.core.handlers.input.InputHandler;
import org.midao.jdbc.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.model.QueryParametersLazyList;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.LazyScrollOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.LazyScrollUpdateOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.LazyUpdateOutputHandler;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.handlers.utils.CallableUtils;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.metadata.MetadataHandler;
import org.midao.jdbc.core.metadata.MetadataUtils;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.core.statement.LazyStatementHandler;
import org.midao.jdbc.core.statement.StatementHandler;
import org.midao.jdbc.core.transaction.TransactionHandler;
import org.midao.jdbc.core.utils.MjdbcUtils;

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

    private ExceptionHandler exceptionHandler;

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
     * @param ds   SQL DataSource
     * @param conn SQL Connection
     */
    AbstractQueryRunner(DataSource ds, Connection conn) {
        this(ds, conn, null, null);
    }

    /**
     * Creates new AbstractQueryRunner instance
     *
     * @param ds                    SQL DataSource
     * @param conn                  SQL Connection
     * @param typeHandlerClazz      TypeHandler implementation class (from which new TypeHandler instance would be created)
     * @param statementHandlerClazz StatementHandler implementation class (from which new StatementHandler instance would be created)
     */
    AbstractQueryRunner(DataSource ds, Connection conn, Class<? extends TypeHandler> typeHandlerClazz, Class<? extends StatementHandler> statementHandlerClazz) {
        this.overrider = MjdbcConfig.getDefaultOverrider();

        if (typeHandlerClazz != null) {
            setTypeHandler(getTypeHandler(typeHandlerClazz));
        } else {
            setTypeHandler(MjdbcConfig.getDefaultTypeHandler(overrider));
        }

        if (statementHandlerClazz != null) {
            setStatementHandler(getStatementHandler(statementHandlerClazz));
        } else {
            setStatementHandler(MjdbcConfig.getDefaultStatementHandler(overrider));
        }

        if (ds != null) {
            setTransactionHandler(MjdbcConfig.getDefaultTransactionHandler(ds));
        } else if (conn != null) {
            setTransactionHandler(MjdbcConfig.getDefaultTransactionHandler(conn));
        } else {
            throw new MjdbcRuntimeException("Either DataSource or Connection should be specified");
        }

        if (ds != null) {
            setMetadataHandler(MjdbcConfig.getDefaultMetadataHandler(ds));
        } else if (conn != null) {
            setMetadataHandler(MjdbcConfig.getDefaultMetadataHandler(conn));
        } else {
            throw new MjdbcRuntimeException("Either DataSource or Connection should be specified");
        }

        String dbName = "absent";

        if (this.isTransactionManualMode() == false) {
            try {
                // might create issue for PostgreSQL
                Connection metadataConn = transactionHandler.getConnection();
                DatabaseMetaData metaData = metadataConn.getMetaData();
                dbName = MetadataUtils.processDatabaseProductName(metaData.getDatabaseProductName());

                if (MappingUtils.hasFunction(transactionHandler.getConnection(), "createClob", new Class[]{}) == false) {
                    overrider.override(MjdbcConstants.OVERRIDE_INT_JDBC3, true);
                }

                transactionHandler.closeConnection();
            } catch (Exception ex) {
                overrider.override(MjdbcConstants.OVERRIDE_INT_JDBC3, true);
            }
        }

        if (ds != null) {
            setExceptionHandler(MjdbcConfig.getDefaultExceptionHandler(dbName));
        } else if (conn != null) {
            setExceptionHandler(MjdbcConfig.getDefaultExceptionHandler(dbName));
        } else {
            throw new MjdbcRuntimeException("Either DataSource or Connection should be specified");
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
        overrider.override(MjdbcConstants.OVERRIDE_INT_IS_MANUAL_MODE, manualMode);
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
     * Returns {@link TypeHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @return {@link TypeHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public TypeHandler getTypeHandler() {
        return typeHandler;
    }

    /**
     * Assigns {@link TypeHandler} implementation to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     * Please be aware that input {@link TypeHandler} should be share same {@link Overrider} instance:
     * <p/>
     * Example: QueryRunner.setTypeHandler(new BaseTypeHandler(queryRunner.getOverrider()));
     *
     * @param typeHandler {@link TypeHandler} implementation
     */
    public void setTypeHandler(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
        this.overrider.override(MjdbcConstants.OVERRIDE_INT_TYPE_HANDLER, typeHandler);
    }

    /**
     * Returns {@link TransactionHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @return {@link TransactionHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    /**
     * Assigns {@link TransactionHandler} implementation to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     * Please be aware that input {@link TransactionHandler} should be share same {@link Overrider} instance:
     * <p/>
     * Example: QueryRunner.setTransactionHandler(new BaseTransactionHandler(queryRunner.getOverrider()));
     *
     * @param transactionHandler {@link TransactionHandler} implementation
     */
    public void setTransactionHandler(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    /**
     * Returns {@link StatementHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @return {@link StatementHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public StatementHandler getStatementHandler() {
        return statementHandler;
    }

    /**
     * Assigns {@link StatementHandler} implementation to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     * Please be aware that input {@link StatementHandler} should be share same {@link Overrider} instance:
     * <p/>
     * Example: QueryRunner.setStatementHandler(new BaseStatementHandler(queryRunner.getOverrider()));
     *
     * @param statementHandler {@link StatementHandler} implementation
     */
    public void setStatementHandler(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
    }

    /**
     * Returns {@link MetadataHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @return {@link MetadataHandler} implementation assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public MetadataHandler getMetadataHandler() {
        return metadataHandler;
    }

    /**
     * Assigns {@link MetadataHandler} implementation to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     * Please be aware that input {@link MetadataHandler} should be share same {@link Overrider} instance:
     * <p/>
     * Example: QueryRunner.setMetadataHandler(new BaseMetadataHandler(queryRunner.getOverrider()));
     *
     * @param metadataHandler {@link MetadataHandler} implementation
     */
    public void setMetadataHandler(MetadataHandler metadataHandler) {
        this.metadataHandler = metadataHandler;
    }

    /**
     * Returns {@link Overrider} instance assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @return {@link Overrider} instance assigned to this {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public Overrider getOverrider() {
        return this.overrider;
    }

    /**
     * Creates new {@link Statement} instance
     *
     * @param conn SQL Connection
     * @param sql  SQL Query string
     * @return new {@link Statement} instance
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected Statement createStatement(Connection conn, OutputHandler outputHandler, String sql)
            throws SQLException {

        Statement result = null;
        Integer resultSetType = null;
        Integer resultSetConcurrency = null;

        if (outputHandler instanceof LazyScrollOutputHandler) {

            if (overrider.hasOverride(MjdbcConstants.OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE) == true) {
                // read value
                overrider.getOverride(MjdbcConstants.OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE);

                resultSetType = ResultSet.TYPE_SCROLL_SENSITIVE;
            } else {
                resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
            }
        }

        if (outputHandler instanceof LazyUpdateOutputHandler) {
            resultSetConcurrency = ResultSet.CONCUR_UPDATABLE;
        }

        if (resultSetType == null && resultSetConcurrency == null) {
            result = conn.createStatement();
        } else {

            resultSetType = (resultSetType == null ? ResultSet.TYPE_FORWARD_ONLY : resultSetType);
            resultSetConcurrency = (resultSetConcurrency == null ? ResultSet.CONCUR_READ_ONLY : resultSetConcurrency);

            result = conn.createStatement(resultSetType, resultSetConcurrency);
        }

        return result;
    }

    /**
     * Creates new {@link PreparedStatement} instance
     *
     * @param conn             SQL Connection
     * @param sql              SQL Query string
     * @param getGeneratedKeys specifies if generated keys should be returned
     * @return new {@link PreparedStatement} instance
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected PreparedStatement prepareStatement(Connection conn, OutputHandler outputHandler, String sql, boolean getGeneratedKeys)
            throws SQLException {
        PreparedStatement result = null;
        String[] overrideGeneratedKeysArr = null;

        Integer resultSetType = null;
        Integer resultSetConcurrency = null;

        if (outputHandler instanceof LazyScrollOutputHandler) {

            if (overrider.hasOverride(MjdbcConstants.OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE) == true) {
                // read value
                overrider.getOverride(MjdbcConstants.OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE);

                resultSetType = ResultSet.TYPE_SCROLL_SENSITIVE;
            } else {
                resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
            }
        }

        if (outputHandler instanceof LazyUpdateOutputHandler) {
            resultSetConcurrency = ResultSet.CONCUR_UPDATABLE;
        }

        if (getGeneratedKeys == true || this.overrider.hasOverride(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS) == true) {

            // if generated values should be returned - it cannot be updateable/scrollable
            if (outputHandler instanceof LazyUpdateOutputHandler || outputHandler instanceof LazyScrollOutputHandler) {
                throw new MjdbcSQLException("You are requesting generated values be handled by lazy scrollable and/or updateable handler. " +
                        "Generated values does not support that action. Please use cached output handler or non updateable/scrollable lazy handler.");
            }

            if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_GENERATED_COLUMN_NAMES) == true) {
                overrideGeneratedKeysArr = (String[]) this.overrider.getOverride(MjdbcConstants.OVERRIDE_GENERATED_COLUMN_NAMES);
                result = conn.prepareStatement(sql, overrideGeneratedKeysArr);
            } else {
                result = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            }

            if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS) == false) {
                this.overrider.overrideOnce(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS, true);
            }
        } else {

            if (resultSetType == null && resultSetConcurrency == null) {
                result = conn.prepareStatement(sql);
            } else {
                resultSetType = (resultSetType == null ? ResultSet.TYPE_FORWARD_ONLY : resultSetType);
                resultSetConcurrency = (resultSetConcurrency == null ? ResultSet.CONCUR_READ_ONLY : resultSetConcurrency);

                result = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
            }
        }

        return result;
    }

    /**
     * Creates new {@link CallableStatement} instance
     *
     * @param conn SQL Connection
     * @param sql  SQL Query string
     * @return new {@link CallableStatement} instance
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected CallableStatement prepareCall(Connection conn, OutputHandler outputHandler, String sql)
            throws SQLException {

        CallableStatement result = null;
        Integer resultSetType = null;
        Integer resultSetConcurrency = null;

        if (outputHandler instanceof LazyScrollOutputHandler) {

            if (overrider.hasOverride(MjdbcConstants.OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE) == true) {
                // read value
                overrider.getOverride(MjdbcConstants.OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE);

                resultSetType = ResultSet.TYPE_SCROLL_SENSITIVE;
            } else {
                resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
            }
        }

        if (outputHandler instanceof LazyUpdateOutputHandler) {
            resultSetConcurrency = ResultSet.CONCUR_UPDATABLE;
        }

        if (resultSetType == null && resultSetConcurrency == null) {
            result = conn.prepareCall(sql);
        } else {

            resultSetType = (resultSetType == null ? ResultSet.TYPE_FORWARD_ONLY : resultSetType);
            resultSetConcurrency = (resultSetConcurrency == null ? ResultSet.CONCUR_READ_ONLY : resultSetConcurrency);

            result = conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        return result;
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
     * @param sql         The SQL query to execute.
     * @param params      An array of query replacement parameters.  Each row in
     *                    this array is one set of batch replacement values.
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

            stmt = this.prepareStatement(conn, null, sql, false);

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

            rethrow(conn, e, sql, (Object[]) params);
        } finally {

            stmtHandler.beforeClose();
            MjdbcUtils.closeQuietly(stmt);
            stmtHandler.afterClose();

            this.transactionHandler.closeConnection();
        }

        return rows;
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     *
     * @param <T>           The type of object that the handler returns
     * @param stmtHandler   {@link StatementHandler} implementation
     * @param sql           The SQL query to execute.
     * @param outputHandler {@link OutputHandler} implementation
     * @param params        parameter values
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

        if (((stmtHandler instanceof LazyStatementHandler) == false) && outputHandler instanceof LazyOutputHandler) {
            throw new MjdbcRuntimeException("In order to use Lazy output handler - Lazy statement handler should be used...");
        }

        if (isTransactionManualMode() == false && stmtHandler instanceof LazyStatementHandler && outputHandler instanceof LazyOutputHandler) {
            throw new MjdbcRuntimeException("In order to use Lazy statement handler along with Lazy output handler - " +
                    "this query runner service should be in manual transaction mode. Please look at setTransactionManualMode");
        }

        Statement stmt = null;
        PreparedStatement pstmt = null;
        List<QueryParameters> paramsList = null;
        T result = null;
        QueryParameters processedParams = null;

        try {
            if (params.size() > 0) {
                stmt = this.prepareStatement(conn, outputHandler, sql, false);
            } else {
                stmt = this.createStatement(conn, outputHandler, sql);
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

            // For LazyOutputHandlers output should not be processed and will be done by cache by itself
            if ((outputHandler instanceof LazyOutputHandler) == false) {
                paramsList = typeHandler.processOutput(stmt, paramsList);
            } else {

                // limiting size of cache in case LazyOutputHandler is used
                if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_LAZY_CACHE_MAX_SIZE) == true) {
                    ((QueryParametersLazyList) paramsList).setMaxCacheSize((Integer) this.overrider.getOverride(
                            MjdbcConstants.OVERRIDE_LAZY_CACHE_MAX_SIZE));
                } else {
                    ((QueryParametersLazyList) paramsList).setMaxCacheSize((Integer) MjdbcConfig.getDefaultLazyCacheMaxSize());
                }

                // changing the type of lazy output cache
                if (outputHandler instanceof LazyScrollUpdateOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.UPDATE_SCROLL);
                } else if (outputHandler instanceof LazyScrollOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.READ_ONLY_SCROLL);
                } else if (outputHandler instanceof LazyUpdateOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.UPDATE_FORWARD);
                }
            }

            result = outputHandler.handle(paramsList);

            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.commit();
            }

        } catch (SQLException ex) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            rethrow(conn, ex, sql, params);
        } catch (MjdbcException ex) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            ExceptionUtils.rethrow(ex);
        } finally {

            stmtHandler.beforeClose();

            // Lazy output handler is responsible for closing statement
            if ((outputHandler instanceof LazyOutputHandler) == false) {
                MjdbcUtils.closeQuietly(stmt);
            }

            stmtHandler.afterClose();

            this.transactionHandler.closeConnection();
        }

        return result;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement without
     * any replacement parameters.
     *
     * @param <T>           The type of object that the handler returns
     * @param stmtHandler   {@link StatementHandler} implementation
     * @param sql           The SQL query to execute.
     * @param outputHandler {@link OutputHandler} implementation
     * @param params        parameter values
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

        if (((stmtHandler instanceof LazyStatementHandler) == false) && outputHandler instanceof LazyOutputHandler) {
            throw new MjdbcRuntimeException("In order to use Lazy output handler - Lazy statement handler should be used...");
        }

        if (isTransactionManualMode() == false && stmtHandler instanceof LazyStatementHandler && outputHandler instanceof LazyOutputHandler) {
            throw new MjdbcRuntimeException("In order to use Lazy statement handler along with Lazy output handler - " +
                    "this query runner service should be in manual transaction mode. Please look at setTransactionManualMode");
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
                    stmt = this.createStatement(conn, outputHandler, sql);
                } else {
                    stmt = this.prepareStatement(conn, outputHandler, sql, false);
                }
            } else {

                stmt = this.prepareStatement(conn, outputHandler, sql, true);
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

            // For LazyOutputHandlers output should not be processed and will be done by cache by itself
            if ((outputHandler instanceof LazyOutputHandler) == false) {
                paramsList = typeHandler.processOutput(stmt, paramsList);
            } else {

                // limiting size of cache in case LazyOutputHandler is used
                if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_LAZY_CACHE_MAX_SIZE) == true) {
                    ((QueryParametersLazyList) paramsList).setMaxCacheSize((Integer) this.overrider.getOverride(
                            MjdbcConstants.OVERRIDE_LAZY_CACHE_MAX_SIZE));
                } else {
                    ((QueryParametersLazyList) paramsList).setMaxCacheSize((Integer) MjdbcConfig.getDefaultLazyCacheMaxSize());
                }

                // changing the type of lazy output cache
                if (outputHandler instanceof LazyScrollUpdateOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.UPDATE_SCROLL);
                } else if (outputHandler instanceof LazyScrollOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.READ_ONLY_SCROLL);
                } else if (outputHandler instanceof LazyUpdateOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.UPDATE_FORWARD);
                }
            }

            result = outputHandler.handle(paramsList);

            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.commit();
            }

        } catch (SQLException e) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            rethrow(conn, e, sql, params);
        } catch (MjdbcException ex) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            ExceptionUtils.rethrow(ex);
        } finally {

            stmtHandler.beforeClose();

            // Lazy output handler is responsible for closing statement
            if ((outputHandler instanceof LazyOutputHandler) == false) {
                MjdbcUtils.closeQuietly(stmt);
            }

            stmtHandler.afterClose();

            this.transactionHandler.closeConnection();
        }

        return result;
    }

    /**
     * Executes the given CALL SQL statement.
     * Allows execution of Stored Procedures/Functions
     *
     * @param stmtHandler   {@link StatementHandler} implementation
     * @param sql           The SQL query to execute.
     * @param params        parameter values
     * @param outputHandler {@link OutputHandler} implementation
     * @return Query Output. All input parameters are updated from OUT parameters. Stored Function return is stored there
     * as well. Can be received by invoking {@link org.midao.jdbc.core.handlers.model.QueryParameters#getReturn()}
     * @throws SQLException if exception would be thrown by Driver/Database
     */
    protected <T> QueryParameters call(StatementHandler stmtHandler, String sql, QueryParameters params, OutputHandler<T> outputHandler)
            throws SQLException {
        Connection conn = this.transactionHandler.getConnection();

        QueryParameters resultParams = new QueryParameters(params);

        if (sql == null) {
            this.transactionHandler.rollback();
            this.transactionHandler.closeConnection();

            throw new SQLException("Null SQL statement");
        }

        if (((stmtHandler instanceof LazyStatementHandler) == false) && outputHandler instanceof LazyOutputHandler) {
            throw new MjdbcRuntimeException("In order to use Lazy output handler - Lazy statement handler should be used...");
        }

        if (isTransactionManualMode() == false && stmtHandler instanceof LazyStatementHandler && outputHandler instanceof LazyOutputHandler) {
            throw new MjdbcRuntimeException("In order to use Lazy statement handler along with Lazy output handler - " +
                    "this query runner service should be in manual transaction mode. Please look at setTransactionManualMode");
        }

        CallableStatement stmt = null;
        List<QueryParameters> paramsList = null;
        Object[] updatedValues = null;
        QueryParameters processedParams = null;

        try {

            stmt = this.prepareCall(conn, outputHandler, sql);

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

            // For LazyOutputHandlers output should not be processed and will be done by cache by itself
            if (params.size() > 0 && (outputHandler instanceof LazyOutputHandler) == false) {
                paramsList = typeHandler.processOutput(stmt, paramsList);
            }

            // limiting size of cache in case LazyOutputHandler is used
            if (outputHandler instanceof LazyOutputHandler) {
                if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_LAZY_CACHE_MAX_SIZE) == true) {
                    ((QueryParametersLazyList) paramsList).setMaxCacheSize((Integer) this.overrider.getOverride(
                            MjdbcConstants.OVERRIDE_LAZY_CACHE_MAX_SIZE));
                } else {
                    ((QueryParametersLazyList) paramsList).setMaxCacheSize((Integer) MjdbcConfig.getDefaultLazyCacheMaxSize());
                }

                // changing the type of lazy output cache
                if (outputHandler instanceof LazyScrollUpdateOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.UPDATE_SCROLL);
                } else if (outputHandler instanceof LazyScrollOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.READ_ONLY_SCROLL);
                } else if (outputHandler instanceof LazyUpdateOutputHandler) {
                    ((QueryParametersLazyList) paramsList).setType(QueryParametersLazyList.Type.UPDATE_FORWARD);
                }
            }

            if (outputHandler != null) {
                resultParams.setReturn(outputHandler.handle(paramsList));
            } else {
                resultParams.setReturn(paramsList);
            }

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

            rethrow(conn, e, sql, params);
        } catch (MjdbcException ex) {
            if (this.isTransactionManualMode() == false) {
                this.transactionHandler.rollback();
            }

            ExceptionUtils.rethrow(ex);
        } finally {

            stmtHandler.beforeClose();

            // Lazy output handler is responsible for closing statement
            if (outputHandler == null || (outputHandler instanceof LazyOutputHandler) == false) {
                MjdbcUtils.closeQuietly(stmt);
            }

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
     *                     parameter values
     * @param catalog      Database Catalog
     * @param schema       Database Schema
     * @param useCache     specifies if {@link MetadataHandler} should use cache
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
            if (expectedReturn == false && (procedureParams.orderSize() == inputParams.orderSize() + 1)) {
                for (String parameterName : procedureParams.keySet()) {
                    if (procedureParams.getDirection(parameterName) == QueryParameters.Direction.RETURN) {
                        procedureParams.remove(parameterName);
                        break;
                    }
                }
            }

            if (procedureParams.orderSize() != inputParams.orderSize()) {
                throw new MjdbcSQLException(String.format("Database reported %d parameters, but only %d were supplied.", procedureParams.orderSize(), inputParams.orderSize()));
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
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
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
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Returns assigned exception handler
     *
     * @return assigned exception handler
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Allows assigning exception handler
     *
     * @param exceptionHandler new exception handler which should be used by this instance of QueryRunner
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Throws a new exception with a more informative error message.
     *
     * @param conn   SQL Connection which is used in current session. Is not guaranteed to be open
     * @param cause  The original exception that will be chained to the new
     *               exception when it's rethrown.
     * @param sql    The query that was executing when the exception happened.
     * @param params The query replacement parameters; <code>null</code> is a valid
     *               value to pass in.
     * @throws SQLException if a database access error occurs
     */
    private void rethrow(Connection conn, SQLException cause, String sql, Object... params)
            throws MjdbcSQLException {

        MjdbcSQLException ex = this.exceptionHandler.convert(conn, cause, sql, params);

        throw ex;
    }
}
