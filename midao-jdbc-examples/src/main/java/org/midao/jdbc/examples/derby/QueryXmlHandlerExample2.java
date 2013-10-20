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
import org.midao.jdbc.core.handlers.xml.XmlInputOutputHandler;
import org.midao.jdbc.core.handlers.xml.XmlRepositoryFactory;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.examples.Student;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 */
public class QueryXmlHandlerExample2 {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MjdbcFactory.getQueryRunner(conn);

        try {
            runner.update("CREATE TABLE students ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "name VARCHAR(24) NOT NULL,"
                    + "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))");

            runner.update("INSERT INTO students (name, address) VALUES ('Not me', 'unknown')", new RowCountOutputHandler<Integer>(), new Object[0]);

            String xmlContent = "<?xml version=\"1.0\"?>" +
                    "<root>" +
                    "<query id='findStudent' outputHandler='MapOutputHandler'>" +
                    "SELECT name FROM students WHERE id = #{id,jdbcType=INTEGER,mode=in}" +
                    "</query>" +
                    "</root>";

            // xml should be added to Repository before it can be executed
            XmlRepositoryFactory.addAll(
                    XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                            xmlContent.getBytes()
                    )
                    ));

            XmlInputOutputHandler handler = new XmlInputOutputHandler("findStudent", 1);

            // XML query execution
            Map<String, Object> student = (Map<String, Object>) runner.execute(handler);

            System.out.println("Query output: " + student.get("name"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP TABLE students");
        }
    }
}
