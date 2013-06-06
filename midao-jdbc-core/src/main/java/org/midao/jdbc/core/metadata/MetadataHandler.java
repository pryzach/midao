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

package org.midao.jdbc.core.metadata;

import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Metadata Handler is responsible for returning Stored Procedure/Function parameters
 */
public interface MetadataHandler {
    /**
     * Returns Stored Procedure/Function parameters
     *
     * @param conn SQL Connection
     * @param catalogName Database Catalog name. IF null is specified - this parameter is ignored
     * @param schemaName Database Schema name. IF null is specified - this parameter is ignored
     * @param procedureName Stored Procedure/Function name
     * @param useCache Indicated if Procedure/Function parameters should be cached. It improves speed but if Procedure/Function
     *                 was modified after it was cached - it won't be updated.
     * @return Stored Procedure/Function parameters
     * @throws SQLException
     */
	public QueryParameters getProcedureParameters(Connection conn, String catalogName, String schemaName, String procedureName, boolean useCache) throws SQLException;
}
