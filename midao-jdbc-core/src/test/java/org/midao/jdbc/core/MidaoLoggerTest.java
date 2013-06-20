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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 */
public class MidaoLoggerTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetLoggerString() throws Exception {
        MidaoLogger logger = MidaoLogger.getLogger("log me!");

        Assert.assertEquals(true, logger instanceof Logger);
        Assert.assertEquals("log me!", logger.getName());
    }

    @Test
    public void testGetLoggerClass() throws Exception {
        MidaoLogger logger = MidaoLogger.getLogger(String.class);

        Assert.assertEquals(true, logger instanceof Logger);
        Assert.assertEquals("java.lang.String", logger.getName());
    }

    @Test
    public void testInfo() throws Exception {

    }

    @Test
    public void testWarning() throws Exception {

    }

    @Test
    public void testSevere() throws Exception {

    }

    @Test
    public void testIsSLF4jAvailable() throws Exception {
        Assert.assertEquals(false, MidaoLogger.isSLF4jAvailable());
    }

    @Test
    public void testIsSLF4jImplementationAvailable() throws Exception {
        Assert.assertEquals(false, MidaoLogger.isSLF4jImplementationAvailable());
    }
}
