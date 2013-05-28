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

import org.midao.core.MidaoLogger;
import org.midao.core.MidaoTypes;
import org.midao.core.Overrider;
import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.utils.MappingUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Written to support JDBC 3.0(Java 5). For JDBC 4.0 (Java 6) please use {@link BaseTypeHandler}
 *
 * Doesn't support majority of features of {@link BaseTypeHandler} due to limitation of JDBC 3.0.
 *
 * If this handler won't work as expected - please use {@link EmptyTypeHandler} for JDBC 3.0 (Java 5) and
 * convert all types manually using JDBC Driver Implementation.
 */
public class JDBC3TypeHandler implements TypeHandler {
    private static MidaoLogger logger = MidaoLogger.getLogger(JDBC3TypeHandler.class);

	private Map<String, Object> localVariables = new HashMap<String, Object>();
	private final Overrider overrider;

    /**
     * Creates new BaseTypeHandler instance
     *
     * @param overrider overrider
     */
	public JDBC3TypeHandler(Overrider overrider) {
		this.overrider = overrider;
	}

    /**
     * {@inheritDoc}
     */
	public QueryParameters processInput(Statement stmt, QueryParameters params) throws SQLException {
        return params;
	}

    /**
     * {@inheritDoc}
     */
	public void afterExecute(Statement stmt, QueryParameters processedInput, QueryParameters params)  throws SQLException {
        // do nothing
	}

    /**
     * {@inheritDoc}
     */
	public QueryParameters processOutput(Statement stmt, QueryParameters params) throws SQLException {
		Object value = null;
		Object convertedValue = null;
		
		//java.sql.Array sqlArray = null;
		//java.sql.Blob sqlBlob = null;
		//java.sql.Clob sqlClob = null;
		//java.sql.SQLXML sqlXml = null;
		
		for (String parameterName : params.keySet()) {
			value = params.getValue(parameterName);
			
			convertedValue = null;

            try {

                if (params.getType(parameterName) == MidaoTypes.ARRAY) {

                    if (value != null && MappingUtils.objectImplements(value, "java.sql.Array") == true) {
                        //sqlArray = ((java.sql.Array) value);
                        //convertedValue = sqlArray.getArray();

                        convertedValue = MappingUtils.invokeFunction(value, "getArray", new Class[]{}, new Object[]{});

                        freeSilently(value);
                    } else {
                        convertedValue = value;
                    }

                } else if (params.getType(parameterName) == MidaoTypes.BLOB) {

                    if (value != null && MappingUtils.objectImplements(value, "java.sql.Blob") == true) {
                        //sqlBlob = (java.sql.Blob) value;
                        convertedValue = TypeHandlerUtils.readBlob(value);

                        freeSilently(value);
                    } else {
                        convertedValue = value;
                    }

                } else if (params.getType(parameterName) == MidaoTypes.CLOB) {

                    if (value != null && MappingUtils.objectImplements(value, "java.sql.Clob") == true) {
                        //sqlClob = (java.sql.Clob) value;
                        convertedValue = new String(TypeHandlerUtils.readClob(value));

                        freeSilently(value);
                    } else {
                        convertedValue = value;
                    }
                } else if (params.getType(parameterName) == MidaoTypes.OTHER) {
                    if (value instanceof ResultSet) {
                        ResultSet rs = (ResultSet) value;
                        convertedValue = MappingUtils.convertResultSet(rs);
                    } else {
                        convertedValue = value;
                    }
                } else {
                    convertedValue = value;
                }
            } catch (MidaoException ex) {
                logger.warning("Failed to process/close resource: " + parameterName + ". Might lead to resource leak!");
                convertedValue = value;
            }
			
			// any other type processing can be added to DataBase specific TypeHandler implementation.
			
			params.updateValue(parameterName, convertedValue);
		}
		
		return params;
	}

    /**
     * {@inheritDoc}
     */
	public List<QueryParameters> processOutput(Statement stmt, List<QueryParameters> paramsList) throws SQLException {
		QueryParameters params = null;
		
		for (int i = 1; i < paramsList.size(); i++) {
			params = paramsList.get(i);
			
			params = processOutput (stmt, params);
			
			paramsList.set(i, params);
		}
		
		return paramsList;
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
