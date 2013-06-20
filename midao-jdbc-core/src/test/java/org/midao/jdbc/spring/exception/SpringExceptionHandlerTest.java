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

package org.midao.jdbc.spring.exception;

import junit.framework.Assert;
import org.junit.Test;
import org.midao.jdbc.core.QueryRunner;
import org.midao.jdbc.core.exception.BaseExceptionHandler;
import org.midao.jdbc.core.exception.ExceptionHandler;
import org.midao.jdbc.core.exception.ExceptionHandlerTest;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.mockito.Mock;

import java.sql.*;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 */
public class SpringExceptionHandlerTest extends ExceptionHandlerTest {
    SpringExceptionHandler springExceptionHandler = new SpringExceptionHandler("absent");
    @Mock BaseExceptionHandler mockBaseExceptionHandler;
    /*
    QueryRunnerService runner;

    @Mock DataSource dataSource;
    @Mock Connection conn;
    @Mock PreparedStatement pstmt;
    @Mock Statement stmt;
    @Mock ParameterMetaData meta;
    @Mock ResultSet results;

    SpringExceptionHandler springExceptionHandler = new SpringExceptionHandler("absent");
    @Mock BaseExceptionHandler mockBaseExceptionHandler;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);    // init the mocks

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.getResultSet()).thenReturn(results);
        when(results.next()).thenReturn(false);

        runner = MidaoFactory.getQueryRunner(dataSource);

        runner.override(MidaoConstants.OVERRIDE_CONTROL_PARAM_COUNT, true);
    }*/

    @Test
    public void testConvertJDBC4Exception1() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLTransactionRollbackException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (ConcurrencyFailureException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception2() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLTransientConnectionException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (TransientDataAccessResourceException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception3() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLTimeoutException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (QueryTimeoutException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception4() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLDataException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (DataIntegrityViolationException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception5() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLFeatureNotSupportedException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (InvalidDataAccessApiUsageException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception6() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLIntegrityConstraintViolationException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (DataIntegrityViolationException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception7() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLInvalidAuthorizationSpecException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (PermissionDeniedDataAccessException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception8() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLNonTransientConnectionException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (DataAccessResourceFailureException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception9() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLSyntaxErrorException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (BadSqlGrammarException ex) {

        }
    }

    @Test
    public void testConvertJDBC4Exception10() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(SQLRecoverableException.class);

        try {
            runner.query("select * from blah where 1 = 1", new MapOutputHandler());
            fail();
        } catch (RecoverableDataAccessException ex) {

        }
    }

    @Test(expected = BadSqlGrammarException.class)
    public void testConvertBySqlStatePrefix1() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "2A11", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testConvertBySqlStatePrefix2() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "2222", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DataAccessResourceFailureException.class)
    public void testConvertBySqlStatePrefix3() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "5433", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = TransientDataAccessResourceException.class)
    public void testConvertBySqlStatePrefix4() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "S144", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = ConcurrencyFailureException.class)
    public void testConvertBySqlStatePrefix5() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler(""));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "6155", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DuplicateKeyException.class)
    public void testConvertVendorSpecificDb2() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("DB2"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", -803));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testConvertVendorSpecificDerby() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("Apache Derby"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "23502", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DataAccessResourceFailureException.class)
    public void testConvertVendorSpecificH2() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("H2"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", 90100));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void testConvertVendorSpecificHSQL() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("HSQL"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", -28));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testConvertVendorSpecificInformix() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("Informix"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", -11030));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = CannotAcquireLockException.class)
    public void testConvertVendorSpecificMsSql() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("Microsoft SQL Server"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", 1222));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DeadlockLoserDataAccessException.class)
    public void testConvertVendorSpecificMySql() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("MySQL"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", 1213));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = InvalidResultSetAccessException.class)
    public void testConvertVendorSpecificOracle() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("Oracle"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", 17003));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = CannotAcquireLockException.class)
    public void testConvertVendorSpecificPostgreSQL() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("PostgreSQL"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "55P03", 0));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test(expected = DeadlockLoserDataAccessException.class)
    public void testConvertVendorSpecificSybase() throws Exception {
        ((QueryRunner) runner).setExceptionHandler(new SpringExceptionHandler("Sybase"));

        reset(stmt);
        when(stmt.execute(any(String.class))).thenThrow(new SQLException("", "", 1205));

        runner.query("select * from blah where 1 = 1", new MapOutputHandler());
    }

    @Test
    public void testBaseExceptionHandler() throws SQLException {
        ((QueryRunner) runner).setExceptionHandler(springExceptionHandler);

        try {
            runner.query("SQL generates Exception", new MapOutputHandler());
        } catch (Exception ex) {
            Assert.assertEquals(" Query: SQL generates Exception Parameters: [QueryParameters CI { }]", ex.getMessage());
        }
    }

    @Override
    protected ExceptionHandler getExceptionHandler() {
        return mockBaseExceptionHandler;
    }
}
