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

package org.midao.core.handlers.utils;

import junit.framework.Assert;
import org.junit.Test;
import org.midao.core.handlers.model.ProcessedInput;
import org.midao.core.handlers.model.QueryParameters;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class InputUtilsTest {
    @Test
    public void testDefineOrder() throws Exception {
        QueryParameters params = new QueryParameters();
        params.set("name", "whiskers");
        params.set("age", 10);

        ProcessedInput input = new ProcessedInput("");
        input.addParameter("age", 10, 12);
        input.addParameter("name", 14, 15);

        Assert.assertEquals(0, params.getPosition("name").intValue());
        Assert.assertEquals(1, params.getPosition("age").intValue());

        InputUtils.defineOrder(input, params);

        Assert.assertEquals(1, params.getPosition("name").intValue());
        Assert.assertEquals(0, params.getPosition("age").intValue());
    }

    @Test
    public void testGetClassName() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        InputUtils.setClassName(map, "cat");

        Assert.assertEquals("cat", InputUtils.getClassName(map));
    }

    @Test
    public void testSetClassName() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        InputUtils.setClassName(map, "cat");

        Assert.assertEquals("cat", InputUtils.getClassName(map));

        InputUtils.setClassName(map, "hot.dog");

        Assert.assertEquals("hot_dog", InputUtils.getClassName(map));
    }

    @Test
    public void testIsClassNameKey() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        InputUtils.setClassName(map, "cat");

        Assert.assertEquals(true, InputUtils.isClassNameKey(map.keySet().toArray(new String[]{})[0]));
    }


    @Test
    public void testAddClassName() throws Exception {
        String className = "cat";
        String key = "name";
        String separator = ".";

        String result = InputUtils.addClassName(className, key);

        Assert.assertEquals(className + separator + key, result);
    }

    @Test
    public void testRemoveClassName() throws Exception {
        String className = "cat";
        String key = "name";
        String separator = ".";

        String result = InputUtils.removeClassName(className + separator + key);

        Assert.assertEquals(key, result);
    }

    @Test
    public void testGetParameterPrefix() throws Exception {
        Assert.assertEquals(":", InputUtils.getParameterPrefix());
    }

    @Test
    public void testAddParameterPrefix() throws Exception {
        String key = "cat.name";

        Assert.assertEquals(InputUtils.getParameterPrefix() + key, InputUtils.addParameterPrefix(key));
    }

    @Test
    public void testRemoveParameterPrefix() throws Exception {
        String key = "cat.name";

        Assert.assertEquals(key, InputUtils.removeParameterPrefix(InputUtils.addParameterPrefix(key)));
    }
}
