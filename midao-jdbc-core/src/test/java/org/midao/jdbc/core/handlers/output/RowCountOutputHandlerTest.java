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
import org.midao.jdbc.core.handlers.HandlersConstants;

import static org.junit.Assert.fail;

/**
 */
public class RowCountOutputHandlerTest extends BaseOutputHandlerTest {
    @Before
    public void setUp() throws Exception {
        init();
    }

    @Test
    public void testHandle() throws Exception {
        RowCountOutputHandler<Integer> handler = new RowCountOutputHandler<Integer>();

        paramsList.get(0).set(HandlersConstants.STMT_UPDATE_COUNT, 3);

        Integer result = handler.handle(paramsList);

        Assert.assertEquals(3, result.intValue());
    }

    @Test
    public void testIncorrect() {
        RowCountOutputHandler<Integer> handler = new RowCountOutputHandler<Integer>();

        try {
            Integer result = handler.handle(paramsList);
            fail(); // exception should be thrown as no update count is specified in paramsList
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void testEmpty() {
        RowCountOutputHandler<Integer> handler = new RowCountOutputHandler<Integer>();

        try {
            Integer result = handler.handle(emptyList);
            fail(); // exception should be thrown as no update count is specified in emptyList
        } catch (IllegalArgumentException ex) {

        }
    }
}
