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
import org.midao.core.handlers.model.QueryParameters;

import org.midao.core.MidaoTypes;

import static org.junit.Assert.fail;

/**
 */
public class CallableUtilsTest {
    @Test
    public void testIsFunctionCall() throws Exception {
        Assert.assertEquals(true, CallableUtils.isFunctionCall("{? = call make_me_some_tea()}"));
    }

    @Test
    public void testGetStoredProcedureShortNameFromSql() throws Exception {
        Assert.assertEquals("function_name", CallableUtils.getStoredProcedureShortNameFromSql("{call schema.package.function_name()}"));
    }

    @Test
    public void testGetStoredProcedureFullName() throws Exception {
        Assert.assertEquals("schema.package.function_name", CallableUtils.getStoredProcedureFullName("{call schema.package.function_name()}"));
    }

    @Test
    public void testUpdateDirections() throws Exception {
        QueryParameters params = new QueryParameters();
        params.set("first", "age");
        params.set("second", "knowledge");

        QueryParameters paramsDirections = new QueryParameters();
        paramsDirections.set("first", "age", QueryParameters.Direction.IN);
        paramsDirections.set("second", "knowledge", QueryParameters.Direction.INOUT);

        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("first"));
        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("second"));

        params = CallableUtils.updateDirections(params, paramsDirections);

        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("first"));
        Assert.assertEquals(QueryParameters.Direction.INOUT, params.getDirection("second"));
    }

    @Test
    public void testUpdateTypes() throws Exception {
        QueryParameters params = new QueryParameters();
        params.set("first", "age");
        params.set("second", "knowledge");

        QueryParameters paramsTypes = new QueryParameters();
        paramsTypes.set("first", "age", 10);
        paramsTypes.set("second", "knowledge", 11);

        Assert.assertEquals(MidaoTypes.OTHER, params.getType("first").intValue());
        Assert.assertEquals(MidaoTypes.OTHER, params.getType("second").intValue());

        params = CallableUtils.updateTypes(params, paramsTypes);

        Assert.assertEquals(10, params.getType("first").intValue());
        Assert.assertEquals(11, params.getType("second").intValue());
    }

    @Test
    public void testUpdateDirectionsByName() throws Exception {
        QueryParameters params = new QueryParameters();
        params.set("first", "age");
        params.set("second", "knowledge");

        QueryParameters paramsDirections = new QueryParameters();
        paramsDirections.set("first", "age", QueryParameters.Direction.IN);
        paramsDirections.set("second", "knowledge", QueryParameters.Direction.INOUT);

        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("first"));
        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("second"));

        params = CallableUtils.updateDirections(params, paramsDirections);

        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("first"));
        Assert.assertEquals(QueryParameters.Direction.INOUT, params.getDirection("second"));
    }

    @Test
    public void testUpdateTypesByName() throws Exception {
        QueryParameters params = new QueryParameters();
        params.set("first", "age");
        params.set("second", "knowledge");

        QueryParameters paramsTypes = new QueryParameters();
        paramsTypes.set("first", "age", 10);
        paramsTypes.set("second", "knowledge", 11);

        Assert.assertEquals(MidaoTypes.OTHER, params.getType("first").intValue());
        Assert.assertEquals(MidaoTypes.OTHER, params.getType("second").intValue());

        params = CallableUtils.updateTypes(params, paramsTypes);

        Assert.assertEquals(10, params.getType("first").intValue());
        Assert.assertEquals(11, params.getType("second").intValue());
    }
}
