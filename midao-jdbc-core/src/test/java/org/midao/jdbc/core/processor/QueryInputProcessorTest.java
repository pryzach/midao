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

package org.midao.jdbc.core.processor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.handlers.input.InputHandler;
import org.midao.jdbc.core.handlers.input.named.BeanInputHandler;
import org.midao.jdbc.core.handlers.input.named.BeanListInputHandler;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.input.named.MapListInputHandler;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
public class QueryInputProcessorTest {
    @Mock
    QueryInputProcessor processor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(processor.hasUnnamedParameters(any(String.class))).thenReturn(false);
        when(processor.processInput(any(String.class), any(Map.class))).thenReturn(new ProcessedInput(""));

        MjdbcConfig.setDefaultQueryInputProcessor(processor);
    }

    @After
    public void destroy() {
        MjdbcConfig.setDefaultQueryInputProcessor(new BasicQueryInputProcessor());
    }

    @Test
    public void testProcessInput() throws Exception {
        InputHandler handler = null;
        String encodedSql = "SELECT GOLD from BANK where AMOUNT > :expectedGoldAmount AND ADDRESS = :someAddress";

        handler = new QueryInputHandler(encodedSql, new QueryParameters());
        handler = new BeanInputHandler<String>(encodedSql, "");
        handler = new BeanListInputHandler<String>(encodedSql, new HashMap<String, String>());
        handler = new MapInputHandler(encodedSql, new HashMap<String, Object>());
        handler = new MapListInputHandler(encodedSql, new HashMap<String, Map<String, Object>>());

        verify(processor, times(5)).processInput(any(String.class), any(Map.class));
    }

    @Test
    public void testHasUnnamedParameters() throws Exception {
        InputHandler handler = null;
        String encodedSql = "SELECT GOLD from BANK where AMOUNT > :expectedGoldAmount AND ADDRESS = :someAddress";

        handler = new QueryInputHandler(encodedSql, new QueryParameters());
        handler = new BeanInputHandler<String>(encodedSql, "");
        handler = new BeanListInputHandler<String>(encodedSql, new HashMap<String, String>());
        handler = new MapInputHandler(encodedSql, new HashMap<String, Object>());
        handler = new MapListInputHandler(encodedSql, new HashMap<String, Map<String, Object>>());

        verify(processor, times(5)).hasUnnamedParameters(any(String.class));
    }
}
