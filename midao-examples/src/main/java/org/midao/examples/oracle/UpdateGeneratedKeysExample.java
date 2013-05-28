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

import org.midao.core.MidaoConstants;
import org.midao.core.MidaoFactory;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.service.QueryRunnerService;
import org.midao.examples.derby.DerbyParameters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 */
public class UpdateGeneratedKeysExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = OracleParameters.createConnection();

        QueryRunnerService runner = MidaoFactory.getQueryRunner(conn);

        try {
            runner.update("CREATE TABLE students ("
                    + "id NUMBER(11),"
                    + "name VARCHAR2(24) NOT NULL,"
                    + "address VARCHAR2(1024)," + "PRIMARY KEY (id))");
            runner.update("CREATE SEQUENCE student_sq START WITH 1 INCREMENT BY 1");
            runner.update("CREATE OR REPLACE TRIGGER student_trg BEFORE INSERT ON students FOR EACH ROW BEGIN SELECT student_sq.nextval INTO :NEW.ID FROM DUAL; END;");

            MapOutputHandler handler = new MapOutputHandler();

            Map<String, Object> generatedKeys = null;

            generatedKeys = runner.overrideOnce(MidaoConstants.OVERRIDE_GENERATED_COLUMN_NAMES, new String [] {"ID"})
                    .update("INSERT INTO students (name, address) VALUES ('Not me', 'unknown')", handler, new Object[0]);
            generatedKeys = runner.overrideOnce(MidaoConstants.OVERRIDE_GENERATED_COLUMN_NAMES, new String [] {"ID"})
                    .update("INSERT INTO students (name, address) VALUES ('Not me', 'unknown')", handler, new Object[0]);

            System.out.println("Update generated keys: " + generatedKeys);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP TABLE students");

            runner.update("DROP SEQUENCE student_sq");
        }
    }
}
