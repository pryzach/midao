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

package org.midao.jdbc.core.handlers.input.model;

import junit.framework.TestCase;
import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.Map;

public class QueryParametersTest extends TestCase {

    public void testConstructors() {
        QueryParameters params = null;

        params = new QueryParameters(0);

        assertEquals(1, params.size());

    }

    public void testConstructorObjectNull() {
        QueryParameters params = null;

        params = new QueryParameters((Object) null);

        assertEquals(0, params.size());
    }

    public void testConstructorObjectsNull() {
        QueryParameters params = null;

        params = new QueryParameters((Object[]) null);

        assertEquals(0, params.size());
    }

    public void testConstructorMapNull() {
        QueryParameters params = null;

        params = new QueryParameters((Map<String, Object>) null);

        assertEquals(0, params.size());
    }

    public void testConstructorBeanNull() {
        QueryParameters params = null;

        params = new QueryParameters(String.class, null);

        assertEquals(0, params.size());
    }

    public void testConstructorQueryParamsNull() {
        QueryParameters params = null;

        params = new QueryParameters((QueryParameters) null);

        assertEquals(0, params.size());
    }
}
