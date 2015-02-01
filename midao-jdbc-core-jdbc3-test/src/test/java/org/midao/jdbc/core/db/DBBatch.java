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

import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.sql.Statement;

public class DBBatch extends BaseDB {
    public static void batchWParamsDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        try {
            structure.create(runner);

            structure.execute(runner);

            int[] rowsUpdated = (int[]) structure.values.get("rowsUpdated");
            assertEquals(2, rowsUpdated.length);
            assertEquals(1, rowsUpdated[0] == Statement.SUCCESS_NO_INFO ? 1 : rowsUpdated[0]);
            assertEquals(1, rowsUpdated[1] == Statement.SUCCESS_NO_INFO ? 1 : rowsUpdated[1]);
        } finally {
            structure.drop(runner);
        }
    }

    public static void batchInputHandler1DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        try {
            structure.create(runner);

            structure.execute(runner);

            int[] rowsUpdated = (int[]) structure.values.get("rowsUpdated");
            assertEquals(2, rowsUpdated.length);
            assertEquals(1, rowsUpdated[0] == Statement.SUCCESS_NO_INFO ? 1 : rowsUpdated[0]);
            assertEquals(1, rowsUpdated[1] == Statement.SUCCESS_NO_INFO ? 1 : rowsUpdated[1]);
        } finally {
            structure.drop(runner);
        }
    }
}
