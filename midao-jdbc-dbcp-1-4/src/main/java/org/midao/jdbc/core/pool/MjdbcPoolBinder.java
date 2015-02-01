/*
 * Copyright 2013 Zakhar Prykhoda
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

package org.midao.jdbc.core.pool;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * Pooled DataSource implementation connector
 */
public class MjdbcPoolBinder {

    /**
     * Returns new Pooled {@link DataSource} implementation
     *
     * @param poolProperties pool properties
     * @return new Pooled {@link DataSource} implementation
     * @throws SQLException
     */
    public static DataSource createDataSource(Properties poolProperties) throws SQLException {
        assertNotNull(poolProperties);

        try {
            return BasicDataSourceFactory.createDataSource(poolProperties);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Returns new Pooled {@link DataSource} implementation
     * <p/>
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param url Database connection url
     * @return new Pooled {@link DataSource} implementation
     * @throws SQLException
     */
    public static DataSource createDataSource(String url) throws SQLException {
        assertNotNull(url);

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);

        return ds;
    }

    /**
     * Returns new Pooled {@link DataSource} implementation
     * <p/>
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param url      Database connection url
     * @param userName Database user name
     * @param password Database user password
     * @return new Pooled {@link DataSource} implementation
     * @throws SQLException
     */
    public static DataSource createDataSource(String url, String userName, String password) throws SQLException {
        assertNotNull(url);
        assertNotNull(userName);
        assertNotNull(password);

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(userName);
        ds.setPassword(password);

        return ds;
    }

    /**
     * Returns new Pooled {@link DataSource} implementation
     * <p/>
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param driverClassName Driver Class name
     * @param url             Database connection url
     * @param userName        Database user name
     * @param password        Database user password
     * @return new Pooled {@link DataSource} implementation
     * @throws SQLException
     */
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password) throws SQLException {
        return createDataSource(driverClassName, url, userName, password, 10, 100);
    }

    /**
     * Returns new Pooled {@link DataSource} implementation
     * <p/>
     * In case this function won't work - use {@link #createDataSource(java.util.Properties)}
     *
     * @param driverClassName Driver Class name
     * @param url             Database connection url
     * @param userName        Database user name
     * @param password        Database user password
     * @param initialSize     initial pool size
     * @param maxActive       max connection active
     * @return new Pooled {@link DataSource} implementation
     * @throws SQLException
     */
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password, int initialSize, int maxActive) throws SQLException {
        assertNotNull(driverClassName);
        assertNotNull(url);
        assertNotNull(userName);
        assertNotNull(password);

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(userName);
        ds.setPassword(password);

        ds.setMaxActive(maxActive);
        ds.setInitialSize(initialSize);

        return ds;
    }

    /**
     * Throws exception if value is null
     *
     * @param value value which would be checked
     */
    private static void assertNotNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
    }
}
