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

package org.midao.core.handlers.type;

import org.midao.core.Overrider;
import org.midao.core.handlers.model.QueryParameters;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Default TypeHandler implementation
 * It is empty implementation and doesn't perform any conversion/processing
 */
public class EmptyTypeHandler implements TypeHandler {
	private final Overrider overrider;

    /**
     * Creates new EmptyTypeHandler instance
     *
     * @param overrider
     */
	public EmptyTypeHandler(Overrider overrider) {
		this.overrider = overrider;
	}

    /**
     * {@inheritDoc}
     */
	public QueryParameters processInput(Statement stmt, QueryParameters params) {
		return params;
	}

    /**
     * {@inheritDoc}
     */
	public void afterExecute(Statement stmt, QueryParameters processedInput, QueryParameters params) {
		// do nothing
	}

    /**
     * {@inheritDoc}
     */
	public List<QueryParameters> processOutput(Statement stmt, List<QueryParameters> paramsList) {
		return paramsList;
	}

    /**
     * {@inheritDoc}
     */
	public QueryParameters processOutput(Statement stmt, QueryParameters params) throws SQLException {
		return params;
	}

}
