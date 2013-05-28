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

package org.midao.core;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.handlers.input.InputHandler;
import org.midao.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.core.handlers.input.query.AbstractQueryInputHandler;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.output.OutputHandler;
import org.midao.core.handlers.type.TypeHandler;
import org.midao.core.service.QueryRunnerService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 */
public class OverriderTest {
    protected Overrider overrider;

    @Mock Connection conn;
    @Mock Overrider overriderMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        overrider = new Overrider();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOverrideOnce() throws Exception {
        String name = "value";
        String value = "once";

        overrider.overrideOnce(name, value);

        Assert.assertEquals(overrider.hasOverride(name), true);
        Assert.assertEquals(overrider.getOverride(name), value);

        Assert.assertEquals(overrider.getOverride(name), null);
        Assert.assertEquals(overrider.hasOverride(name), false);
    }

    @Test
    public void testOverride() throws Exception {
        String name = "value";
        String value = "many";

        overrider.override(name, value);

        Assert.assertEquals(overrider.hasOverride(name), true);
        Assert.assertEquals(overrider.getOverride(name), value);

        Assert.assertEquals(overrider.getOverride(name), value);
        Assert.assertEquals(overrider.hasOverride(name), true);
    }

    @Test
    public void testRemoveOverride() throws Exception {
        String name = "value";
        String value = "many";

        overrider.override(name, value);

        Assert.assertEquals(overrider.hasOverride(name), true);
        Assert.assertEquals(overrider.getOverride(name), value);

        overrider.removeOverride(name);

        Assert.assertEquals(overrider.getOverride(name), null);
        Assert.assertEquals(overrider.hasOverride(name), false);
    }

    @Test
    public void testHasOverride() throws Exception {
        String name = "value";
        String value = "once";

        overrider.overrideOnce(name, value);

        Assert.assertEquals(overrider.hasOverride(name), true);

        overrider.removeOverride(name);

        Assert.assertEquals(overrider.hasOverride(name), false);
    }

    @Test
    public void testGetOverride() throws Exception {
        testOverrideOnce();
        testOverride();
    }

    @Test
    public void testGeneral() throws Exception {
        QueryRunnerService runner = MidaoFactory.getQueryRunner(conn);
        modifyPrivate(runner.getClass().getSuperclass().getDeclaredField("overrider"), runner, overriderMock);

        runner.override("always", "1");
        runner.overrideOnce("once", "2");
        runner.removeOverride("third");

        verify(overriderMock, times(1)).override("always", "1");
        verify(overriderMock, times(1)).overrideOnce("once", "2");
        verify(overriderMock, times(1)).removeOverride("third");
    }

    private void modifyPrivate(Field field, QueryRunnerService runner, Object value) throws Exception{
        field.setAccessible(true);

        field.set(runner, value);
    }
}
