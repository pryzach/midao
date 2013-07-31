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

/**
 * Various utilities used by ExceptionHandlers
 */
public class ExceptionUtils {

    /**
     * Converts MjdbcException into MjdbcSQLException.
     *
     * Useful in cases when internal logic thrown MjdbcException during processing of Query output.
     * This allows for user to catch only SQLException instead of SQLException and MjdbcException
     *
     * @param cause original MjdbcException which would be converted
     * @throws MjdbcSQLException
     */
    public static void rethrow(MjdbcException cause) throws MjdbcSQLException {
        MjdbcSQLException ex = new MjdbcSQLException(cause.getMessage());

        ex.setStackTrace(cause.getStackTrace());

        throw ex;
    }
}
