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
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.BeanLazyScrollUpdateOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyScrollUpdateOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.examples.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class QueryOutputLazyScrollUpdateHandlerExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MjdbcFactory.getQueryRunner(conn);
        runner.setTransactionManualMode(true);

        try {
            runner.update("CREATE TABLE students ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "name VARCHAR(24) NOT NULL,"
                    + "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))");

            Map<String, Object> insertValues = null;

            for (int i = 0; i < 3; i++) {
                insertValues = new HashMap<String, Object>();
                insertValues.put("studentName", "Someone");

                MapInputHandler input = new MapInputHandler("INSERT INTO students (name) VALUES (:studentName)", insertValues);

                runner.update(input, new RowCountOutputHandler<Integer>());
                runner.commit();
            }

            MapInputHandler input = new MapInputHandler("SELECT * FROM students", null);

            MapLazyScrollUpdateOutputHandler lazyMapList = runner.query(input, new MapLazyScrollUpdateOutputHandler());

            BeanLazyScrollUpdateOutputHandler<Student> lazyBeanList = runner.query(input, new BeanLazyScrollUpdateOutputHandler<Student>(Student.class));

            int lazyMapListSize = 0;
            int lazeBeanListSize = 0;

            while (lazyMapList.hasNext() == true) {
                if (lazyMapList.getNext() != null) lazyMapListSize++;
            }

            // going to very beginning
            lazyMapList.moveRelative(-3);
            if (lazyMapList.hasPrev() == true ||
                    (Integer) lazyMapList.getCurrent().get("id") != 1) {

                throw new MjdbcRuntimeException("This never happened before, embarrassing.");
            }

            while (lazyBeanList.hasNext() == true) {
                if (lazyBeanList.getNext() != null) lazeBeanListSize++;
            }

            // going to very beginning
            lazyBeanList.moveRelative(-3);
            if (lazyBeanList.hasPrev() == true || lazyBeanList.getCurrent().getId() != 1) {

                throw new MjdbcRuntimeException("This never happened before, embarrassing.");
            }

            lazyMapList.close();
            lazyBeanList.close();

            System.out.println(String.format("Lazy loaded maps: %d and beans: %d", lazyMapListSize, lazeBeanListSize));
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP TABLE students");
            runner.commit();
        }
    }
}
