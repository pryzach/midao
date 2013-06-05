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

package org.midao.examples.oracle;

import org.midao.core.MidaoFactory;
import org.midao.core.MidaoTypes;
import org.midao.core.handlers.input.named.MapInputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.handlers.output.RowCountOutputHandler;
import org.midao.core.handlers.type.OracleTypeHandler;
import org.midao.core.service.QueryRunnerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class CallCursorRefExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = OracleParameters.createConnection();

        QueryRunnerService runner = MidaoFactory.getQueryRunner(conn, OracleTypeHandler.class);

        try {

            // putting initial data into Database
            runner.update("CREATE TABLE students ("
                    + "id NUMBER(11),"
                    + "name VARCHAR2(24) NOT NULL,"
                    + "address VARCHAR2(1024)," + "PRIMARY KEY (id))");
            runner.update("CREATE SEQUENCE student_sq START WITH 1 INCREMENT BY 1");
            runner.update("CREATE OR REPLACE TRIGGER student_trg BEFORE INSERT ON students FOR EACH ROW BEGIN SELECT student_sq.nextval INTO :NEW.ID FROM DUAL; END;");

            runner.update(new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", new HashMap<String, Object>() {{
                put("studentName", "John");
            }}), new RowCountOutputHandler<Integer>());
            runner.update(new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", new HashMap<String, Object>() {{
                put("studentName", "Doe");}}), new RowCountOutputHandler<Integer>());

            runner.update("CREATE OR REPLACE FUNCTION TEST_PROC_RETURN (p_ID in NUMBER) RETURN SYS_REFCURSOR AS cursor_ref SYS_REFCURSOR; BEGIN OPEN cursor_ref FOR SELECT NAME FROM students WHERE ID = p_ID; return cursor_ref; END;");
            // end of Database initialization

            QueryInputHandler input = null;
            QueryParameters parameters = new QueryParameters();

            parameters.set("cursor", null, oracle.jdbc.OracleTypes.CURSOR, QueryParameters.Direction.OUT);
            parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

            input = new QueryInputHandler("{CALL :cursor := TEST_PROC_RETURN(:id)}", parameters);
            CallResults<QueryParameters, Map<String, Object>> result = runner.call(input, new MapOutputHandler());
            List<QueryParameters> outputList = (List<QueryParameters>) result.getCallInput().getValue("cursor");

            System.out.println("Returned value from CursorRef: " + outputList);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP TABLE students");

            runner.update("DROP SEQUENCE student_sq");
        }
    }
}
