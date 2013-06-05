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

package org.midao.core.statement;

import org.junit.Before;
import org.junit.Test;
import org.midao.core.MidaoConstants;
import org.midao.core.MidaoFactory;
import org.midao.core.MidaoTypes;
import org.midao.core.QueryRunner;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.service.QueryRunnerService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
public class CallableStatementHandlerTest {
    @Mock Connection conn;
    @Mock Statement statement;
    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    @Mock ParameterMetaData pmd;
    @Mock DataSource ds;

    String sql = "INSERT :some, :remove, :out  INTO world";
    QueryParameters params = new QueryParameters().set("some", "luck").set("remove", null).set("out", "ignored value", QueryParameters.Direction.OUT);
    MapOutputHandler outputHandler = new MapOutputHandler();
    QueryInputHandler inputHandler = new QueryInputHandler(sql, params);

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        MockitoAnnotations.initMocks(this);    // init mocks
        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(conn.prepareStatement(any(String.class), any(int.class))).thenReturn(preparedStatement);
        when(conn.prepareCall(any(String.class))).thenReturn(callableStatement);

        when(preparedStatement.getParameterMetaData()).thenReturn(pmd);
        when(callableStatement.getParameterMetaData()).thenReturn(pmd);
        when(pmd.getParameterCount()).thenReturn(params.size());

        when(preparedStatement.getUpdateCount()).thenReturn(1);
        when(callableStatement.getUpdateCount()).thenReturn(1);
    }

    @Test
    public void testSetStatement() throws Exception {
        invokeQueries();

        verify(callableStatement, times(1)).registerOutParameter(3, MidaoTypes.OTHER);
    }

    @Test
    public void testReadStatement() throws Exception {
        invokeQueries();

        verify(callableStatement, times(1)).getObject(3);
    }

    protected void invokeQueries() throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);
        ((QueryRunner) queryRunner).setStatementHandler(new CallableStatementHandler(
                ((QueryRunner) queryRunner).getOverrider()
        ));

        // forcing StatementHandler to read generated keys
        queryRunner.override(MidaoConstants.OVERRIDE_INT_GET_GENERATED_KEYS, true);

        queryRunner.update(inputHandler);
        queryRunner.query(inputHandler, outputHandler);
        queryRunner.batch(new InputHandler[]{inputHandler});
        queryRunner.call(inputHandler);
    }
}
