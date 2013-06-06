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

package org.midao.jdbc.spring.handlers.input.named;

import junit.framework.Assert;
import org.junit.Test;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.spring.handlers.input.named.SpringMapInputHandler;

import java.util.HashMap;

/**
 */
public class SpringMapInputHandlerTest {
    @Test
    public void testHadler() {
        Assert.assertEquals(true, new SpringMapInputHandler("SELECT", new HashMap<String, Object>()) instanceof MapInputHandler);
        Assert.assertEquals(true, new SpringMapInputHandler("SELECT", new HashMap<String, Object>(), "classname") instanceof MapInputHandler);
    }
}
