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
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBBatchQueryStructure {

    public static QueryStructure batchWParamsDS(Map<String, Object> values) {
        return new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                this.values.put("rowsUpdated", runner.batch(DBConstants.INSERT_STUDENT_TABLE_W_PARAMS, new Object[][]{new Object[]{"not me"}, new Object[]{"not me either"}}));
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.DROP_STUDENT_TABLE);
            }

        };

    }

    public static QueryStructure batchInputHandler1DS(Map<String, Object> values) {
        return new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                MapInputHandler[] inputs = new MapInputHandler[]{
                        new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
                            put("studentName", "not me");
                        }}),
                        new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
                            put("studentName", "not me either");
                        }})
                };

                this.values.put("rowsUpdated", runner.batch(inputs));
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.DROP_STUDENT_TABLE);
            }

        };
    }
}
