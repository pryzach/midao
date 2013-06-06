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
package org.midao.jdbc.core.utils;

import org.junit.Test;
import org.midao.jdbc.core.handlers.utils.InputUtils;
import org.midao.jdbc.core.utils.MidaoUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class MidaoUtilsTest {

    @Test
    public void closeNullConnection() throws Exception {
        MidaoUtils.close((Connection) null);
    }

    @Test
    public void closeConnection() throws Exception {
        Connection mockCon = mock(Connection.class);
        MidaoUtils.close(mockCon);
        verify(mockCon).close();
    }

    @Test
    public void closeNullResultSet() throws Exception {
        MidaoUtils.close((ResultSet) null);
    }

    @Test
    public void closeResultSet() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        MidaoUtils.close(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void closeNullStatement() throws Exception {
        MidaoUtils.close((Statement) null);
    }

    @Test
    public void closeStatement() throws Exception {
        Statement mockStatement = mock(Statement.class);
        MidaoUtils.close(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyNullConnection() throws Exception {
        MidaoUtils.closeQuietly((Connection) null);
    }

    @Test
    public void closeQuietlyConnection() throws Exception {
        Connection mockConnection = mock(Connection.class);
        MidaoUtils.closeQuietly(mockConnection);
        verify(mockConnection).close();
    }

    @Test
    public void closeQuietlyConnectionThrowingException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        MidaoUtils.closeQuietly(mockConnection);
    }

    @Test
    public void closeQuietlyNullResultSet() throws Exception {
        MidaoUtils.closeQuietly((ResultSet) null);
    }

    @Test
    public void closeQuietlyResultSet() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        MidaoUtils.closeQuietly(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void closeQuietlyResultSetThrowingException() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        MidaoUtils.closeQuietly(mockResultSet);
    }

    @Test
    public void closeQuietlyNullStatement() throws Exception {
        MidaoUtils.closeQuietly((Statement) null);
    }

    @Test
    public void closeQuietlyStatement() throws Exception {
        Statement mockStatement = mock(Statement.class);
        MidaoUtils.closeQuietly(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyStatementThrowingException() throws Exception {
        Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        MidaoUtils.closeQuietly(mockStatement);
    }

    @Test
    public void closeQuietlyConnectionResultSetStatement() throws Exception {
        Connection mockConnection = mock(Connection.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        MidaoUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionThrowingExceptionResultSetStatement() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        MidaoUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionResultSetThrowingExceptionStatement() throws Exception {
        Connection mockConnection = mock(Connection.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        Statement mockStatement = mock(Statement.class);
        MidaoUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionResultSetStatementThrowingException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        MidaoUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void commitAndClose() throws Exception {
        Connection mockConnection = mock(Connection.class);
        MidaoUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).commit();
        try {
            MidaoUtils.commitAndClose(mockConnection);
            fail("DbUtils.commitAndClose() swallowed SQLEception!");
        } catch (SQLException e) {
            // we expect this exception
        }
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseQuietly() throws Exception {
        Connection mockConnection = mock(Connection.class);
        MidaoUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseQuietlyWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        MidaoUtils.commitAndCloseQuietly(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackNull() throws Exception {
        MidaoUtils.rollback(null);
    }

    @Test
    public void rollback() throws Exception {
        Connection mockConnection = mock(Connection.class);
        MidaoUtils.rollback(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    public void rollbackAndCloseNull() throws Exception {
        MidaoUtils.rollbackAndClose(null);
    }

    @Test
    public void rollbackAndClose() throws Exception {
        Connection mockConnection = mock(Connection.class);
        MidaoUtils.rollbackAndClose(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        try {
            MidaoUtils.rollbackAndClose(mockConnection);
            fail("DbUtils.rollbackAndClose() swallowed SQLException!");
        } catch (SQLException e) {
            // we expect this exeption
        }
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseQuietlyNull() throws Exception {
        MidaoUtils.rollbackAndCloseQuietly(null);
    }

    @Test
    public void rollbackAndCloseQuietly() throws Exception {
        Connection mockConnection = mock(Connection.class);
        MidaoUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseQuietlyWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        MidaoUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }
    
    /*
     * @author Zak
     */
    @Test
    public void testClassName() {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String className = "TestClass";
    	InputUtils.setClassName(map, className);
    	
    	assertEquals(className, InputUtils.getClassName(map));
    }
    
}
