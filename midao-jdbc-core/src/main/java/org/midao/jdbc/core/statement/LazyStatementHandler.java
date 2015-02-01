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

import org.midao.jdbc.core.MjdbcConstants;
import org.midao.jdbc.core.MjdbcLogger;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.model.QueryParametersLazyList;
import org.midao.jdbc.core.handlers.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Universal Statement handler (handles both {@link java.sql.PreparedStatement} and {@link CallableStatement}).
 * Returns query output as Lazy list. If used in conjuction with {@link org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler} -
 * allows reading huge amount of data without using too much memory.
 * <p/>
 * From {@link BaseStatementHandler} inherits PreparedStatement functionality.
 * From {@link CallableStatementHandler} inherits CallableStatement functionality.
 *
 * @see {@link CallableStatementHandler}
 * @see {@link BaseStatementHandler}
 */
public class LazyStatementHandler extends CallableStatementHandler {
    private static MjdbcLogger logger = MjdbcLogger.getLogger(LazyStatementHandler.class);

    /**
     * Creates new BaseStatementHandler instance
     *
     * @param overrider
     */
    public LazyStatementHandler(Overrider overrider) {
        super(overrider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatement(Statement statement, QueryParameters params) throws SQLException {
        super.setStatement(statement, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QueryParameters> wrap(Statement stmt) throws SQLException {
        QueryParametersLazyList result = null;
        ResultSet rs = null;
        boolean readGeneratedKeys = false;

        if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS) == true) {

            // value from this field is irrelevant, but I need to read the value in order to remove it if it should be invoked once.
            this.overrider.getOverride(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS);
            readGeneratedKeys = true;
        }

        TypeHandler typeHandler = (TypeHandler) this.overrider.getOverride(MjdbcConstants.OVERRIDE_INT_TYPE_HANDLER);

        // creating new Lazy cache instance with unlimited caching. If LazyOutputCache is used - limit would be set later on
        result = new QueryParametersLazyList(stmt, typeHandler, readGeneratedKeys, -1);

        return result;
    }

}
