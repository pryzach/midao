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

import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.handlers.input.InputHandler;
import org.midao.jdbc.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.jdbc.core.handlers.input.named.BeanInputHandler;
import org.midao.jdbc.core.handlers.input.query.AbstractQueryInputHandler;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Savepoint;
import java.util.concurrent.Executors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 */
public class AsyncQueryRunnerTest {

    private static final int sleepAmount = 50;

    @Mock
    QueryRunner runner;
    AsyncQueryRunner asyncRunner;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        asyncRunner = new AsyncQueryRunner(runner, Executors.newFixedThreadPool(1));
    }

    @Test
    public void testBatch1() throws Exception {
        asyncRunner.batch(null, new Object[0][0]);

        sleep();

        verify(runner, times(1)).batch(any(String.class), any(Object[][].class));
    }

    @Test
    public void testBatch2() throws Exception {
        asyncRunner.batch(new InputHandler[0]);

        sleep();

        verify(runner, times(1)).batch(any(InputHandler[].class));
    }

    @Test
    public void testQuery1() throws Exception {
        asyncRunner.query("", new MapOutputHandler(), "");

        sleep();

        verify(runner, times(1)).query(any(String.class), any(MapOutputHandler.class), any(String.class));
    }

    @Test
    public void testQuery2() throws Exception {
        asyncRunner.query(new BeanInputHandler("", ""), new MapOutputHandler());

        sleep();

        verify(runner, times(1)).query(any(InputHandler.class), any(MapOutputHandler.class));
    }

    @Test
    public void testQuery3() throws Exception {
        asyncRunner.query("", new MapOutputHandler());

        sleep();

        verify(runner, times(1)).query(any(String.class), any(MapOutputHandler.class));
    }

    @Test
    public void testUpdate1() throws Exception {
        asyncRunner.update("");

        sleep();

        verify(runner, times(1)).update("");
    }

    @Test
    public void testUpdate2() throws Exception {
        asyncRunner.update("", "");

        sleep();

        verify(runner, times(1)).update("", "");
    }

    @Test
    public void testUpdate3() throws Exception {
        asyncRunner.update("", "", "");

        sleep();

        verify(runner, times(1)).update("", "", "");
    }

    @Test
    public void testUpdate4() throws Exception {
        asyncRunner.update(new BeanInputHandler<String>("", ""));

        sleep();

        verify(runner, times(1)).update(any(InputHandler.class));
    }

    @Test
    public void testUpdate5() throws Exception {
        asyncRunner.update(new BeanInputHandler<String>("", ""), new MapOutputHandler());

        sleep();

        verify(runner, times(1)).update(any(InputHandler.class), any(MapOutputHandler.class));
    }

    @Test
    public void testUpdate6() throws Exception {
        asyncRunner.update("", new MapOutputHandler(), "");

        sleep();

        verify(runner, times(1)).update(any(String.class), any(MapOutputHandler.class), any(String.class));
    }

    @Test
    public void testCall1() throws Exception {
        asyncRunner.call(new QueryInputHandler("", new QueryParameters()));

        sleep();

        verify(runner, times(1)).call(any(AbstractQueryInputHandler.class));
    }

    @Test
    public void testCall2() throws Exception {
        asyncRunner.call(new BeanInputHandler<String>("", ""), "", "", false);

        sleep();

        verify(runner, times(1)).call(any(AbstractNamedInputHandler.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testCall3() throws Exception {
        asyncRunner.call(new BeanInputHandler<String>("", ""));

        sleep();

        verify(runner, times(1)).call(any(AbstractNamedInputHandler.class));
    }

    @Test
    public void testCall4() throws Exception {
        asyncRunner.call(new BeanInputHandler<String>("", ""), new MapOutputHandler());

        sleep();

        verify(runner, times(1)).call(any(InputHandler.class), any(MapOutputHandler.class));
    }

    @Test
    public void testCall5() throws Exception {
        asyncRunner.call(new BeanInputHandler<String>("", ""), new MapOutputHandler(), "", "", false);

        sleep();

        verify(runner, times(1)).call(any(AbstractNamedInputHandler.class), any(MapOutputHandler.class), any(String.class), any(String.class), any(boolean.class));
    }

    @Test
    public void testSetTransactionManualMode() throws Exception {
        asyncRunner.setTransactionManualMode(true);

        verify(runner, times(1)).setTransactionManualMode(true);
    }

    @Test
    public void testIsTransactionManualMode() throws Exception {
        asyncRunner.isTransactionManualMode();

        verify(runner, times(1)).isTransactionManualMode();
    }

    @Test
    public void testSetTransactionIsolationLevel() throws Exception {
        Integer value = 1;

        asyncRunner.setTransactionIsolationLevel(value);

        verify(runner, times(1)).setTransactionIsolationLevel(value);
    }

    @Test
    public void testGetTransactionIsolationLevel() throws Exception {
        asyncRunner.getTransactionIsolationLevel();

        verify(runner, times(1)).getTransactionIsolationLevel();
    }

    @Test
    public void testCommit() throws Exception {
        asyncRunner.commit();

        verify(runner, times(1)).commit();
    }

    @Test
    public void testRollback() throws Exception {
        asyncRunner.rollback();

        verify(runner, times(1)).rollback();
    }

    @Test
    public void testSetSavepoint() throws Exception {
        asyncRunner.setSavepoint();

        verify(runner, times(1)).setSavepoint();
    }

    @Test
    public void testSetSavepointNamed() throws Exception {
        String name = "SweetSpot";
        asyncRunner.setSavepoint(name);

        verify(runner, times(1)).setSavepoint(name);
    }

    @Test
    public void testRollbackSavepoint() throws Exception {
        asyncRunner.rollback(null);

        verify(runner, times(1)).rollback(any(Savepoint.class));
    }

    @Test
    public void testReleaseSavepoint() throws Exception {
        asyncRunner.releaseSavepoint(null);

        verify(runner, times(1)).releaseSavepoint(any(Savepoint.class));
    }

    @Test
    public void testOverrideOnce() throws Exception {
        asyncRunner.overrideOnce("", "");

        verify(runner, times(1)).overrideOnce("", "");
    }

    @Test
    public void testOverride() throws Exception {
        asyncRunner.override("", "");

        verify(runner, times(1)).override("", "");
    }

    @Test
    public void testRemoveOverride() throws Exception {
        asyncRunner.removeOverride("");

        verify(runner, times(1)).removeOverride("");
    }

    private void sleep() {
        try {
            Thread.sleep(sleepAmount); // this is async invocation, lets wait till it will be executed
        } catch (InterruptedException e) {
        }
    }
}
