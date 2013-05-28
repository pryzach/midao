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

package org.midao.examples.derby;

import org.midao.core.MidaoFactory;
import org.midao.core.MidaoTypes;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.handlers.type.BaseTypeHandler;
import org.midao.core.handlers.type.TypeHandlerUtils;
import org.midao.core.service.QueryRunnerService;

import java.sql.*;
import java.util.Map;

/**
 */
public class CallLargeParametersExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MidaoFactory.getQueryRunner(conn, BaseTypeHandler.class);

        try {

            // putting initial data into Database
            runner.update("CREATE PROCEDURE TEST_PROC_LARGE (IN clobIn CLOB, OUT clobOut CLOB, IN blobIn BLOB, OUT blobOut BLOB) PARAMETER STYLE JAVA LANGUAGE JAVA no sql EXTERNAL NAME 'org.midao.examples.derby.CallLargeParametersExample.testProcedureLarge'");

            QueryInputHandler input = null;
            QueryParameters parameters = new QueryParameters();

            parameters.set("clobIn", "John", MidaoTypes.CLOB, QueryParameters.Direction.IN);
            parameters.set("clobOut", null, MidaoTypes.CLOB, QueryParameters.Direction.OUT);

            parameters.set("blobIn", "Doe", MidaoTypes.BLOB, QueryParameters.Direction.IN);
            parameters.set("blobOut", null, MidaoTypes.BLOB, QueryParameters.Direction.OUT);

            input = new QueryInputHandler("{call TEST_PROC_LARGE(:clobIn, :clobOut, :blobIn, :blobOut)}", parameters);

            CallResults<QueryParameters, Map<String, Object>> result = runner.call(input, new MapOutputHandler());

            System.out.println("Call parameters returned: " + result.getCallInput());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP PROCEDURE TEST_PROC_LARGE");
        }
    }

    /*
     * DERBY JAVA SQL FUNCTIONS/PROCEDURES
     */
    public static void testProcedureLarge(java.sql.Clob clobIn, java.sql.Clob[] clobOut, java.sql.Blob blobIn, java.sql.Blob[] blobOut) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        Statement stmt = null;

        Clob newClob = (Clob) TypeHandlerUtils.createClob(conn);
        newClob.setString(1, "Hello " + clobIn.getSubString(1, (int) clobIn.length()));

        Blob newBlob = (Blob) TypeHandlerUtils.createBlob(conn);
        newBlob.setBytes(1, ("Hi " + new String(blobIn.getBytes(1, (int) blobIn.length()))).getBytes());

        clobOut[0] = newClob;
        blobOut[0] = newBlob;
    }
}
