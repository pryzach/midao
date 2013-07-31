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

package org.midao.jdbc.examples.oracle;

import org.midao.jdbc.core.MjdbcFactory;
import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.CallResults;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.handlers.type.OracleTypeHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 */
public class CallLargeParametersExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = OracleParameters.createConnection();

        QueryRunnerService runner = MjdbcFactory.getQueryRunner(conn, OracleTypeHandler.class);

        try {

            // putting initial data into Database
            runner.update("CREATE OR REPLACE PROCEDURE TEST_PROC_LARGE (clobIn in CLOB, clobOut out CLOB, blobIn in BLOB, blobOut out BLOB) IS\n" +
                    "clobChar VARCHAR2(12);\n" +
                    "blobChar VARCHAR2(12);\n" +
                    "tempBlob BLOB;\n" +
                    "BEGIN\n" +
                    "clobChar := 'Hello ' || CAST(clobIn AS VARCHAR2);\n" +
                    "clobOut := CAST(clobChar AS CLOB);\n" +
                    "blobChar := 'Hi ' || UTL_RAW.CAST_TO_VARCHAR2(blobIn);\n" +
                    "DBMS_LOB.CREATETEMPORARY(blobOut, TRUE);\n" +
                    "DBMS_LOB.OPEN(blobOut, DBMS_LOB.LOB_READWRITE);\n" +
                    "DBMS_LOB.WRITE(blobOut, LENGTH(blobChar), 1, utl_raw.cast_to_raw(blobChar));\n" +
                    "END;\n");

            QueryInputHandler input = null;
            QueryParameters parameters = new QueryParameters();

            parameters.set("clobIn", "John", MjdbcTypes.CLOB, QueryParameters.Direction.IN);
            parameters.set("clobOut", null, MjdbcTypes.CLOB, QueryParameters.Direction.OUT);

            parameters.set("blobIn", "Doe", MjdbcTypes.BLOB, QueryParameters.Direction.IN);
            parameters.set("blobOut", null, MjdbcTypes.BLOB, QueryParameters.Direction.OUT);

            input = new QueryInputHandler("{call TEST_PROC_LARGE(:clobIn, :clobOut, :blobIn, :blobOut)}", parameters);

            CallResults<QueryParameters, Map<String, Object>> result = runner.call(input, new MapOutputHandler());

            System.out.println("Call parameters returned: " + result.getCallInput());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP PROCEDURE TEST_PROC_LARGE");
        }
    }
}
