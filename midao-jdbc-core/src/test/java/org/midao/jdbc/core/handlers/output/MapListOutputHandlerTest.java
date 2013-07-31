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
import org.midao.jdbc.core.exception.MjdbcException;

import java.util.List;
import java.util.Map;

/**
 */
public class MapListOutputHandlerTest extends BaseOutputHandlerTest {
    @Before
    public void setUp() throws Exception {
        init();
    }

    @Test
    public void testHandle() throws Exception {
        MapListOutputHandler handler = new MapListOutputHandler();

        List<Map<String, Object>> result = handler.handle(paramsList);

        org.junit.Assert.assertArrayEquals(new Object[]{"jack", "sheriff", 36},
                new Object[]{result.get(0).get("name"), result.get(0).get("occupation"), result.get(0).get("age")});
        org.junit.Assert.assertArrayEquals(new Object[]{"henry", "mechanic", 36},
                new Object[]{result.get(1).get("name"), result.get(1).get("occupation"), result.get(1).get("age")});
        org.junit.Assert.assertArrayEquals(new Object[]{"alison", "agent", 30},
                new Object[]{result.get(2).get("name"), result.get(2).get("occupation"), result.get(2).get("age")});
    }

    @Test
    public void testEmpty() throws MjdbcException {
        MapListOutputHandler handler = new MapListOutputHandler();

        List<Map<String, Object>> result = handler.handle(emptyList);

        Assert.assertEquals(0, result.size());
    }
}
