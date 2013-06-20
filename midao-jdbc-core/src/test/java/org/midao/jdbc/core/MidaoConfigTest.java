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
import org.midao.jdbc.core.exception.ExceptionHandler;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.metadata.MetadataHandler;
import org.midao.jdbc.core.processor.QueryInputProcessor;
import org.midao.jdbc.core.processor.QueryOutputProcessor;
import org.midao.jdbc.core.statement.StatementHandler;
import org.midao.jdbc.core.transaction.TransactionHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 */
public class MidaoConfigTest {
    @Mock Connection conn;
    @Mock Overrider overrider;
    @Mock DataSource ds;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDefaultQueryInputProcessor() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultQueryInputProcessor() instanceof QueryInputProcessor);
    }

    @Test
    public void testIsQueryInputProcessorUseCache() throws Exception {
        boolean defaultValue = MidaoConfig.isQueryInputProcessorUseCache();

        MidaoConfig.setQueryInputProcessorUseCache(!defaultValue);

        Assert.assertEquals(!defaultValue, MidaoConfig.isQueryInputProcessorUseCache());

        MidaoConfig.setQueryInputProcessorUseCache(defaultValue);
    }

    @Test
    public void testGetDefaultQueryOutputProcessor() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultQueryOutputProcessor() instanceof QueryOutputProcessor);
    }

    @Test
    public void testGetDefaultStatementHandler() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultStatementHandler(overrider) instanceof StatementHandler);
    }

    @Test
    public void testGetDefaultTypeHandler() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultTypeHandler(overrider) instanceof TypeHandler);
    }

    @Test
    public void testGetDefaultTransactionHandlerConn() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultTransactionHandler(conn) instanceof TransactionHandler);
    }

    @Test
    public void testGetDefaultTransactionHandlerDataSource() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultTransactionHandler(ds) instanceof TransactionHandler);
    }

    @Test
    public void testGetDefaultExceptionHandler() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultExceptionHandler("") instanceof ExceptionHandler);
    }

    @Test
    public void testGetDefaultMetadataHandlerConn() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultMetadataHandler(conn) instanceof MetadataHandler);
    }

    @Test
    public void testGetDefaultMetadataHandlerDataSource() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultMetadataHandler(ds) instanceof MetadataHandler);
    }

    @Test
    public void testGetDefaultOverrider() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getDefaultOverrider() instanceof Overrider);
    }

    @Test
    public void testIsProfilerEnabled() throws Exception {
        boolean defaultValue = MidaoConfig.isProfilerEnabled();

        MidaoConfig.setProfilerEnabled(!defaultValue);

        Assert.assertEquals(!defaultValue, MidaoConfig.isProfilerEnabled());

        MidaoConfig.setProfilerEnabled(defaultValue);
    }

    @Test
    public void testGetProfilerOutputFormat() throws Exception {
        Assert.assertEquals(true, MidaoConfig.getProfilerOutputFormat() != null && MidaoConfig.getProfilerOutputFormat().length() > 0);
    }
}
