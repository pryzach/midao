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

package org.midao.core.handlers.type;

import org.junit.Before;
import org.junit.Test;
import org.midao.core.MidaoConfig;
import org.midao.core.MidaoFactory;
import org.midao.core.QueryRunner;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.service.QueryRunnerService;
import org.midao.core.transaction.BaseTransactionHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class TypeHandlerTest {
    @Mock Connection conn;
    @Mock Statement statement;
    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    @Mock DataSource ds;
    @Mock TypeHandler typeHandler;

    String sql = "INSERT :some INTO world";
    QueryParameters params = new QueryParameters().set("some", "luck");
    QueryParameters processedInput = new QueryParameters(params);
    MapOutputHandler outputHandler = new MapOutputHandler();
    QueryInputHandler inputHandler = new QueryInputHandler(sql, params);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init mocks
        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(conn.prepareCall(any(String.class))).thenReturn(callableStatement);

        //when(typeHandler.processInput(statement, params)).thenReturn(processedInput);
        when(typeHandler.processInput(any(Statement.class), any(QueryParameters.class))).thenReturn(processedInput);

        // if mockito >=1.9.5 org.mockito.AdditionalAnswers.returnsArgAt(1)
        when(typeHandler.processOutput(any(Statement.class), any(ArrayList.class))).then(new Answer<List<QueryParameters>>() {
            public List<QueryParameters> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (List<QueryParameters>) args[1];
            }
        });
    }

    @Test
    public void testProcessInput() throws Exception {
        invokeQueries();
        verify(typeHandler, times(4)).processInput(any(Statement.class), any(QueryParameters.class));

        // when no parameters are specified - type handler shouldn't be invoked
        invokeQueriesNoParameters();
        verify(typeHandler, times(4)).processInput(any(Statement.class), any(QueryParameters.class));
    }

    @Test
    public void testAfterExecute() throws Exception {
        invokeQueries();
        verify(typeHandler, times(4)).afterExecute(any(Statement.class), any(QueryParameters.class), any(QueryParameters.class));

        // when no parameters are specified - type handler shouldn't be invoked
        invokeQueriesNoParameters();
        verify(typeHandler, times(4)).afterExecute(any(Statement.class), any(QueryParameters.class), any(QueryParameters.class));
    }

    @Test
    public void testProcessOutput() throws Exception {
        invokeQueries();
        // invoked only by call
        verify(typeHandler, times(1)).processOutput(any(Statement.class), any(QueryParameters.class));

        // when no parameters are specified - type handler shouldn't be invoked
        invokeQueriesNoParameters();
        verify(typeHandler, times(1)).processOutput(any(Statement.class), any(QueryParameters.class));
    }

    @Test
    public void testProcessOutputList() throws Exception {
        invokeQueries();
        // invoked by batch, query and update
        verify(typeHandler, times(3)).processOutput(any(Statement.class), any(List.class));

        // when no parameters are specified - type handler shouldn't be invoked
        invokeQueriesNoParameters();
        verify(typeHandler, times(3)).processOutput(any(Statement.class), any(List.class));
    }

    private void invokeQueriesNoParameters() throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);
        ((QueryRunner) queryRunner).setTypeHandler(typeHandler);

        queryRunner.update(sql);
        queryRunner.query(sql, outputHandler);
        queryRunner.batch(sql, new Object[0][0]);
        queryRunner.call(new QueryInputHandler(sql.replaceAll(":some", "luck"), new QueryParameters()));
    }

    private void invokeQueries() throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);
        ((QueryRunner) queryRunner).setTypeHandler(typeHandler);

        queryRunner.update(inputHandler);
        queryRunner.query(inputHandler, outputHandler);
        queryRunner.batch(new InputHandler[]{inputHandler});
        queryRunner.call(inputHandler);
    }
}
