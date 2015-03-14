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

package org.midao.jdbc.core.exception;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Midao Exception Handler. Allows rethrowing SQL Exception while populating it with query parameters
 */
public interface ExceptionHandler {

    /**
     * Converts SQLException into MjdbcSQLException while populating it with query parameters
     *
     * @param conn   SQL Connection which is used in current session. Is not guaranteed to be open
     * @param cause  original SQL Exception
     * @param sql    SQL string which was executed
     * @param params parameters which were supplied to the query
     * @return filled {@link MjdbcSQLException}
     */
    public MjdbcSQLException convert(Connection conn, SQLException cause, String sql, Object... params);
}
