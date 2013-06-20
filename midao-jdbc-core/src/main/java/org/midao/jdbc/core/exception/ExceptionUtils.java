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
