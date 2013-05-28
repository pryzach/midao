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

package org.midao.core.exception;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.MidaoConfig;
import org.midao.core.MidaoConstants;
import org.midao.core.MidaoFactory;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.service.QueryRunnerService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExceptionHandlerTest {
    QueryRunnerService runner;

    @Mock DataSource dataSource;
    @Mock Connection conn;
    @Mock PreparedStatement pstmt;
    @Mock Statement stmt;
    @Mock ParameterMetaData meta;
    @Mock ResultSet results;
    @Mock ExceptionHandler exceptionHandler;
	
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLException.class);
        when(stmt.getResultSet()).thenReturn(results);
        when(results.next()).thenReturn(false);

        runner = MidaoFactory.getQueryRunner(dataSource);
         
        runner.override(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT, true);
        
        
    }

    @After
    public void teadDown() {
        MidaoConfig.setDefaultExceptionHandler(new BaseExceptionHandler());
    }

    @Test
	public void testExceptionHandler() throws SQLException {
    	MidaoConfig.setDefaultExceptionHandler(getExceptionHandler());
    	when(getExceptionHandler().convert(any(Connection.class), any(SQLException.class), any(String.class), any(Object.class))).thenReturn(new MidaoSQLException("As Expected"));
    	
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
