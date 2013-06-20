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

import org.midao.jdbc.core.MidaoLogger;
import org.midao.jdbc.core.MidaoTypes;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.MappingUtils;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

/**
 * TypeHandler Implementation created to support JDBC3/JDBC4 Drivers of MySQL/MariaDB, PostgreSQL, MsSQL.
 */
public class UniversalTypeHandler extends BaseTypeHandler {
    private static MidaoLogger logger = MidaoLogger.getLogger(UniversalTypeHandler.class);

    /**
     * Creates new BaseTypeHandler instance
     *
     * @param overrider overrider
     */
    public UniversalTypeHandler(Overrider overrider) {
        super(overrider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryParameters processInput(Statement stmt, QueryParameters params) throws SQLException {
        QueryParameters result = new QueryParameters(params);
        Object value = null;
        Object convertedValue = null;
        Integer convertedType = null;
        Connection conn = stmt.getConnection();

        for (String parameterName : params.keySet()) {
            value = params.getValue(parameterName);
            convertedValue = null;
            convertedType = null;

            if (params.getType(parameterName) == MidaoTypes.ARRAY) {

                if (value instanceof Object[]) {
                    convertedValue = TypeHandlerUtils.convertArray(conn, (Object[]) value);
                } else if (value instanceof Collection) {
                    convertedValue = TypeHandlerUtils.convertArray(conn, (Collection<?>) value);
                } else {
                    convertedValue = value;
                }

            } else if (params.getType(parameterName) == MidaoTypes.BLOB) {

                // the most stable way is to assign byte[] array directly while specifying type BINARY
                if (value instanceof String) {
                    convertedValue = ((String) value).getBytes();
                } else if (value instanceof InputStream) {
                    convertedValue = TypeHandlerUtils.toByteArray((InputStream) value);
                } else if (value instanceof byte[]) {
                    convertedValue = value;
                } else {
                    convertedValue = value;
                }

                convertedType = MidaoTypes.VARBINARY;

            } else if (params.getType(parameterName) == MidaoTypes.CLOB) {

                // the most stable way is to assign String directly while specifying type VARCHAR
                if (value instanceof String) {
                    convertedValue = value;
                } else if (value instanceof InputStream) {
                    convertedValue = new String(TypeHandlerUtils.toByteArray((InputStream) value));
                } else if (value instanceof byte[]) {
                    convertedValue = new String((byte[]) value);
                } else {
                    convertedValue = value;
                }

                convertedType = MidaoTypes.VARCHAR;

            } else if (params.getType(parameterName) == MidaoTypes.SQLXML) {

                // the most stable way is to assign String directly while specifying type VARCHAR
                if (value instanceof String) {
                    convertedValue = value;
                } else if (value instanceof InputStream) {
                    convertedValue = new String(TypeHandlerUtils.toByteArray((InputStream) value));
                } else if (value instanceof byte[]) {
                    convertedValue = new String((byte[]) value);
                } else {
                    convertedValue = value;
                }

                convertedType = MidaoTypes.VARCHAR;

            } else if (params.getType(parameterName) == MidaoTypes.VARCHAR && TypeHandlerUtils.isJDBC3(overrider) == true && value instanceof Reader) {
                convertedValue = TypeHandlerUtils.toString((Reader) value);
            } else if (params.getType(parameterName) == MidaoTypes.VARBINARY && TypeHandlerUtils.isJDBC3(overrider) == true && value instanceof InputStream) {
                convertedValue = TypeHandlerUtils.toByteArray((InputStream) value);
            } else {
                convertedValue = value;
            }

            // any other type processing can be added to DataBase specific TypeHandler implementation.
            result.updateValue(parameterName, convertedValue);

            if (convertedType != null) {
                result.updateType(parameterName, convertedType);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterExecute(Statement stmt, QueryParameters processedInput, QueryParameters params)  throws SQLException {
        Object value = null;
        Object convertedValue = null;
        Connection conn = stmt.getConnection();

        for (String parameterName : params.keySet()) {
            value = params.getValue(parameterName);
            convertedValue = processedInput.getValue(parameterName);

            try {

                if (params.getType(parameterName) == MidaoTypes.ARRAY) {

                    if (value instanceof Object[] || value instanceof Collection) {
                        if (convertedValue != null && MappingUtils.objectImplements(convertedValue, "java.sql.Array") == true) {
                            MappingUtils.invokeFunction(convertedValue, "free", new Class[]{}, new Object[]{});
                        }
                    }

                } else if (params.getType(parameterName) == MidaoTypes.BLOB) {
                    // don't do anything as we don't use BLOBs

                } else if (params.getType(parameterName) == MidaoTypes.CLOB) {
                    // don't do anything as we don't use CLOBs

                } else if (params.getType(parameterName) == MidaoTypes.SQLXML) {
                    // don't do anything as we don't use SQLXMLs

                }
            } catch (MidaoException ex) {
                logger.warning("Failed to close/free resource: " + parameterName + ". Might lead to resource leak!");
            }

        }
    }

    /**
     * Tries to guess "free" function of SQL Type container implementation class.
     * This is not best practise, but due to limitation of JDBC 3.0 standard - it should provide some compatibility
     * with existing JDBC 3.0 Drivers.
     *
     * @param sqlTypeContainer SQL Type implementation class
     * @throws MidaoException if "free" invocation failed
     */
    private void freeSilently(Object sqlTypeContainer) throws MidaoException {
        Class[] noParameters = new Class[]{};
        Object[] noValues = new Object[]{};

        if (MappingUtils.hasFunction(sqlTypeContainer, "close", noParameters) == true) {
            MappingUtils.invokeFunction(sqlTypeContainer, "close", noParameters, noValues);
        } else if (MappingUtils.hasFunction(sqlTypeContainer, "free", noParameters) == true) {
            MappingUtils.invokeFunction(sqlTypeContainer, "free", noParameters, noValues);
        } else if (MappingUtils.hasFunction(sqlTypeContainer, "freeTemporary", noParameters) == true) {
            MappingUtils.invokeFunction(sqlTypeContainer, "freeTemporary", noParameters, noValues);
        } else {
            throw new MidaoException("Cannot close resource: " + sqlTypeContainer);
        }
    }
}
