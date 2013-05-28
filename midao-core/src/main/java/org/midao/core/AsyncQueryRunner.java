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

import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.core.handlers.input.query.AbstractQueryInputHandler;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.OutputHandler;
import org.midao.core.service.AsyncQueryRunnerService;
import org.midao.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base {@link AsyncQueryRunnerService} implementation
 */
public class AsyncQueryRunner implements AsyncQueryRunnerService {

    private final ExecutorService executorService;
    private final QueryRunnerService queryRunner;

    /**
     * Creates new AsyncQueryRunner instance
     *
     * @param runner {@link QueryRunnerService} implementation
     * @param executorService {@link ExecutorService} implementation
     */
    public AsyncQueryRunner(QueryRunnerService runner, ExecutorService executorService) {
        this.executorService = executorService;
        this.queryRunner = runner;
    }

    /**
     * {@inheritDoc}
     */
    public Future<int[]> batch(final String sql, final Object[][] params) throws SQLException {
        return executorService.submit(new Callable<int[]>() {

            /**
             * {@inheritDoc}
             */
            public int[] call() throws Exception {
                return queryRunner.batch(sql, params);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public Future<int[]> batch(final InputHandler[] inputHandlers) throws SQLException {
        return executorService.submit(new Callable<int[]>() {

            /**
             * {@inheritDoc}
             */
            public int[] call() throws Exception {
            	return queryRunner.batch(inputHandlers);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> query(final String sql, final OutputHandler<T> outputHandler, final Object... params) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.query(sql, outputHandler, params);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> query(final InputHandler inputHandler, final OutputHandler<T> outputHandler) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.query(inputHandler, outputHandler);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> query(final String sql, final OutputHandler<T> outputHandler) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.query(sql, outputHandler);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public Future<Integer> update(final String sql) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            /**
             * {@inheritDoc}
             */
            public Integer call() throws Exception {
                return queryRunner.update(sql);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public Future<Integer> update(final String sql, final Object param) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            /**
             * {@inheritDoc}
             */
            public Integer call() throws Exception {
                return queryRunner.update(sql, param);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public Future<Integer> update(final String sql, final Object... params) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            /**
             * {@inheritDoc}
             */
            public Integer call() throws Exception {
                return queryRunner.update(sql, params);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public Future<Integer> update(final InputHandler inputHandler) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            /**
             * {@inheritDoc}
             */
            public Integer call() throws Exception {
                return queryRunner.update(inputHandler);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> update(final InputHandler inputHandler, final OutputHandler<T> outputHandler) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.update(inputHandler, outputHandler);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> update(final String sql, final OutputHandler<T> outputHandler, final Object... params) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.update(sql, outputHandler, params);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public Future<QueryParameters> call(final AbstractQueryInputHandler inputHandler) throws SQLException {
        return executorService.submit(new Callable<QueryParameters>() {

            /**
             * {@inheritDoc}
             */
            public QueryParameters call() throws Exception {
                return queryRunner.call(inputHandler);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> call(final AbstractNamedInputHandler<T> inputHandler, final String catalog, final String schema, final boolean useCache) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.call(inputHandler, catalog, schema, useCache);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> call(final AbstractNamedInputHandler<T> inputHandler) throws SQLException {
        return executorService.submit(new Callable<T>() {

            /**
             * {@inheritDoc}
             */
            public T call() throws Exception {
                return queryRunner.call(inputHandler);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
	public <T, S> Future<CallResults<T, S>> call(final InputHandler<T> inputHandler, final OutputHandler<S> outputHandler) throws SQLException {
        return executorService.submit(new Callable<CallResults<T, S>>() {

            /**
             * {@inheritDoc}
             */
            public CallResults<T, S> call() throws Exception {
                return queryRunner.call(inputHandler, outputHandler);
            }

        });
	}

    /**
     * {@inheritDoc}
     */
	public <T, S> Future<CallResults<T, S>> call(final InputHandler<T> inputHandler, final OutputHandler<S> outputHandler, final String catalog, final String schema, final boolean useCache) throws SQLException {
        return executorService.submit(new Callable<CallResults<T, S>>() {

            /**
             * {@inheritDoc}
             */
            public CallResults<T, S> call() throws Exception {
                return queryRunner.call(inputHandler, outputHandler, catalog, schema, useCache);
            }

        });
	}

    /**
     * {@inheritDoc}
     */
	public void setTransactionManualMode(boolean manualMode) {
		this.queryRunner.setTransactionManualMode(manualMode);
	}

    /**
     * {@inheritDoc}
     */
	public boolean isTransactionManualMode() {
		return this.queryRunner.isTransactionManualMode();
	}

    /**
     * {@inheritDoc}
     */
	public void setTransactionIsolationLevel(Integer level) {
		this.queryRunner.setTransactionIsolationLevel(level);
	}

    /**
     * {@inheritDoc}
     */
	public Integer getTransactionIsolationLevel() {
		return this.queryRunner.getTransactionIsolationLevel();
	}

    /**
     * {@inheritDoc}
     */
	public void commit() throws SQLException {
		this.queryRunner.commit();
	}

    /**
     * {@inheritDoc}
     */
	public void rollback() throws SQLException {
		this.queryRunner.rollback();
	}

    /**
     * {@inheritDoc}
     */
	public Savepoint setSavepoint() throws SQLException {
		return this.queryRunner.setSavepoint();
	}

    /**
     * {@inheritDoc}
     */
	public Savepoint setSavepoint(String name) throws SQLException {
		return this.queryRunner.setSavepoint(name);
	}

    /**
     * {@inheritDoc}
     */
	public void rollback(Savepoint savepoint) throws SQLException {
		this.queryRunner.rollback(savepoint);
	}

    /**
     * {@inheritDoc}
     */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.queryRunner.releaseSavepoint(savepoint);
	}

    /**
     * {@inheritDoc}
     */
	public AsyncQueryRunnerService overrideOnce(String operation, Object value) {
		this.queryRunner.overrideOnce(operation, value);
		
		return this;
	}

    /**
     * {@inheritDoc}
     */
	public AsyncQueryRunnerService override(String operation, Object value) {
		this.queryRunner.override(operation, value);
		
		return this;
	}

    /**
     * {@inheritDoc}
     */
	public AsyncQueryRunnerService removeOverride(String operation) {
		this.queryRunner.removeOverride(operation);
		
		return this;
	}
}
