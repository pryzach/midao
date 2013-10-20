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
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.handlers.xml.XmlInputOutputHandler;
import org.midao.jdbc.core.handlers.xml.XmlRepositoryFactory;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 */
public class UpdateXmlGeneratedKeysExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MjdbcFactory.getQueryRunner(conn);

        try {
            runner.update("CREATE TABLE students ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "name VARCHAR(24) NOT NULL,"
                    + "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))");

            Map<String, Object> generatedKeys = null;

            XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                    ("<?xml version=\"1.0\"?><root><update id='insertStudent' generateKeys='true' outputHandler='MapOutputHandler'>" +
                            "INSERT INTO students (name, address) VALUES ('Not me', 'unknown')" +
                            "</update></root>").getBytes()
            )));

            XmlInputOutputHandler handler1 = new XmlInputOutputHandler("insertStudent");

            generatedKeys = (Map<String, Object>) runner.execute(handler1);
            generatedKeys = (Map<String, Object>) runner.execute(handler1);
            generatedKeys = (Map<String, Object>) runner.execute(handler1);

            System.out.println("Update generated keys: " + generatedKeys);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP TABLE students");
        }
    }
}
