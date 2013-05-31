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

package org.midao.core.pool;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class MidaoFrameworkPoolBinder {
    private final static String PROP_PASSWORD = "password";
    private final static String PROP_URL = "jdbcUrl";
    private final static String PROP_USERNAME = "user";
    private final static String PROP_DRIVERCLASSNAME = "driverClass";
    private final static String PROP_MAXACTIVE = "maxPoolSize";
    private final static String PROP_INITIALSIZE = "initialPoolSize";
	
    public static DataSource createDataSource(Properties poolProperties) throws SQLException {
        assertNotNull(poolProperties);

        if (poolProperties.containsKey(PROP_URL) == false) {
            throw new SQLException("Could not find field: " + PROP_URL);
        }

        DataSource ds = null;
        String jdbcUrl = (String) poolProperties.get(PROP_URL);
    	try {
            ds = DataSources.unpooledDataSource(jdbcUrl, poolProperties);

			return DataSources.pooledDataSource(ds);
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }
    
    public static DataSource createDataSource(String url) throws SQLException {
        assertNotNull(url);

        DataSource ds = null;

        try {
            ds = DataSources.unpooledDataSource(url);

            return DataSources.pooledDataSource(ds);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
    
    public static DataSource createDataSource(String url, String userName, String password) throws SQLException {
        assertNotNull(url);
        assertNotNull(userName);
        assertNotNull(password);

        DataSource ds = null;

        try {
            ds = DataSources.unpooledDataSource(url, userName, password);

            return DataSources.pooledDataSource(ds);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
    
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password) throws SQLException {
    	return createDataSource(driverClassName, url, userName, password, 10, 100);
    }
    
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password, int initialSize, int maxActive) throws SQLException {
        assertNotNull(driverClassName);
        assertNotNull(url);
        assertNotNull(userName);
        assertNotNull(password);

        ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();


        try {
            pooledDataSource.setDriverClass(driverClassName);
            pooledDataSource.setJdbcUrl(url);
            pooledDataSource.setUser(userName);
            pooledDataSource.setPassword(password);
            pooledDataSource.setMaxPoolSize(maxActive);
            pooledDataSource.setInitialPoolSize(initialSize);
        } catch (PropertyVetoException ex) {
            throw new SQLException(ex);
        }

    	return pooledDataSource;
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
