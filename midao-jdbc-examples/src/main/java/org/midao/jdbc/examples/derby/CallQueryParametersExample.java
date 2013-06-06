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
import org.midao.jdbc.core.MidaoTypes;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 */
public class CallQueryParametersExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DerbyParameters.createConnection();

        QueryRunnerService runner = MidaoFactory.getQueryRunner(conn);

        try {

            // putting initial data into Database
            runner.update("CREATE PROCEDURE TEST_INOUT (IN NAME varchar(50), INOUT SURNAME varchar(50), OUT FULLNAME varchar(50)) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 0 EXTERNAL NAME 'org.midao.jdbc.examples.derby.CallQueryParametersExample.testInOut'");

            QueryParameters parameters = new QueryParameters();
            parameters.set("name", "John", QueryParameters.Direction.IN);
            parameters.set("surname", "doe", MidaoTypes.VARCHAR, QueryParameters.Direction.INOUT);
            parameters.set("fullname", null, MidaoTypes.VARCHAR, QueryParameters.Direction.OUT);

            QueryInputHandler input = new QueryInputHandler("{call TEST_INOUT(:name, :surname, :fullname)}", parameters);

            QueryParameters result = runner.call(input);

            System.out.println("Call parameters returned: " + result.toMap());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            runner.update("DROP PROCEDURE TEST_INOUT");
        }
    }

    /*
     * DERBY JAVA SQL FUNCTIONS/PROCEDURES
     */
    public static void testInOut(String name, String[] surname, String[] fullName) {
        surname[0] = surname[0].toUpperCase();

        fullName[0] = name + " " + surname[0];
    }
}
