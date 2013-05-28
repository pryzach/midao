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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.exception.MidaoException;

import java.util.List;

/**
 */
public class BeanListOutputHandlerTest extends BaseOutputHandlerTest {
    @Before
    public void setUp() {
        init();
    }

    @Test
    public void testHandle() throws MidaoException {
        List<Character> result = new BeanListOutputHandler<Character>(Character.class).handle(paramsList);

        Assert.assertArrayEquals(new Object[]{"jack", "sheriff", 36}, new Object[]{result.get(0).getName(), result.get(0).getOccupation(), result.get(0).getAge()});
        Assert.assertArrayEquals(new Object[]{"henry", "mechanic", 36}, new Object[]{result.get(1).getName(), result.get(1).getOccupation(), result.get(1).getAge()});
        Assert.assertArrayEquals(new Object[]{"alison", "agent", 30}, new Object[]{result.get(2).getName(), result.get(2).getOccupation(), result.get(2).getAge()});
    }

    @Test
    public void testEmpty() throws MidaoException {
        List<Character> result = new BeanListOutputHandler<Character>(Character.class).handle(emptyList);

        Assert.assertEquals(0, result.size());
    }
}
