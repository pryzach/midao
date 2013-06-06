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
import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.*;
import org.midao.jdbc.core.processor.BasicQueryOutputProcessor;
import org.midao.jdbc.core.processor.QueryOutputProcessor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 */
public class QueryOutputProcessorTest {
    @Mock QueryOutputProcessor processor;

    protected List<QueryParameters> paramsList;
    protected List<QueryParameters> emptyList = new ArrayList<QueryParameters>();
    protected QueryParameters params;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        QueryParameters param1 = new QueryParameters().set("name", "jack").set("occupation", "sheriff").set("age", 36);
        QueryParameters param2 = new QueryParameters().set("name", "henry").set("occupation", "mechanic").set("age", 36);
        QueryParameters param3 = new QueryParameters().set("name", "alison").set("occupation", "agent").set("age", 30);

        params = new QueryParameters();
        params.set("name", "zoe");
        params.set("occupation", "child");
        params.set("age", 15);

        paramsList = Arrays.asList(new QueryParameters(), param1, param2, param3);

        MidaoConfig.setDefaultQueryOutputProcessor(processor);
    }

    @After
    public void tearDown() throws Exception {
        MidaoConfig.setDefaultQueryOutputProcessor(new BasicQueryOutputProcessor());
    }

    @Test
    public void testToArray() throws Exception {
        ArrayOutputHandler handler = new ArrayOutputHandler();

        handler.handle(paramsList);

        verify(processor, times(1)).toArray(any(List.class));
    }

    @Test
    public void testToArrayList() throws Exception {
        ArrayListOutputHandler handler = new ArrayListOutputHandler();

        handler.handle(paramsList);

        verify(processor, times(1)).toArrayList(any(List.class));
    }

    @Test
    public void testToBean1() throws Exception {
        BeanOutputHandler handler = new BeanOutputHandler(String.class);

        handler.handle(paramsList);

        verify(processor, times(1)).toBean(any(List.class), any(Class.class));
    }

    @Test
    public void testToBean2() throws Exception {
        BeanMapOutputHandler handler = new BeanMapOutputHandler(String.class);

        handler.handle(paramsList);

        verify(processor, times(paramsList.size() - 1)).toBean(any(QueryParameters.class), any(Class.class));
    }

    @Test
    public void testToBeanList() throws Exception {
        BeanListOutputHandler handler = new BeanListOutputHandler(String.class);

        handler.handle(paramsList);

        verify(processor, times(1)).toBeanList(any(List.class), any(Class.class));
    }

    @Test
    public void testToMap1() throws Exception {
        MapOutputHandler handler = new MapOutputHandler();

        handler.handle(paramsList);

        verify(processor, times(1)).toMap(any(List.class));
    }

    @Test
    public void testToMap2() throws Exception {
        KeyedOutputHandler handler = new KeyedOutputHandler();

        handler.handle(paramsList);

        verify(processor, times(paramsList.size() - 1)).toMap(any(QueryParameters.class));
    }

    @Test
    public void testToMapList() throws Exception {
        MapListOutputHandler handler = new MapListOutputHandler();

        handler.handle(paramsList);

        verify(processor, times(1)).toMapList(any(List.class));
    }

    @Test
    public void testProcessValue() throws Exception {
        // this function is not used outside OutputProcessor. Might be removed in future
    }

}
