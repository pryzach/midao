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

package org.midao.core.handlers.output;

import org.junit.Before;
import org.junit.Test;
import org.midao.core.MidaoFactory;
import org.midao.core.QueryRunner;
import org.midao.core.handlers.HandlersConstants;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.statement.StatementHandler;
import org.midao.core.service.QueryRunnerService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class OutputHandlerTest {
    @Mock Connection conn;
    @Mock Statement statement;
    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    @Mock DataSource ds;
    @Mock StatementHandler statementHandler;
    @Mock OutputHandler outputHandler;

    String sql = "INSERT :some INTO world";
    QueryParameters params = new QueryParameters().set("some", "luck");
    QueryParameters processedInput = new QueryParameters(params);
    QueryInputHandler inputHandler = new QueryInputHandler(sql, params);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        MockitoAnnotations.initMocks(this);    // init mocks
        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(conn.prepareCall(any(String.class))).thenReturn(callableStatement);

        when(statementHandler.wrap(any(Statement.class))).thenReturn(Arrays.asList(new QueryParameters().set(HandlersConstants.STMT_UPDATE_COUNT, 0)));
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
    }

    @Test
    public void testHandle() throws Exception {
        invokeQueries();

        verify(outputHandler, times(2)).handle(any(List.class));
    }

    protected void invokeQueries() throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);
        ((QueryRunner) queryRunner).setStatementHandler(statementHandler);

        queryRunner.update(inputHandler);
        queryRunner.query(inputHandler, outputHandler);
        queryRunner.batch(new InputHandler[]{inputHandler});
        queryRunner.call(new QueryInputHandler("{call bla(:some)}", params), outputHandler);
    }
}
