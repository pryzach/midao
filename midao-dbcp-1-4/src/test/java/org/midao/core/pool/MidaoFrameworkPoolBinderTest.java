/*
 * Copyright 2013 Zakhar Prykhoda
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

package org.midao.core.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbcp.SQLNestedException;
import org.junit.Before;
import org.junit.Test;
import org.midao.dao.pool.MidaoFrameworkPoolBinder;

public class MidaoFrameworkPoolBinderTest extends TestCase {
    private final static String PROP_DEFAULTAUTOCOMMIT = "defaultAutoCommit";
    private final static String PROP_DEFAULTREADONLY = "defaultReadOnly";
    private final static String PROP_DEFAULTTRANSACTIONISOLATION = "defaultTransactionIsolation";
    private final static String PROP_DEFAULTCATALOG = "defaultCatalog";
    private final static String PROP_DRIVERCLASSNAME = "driverClassName";
    private final static String PROP_MAXACTIVE = "maxActive";
    private final static String PROP_MAXIDLE = "maxIdle";
    private final static String PROP_MINIDLE = "minIdle";
    private final static String PROP_INITIALSIZE = "initialSize";
    private final static String PROP_MAXWAIT = "maxWait";
    private final static String PROP_TESTONBORROW = "testOnBorrow";
    private final static String PROP_TESTONRETURN = "testOnReturn";
    private final static String PROP_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    private final static String PROP_NUMTESTSPEREVICTIONRUN = "numTestsPerEvictionRun";
    private final static String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    private final static String PROP_TESTWHILEIDLE = "testWhileIdle";
    private final static String PROP_PASSWORD = "password";
    private final static String PROP_URL = "url";
    private final static String PROP_USERNAME = "username";
    private final static String PROP_VALIDATIONQUERY = "validationQuery";
    private final static String PROP_VALIDATIONQUERY_TIMEOUT = "validationQueryTimeout";
    private final static String PROP_INITCONNECTIONSQLS = "initConnectionSqls";
    private final static String PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED = "accessToUnderlyingConnectionAllowed";
    private final static String PROP_REMOVEABANDONED = "removeAbandoned";
    private final static String PROP_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    private final static String PROP_LOGABANDONED = "logAbandoned";
    private final static String PROP_POOLPREPAREDSTATEMENTS = "poolPreparedStatements";
    private final static String PROP_MAXOPENPREPAREDSTATEMENTS = "maxOpenPreparedStatements";
    private final static String PROP_CONNECTIONPROPERTIES = "connectionProperties";
	
	private static Properties poolProperties = new Properties();
	
	@Before
	public void setUp() {
		//"org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:mymemdb", "SA", ""
		//"org.h2.Driver", "jdbc:h2:�/test", "sa", "sa"
		poolProperties.put(PROP_URL, "jdbc:hsqldb:mem:mymemdb");
		poolProperties.put(PROP_DRIVERCLASSNAME, "org.hsqldb.jdbc.JDBCDriver");
		poolProperties.put(PROP_USERNAME, "SA");
		poolProperties.put(PROP_PASSWORD, "");
		
		poolProperties.put(PROP_MAXACTIVE, "10");
		poolProperties.put(PROP_INITIALSIZE, "100");
		
		System.out.println("INFO: Preparing to execute query on H2 SQL Database");
	}
	
	@Test
    public void testCreateDataSourceProperties() throws SQLException {
		DataSource dataSource =	MidaoFrameworkPoolBinder.createDataSource(this.poolProperties);
		
		assertNotNull(dataSource);
		assertNotNull(dataSource.getConnection());
		
		testDataSource(dataSource);
		
		((BasicDataSource) dataSource).close();
    }
    
	/*
	 * This type of invocation is not supported by H2
	 */
	
	@Test
    public void testCreateDataSourceURL() throws Exception {
		DataSource dataSource =	MidaoFrameworkPoolBinder.createDataSource(this.poolProperties.getProperty(PROP_URL));
		
		assertNotNull(dataSource);
		assertNotNull(dataSource.getConnection());

		testDataSource(dataSource);
		
		((BasicDataSource) dataSource).close();
    }
    
	@Test
    public void testCreateDataSourceWithoutDriverName() throws SQLException {
		DataSource dataSource =	MidaoFrameworkPoolBinder.createDataSource(this.poolProperties.getProperty(PROP_URL),
				this.poolProperties.getProperty(PROP_USERNAME),
				this.poolProperties.getProperty(PROP_PASSWORD));
		
		assertNotNull(dataSource);
		assertNotNull(dataSource.getConnection());
		
		testDataSource(dataSource);
		
		((BasicDataSource) dataSource).close();
    }
    
	@Test
    public void testCreateDataSourceWithDriverName() throws SQLException {
		DataSource dataSource =	MidaoFrameworkPoolBinder.createDataSource(this.poolProperties.getProperty(PROP_DRIVERCLASSNAME),
				this.poolProperties.getProperty(PROP_URL),
				this.poolProperties.getProperty(PROP_USERNAME),
				this.poolProperties.getProperty(PROP_PASSWORD));
		
		assertNotNull(dataSource);
		assertNotNull(dataSource.getConnection());
		
		testDataSource(dataSource);
		
		((BasicDataSource) dataSource).close();
    }
    
	@Test
    public void testCreateDataSourceAll() throws SQLException {
		DataSource dataSource =	MidaoFrameworkPoolBinder.createDataSource(this.poolProperties.getProperty(PROP_DRIVERCLASSNAME),
				this.poolProperties.getProperty(PROP_URL),
				this.poolProperties.getProperty(PROP_USERNAME),
				this.poolProperties.getProperty(PROP_PASSWORD),
				Integer.parseInt(this.poolProperties.getProperty(PROP_INITIALSIZE)),
				Integer.parseInt(this.poolProperties.getProperty(PROP_MAXACTIVE)));
		
		assertNotNull(dataSource);
		assertNotNull(dataSource.getConnection());
		
		testDataSource(dataSource);
		
		((BasicDataSource) dataSource).close();
    }
	
	private void testDataSource(DataSource dataSource) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			System.out.println("INFO: Creating connection.");
			conn = dataSource.getConnection();
			System.out.println("INFO: Creating statement.");
			stmt = conn.createStatement();
			System.out.println("INFO: Executing statement.");
			//stmt.execute("CREATE DATABASE TEST_DB");
			stmt.execute("CREATE TABLE TEST_TABLE (test varchar(6))");
			stmt.execute("INSERT INTO TEST_TABLE VALUES ('Sucess')");
			rset = stmt.executeQuery("SELECT * FROM TEST_TABLE");
			System.out.println("Results:");
			int numcols = rset.getMetaData().getColumnCount();
			while (rset.next()) {
				for (int i = 1; i <= numcols; i++) {
					System.out.print("\t" + rset.getString(i));
				}
				System.out.println("");
			}
			stmt.execute("DROP TABLE TEST_TABLE");
			//stmt.execute("DROP DATABASE TEST_DB");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
			System.out.println("");
		}
	}
}
