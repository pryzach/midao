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
import org.midao.jdbc.core.handlers.model.CallResults;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.BeanOutputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.examples.Student;

import java.sql.*;
import java.util.HashMap;

/**
 */
public class CallORMReturnExample {
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

            runner.update("CREATE PROCEDURE TEST_PROC_RETURN (IN ID int) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 1 EXTERNAL NAME 'org.midao.jdbc.examples.derby.CallORMReturnExample.testProcedureReturn'");
            // end of Database initialization

            QueryInputHandler input = null;
            QueryParameters parameters = new QueryParameters();

            parameters.set("id", 2, MjdbcTypes.INTEGER, QueryParameters.Direction.IN);

            input = new QueryInputHandler("{call TEST_PROC_RETURN(:id)}", parameters);

            CallResults<QueryParameters, Student> result = runner.call(input, new BeanOutputHandler<Student>(Student.class));

            System.out.println("Object loaded from Database: " + result.getCallOutput());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP PROCEDURE TEST_PROC_RETURN");
            runner.update("DROP TABLE students");
        }
    }

    /*
     * DERBY JAVA SQL FUNCTIONS/PROCEDURES
     */
    public static void testProcedureReturn(Integer id, ResultSet[] rs) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        Statement stmt = null;
        String query = null;

        query = "SELECT name, id, address FROM students WHERE id = " + id;

        stmt = conn.createStatement();

        rs[0] = stmt.executeQuery(query);
    }
}
