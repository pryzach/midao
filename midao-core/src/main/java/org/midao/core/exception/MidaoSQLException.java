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

import java.sql.SQLException;

/**
 * Default Midao Exception thrown during interaction with JDBC driver
 */
public class MidaoSQLException extends SQLException {

	private static final long serialVersionUID = -3266997585786237976L;

    /**
     * Creates new MidaoSQLException instance
     *
     * @param reason description of this exception
     * @param cause Throwable cause
     */
	public MidaoSQLException(String reason, Throwable cause) {
		super(reason);
        this.setStackTrace(cause.getStackTrace());
	}

    /**
     * Creates new MidaoSQLException instance
     *
     * @param reason description of this exception
     */
	public MidaoSQLException(String reason) {
		super(reason);
	}

    /**
     * Creates new MidaoSQLException instance
     *
     * @param cause Throwable cause
     */
	public MidaoSQLException(Throwable cause) {
		super(cause.getMessage());
        this.setStackTrace(cause.getStackTrace());
	}

    /**
     * Creates new MidaoSQLException instance
     *
     * @param reason description of this exception
     * @param SQLState {@link SQLException#SQLState}
     * @param vendorCode {@link SQLException#vendorCode}
     */
	public MidaoSQLException(String reason, String SQLState, int vendorCode) {
		super(reason, SQLState, vendorCode);
	}

}
