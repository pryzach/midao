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

package org.midao.core;

import org.junit.Before;
import org.junit.Test;
import org.midao.core.handlers.HandlersConstants;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.core.handlers.input.named.BeanInputHandler;
import org.midao.core.handlers.input.named.MapInputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.handlers.output.RowCountOutputHandler;
import org.midao.core.handlers.type.EmptyTypeHandler;
import org.midao.core.handlers.type.TypeHandler;
import org.midao.core.metadata.MetadataHandler;
import org.midao.core.service.QueryRunnerService;
import org.midao.core.statement.BaseStatementHandler;
import org.midao.core.statement.StatementHandler;
import org.midao.core.transaction.TransactionHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * In here I am testing execution of Statement/Connection/TransactionHandler, MetadataHandler and StatementHandler
 *
 * Test of Input/Output and Type Handlers are not performed as technically they are not mandatory for QueryRunner.
 */
public class QueryRunnerTest {
    @Mock Connection conn;
    @Mock Statement statement;
    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    @Mock DataSource ds;
    @Mock TypeHandler typeHandler;
    @Mock StatementHandler statementHandler;
    @Mock TransactionHandler transactionHandler;
    @Mock Overrider overrider;
    @Mock MetadataHandler metadataHandler;
    @Mock ParameterMetaData parameterMetaData;

    String sql = "INSERT :some INTO world";
    QueryParameters params = new QueryParameters().set("some", "luck");
    QueryParameters processedInput = new QueryParameters(params);
    MapOutputHandler outputHandler = new MapOutputHandler();
    QueryInputHandler inputHandler = new QueryInputHandler(sql, params);

    QueryRunnerService queryRunner;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init mocks
        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(conn.prepareStatement(any(String.class), any(int.class))).thenReturn(preparedStatement);
        when(preparedStatement.getParameterMetaData()).thenReturn(parameterMetaData);
        when(conn.prepareCall(any(String.class))).thenReturn(callableStatement);

