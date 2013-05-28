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

package org.midao.core.metadata;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.MidaoFactory;
import org.midao.core.QueryRunner;
import org.midao.core.handlers.HandlersConstants;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.MapInputHandler;
import org.midao.core.handlers.input.query.QueryInputHandler;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.OutputHandler;
import org.midao.core.statement.StatementHandler;
import org.midao.core.service.QueryRunnerService;
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
public class BaseMetadataHandlerTest {
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

        // one line for getProcedures
        when(rs.next()).thenReturn(true).thenReturn(true)
                .thenReturn(false).thenReturn(false);

        when(rs.getString("PROCEDURE_CAT")).thenReturn("PROCEDURE_CAT");
        when(rs.getString("PROCEDURE_SCHEM")).thenReturn("PROCEDURE_SCHEM");
        when(rs.getString("PROCEDURE_NAME")).thenReturn("bla");

        when(rs.getString("COLUMN_NAME")).thenReturn("COLUMN_NAME");
        when(rs.getInt("COLUMN_TYPE")).thenReturn(1);
        when(rs.getInt("DATA_TYPE")).thenReturn(2);

        when(metaData.getProcedures(any(String.class), any(String.class), any(String.class))).thenReturn(rs);
        when(metaData.getProcedureColumns(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(rs);

        when(statementHandler.wrap(any(Statement.class))).thenReturn(Arrays.asList(new QueryParameters().set(HandlersConstants.STMT_UPDATE_COUNT, 0)));
        when(statementHandler.readStatement(any(Statement.class), any(QueryParameters.class))).thenReturn(new Object[]{null});
    }

    @Test
    public void testConstructorConn() throws SQLException {
        // don't cache existing stored procedures during initialization
        BaseMetadataHandler metadataHandler = new BaseMetadataHandler(conn, false);

        verify(conn, never()).getMetaData();
        verify(metaData, never()).getProcedures(any(String.class), any(String.class), any(String.class));
        verify(metaData, never()).getProcedureColumns(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(rs, never()).next();

        // cache during all stored procedures/functions during initialization
        metadataHandler = new BaseMetadataHandler(conn, true);

        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getProcedures(any(String.class), any(String.class), any(String.class));
        verify(metaData, times(1)).getProcedureColumns(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(rs, times(4)).next();
    }

    @Test
    public void testConstructorDatasource() throws SQLException {
        // don't cache existing stored procedures during initialization
        BaseMetadataHandler metadataHandler = new BaseMetadataHandler(ds, false);

        verify(ds, never()).getConnection();
        verify(conn, never()).getMetaData();
        verify(metaData, never()).getProcedures(any(String.class), any(String.class), any(String.class));
        verify(metaData, never()).getProcedureColumns(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(rs, never()).next();

        // cache during all stored procedures/functions during initialization
        metadataHandler = new BaseMetadataHandler(ds, true);

        verify(ds, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getProcedures(any(String.class), any(String.class), any(String.class));
        verify(metaData, times(1)).getProcedureColumns(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(rs, times(4)).next();
    }

    @Test
    public void testUpdateCache() throws Exception {
        BaseMetadataHandler metadataHandler = new BaseMetadataHandler(conn, false);

        Assert.assertEquals(1, metadataHandler.updateCache(metaData, null, null, "bla"));
    }

    @Test
    public void testGetProcedureParameters() throws Exception {
        QueryRunnerService queryRunner = null;

        queryRunner = MidaoFactory.getQueryRunner(ds);
        ((QueryRunner) queryRunner).setStatementHandler(statementHandler);

        queryRunner.update(inputHandler);
        queryRunner.query(inputHandler, outputHandler);
        queryRunner.batch(new InputHandler[]{inputHandler});

        queryRunner.call(new MapInputHandler("{call bla(:some)}", params.toMap()), outputHandler);

        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getProcedures(any(String.class), any(String.class), any(String.class));
        verify(metaData, times(1)).getProcedureColumns(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(rs, times(4)).next();
    }
}
