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

import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.output.ArrayOutputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBGenericQueryStructure {
    public static QueryStructure genericTransactionHandlerRollback(Map<String, Object> values) throws SQLException {
        return new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                //runner.update(CREATE_STUDENT_TABLE_DERBY);

                runner.setTransactionManualMode(true);
                runner.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);

                runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
                    put("studentName", "John");
                }}), new RowCountOutputHandler<Integer>());
                runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
                    put("studentName", "Doe");
                }}), new RowCountOutputHandler<Integer>());
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                runner.rollback();

                runner.setTransactionManualMode(false);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.DROP_STUDENT_TABLE);
            }

        };
    }

    public static QueryStructure genericExceptionHandler(Map<String, Object> values) throws SQLException {
        return new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                runner.query("SELECT fail FROM exception", new ArrayOutputHandler());
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
            }

        };
    }
}
