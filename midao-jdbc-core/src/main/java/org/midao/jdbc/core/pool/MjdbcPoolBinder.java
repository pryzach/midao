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

package org.midao.jdbc.core.pool;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Pooled DataSource implementation connector
 */
public class MjdbcPoolBinder {
    /**
     * @see {@link org.midao.jdbc.core.MjdbcFactory#createDataSource(java.util.Properties)}
     */
    public static DataSource createDataSource(Properties poolProperties) throws SQLException {
        throw new IllegalStateException("This class should never be in jar file");
    }

    /**
     * @see {@link org.midao.jdbc.core.MjdbcFactory#createDataSource(String)}
     */
    public static DataSource createDataSource(String url) throws SQLException {
        throw new IllegalStateException("This class should never be in jar file");
    }

    /**
     * @see {@link org.midao.jdbc.core.MjdbcFactory#createDataSource(String, String, String)}
     */
    public static DataSource createDataSource(String url, String userName, String password) throws SQLException {
        throw new IllegalStateException("This class should never be in jar file");
    }

    /**
     * @see {@link org.midao.jdbc.core.MjdbcFactory#createDataSource(String, String, String, String)}
     */
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password) throws SQLException {
        throw new IllegalStateException("This class should never be in jar file");
    }

    /**
     * @see {@link org.midao.jdbc.core.MjdbcFactory#createDataSource(String, String, String, String, int, int)}
     */
    public static DataSource createDataSource(String driverClassName, String url, String userName, String password, int initialSize, int maxActive) throws SQLException {
        throw new IllegalStateException("This class should never be in jar file");
    }
}
