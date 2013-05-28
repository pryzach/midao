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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class ExceptionUtilsTest {
	@Mock ExceptionHandler exceptionHandler;
    @Mock Connection conn;
	
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks
    }

    @After
    public void teadDown() {
        MidaoConfig.setDefaultExceptionHandler(new BaseExceptionHandler());
    }

    @Test
	public void testRethrow() {
		MidaoConfig.setDefaultExceptionHandler(exceptionHandler);
		
		SQLException exception = new SQLException("Just my luck");
		String sql = "Wrong again!";
		String value = "value";
		
		when(exceptionHandler.convert(conn, exception, sql, value)).thenReturn(new MidaoSQLException("As Expected"));
		
		try {
			ExceptionUtils.rethrow(conn, exception, sql, value);
		} catch (MidaoSQLException ex) {
			Assert.assertEquals("As Expected", ex.getMessage());
		}
		
		when(exceptionHandler.convert(conn, exception, sql, value)).thenReturn(new MidaoSQLException("As Expected"));
	}
}
