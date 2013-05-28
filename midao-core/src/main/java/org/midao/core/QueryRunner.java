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
import org.midao.core.handlers.HandlersConstants;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.core.handlers.input.query.AbstractQueryInputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.OutputHandler;
import org.midao.core.handlers.type.TypeHandler;
import org.midao.core.handlers.utils.CallableUtils;
import org.midao.core.utils.AssertUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base {@link org.midao.core.service.QueryRunnerService} implementation
 */
public class QueryRunner extends AbstractQueryRunner {
	private static final MidaoLogger log = MidaoLogger.getLogger(QueryRunner.class);

    /**
     * Creates new QueryRunner instance
     *
     * @param ds SQL DataSource
     */
    public QueryRunner(DataSource ds) {
    	super(ds);
    }

    /**
     * Creates new QueryRunner instance
     *
     * @param conn SQL Connection
     */
    public QueryRunner(Connection conn) {
    	super(conn);
    }

    /**
     * Creates new QueryRunner instance
     *
     * @param ds SQL DataSource
     * @param typeHandlerClazz TypeHandler implementation class (from which new TypeHandler instance would be created)
     */
    public QueryRunner(DataSource ds, Class<? extends TypeHandler> typeHandlerClazz) {
    	super(ds, null, typeHandlerClazz, null);
    }

    /**
     * Creates new QueryRunner instance
     *
     * @param conn SQL Connection
     * @param typeHandlerClazz TypeHandler implementation class (from which new TypeHandler instance would be created)
     */
    public QueryRunner(Connection conn, Class<? extends TypeHandler> typeHandlerClazz) {
    	super(null, conn, typeHandlerClazz, null);
    }

