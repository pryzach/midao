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

package org.midao.core.exception;

import org.midao.core.MidaoConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Various utilities used by ExceptionHandlers
 */
public class ExceptionUtils {
    /**
     * Throws a new exception with a more informative error message.
     *
     * @param conn
     *            SQL Connection which is used in current session. Is not guaranteed to be open
     *
     * @param cause
     *            The original exception that will be chained to the new
     *            exception when it's rethrown.
     *
     * @param sql
     *            The query that was executing when the exception happened.
     *
     * @param params
     *            The query replacement parameters; <code>null</code> is a valid
     *            value to pass in.
     *
     * @throws SQLException
     *             if a database access error occurs
     */
    public static void rethrow(Connection conn, SQLException cause, String sql, Object... params)
    throws MidaoSQLException {

    	MidaoSQLException ex = MidaoConfig.getDefaultExceptionHandler().convert(conn, cause, sql, params);

        throw ex;
    }

    /**
     * Converts MidaoException into MidaoSQLException.
     *
     * Useful in cases when internal logic thrown MidaoException during processing of Query output.
     * This allows for user to catch only SQLException instead of SQLException and MidaoException
     *
     * @param cause original MidaoException which would be converted
     * @throws MidaoSQLException
     */
    public static void rethrow(MidaoException cause) throws MidaoSQLException {
        MidaoSQLException ex = new MidaoSQLException(cause.getMessage());

        ex.setStackTrace(cause.getStackTrace());

        throw ex;
    }
}
