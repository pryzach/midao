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

package org.midao.jdbc.core.handlers.type;

import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Automatically converts Java types into JDBC SQL Types
 */
public interface TypeHandler {

    /**
     * Reads @params and creates new QueryParameters with converted Java types into JDBC SQL Types
     *
     * @param stmt   JDBC Statement
     * @param params QueryParameters which are read
     * @return QueryParameters with converted values into JDBC SQL Type
     * @throws SQLException
     */
    public QueryParameters processInput(Statement stmt, QueryParameters params) throws SQLException;

    /**
     * Reads @processedInput and closes all JDBC SQL Type to prevent any memory leaks.
     *
     * @param stmt           JDBC Statement
     * @param processedInput values returned from {@link #processInput(java.sql.Statement, org.midao.jdbc.core.handlers.model.QueryParameters)}
     * @param params         original values
     * @throws SQLException
     */
    public void afterExecute(Statement stmt, QueryParameters processedInput, QueryParameters params) throws SQLException;

    /**
     * Processes QueryParameters returned after Statement execution and converts
     * any JDBC SQL Type into Java Type
     *
     * @param stmt   JDBC Statement
     * @param params QueryParameters returned after Statement execution
     * @return QueryParameters with converted values into Java Type
     * @throws SQLException
     */
    public QueryParameters processOutput(Statement stmt, QueryParameters params) throws SQLException;

    /**
     * Works the same as {@link #processInput(java.sql.Statement, org.midao.jdbc.core.handlers.model.QueryParameters)}
     * Allows processing of the List
     *
     * @param stmt       JDBC Statement
     * @param paramsList QueryParameters List returned after Statement execution
     * @return QueryParameters List with converted values into Java Type
     * @throws SQLException
     */
    public List<QueryParameters> processOutput(Statement stmt, List<QueryParameters> paramsList) throws SQLException;

}
