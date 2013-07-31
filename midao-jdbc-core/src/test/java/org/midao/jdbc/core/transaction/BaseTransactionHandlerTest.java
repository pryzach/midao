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

package org.midao.jdbc.core.transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.MjdbcFactory;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 *
 */
public class BaseTransactionHandlerTest {
    @Mock TransactionHandler transactionHandler;
    @Mock Connection conn;
    @Mock Statement statement;
    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    @Mock DataSource ds;

    String sql = "INSERT luck INTO world;";
    MapOutputHandler outputHandler = new MapOutputHandler();
    QueryInputHandler inputHandler = new QueryInputHandler(sql, new QueryParameters());

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init mocks
        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(conn.prepareCall(any(String.class))).thenReturn(callableStatement);

        MjdbcConfig.setDefaultTransactionHandler(BaseTransactionHandler.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testManualModeUpdateDS() throws SQLException {
        testManualModeDS("update", "commit");
    }

    @Test
    public void testManualModeQueryDS() throws SQLException {
        testManualModeDS("query", "commit");
    }

    @Test
    public void testManualModeBatchDS() throws SQLException {
        testManualModeDS("batch", "commit");
    }

    @Test
    public void testManualModeCallDS() throws SQLException {
        testManualModeDS("call", "commit");
    }

    @Test
    public void testManualModeUpdateRollbackDS() throws SQLException {
        testManualModeDS("update", "rollback");
    }

    @Test
    public void testManualModeQueryRollbackDS() throws SQLException {
        testManualModeDS("query", "rollback");
    }

    @Test
    public void testManualModeBatchRollbackDS() throws SQLException {
        testManualModeDS("batch", "rollback");
    }

    @Test
    public void testManualModeCallRollbackDS() throws SQLException {
        testManualModeDS("call", "rollback");
    }

    private void testManualModeDS(String operation, String type) throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MjdbcFactory.getQueryRunner(ds);

        queryRunner.setTransactionManualMode(true);
        if ("update".equals(operation) == true) {
            queryRunner.update(sql);
        } else if ("query".equals(operation) == true) {
            queryRunner.query(sql, outputHandler);
        } else if ("batch".equals(operation) == true) {
            queryRunner.batch(sql, new Object[0][0]);
        } else if ("call".equals(operation) == true) {
            queryRunner.call(inputHandler);
        } else {
            fail();
        }

        verify(conn, never()).close();

        // in this scenario close should be invoked only with commit
        if ("commit".equals(type) == true) {
            queryRunner.commit();
            verify(conn, times(1)).commit();
        } else if ("rollback".equals(type) == true) {
            queryRunner.rollback();
            verify(conn, times(1)).rollback();
        } else {
            fail();
        }
        verify(ds, times(1)).getConnection();
        verify(conn, times(1)).close();

        queryRunner.setTransactionManualMode(false);
        if ("update".equals(operation) == true) {
            queryRunner.update(sql);
        } else if ("query".equals(operation) == true) {
            queryRunner.query(sql, outputHandler);
        } else if ("batch".equals(operation) == true) {
            queryRunner.batch(sql, new Object[0][0]);
        } else if ("call".equals(operation) == true) {
            queryRunner.call(inputHandler);
        } else {
            fail();
        }

        // in this scenario commit/close should be invoked after every operation
        verify(conn, times(2)).close();
        if ("commit".equals(type) == true) {
            verify(conn, times(2)).commit();
        } else if ("rollback".equals(type) == true) {
            verify(conn, times(1)).commit();
            verify(conn, times(1)).rollback();
        } else {
            fail();
        }

        if ("commit".equals(type) == true) {
            queryRunner.commit();
            // checking if there were no additional invocations
            verify(conn, times(2)).commit();
        } else if ("rollback".equals(type) == true) {
            queryRunner.rollback();
            // rollback shouldn't be invoked as commit is already performed
            verify(conn, times(1)).rollback();
        } else {
            fail();
        }

        verify(ds, times(2)).getConnection();
        verify(conn, times(2)).close();
    }

    @Test
    public void testManualModeUpdateConn() throws SQLException {
        testManualModeConn("update", "commit");
    }

    @Test
    public void testManualModeQueryConn() throws SQLException {
        testManualModeConn("query", "commit");
    }

    @Test
    public void testManualModeBatchConn() throws SQLException {
        testManualModeConn("batch", "commit");
    }

    @Test
    public void testManualModeCallConn() throws SQLException {
        testManualModeConn("call", "commit");
    }

    @Test
    public void testManualModeUpdateRollbackConn() throws SQLException {
        testManualModeConn("update", "rollback");
    }

    @Test
    public void testManualModeQueryRollbackConn() throws SQLException {
        testManualModeConn("query", "rollback");
    }

