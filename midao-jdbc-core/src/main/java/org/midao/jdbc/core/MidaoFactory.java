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

import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.pool.MidaoFrameworkPoolBinder;
import org.midao.jdbc.core.profiler.ProfilerFactory;
import org.midao.jdbc.core.service.AsyncQueryRunnerService;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.core.statement.StatementHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * Midao Factory is responsible for creating core classes instances
 *
 * <p>This class is Deprecated - please use {@link MjdbcFactory} instead</p>
 */
@Deprecated
public class MidaoFactory {
	private static final String ERROR_COULDNT_FIND_POOL_PROVIDER = "Error. Couldn't find any accepted pool provider. Please put midao-jdbc-c3p0(<=0.9.1), midao-jdbc-dbcp(<=1.3/4.1) etc. into CLASSPATH. For never versions please use MjdbcFactory.";

    /**
     * Returns new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @param ds SQL DataSource
     * @return new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public static QueryRunnerService getQueryRunner(DataSource ds) {
    	return (QueryRunnerService) ProfilerFactory.newInstance(new QueryRunner(ds));
    }

    /**
     * Returns new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @param ds SQL DataSource
     * @param typeHandlerClazz {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation
     * @return new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public static QueryRunnerService getQueryRunner(DataSource ds, Class<? extends TypeHandler> typeHandlerClazz) {
    	return (QueryRunnerService) ProfilerFactory.newInstance(new QueryRunner(ds, typeHandlerClazz));
    }

    /**
     * Returns new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @param ds SQL DataSource
     * @param typeHandlerClazz {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation
     * @param statementHandlerClazz {@link org.midao.jdbc.core.statement.StatementHandler} implementation
     * @return new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public static QueryRunnerService getQueryRunner(DataSource ds, Class<? extends TypeHandler> typeHandlerClazz, Class<? extends StatementHandler> statementHandlerClazz) {
        return (QueryRunnerService) ProfilerFactory.newInstance(new QueryRunner(ds, typeHandlerClazz, statementHandlerClazz));
    }

    /**
     * Returns new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @param conn SQL Connection
     * @return new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public static QueryRunnerService getQueryRunner(Connection conn) {
    	return (QueryRunnerService) ProfilerFactory.newInstance(new QueryRunner(conn));
    }

    /**
     * Returns new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @param conn SQL Connection
     * @param typeHandlerClazz {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation
     * @return new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public static QueryRunnerService getQueryRunner(Connection conn, Class<? extends TypeHandler> typeHandlerClazz) {
    	return (QueryRunnerService) ProfilerFactory.newInstance(new QueryRunner(conn, typeHandlerClazz));
    }

    /**
     * Returns new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     *
     * @param conn SQL Connection
     * @param typeHandlerClazz {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation
     * @param statementHandlerClazz {@link org.midao.jdbc.core.statement.StatementHandler} implementation
     * @return new {@link org.midao.jdbc.core.service.QueryRunnerService} instance
     */
    public static QueryRunnerService getQueryRunner(Connection conn, Class<? extends TypeHandler> typeHandlerClazz, Class<? extends StatementHandler> statementHandlerClazz) {
        return (QueryRunnerService) ProfilerFactory.newInstance(new QueryRunner(conn, typeHandlerClazz, statementHandlerClazz));
    }

    /**
     * Returns new {@link org.midao.jdbc.core.service.AsyncQueryRunnerService} instance
     *
     * @param runner {@link org.midao.jdbc.core.service.QueryRunnerService} implementation
     * @param executorService {@link java.util.concurrent.ExecutorService} implementation
     * @return new {@link org.midao.jdbc.core.service.AsyncQueryRunnerService} instance
     */
    public static AsyncQueryRunnerService getAsyncQueryRunner(QueryRunner runner, ExecutorService executorService) {
        return new AsyncQueryRunner(runner, executorService);
    }

    /**
     * Returns new Pooled {@link javax.sql.DataSource} implementation
     *
     * @param poolProperties pool properties
     * @return new Pooled {@link javax.sql.DataSource} implementation
     * @throws java.sql.SQLException
     */
    public static DataSource createDataSource(Properties poolProperties) throws SQLException {
    	try {
    		return MidaoFrameworkPoolBinder.createDataSource(poolProperties);
    	} catch (NoClassDefFoundError ex) {
    		throw new NoClassDefFoundError(ERROR_COULDNT_FIND_POOL_PROVIDER);
    	}
    }

    /**
     * Returns new Pooled {@link javax.sql.DataSource} implementation
     *
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param url Database connection url
     * @return new Pooled {@link javax.sql.DataSource} implementation
     * @throws java.sql.SQLException
     */
    public static DataSource createDataSource(String url) throws SQLException {
    	try {
    		return MidaoFrameworkPoolBinder.createDataSource(url);
    	} catch (NoClassDefFoundError ex) {
    		throw new NoClassDefFoundError(ERROR_COULDNT_FIND_POOL_PROVIDER);
    	}
    }

    /**
     * Returns new Pooled {@link javax.sql.DataSource} implementation
     *
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param url Database connection url
     * @param userName Database user name
     * @param password Database user password
     * @return new Pooled {@link javax.sql.DataSource} implementation
     * @throws java.sql.SQLException
     */
    public static DataSource createDataSource(String url, String userName, String password) throws SQLException {
    	try {
    		return MidaoFrameworkPoolBinder.createDataSource(url, userName, password);
    	} catch (NoClassDefFoundError ex) {
    		throw new NoClassDefFoundError(ERROR_COULDNT_FIND_POOL_PROVIDER);
    	}
    }

    /**
     * Returns new Pooled {@link javax.sql.DataSource} implementation
     *
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param driverClassName Driver Class name
     * @param url Database connection url
     * @param userName Database user name
     * @param password Database user password
     * @return new Pooled {@link javax.sql.DataSource} implementation
     * @throws java.sql.SQLException
     */
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password) throws SQLException {
    	try {
    		return MidaoFrameworkPoolBinder.createDataSource(driverClassName, url, userName, password);
    	} catch (NoClassDefFoundError ex) {
    		throw new NoClassDefFoundError(ERROR_COULDNT_FIND_POOL_PROVIDER);
    	}
    }

    /**
     * Returns new Pooled {@link javax.sql.DataSource} implementation
     *
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param driverClassName Driver Class name
     * @param url Database connection url
     * @param userName Database user name
     * @param password Database user password
     * @param initialSize initial pool size
     * @param maxActive max connection active
     * @return new Pooled {@link javax.sql.DataSource} implementation
     * @throws java.sql.SQLException
     */
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password, int initialSize, int maxActive) throws SQLException {
    	try {
    		return MidaoFrameworkPoolBinder.createDataSource(driverClassName, url, userName, password, initialSize, maxActive);
    	} catch (NoClassDefFoundError ex) {
    		throw new NoClassDefFoundError(ERROR_COULDNT_FIND_POOL_PROVIDER);
    	}
    }
 }
