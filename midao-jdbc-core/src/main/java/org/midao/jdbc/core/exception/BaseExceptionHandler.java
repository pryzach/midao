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
import java.util.Arrays;

/**
 * Base ExceptionHandler implementation
 */
public class BaseExceptionHandler implements ExceptionHandler {
    private final String dbName;

    /**
     * Creates new BaseExceptionHandler instance.
     *
     * @param dbName Database name
     */
    public BaseExceptionHandler(String dbName) {
        this.dbName = dbName;
    }

    /**
     * {@inheritDoc}
     */
	public MidaoSQLException convert(Connection conn, SQLException cause, String sql, Object... params) {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.deepToString(params));
        }

        MidaoSQLException ex = new MidaoSQLException(msg.toString(), cause.getSQLState(),
                cause.getErrorCode());
        ex.setStackTrace(cause.getStackTrace());
        
        return ex;
	}

}
