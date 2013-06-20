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

package org.midao.jdbc.core.metadata;

import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.MidaoFactory;
import org.midao.jdbc.core.QueryRunner;
import org.midao.jdbc.core.exception.MidaoSQLException;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.input.InputHandler;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.core.statement.StatementHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
public class MetadataHandlerTest {
    @Mock Connection conn;
    @Mock Statement statement;
    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    @Mock DataSource ds;
    @Mock StatementHandler statementHandler;
    @Mock OutputHandler outputHandler;
    @Mock DatabaseMetaData metaData;
    @Mock ResultSet rs;

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
        when(conn.getMetaData()).thenReturn(metaData);

        // don't go into details. We are checking if it was invoked at all
        when(rs.next()).thenReturn(false);
        when(metaData.getProcedures(any(String.class), any(String.class), any(String.class))).thenReturn(rs);

        when(statementHandler.wrap(any(Statement.class))).thenReturn(Arrays.asList(new QueryParameters().set(HandlersConstants.STMT_UPDATE_COUNT, 0)));
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
    }

    @Test
    public void testGetProcedureParameters() throws Exception {
        QueryRunnerService queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);
        ((QueryRunner) queryRunner).setStatementHandler(statementHandler);

        queryRunner.update(inputHandler);
        queryRunner.query(inputHandler, outputHandler);
        queryRunner.batch(new InputHandler[]{inputHandler});

        try {
            queryRunner.call(new MapInputHandler("{call bla(:some)}", params.toMap()), outputHandler);
            fail();
        } catch (MidaoSQLException ex) {
            // nothing was found - exception thrown. it is correct
        }

        // once during initialization to get dbName and second to actually get procedure parameters
        verify(conn, times(2)).getMetaData();
        verify(metaData, times(1)).getProcedures(any(String.class), any(String.class), any(String.class));
        verify(rs, times(1)).next();
    }
}
