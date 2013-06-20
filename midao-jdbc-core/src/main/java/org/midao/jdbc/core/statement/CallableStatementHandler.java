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

package org.midao.jdbc.core.statement;

import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.utils.AssertUtils;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Universal Statement handler (handles both {@link java.sql.PreparedStatement} and {@link CallableStatement}).
 * From BaseStatementHandler inherits PreparedStatement functionality
 *
 * @see {@link BaseStatementHandler}
 */
public class CallableStatementHandler extends BaseStatementHandler {

    /**
     * Creates new CallableStatementHandler instance
     *
     * @param overrider overrider
     */
	public CallableStatementHandler(Overrider overrider) {
		super(overrider);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void setStatement(Statement statement, QueryParameters params) throws SQLException {
		
		// setting in parameters
		super.setStatement(statement, params);
		
		if (statement instanceof CallableStatement) {

			// registering out parameters
			CallableStatement callStmt = (CallableStatement) statement;

			String parameterName = null;
			for (int i = 0; i < params.size(); i++) {
				parameterName = params.getNameByPosition(i);

				if (params.isOutParameter(parameterName) == true) {
					callStmt.registerOutParameter(i + 1, params.getType(parameterName));
				}
			}
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Object[] readStatement(Statement statement, QueryParameters params) throws SQLException {
		Object[] result = new Object[0];
		
		AssertUtils.assertNotNull(params);
		
		super.readStatement(statement, params);
		
		if (statement instanceof CallableStatement) {

			CallableStatement callStmt = (CallableStatement) statement;

			result = new Object[params.size()];

			String parameterName = null;
			for (int i = 0; i < params.size(); i++) {
				parameterName = params.getNameByPosition(i);

				if (params.isOutParameter(parameterName) == true) {
					result[i] = callStmt.getObject(i + 1);
				} else {
					result[i] = null;
				}
			}
		}
    	
    	return result;
	}

}
