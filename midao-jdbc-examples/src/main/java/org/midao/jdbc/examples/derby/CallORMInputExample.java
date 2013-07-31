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
import org.midao.jdbc.core.handlers.input.named.BeanInputHandler;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.handlers.type.BaseTypeHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.examples.Student;

import java.sql.*;
import java.util.HashMap;

/**
 */
public class CallORMInputExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MjdbcFactory.getQueryRunner(conn, BaseTypeHandler.class);

        try {

            // putting initial data into Database
            runner.update("CREATE TABLE students ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "name VARCHAR(24) NOT NULL,"
                    + "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))");

            runner.update(new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", new HashMap<String, Object>() {{
                put("studentName", "John");
                put("studentAddress22", "Somewhere safe");
            }}), new RowCountOutputHandler<Integer>());
            runner.update(new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", new HashMap<String, Object>() {{
                put("studentName", "Doe");
                put("studentAddress22", "Somewhere far");
            }}), new RowCountOutputHandler<Integer>());

            runner.update("CREATE PROCEDURE TEST_NAMED (IN ID integer, OUT NAME varchar(50), OUT ADDRESS varchar(50)) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 0 EXTERNAL NAME 'org.midao.jdbc.examples.derby.CallORMInputExample.testNamed'");
            // end of Database initialization

            BeanInputHandler<Student> input = null;
            Student student = new Student();
            student.setId(2);

            input = new BeanInputHandler<Student>("{call TEST_NAMED(:id, :name, :address)}", student);

            Student student1 = runner.call(input);

            student.setId(1);

            input = new BeanInputHandler<Student>("{call TEST_NAMED(:id, :name, :address)}", student);

            Student student2 = runner.call(input);

            System.out.println("Object filled from stored procedure: " + student1);
            System.out.println("Object filled from stored procedure: " + student2);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP PROCEDURE TEST_NAMED");
            runner.update("DROP TABLE students");
        }
    }

    /*
     * DERBY JAVA SQL FUNCTIONS/PROCEDURES
     */
    public static void testNamed(Integer id, String[] name, String[] address) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");

        Statement stmt = null;
        stmt = con.createStatement();

        String query = "SELECT name, id, address FROM students WHERE id = " + id;

        ResultSet rs = stmt.executeQuery(query);
        rs.next();

        name[0] = rs.getString("name");
        address[0] = rs.getString("address");

        rs.close();
    }
}
