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

package org.midao.jdbc.core.statement;

import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.Overrider;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.mockito.Mockito.*;

/**
 */
public class LazyStatementHandlerTest {
    @Mock
    Statement stmt;
    @Mock
    ResultSet rs;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetStatement() throws Exception {
        // nothing to check
    }

    @Test
    public void testWrap() throws Exception {
        when(stmt.getResultSet()).thenReturn(rs);

        new LazyStatementHandler(new Overrider()).wrap(stmt);

        verify(stmt, times(1)).getResultSet();
        verify(stmt, never()).getMoreResults();
        verify(rs, never()).next();
    }
}
