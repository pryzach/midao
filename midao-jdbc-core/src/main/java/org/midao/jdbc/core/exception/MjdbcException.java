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
 * Default Midao JDBC Exception which can be caught and mitigated
 */
public class MjdbcException extends Exception {

    /**
     * Creates new MjdbcException instance
     */
    public MjdbcException() {
    }

    /**
     * Creates new MjdbcException instance
     *
     * @param message description of this exception
     */
    public MjdbcException(String message) {
        super(message);
    }

    /**
     * Creates new MjdbcException instance
     *
     * @param cause Throwable cause
     */
    public MjdbcException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates new MjdbcException instance
     *
     * @param message description of this exception
     * @param cause   Throwable cause
     */
    public MjdbcException(String message, Throwable cause) {
        super(message, cause);
    }

}
