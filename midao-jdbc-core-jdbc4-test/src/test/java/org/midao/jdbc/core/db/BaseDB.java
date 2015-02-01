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

package org.midao.jdbc.core.db;

import junit.framework.TestCase;
import org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.utils.MidaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 */
public class BaseDB extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(BaseDB.class);

    protected Connection conn = null;
    protected DataSource dataSource = null;
    protected Map<String, Boolean> connectionEstablished = new HashMap<String, Boolean>();

    protected void establishConnection(String dbName) throws IOException {
        String serverDbName = null;
        String driver = null;
        DataSource ds = null;

        Properties props = new Properties();
        props.load(new FileInputStream("test.properties"));

        if (props.size() == 0) {
            throw new RuntimeException("Failed to load test.properties!");
        }

        serverDbName = props.getProperty(dbName + ".databaseName");
        driver = props.getProperty(dbName + ".className");

        //System.out.println("Establishing connection with: " + dbName);
        /*
        if (dbName.equals(DBConstants.derby) == true) {
            serverDbName = DBConstants.derbyDbName;
            driver = DBConstants.derbyDataSourceClass;
        } else if (dbName.equals(DBConstants.oracle) == true) {
        	serverDbName = DBConstants.oracleDbName;
        	driver = DBConstants.oracleDataSourceClass;
        } else if (dbName.equals(DBConstants.mysql) == true) {
        	serverDbName = DBConstants.mysqlDbName;
        	driver = DBConstants.mysqlDataSourceClass;
        } else if (dbName.equals(DBConstants.postgres) == true) {
        	serverDbName = DBConstants.postgresDbName;
        	driver = DBConstants.postgresDataSourceClass;
        }
        */
        try {

            MidaoUtils.loadDriver(driver);

            Class<?> dbClass = Class.forName(driver);
            ds = (DataSource) dbClass.newInstance();

            if (dbName.equals(DBConstants.derby) == true) {
                invokeSetFunction(ds, "createDatabase", props.getProperty(dbName + ".createDatabase"));
                invokeSetFunction(ds, "databaseName", serverDbName);
            } else if (dbName.equals(DBConstants.oracle) == true) {
                invokeSetFunction(ds, "driverType", props.getProperty(dbName + ".driverType"));
                invokeSetFunction(ds, "serverName", props.getProperty(dbName + ".serverName"));
                invokeSetFunction(ds, "databaseName", serverDbName);
                invokeSetFunction(ds, "portNumber", Integer.parseInt(props.getProperty(dbName + ".portNumber")));
                invokeSetFunction(ds, "user", props.getProperty(dbName + ".user"));
                invokeSetFunction(ds, "password", props.getProperty(dbName + ".password"));
            } else if (dbName.equals(DBConstants.mysql) == true) {
                invokeSetFunction(ds, "user", props.getProperty(dbName + ".user"));
                invokeSetFunction(ds, "password", props.getProperty(dbName + ".password"));
                invokeSetFunction(ds, "serverName", props.getProperty(dbName + ".serverName"));
                invokeSetFunction(ds, "databaseName", serverDbName);
            } else if (dbName.equals(DBConstants.postgres) == true) {
                invokeSetFunction(ds, "user", props.getProperty(dbName + ".user"));
                invokeSetFunction(ds, "password", props.getProperty(dbName + ".password"));
                invokeSetFunction(ds, "portNumber", Integer.parseInt(props.getProperty(dbName + ".portNumber")));
                invokeSetFunction(ds, "serverName", props.getProperty(dbName + ".serverName"));
                invokeSetFunction(ds, "databaseName", serverDbName);
            } else if (dbName.equals(DBConstants.mssql) == true) {
                invokeSetFunction(ds, "user", props.getProperty(dbName + ".user"));
                invokeSetFunction(ds, "password", props.getProperty(dbName + ".password"));
                invokeSetFunction(ds, "portNumber", Integer.parseInt(props.getProperty(dbName + ".portNumber")));
                invokeSetFunction(ds, "serverName", props.getProperty(dbName + ".serverName"));
                invokeSetFunction(ds, "databaseName", serverDbName);
            }

            this.conn = ds.getConnection();

            if (this.conn != null) {
                this.connectionEstablished.put(dbName, true);
            } else {
                this.connectionEstablished.put(dbName, false);
            }

            this.dataSource = ds;
        } catch (Exception ex) {
            this.connectionEstablished.put(dbName, false);
        }

        if (this.connectionEstablished.get(dbName) == false) {
            log.info("DB test would be skipped. Failed to establish connection with: [" + dbName + "].");
        }
    }

    protected void closeConnection() {
        MidaoUtils.closeQuietly(this.conn);
    }

    protected boolean checkConnected(String dbName) {
        return this.connectionEstablished.get(dbName);
    }

    protected static void closeQuietly(LazyOutputHandler handler) {
        try {
            if (handler != null) {
                handler.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void invokeSetFunction(Object bean, String functionName, Object value) {
        PropertyDescriptor[] descriptions = MappingUtils.propertyDescriptors(bean.getClass());

        for (PropertyDescriptor property : descriptions) {
            if (property.getName().equals(functionName) == true) {
                MappingUtils.callSetter(bean, property, value);
                break;
            }
        }
    }
}