    /**
     * {@inheritDoc}
     */
    public int[] batch(String sql, Object[][] params) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());
    	AssertUtils.assertNotNull(params, nullException());
    	
        return this.batch(this.getStatementHandler(), sql, getQueryParams(params));
    }

    /**
     * {@inheritDoc}
     */
    public int[] batch(InputHandler[] inputHandlers) throws SQLException {
    	AssertUtils.assertNotNull(inputHandlers, nullException());
    	
        String sql = this.getSqlQuery(inputHandlers);

        return this.batch(this.getStatementHandler(), sql, getQueryParams(inputHandlers));
    }

    /**
     * {@inheritDoc}
     */
    public <T> T query(String sql, OutputHandler<T> outputHandler, Object... params) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());
    	AssertUtils.assertNotNull(outputHandler, nullException());
    	
        return this.<T>query(this.getStatementHandler(), sql, outputHandler, new QueryParameters(params));
    }

    /**
     * {@inheritDoc}
     */
    public <T> T query(InputHandler inputHandler, OutputHandler<T> outputHandler) throws SQLException {
    	AssertUtils.assertNotNull(inputHandler, nullException());
    	AssertUtils.assertNotNull(outputHandler, nullException());
    	
    	String sql = inputHandler.getQueryString();

        return this.<T>query(this.getStatementHandler(), sql, outputHandler, inputHandler.getQueryParameters());
    }

    /**
     * {@inheritDoc}
     */
    public <T> T query(String sql, OutputHandler<T> outputHandler) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());
    	AssertUtils.assertNotNull(outputHandler, nullException());
    	
        return this.<T>query(this.getStatementHandler(), sql, outputHandler, HandlersConstants.EMPTY_QUERY_PARAMS);
    }

    /**
     * {@inheritDoc}
     */
    public int update(String sql) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());
    	
        Integer rows = (Integer) this.update(this.getStatementHandler(), sql, HandlersConstants.UPDATE_ROW_COUNT_HANDLER, HandlersConstants.EMPTY_QUERY_PARAMS);
        
        return rows.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int update(String sql, Object param) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());
    	
        Integer rows = (Integer) this.update(this.getStatementHandler(), sql, HandlersConstants.UPDATE_ROW_COUNT_HANDLER, new QueryParameters(param));
        
        return rows.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int update(String sql, Object... params) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());

        Integer rows = (Integer) this.update(this.getStatementHandler(), sql, HandlersConstants.UPDATE_ROW_COUNT_HANDLER, new QueryParameters(params));
        
        return rows.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int update(InputHandler inputHandler) throws SQLException {
    	AssertUtils.assertNotNull(inputHandler, nullException());
    	
    	String sql = inputHandler.getQueryString();

    	Integer rows = (Integer) this.update(this.getStatementHandler(), sql, HandlersConstants.UPDATE_ROW_COUNT_HANDLER, inputHandler.getQueryParameters());
    	
    	return rows.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T update(InputHandler inputHandler, OutputHandler<T> outputHandler) throws SQLException {
    	AssertUtils.assertNotNull(inputHandler, nullException());
    	AssertUtils.assertNotNull(outputHandler, nullException());
    	
    	String sql = inputHandler.getQueryString();

    	return this.<T>update(this.getStatementHandler(), sql, outputHandler, inputHandler.getQueryParameters());
    }

    /**
     * {@inheritDoc}
     */
    public <T> T update(String sql, OutputHandler<T> outputHandler, Object... params) throws SQLException {
    	AssertUtils.assertNotNull(sql, nullException());
    	AssertUtils.assertNotNull(outputHandler, nullException());
    	
    	return this.<T>update(this.getStatementHandler(), sql, outputHandler, new QueryParameters(params));
    }

    /**
     * {@inheritDoc}
     */
    public QueryParameters call(AbstractQueryInputHandler inputHandler) throws SQLException {
    	AssertUtils.assertNotNull(inputHandler, nullException());
    	
    	String sql = inputHandler.getQueryString();
    	
    	QueryParameters params = null;

    	params = this.call(this.getStatementHandler(), sql, inputHandler.getQueryParameters());
        
        return params;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T call(AbstractNamedInputHandler<T> inputHandler, String catalog, String schema, boolean useCache) throws SQLException {
    	AssertUtils.assertNotNull(inputHandler, nullException());
    	
    	QueryInputHandler queryInput = convertToQueryInputHandler(inputHandler, catalog, schema, useCache);
    	
    	QueryParameters updatedParameters = this.call(queryInput);
    	
    	return inputHandler.updateInput(updatedParameters);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T call(AbstractNamedInputHandler<T> inputHandler) throws SQLException {
    	AssertUtils.assertNotNull(inputHandler, nullException());
    	
    	return this.call(inputHandler, null, null, false);
    }

    /**
     * {@inheritDoc}
     */
	public <T, S> CallResults call(InputHandler<T> inputHandler, OutputHandler<S> outputHandler, String catalog, String schema, boolean useCache) throws SQLException {
        AssertUtils.assertNotNull(inputHandler, nullException());
        AssertUtils.assertNotNull(outputHandler, nullException());

    	QueryParameters params = null;
    	String procedureName = CallableUtils.getStoredProcedureShortNameFromSql(inputHandler.getQueryString());
    	boolean isFunction = CallableUtils.isFunctionCall(inputHandler.getQueryString());
    	T input = null;
    	S output = null;
    	
    	CallResults<T, S> results = new CallResults(procedureName, isFunction);
    	
    	if (inputHandler instanceof AbstractQueryInputHandler) {
    		params = this.call((AbstractQueryInputHandler) inputHandler);
    	} else if (inputHandler instanceof AbstractNamedInputHandler) {
        	QueryInputHandler queryInput = convertToQueryInputHandler( (AbstractNamedInputHandler) inputHandler, catalog, schema, useCache);
        	
        	params = this.call(queryInput);
    	} else {
    		throw new IllegalArgumentException();
    	}

        try {
    	    output = outputHandler.handle(params.getReturn());
        } catch (MidaoException ex) {
            ExceptionUtils.rethrow(ex);
        }

    	params.removeReturn();
    	
    	results.setCallOutput(output);
    	
    	if (inputHandler instanceof AbstractQueryInputHandler) {
    		input = (T) params;
    	} else if (inputHandler instanceof AbstractNamedInputHandler) {
    		input = (T) ((AbstractNamedInputHandler) inputHandler).updateInput(params);
    	} else {
    		throw new IllegalArgumentException();
    	}
    	results.setCallInput(input);
    	
    	return results;
	}

    /**
     * {@inheritDoc}
     */
	public <T, S> CallResults call(InputHandler<T> inputHandler, OutputHandler<S> outputHandler) throws SQLException {
    	return this.call(inputHandler, outputHandler, null, null, false);
	}

}
