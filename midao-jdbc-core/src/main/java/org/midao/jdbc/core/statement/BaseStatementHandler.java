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
import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.utils.AssertUtils;
import org.midao.jdbc.core.utils.MjdbcUtils;

import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base StatementHandler. Handles {@link PreparedStatement}
 */
public class BaseStatementHandler implements StatementHandler {
    private static MjdbcLogger logger = MjdbcLogger.getLogger(BaseStatementHandler.class);

    protected final Overrider overrider;

    protected Map<String, Object> localVariables = new HashMap<String, Object>();
    protected boolean useMetadata = true;

    /**
     * Creates new BaseStatementHandler instance
     *
     * @param overrider
     */
    public BaseStatementHandler(Overrider overrider) {
        this.overrider = overrider;
    }

    /**
     * {@inheritDoc}
     */
    public void setStatement(Statement statement, QueryParameters params) throws SQLException {
        AssertUtils.assertNotNull(params);

        PreparedStatement preparedStmt = (PreparedStatement) statement;

        // check the parameter count, if we can
        ParameterMetaData pmd = null;
        int stmtCount = -1;
        int paramsCount = params == null ? 0 : params.orderSize();

        try {
            if (useMetadata == true) {
                pmd = preparedStmt.getParameterMetaData();
                stmtCount = pmd.getParameterCount();
            } else {
                // check is not performed, assuming that it is equal. If not - exception would be thrown later...
                stmtCount = paramsCount;
            }
        } catch (Exception ex) {
            // driver doesn't support properly that function. Assuming it is equals
            useMetadata = false;
            stmtCount = paramsCount;
        }

        if (stmtCount != paramsCount) {
            if (this.overrider.hasOverride(MjdbcConstants.OVERRIDE_CONTROL_PARAM_COUNT) == true) {

                // value from this field is irrelevant, but I need to read the value in order to remove it if it should be invoked once.
                this.overrider.getOverride(MjdbcConstants.OVERRIDE_CONTROL_PARAM_COUNT);

                throw new SQLException("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
            } else {
                // Due to the fact that sometimes getParameterCount returns
                // unexpected value - warning about inconsistency but not throwing an exception.
                logger.warning("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
            }
        }

        // nothing to do here
        if (params == null) {
            return;
        }

        String parameterName = null;
        Object parameterValue = null;
        Integer parameterType = null;

        for (int i = 0; i < params.orderSize(); i++) {
            parameterName = params.getNameByPosition(i);
            parameterValue = params.getValue(parameterName);
            parameterType = params.getType(parameterName);

            if (params.isInParameter(parameterName) == true) {
                if (parameterValue != null) {
                    if (parameterType != null && parameterType.intValue() != MjdbcTypes.OTHER) {

                        try {
                            if (parameterType.intValue() == MjdbcTypes.VARCHAR && parameterValue instanceof Reader) {

                                //preparedStmt.setCharacterStream(i + 1, (Reader) parameterValue);
                                MappingUtils.invokeFunction(preparedStmt, "setCharacterStream",
                                        new Class[]{int.class, Reader.class},
                                        new Object[]{i + 1, parameterValue});

                            } else if (parameterType.intValue() == MjdbcTypes.VARBINARY && parameterValue instanceof InputStream) {

                                //preparedStmt.setBinaryStream(i + 1, (InputStream) parameterValue);
                                MappingUtils.invokeFunction(preparedStmt, "setBinaryStream",
                                        new Class[]{int.class, InputStream.class},
                                        new Object[]{i + 1, parameterValue});

                            } else {
                                preparedStmt.setObject(i + 1, parameterValue, parameterType);
                            }
                        } catch (MjdbcException ex) {
                            preparedStmt.setObject(i + 1, parameterValue, parameterType);
                        }

                    } else {
                        preparedStmt.setObject(i + 1, parameterValue);
                    }
                } else {
                    // VARCHAR works with many drivers regardless
                    // of the actual column type. Oddly, NULL and
                    // OTHER don't work with Oracle's drivers.
                    int sqlType = MjdbcTypes.VARCHAR;
                    if (useMetadata == true) {
                        try {
                            sqlType = pmd.getParameterType(i + 1);
                        } catch (SQLException e) {
                            useMetadata = false;
                        }
                    }
                    preparedStmt.setNull(i + 1, sqlType);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<QueryParameters> wrap(Statement stmt) throws SQLException {
        List<QueryParameters> mergedResult = new ArrayList<QueryParameters>();
        List<QueryParameters> converted = null;
        ResultSet rs = null;

        List<ResultSet> closedResultSet = new ArrayList<ResultSet>();

        // generating QueryParameters class with values from Statement
        QueryParameters statementParams = new QueryParameters();
        statementParams.set(HandlersConstants.STMT_UPDATE_COUNT, stmt.getUpdateCount());
        mergedResult.add(statementParams);

        if ((Integer) statementParams.getValue(HandlersConstants.STMT_UPDATE_COUNT) > 0 &&
                this.overrider.hasOverride(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS) == true) {

            // value from this field is irrelevant, but I need to read the value in order to remove it if it should be invoked once.
            this.overrider.getOverride(MjdbcConstants.OVERRIDE_INT_GET_GENERATED_KEYS);

            rs = stmt.getGeneratedKeys();

            if (rs != null) {
                converted = MappingUtils.convertResultSet(rs);
                mergedResult.addAll(converted);

                MjdbcUtils.closeQuietly(rs);
                closedResultSet.add(rs);
            }
        }

        rs = stmt.getResultSet();

        while (rs != null) {

            // it is possible that ResultSet might be already returned by getGeneratedKeys.
            if (closedResultSet.contains(rs) == false) {
                converted = MappingUtils.convertResultSet(rs);
                mergedResult.addAll(converted);

                MjdbcUtils.closeQuietly(rs);
                closedResultSet.add(rs);
            }

            if (stmt.getMoreResults() == true) {
                rs = stmt.getResultSet();
            } else {
                rs = null;
            }
        }

        return mergedResult;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] readStatement(Statement statement, QueryParameters params) throws SQLException {

        return new Object[params.orderSize()];
    }

    /**
     * {@inheritDoc}
     */
    public void beforeClose() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void afterClose() {
        // nothing to do
    }

}
