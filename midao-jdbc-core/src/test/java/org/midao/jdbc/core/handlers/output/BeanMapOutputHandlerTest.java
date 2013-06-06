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

package org.midao.jdbc.core.handlers.output;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.output.BeanMapOutputHandler;

import java.util.Map;

/**
 */
public class BeanMapOutputHandlerTest extends BaseOutputHandlerTest {

    @Before
    public void setUp() {
        init();
    }

    @Test
    public void testCreateRow() throws Exception {
        BeanMapOutputHandler<String, Character> handler = new BeanMapOutputHandler<String, Character>(Character.class);

        Character result = handler.createRow(params);

        Assert.assertEquals(params.getValue("name"), result.getName());
        Assert.assertEquals(params.getValue("occupation"), result.getOccupation());
        Assert.assertEquals(params.getValue("age"), result.getAge());
    }

    @Test
    public void testCreateKey() throws MidaoException {
        BeanMapOutputHandler<String, Character> handler = new BeanMapOutputHandler<String, Character>(Character.class);

        String key = handler.createKey(params);

        // default key column should be first one
        Assert.assertEquals(params.getValue("name"), key);
    }

    @Test
    public void testHandler() throws MidaoException {
        BeanMapOutputHandler<String, Character> handler = new BeanMapOutputHandler<String, Character>(Character.class);

        Map<String, Character> result = handler.handle(paramsList);

        org.junit.Assert.assertArrayEquals(new Object[]{"jack", "sheriff", 36}, new Object[]{result.get("jack").getName(), result.get("jack").getOccupation(), result.get("jack").getAge()});
        org.junit.Assert.assertArrayEquals(new Object[]{"henry", "mechanic", 36}, new Object[]{result.get("henry").getName(), result.get("henry").getOccupation(), result.get("henry").getAge()});
        org.junit.Assert.assertArrayEquals(new Object[]{"alison", "agent", 30}, new Object[]{result.get("alison").getName(), result.get("alison").getOccupation(), result.get("alison").getAge()});
    }

    @Test
    public void testEmpty() throws MidaoException {
        BeanMapOutputHandler<String, Character> handler = new BeanMapOutputHandler<String, Character>(Character.class);

        Map<String, Character> result = handler.handle(emptyList);

        Assert.assertEquals(0, result.size());
    }
}
