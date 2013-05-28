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

package org.midao.core.service;

import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.core.handlers.input.query.AbstractQueryInputHandler;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.OutputHandler;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Future;

/**
 * Core Service of Midao.
 * Asynchronously executes all type of Queries.
 */
public interface AsyncQueryRunnerService {

    /**
     * @see QueryRunnerService#batch(String, Object[][])
     */
    public Future<int[]> batch(final String sql, final Object[][] params) throws SQLException;

    /**
     * @see QueryRunnerService#batch(org.midao.core.handlers.input.InputHandler[])
     */
    public Future<int[]> batch(final InputHandler[] inputHandlers) throws SQLException;

    /**
     * @see QueryRunnerService#query(String, org.midao.core.handlers.output.OutputHandler, Object...)
     */
    public <T> Future<T> query(final String sql, final OutputHandler<T> outputHandler, final Object... params) throws SQLException;

    /**
     * @see QueryRunnerService#query(org.midao.core.handlers.input.InputHandler, org.midao.core.handlers.output.OutputHandler)
     */
    public <T> Future<T> query(final InputHandler inputHandler, final OutputHandler<T> outputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#query(String, org.midao.core.handlers.output.OutputHandler)
     */
    public <T> Future<T> query(final String sql, final OutputHandler<T> outputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#update(String)
     */
    public Future<Integer> update(final String sql) throws SQLException;

    /**
     * @see QueryRunnerService#update(String, Object)
     */
    public Future<Integer> update(final String sql, final Object param) throws SQLException;

    /**
     * @see QueryRunnerService#update(String, Object...)
     */
    public Future<Integer> update(final String sql, final Object... params) throws SQLException;

    /**
     * @see QueryRunnerService#update(org.midao.core.handlers.input.InputHandler)
     */
    public Future<Integer> update(final InputHandler inputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#update(org.midao.core.handlers.input.InputHandler, org.midao.core.handlers.output.OutputHandler)
     */
    public <T> Future<T> update(final InputHandler inputHandler, final OutputHandler<T> outputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#update(String, org.midao.core.handlers.output.OutputHandler, Object...)
     */
    public <T> Future<T> update(final String sql, final OutputHandler<T> outputHandler, final Object... params) throws SQLException;

    /**
     * @see QueryRunnerService#call(org.midao.core.handlers.input.query.AbstractQueryInputHandler)
     */
	public Future<QueryParameters> call(final AbstractQueryInputHandler inputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#call(org.midao.core.handlers.input.named.AbstractNamedInputHandler)
     */
	public <T> Future<T> call(final AbstractNamedInputHandler<T> inputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#call(org.midao.core.handlers.input.named.AbstractNamedInputHandler, String, String, boolean)
     */
	public <T> Future<T> call(final AbstractNamedInputHandler<T> inputHandler, final String catalog, final String schema, final boolean useCache) throws SQLException;

    /**
     * @see QueryRunnerService#call(org.midao.core.handlers.input.InputHandler, org.midao.core.handlers.output.OutputHandler)
     */
	public <T, S> Future<CallResults<T, S>> call(final InputHandler<T> inputHandler, final OutputHandler<S> outputHandler) throws SQLException;

    /**
     * @see QueryRunnerService#call(org.midao.core.handlers.input.InputHandler, org.midao.core.handlers.output.OutputHandler, String, String, boolean)
     */
	public <T, S> Future<CallResults<T, S>> call(final InputHandler<T> inputHandler, final OutputHandler<S> outputHandler, final String catalog, final String schema, final boolean useCache) throws SQLException;

    /**
     * @see QueryRunnerService#overrideOnce(String, Object)
     */
    public AsyncQueryRunnerService overrideOnce(String operation, Object value);

    /**
     * @see QueryRunnerService#override(String, Object)
     */
    public AsyncQueryRunnerService override(String operation, Object value);

    /**
     * @see QueryRunnerService#removeOverride(String)
     */
    public AsyncQueryRunnerService removeOverride(String operation);

    /**
     * @see QueryRunnerService#setTransactionManualMode(boolean)
     */
    public void setTransactionManualMode(boolean manualMode);

    /**
     * @see org.midao.core.service.QueryRunnerService#isTransactionManualMode()
     */
    public boolean isTransactionManualMode();

    /**
     * @see QueryRunnerService#setTransactionIsolationLevel(Integer)
     */
    public void setTransactionIsolationLevel(Integer level);

    /**
     * @see org.midao.core.service.QueryRunnerService#getTransactionIsolationLevel()
     */
    public Integer getTransactionIsolationLevel();

    /**
     * @see org.midao.core.service.QueryRunnerService#commit()
     */
    public void commit() throws SQLException;

    /**
     * @see org.midao.core.service.QueryRunnerService#rollback()
     */
    public void rollback() throws SQLException;

    /**
     * @see org.midao.core.service.QueryRunnerService#setSavepoint()
     */
    public Savepoint setSavepoint() throws SQLException;

    /**
     * @see QueryRunnerService#setSavepoint(String)
     */
    public Savepoint setSavepoint(String name) throws SQLException;

    /**
     * @see org.midao.core.service.QueryRunnerService#rollback(java.sql.Savepoint)
     */
    public void rollback(Savepoint savepoint) throws SQLException;

    /**
     * @see QueryRunnerService#releaseSavepoint(java.sql.Savepoint)
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException;
}
