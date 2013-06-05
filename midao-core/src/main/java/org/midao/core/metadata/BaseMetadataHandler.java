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

package org.midao.core.metadata;

import org.midao.core.exception.MidaoSQLException;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.utils.MidaoUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Base Metadata Handler is responsible for returning Stored Procedure/Function parameters
 */
public class BaseMetadataHandler implements MetadataHandler {
	private Map<StoredProcedure, QueryParameters> procedureParameters = new TreeMap<StoredProcedure, QueryParameters>();

    /**
     * Creates new BaseMetadataHandler instance
     *
     * @param conn SQL connection
     * @param useCache invokes caching of all Stored Procedures available via specified connection
     * @throws SQLException
     */
	public BaseMetadataHandler(Connection conn, boolean useCache) throws SQLException {
		if (useCache == true) {
            DatabaseMetaData metaData = conn.getMetaData();

			updateCache(metaData, null, null, null);
		}
	}

    /**
     * Creates new BaseMetadataHandler instance
     *
     * @param ds SQL DataSource
     * @param useCache invokes caching of all Stored Procedures available via specified connection
     * @throws SQLException
     */
	public BaseMetadataHandler(DataSource ds, boolean useCache) throws SQLException {
		Connection conn = null;
		
		if (useCache == true) {
			try {
				conn = ds.getConnection();
                DatabaseMetaData metaData = conn.getMetaData();

				updateCache(metaData, null, null, null);
			} catch (SQLException ex) {
				throw ex;
			} finally {
				MidaoUtils.closeQuietly(conn);
			}
		}
	}

    /**
     * Function which is responsible for retrieving Stored Procedure parameters and storage it into cache.
     * This function doesn't perform cache read - only cache update.
     *
     * @param metaData Database Metadata description class
     * @param catalogName Database Catalog
     * @param schemaName Database Schema
     * @param procedureName Procedure/Function name
     * @return amount of elements put into cache
     * @throws SQLException
     */
	public int updateCache(DatabaseMetaData metaData, String catalogName, String schemaName, String procedureName) throws SQLException {
        String dbProductName = processDatabaseProductName(metaData.getDatabaseProductName());

		ResultSet procedures = null;
		ResultSet procedureParameters = null;
		List<String> proceduresList = new ArrayList<String>();
		
		String procedureCatalog = null;
		String procedureSchema = null;
		String procedureNameFull = null;
		
		String procedureParameterName = null;
		Integer procedureParameterDirection = null;
		Integer procedureParameterType = null;
		QueryParameters.Direction direction = null;
		
		QueryParameters procedureParams = null;
		StoredProcedure procedurePath = null;
		
		procedures = metaData.getProcedures(catalogName, schemaName, procedureName);
		
		while (procedures.next() == true) {
			procedureCatalog = procedures.getString("PROCEDURE_CAT");
			procedureSchema = procedures.getString("PROCEDURE_SCHEM");
			procedureNameFull = processProcedureName(dbProductName, procedures.getString("PROCEDURE_NAME"));
			
			procedurePath = new StoredProcedure(procedureCatalog, procedureSchema, procedureNameFull);
			procedureParameters = metaData.getProcedureColumns(procedureCatalog, procedureSchema, procedureNameFull, null);
			
			proceduresList.add(procedureCatalog + "." + procedureSchema + "." + procedureNameFull);
			procedureParams = new QueryParameters();
			
			//procedureParameters = metaData.getProcedureColumns(null, procedureSchema, "%", "%");
			//procedureParameters = metaData.getProcedureColumns(procedureCatalog, procedureSchema, "%" + procedureNameFull + "%", "%");
			
			while (procedureParameters.next() == true) {
				
				//procedureCatalog = procedureParameters.getString("PROCEDURE_CAT");
				//procedureSchema = procedureParameters.getString("PROCEDURE_SCHEM");
				//procedureNameFull = procedureParameters.getString("PROCEDURE_NAME");
				//System.out.println(procedureCatalog + "." + procedureSchema + "." + procedureNameFull);
				
				procedureParameterName = procedureParameters.getString("COLUMN_NAME");
				procedureParameterDirection = procedureParameters.getInt("COLUMN_TYPE");
				procedureParameterType = procedureParameters.getInt("DATA_TYPE");
				
				if (procedureParameterName == null && (procedureParameterDirection == DatabaseMetaData.procedureColumnIn || procedureParameterDirection == DatabaseMetaData.procedureColumnInOut || procedureParameterDirection == DatabaseMetaData.procedureColumnOut)) {
					// according to Spring - it is probably a member of a collection
				} else {
					direction = convertToDirection(procedureParameterDirection);

    			    procedureParams.set(procedureParameterName, null, procedureParameterType, direction, procedureParams.size());
				}
			}
			
			MidaoUtils.closeQuietly(procedureParameters);
			
			this.procedureParameters.put(procedurePath, procedureParams);
		}
		
		MidaoUtils.closeQuietly(procedures);
		
		return proceduresList.size();
	}

    /**
     * {@inheritDoc}
     */
	public QueryParameters getProcedureParameters(Connection conn, String catalogName, String schemaName, String procedureName,
			boolean useCache) throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		
		String userName = metaData.getUserName();
		String dbProductName = processDatabaseProductName(metaData.getDatabaseProductName());
		
		String catalogNameProcessed = processCatalogName(dbProductName, userName, catalogName);
		String schemaNameProcessed = processSchemaName(dbProductName, userName, schemaName);
		String procedureNameProcessed = processProcedureName(dbProductName, procedureName);
		
