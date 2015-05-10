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

import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Statement Handler is responsible for setting and reading values from java.sql.Statement
 */
public interface StatementHandler {

    /**
     * Sets Query input Parameters into @statement
     * Invoked only when @statement is instance of PreparedStatement or CallableStatement
     *
     * @param statement sql Statement
     * @param params    Query input Parameters
     * @throws SQLException
     */
    public void setStatement(Statement statement, QueryParameters params) throws SQLException;

    /**
     * Reads all Statement output - Generated Keys, Result sets and wraps them into
     * List<QueryParameters>.
     * First element should always be added to keep some technical values (update count etc.).
     *
     * @param stmt sql Statement
     * @return All output returned by sql Statement
     * @throws SQLException
     */
    public List<QueryParameters> wrap(Statement stmt) throws SQLException;

    /**
     * The purpose of this function is to read all OUT/INOUT parameters from Statement
     * and return them as Array.
     * Returned array size is equals @params.size(). Every non OUT parameter should be set as null value
     *
     * @param statement sql Statement
     * @param params    input QueryParameters
     * @return array of out parameters(and null for in)
     * @throws SQLException
     */
    public Object[] readStatement(Statement statement, QueryParameters params) throws SQLException;

    /**
     * Function is invoked before Connection is closed.
     * In here local variables can be released
     *
     * @throws SQLException
     */
    public void beforeClose() throws SQLException;

    /**
     * Function is invoked after Connection is closed.
     * In here local variables can be released
     *
     * @throws SQLException
     */
    public void afterClose() throws SQLException;

}
