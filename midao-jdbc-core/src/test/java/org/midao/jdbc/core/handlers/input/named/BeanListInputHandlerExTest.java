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

package org.midao.jdbc.core.handlers.input.named;

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 */
public class BeanListInputHandlerExTest {
    @Test
    public void testHadler() {
        Assert.assertEquals(true, new BeanListInputHandlerEx<String>("SELECT", new HashMap<String, String>()) instanceof BeanListInputHandler);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testHandlerExcepton() {
        new BeanListInputHandlerEx<String>("SELECT #{value1}", new HashMap<String, String>());
    }
}