		StoredProcedure requiredStoredProcedure = new StoredProcedure(catalogName, schemaName, procedureName);
		
		if (useCache == false || this.procedureParameters.containsKey(requiredStoredProcedure) == false) {
			updateCache(metaData, catalogNameProcessed, schemaNameProcessed, procedureNameProcessed);
		}
		
		List<StoredProcedure> foundStoredProcedures = new ArrayList<StoredProcedure>();
		
		// TODO Optimize
		for (StoredProcedure storedProcedure : this.procedureParameters.keySet()) {
			if (requiredStoredProcedure.equals(storedProcedure) == true) {
				foundStoredProcedures.add(storedProcedure);
			}
		}
		
		if (foundStoredProcedures.size() == 0) {
			throw new MidaoSQLException("Haven't found procedure matching required parameters: " + requiredStoredProcedure);
		} else if (foundStoredProcedures.size() > 1) {
			throw new MidaoSQLException("Found more than one procedure matching required parameters. Searched: " + requiredStoredProcedure + "\nGot: " + foundStoredProcedures.toString());
		}
		
		return this.procedureParameters.get(foundStoredProcedures.get(0));
	}

    /**
     * Converts SQL column type (returned from Database Metadata description class) into QueryParameters Direction
     * Enumeration
     *
     * @param columnType SQL column type (returned from DatabaseMetadata class)
     * @return QueryParameters.Direction value
     */
	private QueryParameters.Direction convertToDirection(int columnType) {
		QueryParameters.Direction result = null;
		
		if (columnType == DatabaseMetaData.procedureColumnIn) {
			result = QueryParameters.Direction.IN; 
		} else if (columnType == DatabaseMetaData.procedureColumnInOut) {
			result = QueryParameters.Direction.INOUT;
		} else if (columnType == DatabaseMetaData.procedureColumnOut) {
			result = QueryParameters.Direction.OUT;
		} else if (columnType == DatabaseMetaData.procedureColumnReturn) {
			result = QueryParameters.Direction.RETURN;
		} else if (columnType == DatabaseMetaData.procedureColumnResult) {
			result = QueryParameters.Direction.RETURN;
		} else {
			throw new IllegalArgumentException("Incorrect column type: " + columnType);
		}
		
		return result;
	}

    /**
     * Reads value returned from getDatabaseProductName (DatabaseMetadata class) and converts it into
     * "short" name
     *
     * @param databaseProductName {@link java.sql.DatabaseMetaData#getDatabaseProductName()}
     * @return short database name
     */
	private String processDatabaseProductName(String databaseProductName) {
		String result = databaseProductName;
		
		if (databaseProductName != null && databaseProductName.startsWith("DB2") == true) {
			result = "DB2";
		} else if ("Sybase SQL Server".equals(databaseProductName) == true
				|| "Adaptive Server Enterprise".equals(databaseProductName) == true || "ASE".equals(databaseProductName) == true
				|| "sql server".equals(databaseProductName) == true) {
			result = "Sybase";
		}
		
		return result;
	}

    /**
     * Processes Catalog name so it would be compatible with database
     *
     * @param dbProvideName short database name
     * @param userName user name
     * @param catalogName catalog name which would be processed
     * @return processed catalog name
     */
	private String processCatalogName(String dbProvideName, String userName, String catalogName) {
		String result = null;
		
		if (catalogName != null) {
			result = catalogName.toUpperCase();
		} else {
			if ("Oracle".equals(dbProvideName) == true) {
				// SPRING: Oracle uses catalog name for package name or an empty string if no package
				result = "";
			}
		}
		
		return result;
	}

    /**
     * Processes Schema name so it would be compatible with database
     *
     * @param dbProductName short database name
     * @param userName user name
     * @param schemaName schema name which would be processed
     * @return processed schema name
     */
	private String processSchemaName(String dbProductName, String userName, String schemaName) {
		String result = null;
		
		if (schemaName != null) {
			result = schemaName.toUpperCase();
		} else {
			if ("DB2".equals(dbProductName) == true || "Apache Derby".equals(dbProductName) == true || "Oracle".equals(dbProductName) == true) {
				if (userName != null) {
					result = userName.toUpperCase();
				}
			} else if ("PostgreSQL".equals(dbProductName) == true) {
				// for PostgreSQL - if no schema specified - public should be used
				result = "public";
			}
		}
		
		return result;
	}

    /**
     * Processes Procedure/Function name so it would be compatible with database
     * Also is responsible for cleaning procedure name returned by DatabaseMetadata
     *
     * @param dbProductName short database name
     * @param procedureName Stored Procedure/Function
     * @return processed Procedure/Function name
     */
	private String processProcedureName(String dbProductName, String procedureName) {
		String result = null;
		
		if (procedureName != null) {
			if ("Microsoft SQL Server".equals(dbProductName) == true || "Sybase".equals(dbProductName) == true) {
                if ("Microsoft SQL Server".equals(dbProductName) == true && procedureName.indexOf(";") > 1) {
                    result = procedureName.substring(0, procedureName.indexOf(";")).toUpperCase();
                } else if (procedureName.length() > 1 && procedureName.startsWith("@") == true) {
					result = procedureName.substring(1).toUpperCase();
                } else {
					result = procedureName.toUpperCase();
				}
			} else if ("PostgreSQL".equals(dbProductName) == true) {
				result = procedureName.toLowerCase();
			} else {
				result = procedureName.toUpperCase();
			}
		}
		
		return result;
	}
}
