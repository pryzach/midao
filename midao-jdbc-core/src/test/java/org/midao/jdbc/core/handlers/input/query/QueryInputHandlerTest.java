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

package org.midao.jdbc.core.handlers.input.query;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.handlers.model.QueryParameters;

/**
 */
public class QueryInputHandlerTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGeneral() {
        QueryParameters params = new QueryParameters();
        params.set("boss", "artie");
        params.set("agent", "myka");

        QueryInputHandler input = new QueryInputHandler("SELECT * FROM AGENTS WHERE name = :boss OR name = :agent", params);

        Assert.assertEquals(false, params == input.getQueryParameters());
        Assert.assertEquals(params.getValue("boss"), input.getQueryParameters().getValue("boss"));
        Assert.assertEquals(params.getValue("agent"), input.getQueryParameters().getValue("agent"));
    }
}
