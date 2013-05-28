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

package org.midao.examples.oracle;

import oracle.jdbc.pool.OracleDataSource;
import org.midao.core.utils.MidaoUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class OracleParameters {
    public static Connection createConnection() throws SQLException {
        MidaoUtils.loadDriver("oracle.jdbc.pool.OracleDataSource");

        OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();

        ds.setDriverType("thin");
        ds.setServerName("localhost");
        ds.setDatabaseName("xe");
        ds.setPortNumber(1521);
        ds.setUser("chris");
        ds.setPassword("welcome1");

        Connection conn = ds.getConnection();

        return conn;
    }
}