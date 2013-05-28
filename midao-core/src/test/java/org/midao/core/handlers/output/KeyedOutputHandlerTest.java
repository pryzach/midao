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

package org.midao.core.handlers.output;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.exception.MidaoException;

import java.util.Map;

/**
 */
public class KeyedOutputHandlerTest extends BaseOutputHandlerTest {
    @Before
    public void setUp() {
        init();
    }

    @Test
    public void testCreateRow() throws Exception {
        KeyedOutputHandler<String> handler = new KeyedOutputHandler<String>();

        Map<String, Object> result = handler.createRow(params);

        Assert.assertEquals(params.getValue("name"), result.get("name"));
        Assert.assertEquals(params.getValue("occupation"), result.get("occupation"));
        Assert.assertEquals(params.getValue("age"), result.get("age"));
    }

    @Test
    public void testCreateKey() throws MidaoException {
        KeyedOutputHandler<String> handler = new KeyedOutputHandler<String>();

        String key = handler.createKey(params);

        // default key column should be first one
        Assert.assertEquals(params.getValue("name"), key);
    }

    @Test
    public void testHandler() throws MidaoException {
        KeyedOutputHandler<String> handler = new KeyedOutputHandler<String>();

        Map<String, Map<String, Object>> result = handler.handle(paramsList);

        org.junit.Assert.assertArrayEquals(new Object[]{"jack", "sheriff", 36},
                new Object[]{result.get("jack").get("name"), result.get("jack").get("occupation"), result.get("jack").get("age")});
        org.junit.Assert.assertArrayEquals(new Object[]{"henry", "mechanic", 36},
                new Object[]{result.get("henry").get("name"), result.get("henry").get("occupation"), result.get("henry").get("age")});
        org.junit.Assert.assertArrayEquals(new Object[]{"alison", "agent", 30},
                new Object[]{result.get("alison").get("name"), result.get("alison").get("occupation"), result.get("alison").get("age")});
    }

    @Test
    public void testEmpty() throws MidaoException {
        KeyedOutputHandler<String> handler = new KeyedOutputHandler<String>();

        Map<String, Map<String, Object>> result = handler.handle(emptyList);

        Assert.assertEquals(0, result.size());
    }
}
