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

package org.midao.jdbc.core.utils;

import java.sql.SQLException;

/**
 * Assert Utils class.
 * Allows throwing exception is input value is nill. Useful in cases when function cannot accept some it parameters as null
 */
public class AssertUtils {

    /**
     * Throws exception if value is null
     *
     * @param value value which would be checked
     */
    public static void assertNotNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws exception if value is null.
     *
     * @param value   values which would be checked
     * @param message message which would be added to exception
     */
    public static void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws exception if value is null.
     *
     * @param value     values which would be checked
     * @param exception Exception which would be thrown if value == null
     * @throws SQLException
     */
    public static void assertNotNull(Object value, SQLException exception) throws SQLException {
        if (value == null) {
            throw exception;
        }
    }

    /**
     * Throws exception if value is true.
     *
     * @param value   values which would be checked
     * @param message message which would be added to exception
     */
    public static void assertNotTrue(boolean value, String message) {
        if (value == true) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws exception if value is false.
     *
     * @param value   values which would be checked
     * @param message message which would be added to exception
     */
    public static void assertNotFalse(boolean value, String message) {
        if (value == false) {
            throw new IllegalArgumentException(message);
        }
    }
}