    @Test
    public void testManualModeBatchRollbackConn() throws SQLException {
        testManualModeConn("batch", "rollback");
    }

    @Test
    public void testManualModeCallRollbackConn() throws SQLException {
        testManualModeConn("call", "rollback");
    }

    private void testManualModeConn(String operation, String type) throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MjdbcFactory.getQueryRunner(conn);

        queryRunner.setTransactionManualMode(true);
        if ("update".equals(operation) == true) {
            queryRunner.update(sql);
        } else if ("query".equals(operation) == true) {
            queryRunner.query(sql, outputHandler);
        } else if ("batch".equals(operation) == true) {
            queryRunner.batch(sql, new Object[0][0]);
        } else if ("call".equals(operation) == true) {
            queryRunner.call(inputHandler);
        } else {
            fail();
        }

        // if transaction manager is created based on connection - it should never close it.
        verify(conn, never()).close();

        if ("commit".equals(type) == true) {
            queryRunner.commit();
            verify(conn, times(1)).commit();
        } else if ("rollback".equals(type) == true) {
            queryRunner.rollback();
            verify(conn, times(1)).rollback();
        } else {
            fail();
        }
        verify(conn, never()).close();

        queryRunner.setTransactionManualMode(false);
        if ("update".equals(operation) == true) {
            queryRunner.update(sql);
        } else if ("query".equals(operation) == true) {
            queryRunner.query(sql, outputHandler);
        } else if ("batch".equals(operation) == true) {
            queryRunner.batch(sql, new Object[0][0]);
        } else if ("call".equals(operation) == true) {
            queryRunner.call(inputHandler);
        } else {
            fail();
        }

        // if transaction manager is created based on connection - it should never close it, but commit should be invoked
        verify(conn, never()).close();

        if ("commit".equals(type) == true) {
            verify(conn, times(2)).commit();
        } else if ("rollback".equals(type) == true) {
            verify(conn, times(1)).commit();
            verify(conn, times(1)).rollback();
        } else {
            fail();
        }

        if ("commit".equals(type) == true) {
            queryRunner.commit();
            // commit should be invoked
            verify(conn, times(3)).commit();
        } else if ("rollback".equals(type) == true) {
            queryRunner.rollback();
            // rollback shouldn't be invoked as commit is already performed
            verify(conn, times(2)).rollback();
            verify(conn, times(1)).commit();
        } else {
            fail();
        }

        verify(conn, never()).close();
    }

    @Test
    public void testIsolationLevelDS() throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MjdbcFactory.getQueryRunner(ds);

        testIsolationLevel(queryRunner);

        verify(ds, times(4)).getConnection();
        verify(conn, never()).setTransactionIsolation(any(int.class));

        queryRunner.setTransactionIsolationLevel(1);
        testIsolationLevel(queryRunner);

        verify(ds, times(8)).getConnection();
        verify(conn, times(4)).setTransactionIsolation(any(int.class));
    }

    @Test
    public void testIsolationLevelConn() throws SQLException {
        QueryRunnerService queryRunner = null;

        queryRunner = MjdbcFactory.getQueryRunner(conn);

        testIsolationLevel(queryRunner);

        verify(conn, never()).setTransactionIsolation(any(int.class));

        queryRunner.setTransactionIsolationLevel(1);
        testIsolationLevel(queryRunner);

        verify(conn, times(4)).setTransactionIsolation(any(int.class));
    }

    private void testIsolationLevel(QueryRunnerService queryRunner) throws SQLException {
        queryRunner.update(sql);
        queryRunner.query(sql, outputHandler);
        queryRunner.batch(sql, new Object[0][0]);
        queryRunner.call(inputHandler);
    }

    @Test
    public void testSavepointDS() throws SQLException {
        QueryRunnerService queryRunner = MjdbcFactory.getQueryRunner(ds);

        // manual transaction control needed so connection won't be closed
        queryRunner.setTransactionManualMode(true);
        testSavepoint(queryRunner);
    }

    @Test
    public void testSavepointConn() throws SQLException {
        QueryRunnerService queryRunner = MjdbcFactory.getQueryRunner(conn);

        testSavepoint(queryRunner);
    }

    private void testSavepoint(QueryRunnerService queryRunner) throws SQLException {
        queryRunner.update(sql);
        queryRunner.query(sql, outputHandler);
        queryRunner.batch(sql, new Object[0][0]);
        queryRunner.call(inputHandler);

        queryRunner.setSavepoint();
        queryRunner.setSavepoint("something");
        queryRunner.releaseSavepoint(null);
        queryRunner.rollback(null);

        verify(conn, times(1)).setSavepoint();
        verify(conn, times(1)).setSavepoint(any(String.class));
        verify(conn, times(1)).releaseSavepoint(null);
        verify(conn, times(1)).rollback(null);
    }
}