        when(metadataHandler.getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class)))
                .thenReturn(new QueryParameters());

        when(transactionHandler.getConnection()).thenReturn(conn);

        //when(typeHandler.processInput(statement, params)).thenReturn(processedInput);
        when(typeHandler.processInput(any(Statement.class), any(QueryParameters.class))).thenReturn(processedInput);

        // if mockito >=1.9.5 org.mockito.AdditionalAnswers.returnsArgAt(1)
        when(typeHandler.processOutput(any(Statement.class), any(ArrayList.class))).then(new Answer<List<QueryParameters>>() {
            //@Override
            public List<QueryParameters> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (List<QueryParameters>) args[1];
            }
        });

        when(typeHandler.processOutput(any(Statement.class), any(QueryParameters.class))).then(new Answer<QueryParameters>() {
            //@Override
            public QueryParameters answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (QueryParameters) args[1];
            }
        });

        when(statementHandler.wrap(any(Statement.class))).thenReturn(Arrays.asList(new QueryParameters().set(HandlersConstants.STMT_UPDATE_COUNT, 0)));

        queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);

        modifyPrivate(queryRunner.getClass().getSuperclass().getDeclaredField("overrider"), queryRunner, overrider);

        ((QueryRunner) queryRunner).setTypeHandler(typeHandler);
        ((QueryRunner) queryRunner).setTransactionHandler(transactionHandler);
        ((QueryRunner) queryRunner).setStatementHandler(statementHandler);
        ((QueryRunner) queryRunner).setMetadataHandler(metadataHandler);

    }

    @Test
    public void testBatch1NoValue() throws Exception {
        queryRunner.batch("", new Object[0][0]);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeBatch();

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testBatch1Value() throws Exception {
        queryRunner.batch("bla", new Object[][]{{"bla"}});

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeBatch();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testBatch1SqlNull() throws Exception {
        queryRunner.batch(null, new Object[][]{{"bla"}});
    }

    @Test(expected = java.sql.SQLException.class)
    public void testBatch1ParamNull() throws Exception {
        queryRunner.batch("", null);
    }

    @Test(expected = java.sql.SQLException.class)
    public void testBatch2NoValue() throws Exception {
        queryRunner.batch(new InputHandler[]{});
    }

    @Test
    public void testBatch2Value() throws Exception {
        queryRunner.batch(new InputHandler[]{new QueryInputHandler("bla", new QueryParameters())});

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeBatch();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBatch2SqlNull() throws Exception {
        queryRunner.batch(new InputHandler[]{new QueryInputHandler(null, new QueryParameters())});
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBatch2ParamNull() throws Exception {
        queryRunner.batch(new InputHandler[]{new QueryInputHandler("SELECT :value", null)});
    }

    @Test
    public void testQuery1NoValue() throws Exception {
        queryRunner.query("", new MapOutputHandler(), null);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testQuery1Value() throws Exception {
        queryRunner.query("", new MapOutputHandler(), "bla");

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuery1SqlNull() throws Exception {
        queryRunner.query((String) null, new MapOutputHandler());
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuery1ParamNull() throws Exception {
        queryRunner.query("", null);
    }

    @Test
    public void testQuery2NoValue() throws Exception {
        queryRunner.query(new QueryInputHandler("", new QueryParameters()), new MapOutputHandler());

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testQuery2Value() throws Exception {
        queryRunner.query(new QueryInputHandler("SELECT :value", new QueryParameters().set("value", "bla")), new MapOutputHandler());

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQuery2SqlNull() throws Exception {
        queryRunner.query(new QueryInputHandler(null, new QueryParameters().set("value", "bla")), new MapOutputHandler());
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuery2ParamNull() throws Exception {
        queryRunner.query(new QueryInputHandler("SELECT :value", new QueryParameters().set("value", "bla")), null);
    }

    @Test
    public void testQuery3NoValue() throws Exception {
        queryRunner.query("", new MapOutputHandler());

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testQuery3Value() throws Exception {
        queryRunner.query("some sql", new MapOutputHandler());

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuery3SqlNull() throws Exception {
        queryRunner.query((String) null, new MapOutputHandler());
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuery3ParamNull() throws Exception {
        queryRunner.query("some sql", null);
    }

    @Test
    public void testUpdate1NoValue() throws Exception {
        queryRunner.update("");

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testUpdate1Value() throws Exception {
        queryRunner.update("bla");

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdate1SqlNull() throws Exception {
        queryRunner.update((String) null);
    }

    @Test
    public void testUpdate1ParamNull() throws Exception {
        // nothing to test
    }

    @Test
    public void testUpdate2NoValue() throws Exception {
        queryRunner.update("SQL", null);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testUpdate2Value() throws Exception {
        queryRunner.update("SQL", "bla");

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeUpdate();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdate2SqlNull() throws Exception {
        queryRunner.update(null, "bla");
    }

    @Test
    public void testUpdate2ParamNull() throws Exception {
        queryRunner.update("SQL", null);
    }

    @Test
    public void testUpdate3NoValue() throws Exception {
        // impossible to not specify values but invoke update(String sql, Object... params)
    }

    @Test
    public void testUpdate3Value() throws Exception {
        queryRunner.update("SQL", "", "");

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeUpdate();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdate3SqlNull() throws Exception {
        queryRunner.update(null, "", "");
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdate3ParamNull() throws Exception {
        queryRunner.update("SQL", null, null);
    }

    @Test
    public void testUpdate4NoValue() throws Exception {
        queryRunner.update(new QueryInputHandler("", new QueryParameters()));

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testUpdate4Value() throws Exception {
        queryRunner.update(new QueryInputHandler("SELECT :value", new QueryParameters().set("value", "bla")));

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeUpdate();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate4SqlNull() throws Exception {
        queryRunner.update(new QueryInputHandler(null, new QueryParameters()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate4ParamNull() throws Exception {
        queryRunner.update(new QueryInputHandler("SELECT :value", null));
    }

    @Test
    public void testUpdate5NoValue() throws Exception {
        queryRunner.update(new QueryInputHandler("", new QueryParameters()), new RowCountOutputHandler<Number>());

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testUpdate5Value() throws Exception {
        queryRunner.update(new QueryInputHandler("SELECT :value", new QueryParameters().set("value", "bla")), new MapOutputHandler());

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class), any(int.class));
        verify(preparedStatement, times(1)).executeUpdate();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate5SqlNull() throws Exception {
        queryRunner.update(new QueryInputHandler(null, new QueryParameters().set("value", "bla")), new MapOutputHandler());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate5ParamNull() throws Exception {
        queryRunner.update(new QueryInputHandler("SELECT :value", null), new MapOutputHandler());
    }

    @Test
    public void testUpdate6NoValue() throws Exception {
        queryRunner.update("", new RowCountOutputHandler<Number>(), new Object[0]);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).createStatement();
        verify(statement, times(1)).execute(any(String.class));

        verify(statementHandler, never()).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testUpdate6Value() throws Exception {
        queryRunner.update("", new MapOutputHandler(), new Object[]{""});

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class), any(int.class));
        verify(preparedStatement, times(1)).executeUpdate();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, never()).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdate6SqlNull() throws Exception {
        queryRunner.update(null, new MapOutputHandler(), new Object[]{""});
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdate6ParamNull() throws Exception {
        queryRunner.update("", null, new Object[]{""});
    }

    @Test
    public void testCall1NoValue() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{});

        queryRunner.call(new MapInputHandler("{CALL something()}", new HashMap<String, Object>()));

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testCall1Value() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
        when(metadataHandler.getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class)))
                .thenReturn(new QueryParameters("value"));


        queryRunner.call(new MapInputHandler("{CALL something(:value)}", new HashMap<String, Object>() {{
            put("value", "bla");
        }}));

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall1SqlNull() throws Exception {
        queryRunner.call(new MapInputHandler(null, new HashMap<String, Object>() {{
            put("value", "bla");
        }}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall1SqlIncorrect() throws Exception {
        queryRunner.call(new MapInputHandler("", new HashMap<String, Object>() {{
            put("value", "bla");
        }}));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testCall1InputNull() throws Exception {
        queryRunner.call((AbstractNamedInputHandler) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall1ParamNull() throws Exception {
        queryRunner.call(new MapInputHandler("{CALL something(:value)}", null));
    }

    @Test
    public void testCall2NoValue() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{});

        queryRunner.call(new MapInputHandler("{CALL something()}", new HashMap<String, Object>()), "", "", false);

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testCall2Value() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
        when(metadataHandler.getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class)))
                .thenReturn(new QueryParameters("value"));

        queryRunner.call(new MapInputHandler("{CALL something(:value)}", new HashMap<String, Object>(){{put("value", "bla");}}), "", "", false);

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall2SqlNull() throws Exception {
        queryRunner.call(new MapInputHandler(null, new HashMap<String, Object>() {{
            put("value", "bla");
        }}), "", "", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall2SqlIncorrect() throws Exception {
        queryRunner.call(new MapInputHandler("", new HashMap<String, Object>() {{
            put("value", "bla");
        }}), "", "", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall2ParamNull() throws Exception {
        queryRunner.call(new MapInputHandler("{CALL something(:value)}", null), "", "", false);
    }

    @Test(expected = java.sql.SQLException.class)
    public void testCall2InputNull() throws Exception {
        queryRunner.call((AbstractNamedInputHandler) null, "", "", false);
    }

    @Test
    public void testCall3NoValue() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{});

        queryRunner.call(new BeanInputHandler<Superhero>("CALL something()", new Superhero()));

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testCall3Value() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
        when(metadataHandler.getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class)))
                .thenReturn(new QueryParameters("value"));

        queryRunner.call(new BeanInputHandler<Superhero>("CALL something(:name)", new Superhero("not_me")));

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall3SqlNull() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>(null, new Superhero("not_me")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall3SqlIncorrect() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>("", new Superhero("not_me")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall3ParamNull() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>("CALL something(:name)", null));
    }

    @Test(expected = java.sql.SQLException.class)
    public void testCall3InputNull() throws Exception {
        queryRunner.call((AbstractNamedInputHandler<String>) null);
    }

    @Test
    public void testCall4NoValue() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{});

        queryRunner.call(new BeanInputHandler<Superhero>("CALL something()", new Superhero()), "", "", false);

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testCall4Value() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
        when(metadataHandler.getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class)))
                .thenReturn(new QueryParameters("value"));

        queryRunner.call(new BeanInputHandler<Superhero>("CALL something(:name)", new Superhero("not_me")), "", "", false);

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall4SqlNull() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>(null, new Superhero("not_me")), "", "", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall4SqlIncorrect() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>("", new Superhero("not_me")), "", "", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall4ParamNull() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>("CALL something(:name)", null), "", "", false);
    }

    @Test(expected = java.sql.SQLException.class)
    public void testCall4InputNull() throws Exception {
        queryRunner.call((AbstractNamedInputHandler<String>) null, "", "", false);
    }

    @Test
    public void testCall5NoValue() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{});

        queryRunner.call(new BeanInputHandler<Superhero>("CALL something()", new Superhero()), new MapOutputHandler(), "", "", false);

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testCall5Value() throws Exception {
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
        when(metadataHandler.getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class)))
                .thenReturn(new QueryParameters("value"));

        queryRunner.call(new BeanInputHandler<Superhero>("CALL something(:name)", new Superhero("not_me")), new MapOutputHandler(), "", "", false);

        verify(transactionHandler, times(2)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(2)).closeConnection();

        verify(conn, times(1)).prepareCall(any(String.class));
        verify(callableStatement, times(1)).execute();

        verify(statementHandler, times(1)).setStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).readStatement(any(Statement.class), any(QueryParameters.class));
        verify(statementHandler, times(1)).wrap(any(Statement.class));
        verify(statementHandler, times(1)).beforeClose();
        verify(statementHandler, times(1)).afterClose();

        verify(metadataHandler, times(1)).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall5SqlNull() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>(null, new Superhero("not_me")), new MapOutputHandler(), "", "", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall5SqlIncorrect() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>("", new Superhero("not_me")), new MapOutputHandler(), "", "", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCall5ParamNull() throws Exception {
        queryRunner.call(new BeanInputHandler<Superhero>("CALL something(:name)", null), new MapOutputHandler(), "", "", false);
    }

    @Test(expected = java.sql.SQLException.class)
    public void testCall5InputNull() throws Exception {
        queryRunner.call((AbstractNamedInputHandler<String>) null, new MapOutputHandler(), "", "", false);
    }

    @Test
    public void testConstructorStatement() {
    }

    @Test
    public void testBatchSimple() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(2);

        invokeBatch(new String[][] {{"some", "thing"}, {"something", "else"}});
    }

    @Test
    public void testBatchSimpleNull() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(2);

        invokeBatch(new String[][] {{null, "thing"}, {"something", null}});
    }

    @Test(expected = java.sql.SQLException.class)
    public void testBatchSimpleException1() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(2);

        invokeBatch(new String[][] {{"some"}});
    }

    @Test(expected = java.sql.SQLException.class)
    public void testBatchSimpleException2() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(2);

        invokeBatch(new String[][] {{"some", "thing"}, {"same"}, {"last", "thing"}});
    }

    @Test
    public void testQuerySimple() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeQuery("next", "best", "thing");
    }

    @Test
    public void testQuerySimpleNull() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeQuery("next", null, "thing");
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuerySimpleException1() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeQuery("next", "thing");
    }

    @Test(expected = java.sql.SQLException.class)
    public void testQuerySimpleException2() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeQuery("next", "best", "thing", "here");
    }

    @Test
    public void testUpdateSimple() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeUpdate("next", "best", "thing");
    }

    @Test
    public void testUpdateSimpleNull() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeUpdate("next", null, "thing");
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdateSimpleException1() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeUpdate("next", "thing");
    }

    @Test(expected = java.sql.SQLException.class)
    public void testUpdateSimpleException2() throws SQLException {
        when(overrider.getOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(overrider.hasOverride(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT)).thenReturn(true);
        when(parameterMetaData.getParameterCount()).thenReturn(3);

        invokeUpdate("next", "best", "thing", "here");
    }

    private void invokeBatch(Object[][] params) throws SQLException {
        ((QueryRunner) queryRunner).setStatementHandler(new BaseStatementHandler(((QueryRunner) queryRunner).getOverrider()));
        ((QueryRunner) queryRunner).setTypeHandler(new EmptyTypeHandler(((QueryRunner) queryRunner).getOverrider()));

        queryRunner.batch("someblabla", params);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).executeBatch();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    private void invokeQuery(Object... params) throws SQLException {
        ((QueryRunner) queryRunner).setStatementHandler(new BaseStatementHandler(((QueryRunner) queryRunner).getOverrider()));
        ((QueryRunner) queryRunner).setTypeHandler(new EmptyTypeHandler(((QueryRunner) queryRunner).getOverrider()));

        queryRunner.query("", new MapOutputHandler(), params);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class));
        verify(preparedStatement, times(1)).execute();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    private void invokeUpdate(Object... params) throws SQLException {
        ((QueryRunner) queryRunner).setStatementHandler(new BaseStatementHandler(((QueryRunner) queryRunner).getOverrider()));
        ((QueryRunner) queryRunner).setTypeHandler(new EmptyTypeHandler(((QueryRunner) queryRunner).getOverrider()));

        queryRunner.update("", new MapOutputHandler(), params);

        verify(transactionHandler, times(1)).getConnection();
        verify(transactionHandler, times(1)).commit();
        verify(transactionHandler, times(1)).closeConnection();

        verify(conn, times(1)).prepareStatement(any(String.class), any(int.class));
        verify(preparedStatement, times(1)).executeUpdate();

        verify(metadataHandler, never()).getProcedureParameters(any(Connection.class), any(String.class), any(String.class), any(String.class), any(boolean.class));
    }

    private void invokeQueries() throws SQLException {
        queryRunner.update(inputHandler);
        queryRunner.query(inputHandler, outputHandler);
        queryRunner.batch(new InputHandler[]{inputHandler});
        queryRunner.call(inputHandler);
    }

    private void modifyPrivate(Field field, QueryRunnerService runner, Object value) throws Exception{
        field.setAccessible(true);

        field.set(runner, value);
    }

    public static class Superhero {
        String name;

        public Superhero() {

        }

        public Superhero(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
