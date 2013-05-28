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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Universal TypeHandler Implementation.
 * Created to work with Derby, MySQL, Postgres and any other JDBC Driver which supports JDBC Standards.
 *
 * Written to support JDBC 4.0(Java 6). Please avoid using for JDBC 3.0(Java 5) Drivers.
 */
public class BaseTypeHandler implements TypeHandler {
    private static MidaoLogger logger = MidaoLogger.getLogger(BaseTypeHandler.class);

	private Map<String, Object> localVariables = new HashMap<String, Object>();
	private final Overrider overrider;

    /**
     * Creates new BaseTypeHandler instance
     *
     * @param overrider overrider
     */
	public BaseTypeHandler(Overrider overrider) {
		this.overrider = overrider;
	}

    /**
     * {@inheritDoc}
     */
	public QueryParameters processInput(Statement stmt, QueryParameters params) throws SQLException {
		QueryParameters result = new QueryParameters(params);
		Object value = null;
		Object convertedValue = null;
		Connection conn = stmt.getConnection();
		
		for (String parameterName : params.keySet()) {
			value = params.getValue(parameterName);
			convertedValue = null;
			
			if (params.getType(parameterName) == MidaoTypes.ARRAY) {
				
				if (value instanceof Object[]) {
					convertedValue = TypeHandlerUtils.convertArray(conn, (Object[]) value);
				} else if (value instanceof Collection) {
					convertedValue = TypeHandlerUtils.convertArray(conn, (Collection<?>) value);
				} else {
					convertedValue = value;
				}
				
			} else if (params.getType(parameterName) == MidaoTypes.BLOB) {
				
				if (value instanceof String) {
					convertedValue = TypeHandlerUtils.convertBlob(conn, (String) value);
				} else if (value instanceof InputStream) {
					convertedValue = TypeHandlerUtils.convertBlob(conn, (InputStream) value);
				} else if (value instanceof byte[]) {
					convertedValue = TypeHandlerUtils.convertBlob(conn, (byte[]) value);
				} else {
					convertedValue = value;
				}
				
			} else if (params.getType(parameterName) == MidaoTypes.CLOB) {
				
				if (value instanceof String) {
					convertedValue = TypeHandlerUtils.convertClob(conn, (String) value);
				} else if (value instanceof InputStream) {
					convertedValue = TypeHandlerUtils.convertClob(conn, (InputStream) value);
				} else if (value instanceof byte[]) {
					convertedValue = TypeHandlerUtils.convertClob(conn, (byte[]) value);
				} else {
					convertedValue = value;
				}
				
			} else if (params.getType(parameterName) == MidaoTypes.SQLXML) {

                if (value instanceof String) {
                    convertedValue = TypeHandlerUtils.convertSqlXml(conn, (String) value);
                } else if (value instanceof InputStream) {
                    convertedValue = TypeHandlerUtils.convertSqlXml(conn, (InputStream) value);
                } else if (value instanceof byte[]) {
                    convertedValue = TypeHandlerUtils.convertSqlXml(conn, (byte[]) value);
                } else {
                    convertedValue = value;
                }

			} else {
				convertedValue = value;
			}
			
			// any other type processing can be added to DataBase specific TypeHandler implementation.
			
			result.updateValue(parameterName, convertedValue);
		}
		
		return result;
	}

    /**
     * {@inheritDoc}
     */
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

                    if (value instanceof String || value instanceof InputStream || value instanceof byte[]) {
                        if (convertedValue != null && MappingUtils.objectImplements(convertedValue, "java.sql.Blob") == true) {
                            MappingUtils.invokeFunction(convertedValue, "free", new Class[]{}, new Object[]{});
                        }
                    }

                } else if (params.getType(parameterName) == MidaoTypes.CLOB) {

                    if (value instanceof String || value instanceof InputStream || value instanceof byte[]) {
                        if (convertedValue != null && MappingUtils.objectImplements(convertedValue, "java.sql.Clob") == true) {
                            MappingUtils.invokeFunction(convertedValue, "free", new Class[]{}, new Object[]{});
                        }
                    }

                } else if (params.getType(parameterName) == MidaoTypes.SQLXML) {

                    if (value instanceof String || value instanceof InputStream || value instanceof byte[]) {
                        if (convertedValue != null && MappingUtils.objectImplements(convertedValue, "java.sql.SQLXML") == true) {
                            MappingUtils.invokeFunction(convertedValue, "free", new Class[]{}, new Object[]{});
                        }
                    }

                }
            } catch (MidaoException ex) {
                logger.warning("Failed to close/free resource: " + parameterName + ". Might lead to resource leak!");
            }

        }
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

                        MappingUtils.invokeFunction(value, "free", new Class[]{}, new Object[]{});
                    } else {
                        convertedValue = value;
                    }

                } else if (params.getType(parameterName) == MidaoTypes.BLOB) {

                    if (value != null && MappingUtils.objectImplements(value, "java.sql.Blob") == true) {
                        //sqlBlob = (java.sql.Blob) value;
                        convertedValue = TypeHandlerUtils.readBlob(value);

                        MappingUtils.invokeFunction(value, "free", new Class[]{}, new Object[]{});
                    } else {
                        convertedValue = value;
                    }

                } else if (params.getType(parameterName) == MidaoTypes.CLOB) {

                    if (value != null && MappingUtils.objectImplements(value, "java.sql.Clob") == true) {
                        //sqlClob = (java.sql.Clob) value;
                        convertedValue = new String(TypeHandlerUtils.readClob(value));

                        MappingUtils.invokeFunction(value, "free", new Class[]{}, new Object[]{});
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
                } else if (params.getType(parameterName) == MidaoTypes.SQLXML) {

                    if (value != null && MappingUtils.objectImplements(value, "java.sql.SQLXML") == true) {
                        //sqlXml = (java.sql.SQLXML) value;
                        convertedValue = TypeHandlerUtils.readSqlXml(value);

                        MappingUtils.invokeFunction(value, "free", new Class[]{}, new Object[]{});
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

}
