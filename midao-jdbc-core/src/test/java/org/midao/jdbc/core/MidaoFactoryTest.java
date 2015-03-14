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

package org.midao.jdbc.core;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.handlers.type.EmptyTypeHandler;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.statement.BaseStatementHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 */
public class MidaoFactoryTest {
    @Mock
    Connection conn;
    @Mock
    Overrider overrider;
    @Mock
    DataSource ds;
    @Mock
    TypeHandler typeHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetQueryRunnerDataSource() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getQueryRunner(ds) instanceof QueryRunner);
    }

    @Test
    public void testGetQueryRunnerDataSourceTypeHandler() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getQueryRunner(ds, EmptyTypeHandler.class) instanceof QueryRunner);
    }

    @Test
    public void testGetQueryRunnerDataSourceTypeStatementHandler() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getQueryRunner(ds, EmptyTypeHandler.class, BaseStatementHandler.class) instanceof QueryRunner);
    }

    @Test
    public void testGetQueryRunnerCoonection() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getQueryRunner(conn) instanceof QueryRunner);
    }

    @Test
    public void testGetQueryRunnerConnectionTypeHandler() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getQueryRunner(conn, EmptyTypeHandler.class) instanceof QueryRunner);
    }

    @Test
    public void testGetQueryRunnerConnectionTypeStatementHandler() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getQueryRunner(conn, EmptyTypeHandler.class, BaseStatementHandler.class) instanceof QueryRunner);
    }

    @Test
    public void testGetAsyncQueryRunner() throws Exception {
        Assert.assertEquals(true, MidaoFactory.getAsyncQueryRunner(null, null) instanceof AsyncQueryRunner);
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testCreateDataSourceProp() throws Exception {
        MidaoFactory.createDataSource(new Properties());
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testCreateDataSourceUrl1() throws Exception {
        MidaoFactory.createDataSource("");
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testCreateDataSourceUrl2() throws Exception {
        MidaoFactory.createDataSource("", "", "");
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testCreateDataSourceUrl3() throws Exception {
        MidaoFactory.createDataSource("", "", "", "");
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testCreateDataSourceUrl4() throws Exception {
        MidaoFactory.createDataSource("", "", "", "", 0, 0);
    }
}
