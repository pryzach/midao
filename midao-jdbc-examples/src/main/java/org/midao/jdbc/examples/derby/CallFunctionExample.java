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

package org.midao.jdbc.examples.derby;

import org.midao.jdbc.core.MjdbcFactory;
import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.*;
import java.util.HashMap;

/**
 */
public class CallFunctionExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MjdbcFactory.getQueryRunner(conn);

        try {

            // putting initial data into Database
            runner.update("CREATE TABLE students ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "name VARCHAR(24) NOT NULL,"
                    + "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))");

            runner.update(new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", new HashMap<String, Object>() {{
                put("studentName", "John");
            }}), new RowCountOutputHandler<Integer>());
            runner.update(new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", new HashMap<String, Object>() {{
                put("studentName", "Doe");
            }}), new RowCountOutputHandler<Integer>());

            runner.update("CREATE FUNCTION TEST_FUNC (ID integer) RETURNS varchar(30) PARAMETER STYLE JAVA LANGUAGE JAVA EXTERNAL NAME 'org.midao.jdbc.examples.derby.CallFunctionExample.testFunction'");

            // end of Database initialization

            QueryInputHandler input = null;
            QueryParameters parameters = new QueryParameters();
            QueryParameters result = null;

            parameters.set("id", 2, MjdbcTypes.INTEGER, QueryParameters.Direction.IN);
            parameters.set("name", null, MjdbcTypes.VARCHAR, QueryParameters.Direction.OUT);

            input = new QueryInputHandler("{:name = call TEST_FUNC(:id)}", parameters);

            result = runner.call(input);
            System.out.println("Call parameters returned: " + result);

            parameters.set("id", 1, MjdbcTypes.INTEGER, QueryParameters.Direction.IN);
            parameters.set("name", null, MjdbcTypes.VARCHAR, QueryParameters.Direction.OUT);

            input = new QueryInputHandler("{:name = call TEST_FUNC(:id)}", parameters);

            result = runner.call(input);
            System.out.println("Call parameters returned: " + result);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP FUNCTION TEST_FUNC");
            runner.update("DROP TABLE students");
        }
    }

    /*
     * DERBY JAVA SQL FUNCTIONS/PROCEDURES
     */
    public static String testFunction(Integer id) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");

        Statement stmt = null;
        stmt = conn.createStatement();

        String query = "SELECT name, id, address FROM students WHERE id = " + id;

        ResultSet rs = stmt.executeQuery(query);
        rs.next();

        return rs.getString(1);
    }
}
