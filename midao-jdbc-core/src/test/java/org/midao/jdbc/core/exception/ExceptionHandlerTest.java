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

package org.midao.jdbc.core.exception;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.MjdbcConstants;
import org.midao.jdbc.core.MjdbcFactory;
import org.midao.jdbc.core.QueryRunner;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExceptionHandlerTest {
    public QueryRunnerService runner;

    @Mock
    public DataSource dataSource;
    @Mock
    public Connection conn;
    @Mock
    public PreparedStatement pstmt;
    @Mock
    public Statement stmt;
    @Mock
    public ParameterMetaData meta;
    @Mock
    public ResultSet results;
    @Mock
    public ExceptionHandler exceptionHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLException.class);
        when(stmt.getResultSet()).thenReturn(results);
        when(results.next()).thenReturn(false);

        runner = MjdbcFactory.getQueryRunner(dataSource);

        runner.override(MjdbcConstants.OVERRIDE_CONTROL_PARAM_COUNT, true);

    }

    @After
    public void teadDown() {
        MjdbcConfig.setDefaultExceptionHandler(BaseExceptionHandler.class);
    }

    @Test
    public void testExceptionHandler() throws SQLException {
        ((QueryRunner) runner).setExceptionHandler(getExceptionHandler());

        when(getExceptionHandler().convert(any(Connection.class), any(SQLException.class), any(String.class), any(Object.class))).thenReturn(new MjdbcSQLException("As Expected"));

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
        } catch (Exception ex) {
            Assert.assertEquals("As Expected", ex.getMessage());
        }
        verify(getExceptionHandler(), times(1)).convert(any(Connection.class), any(SQLException.class), any(String.class), any(Object.class));
    }

    protected ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
}
