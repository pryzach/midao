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

import org.midao.jdbc.core.MidaoFactory;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class UpdateInputHandlerExample1 {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MidaoFactory.getQueryRunner(conn);

        try {
            runner.update("CREATE TABLE students ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "name VARCHAR(24) NOT NULL,"
                    + "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))");

            Map<String, Object> updateParameters = new HashMap<String, Object>();
            updateParameters.put("studentName", "not me");

            MapInputHandler input = new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", updateParameters);

            runner.update(input, new RowCountOutputHandler<Integer>());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP TABLE students");
        }
    }
}
