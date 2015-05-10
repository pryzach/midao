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

package org.midao.jdbc.spring.exception;

import org.midao.jdbc.core.exception.MjdbcSQLException;

/**
 * Exception is thrown on a query timeout. This could have different causes depending on the database API in use,
 * but most likely thrown after the database interrupts or stops the processing of a query before it has completed.
 * <p/>
 * <p>This exception can be thrown by user code trapping the native database exception or
 * by exception translation.</p>
 * <p/>
 * <p><i>Above description was taken from Spring JDBC documentation</i></p>
 */
public class QueryTimeoutException extends MjdbcSQLException {

    /**
     * Creates new QueryTimeoutException instance
     *
     * @param reason     a description of the exception
     * @param SQLState   an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     */
    public QueryTimeoutException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }
}
