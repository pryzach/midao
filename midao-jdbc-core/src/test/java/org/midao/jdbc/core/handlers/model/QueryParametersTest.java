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

package org.midao.jdbc.core.handlers.model;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.handlers.utils.InputUtils;

import java.util.*;

import static org.junit.Assert.fail;

/**
 */
public class QueryParametersTest {
    Superhero superman;
    Map superMap;

    @Before
    public void setUp() throws Exception {
        superman = new Superhero();
        superman.setName("superman");
        superman.setOrigin("krypton");
        superman.setStrength(100);

        superMap = new HashMap<String, Object>();
        superMap.put("name", "superman");
        superMap.put("origin", "krypton");
        superMap.put("strength", 100);
    }

    @Test
    public void testConstructorMap() {
        QueryParameters params = new QueryParameters(superMap);

        Assert.assertEquals(superMap.get("name"), params.getValue("name"));
        Assert.assertEquals(superMap.get("origin"), params.getValue("origin"));
        Assert.assertEquals(superMap.get("strength"), params.getValue("strength"));
    }

    @Test
    public void testConstructorBean() {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(superman.getName(), params.getValue("name"));
        Assert.assertEquals(superman.getOrigin(), params.getValue("origin"));
        Assert.assertEquals(superman.getStrength(), params.getValue("strength"));
    }

    @Test
    public void testConstructorQueryParameters() {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);
        params.set("strength", 100, 2, QueryParameters.Direction.INOUT);

        QueryParameters paramsClone = new QueryParameters(params);

        Assert.assertEquals(superman.getName(), paramsClone.getValue("name"));
        Assert.assertEquals(superman.getOrigin(), paramsClone.getValue("origin"));
        Assert.assertEquals(superman.getStrength(), paramsClone.getValue("strength"));
        Assert.assertEquals(2, ((Integer) paramsClone.getType("strength")).intValue());
        Assert.assertEquals(QueryParameters.Direction.INOUT, paramsClone.getDirection("strength"));
    }

    @Test
    public void testConstructorProcessedInput() {
        ProcessedInput processedInput = new ProcessedInput("original query");

        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 1, 2);
        processedInput.addParameter("strength", 1, 2);
        processedInput.setSqlParameterValues(Arrays.<Object>asList(superman.getName(), superman.getOrigin(), superman.getStrength()));

        QueryParameters params = new QueryParameters(processedInput);

        Assert.assertEquals(superman.getName(), params.getValue("name"));
        Assert.assertEquals(superman.getOrigin(), params.getValue("origin"));
        Assert.assertEquals(superman.getStrength(), params.getValue("strength"));
    }

    @Test
    public void testConstructorArray() throws NoSuchFieldException {
        QueryParameters params = new QueryParameters(superman.getName(), superman.getOrigin(), superman.getStrength());

        Assert.assertEquals(superman.getName(), params.getValueByPosition(0));
        Assert.assertEquals(superman.getOrigin(), params.getValueByPosition(1));
        Assert.assertEquals(superman.getStrength(), params.getValueByPosition(2));
    }

    @Test
    public void testImportValues() throws Exception {
        QueryParameters params = new QueryParameters();
        params.importValues(superMap);

        Assert.assertEquals(superMap.get("name"), params.getValue("name"));
        Assert.assertEquals(superMap.get("origin"), params.getValue("origin"));
        Assert.assertEquals(superMap.get("strength"), params.getValue("strength"));
    }

    /**
     * {@link QueryParameters#set(String, Object, Integer, org.midao.jdbc.core.handlers.model.QueryParameters.Direction, Integer)}
     */
    @Test
    public void testSet1() throws Exception {
        QueryParameters params = new QueryParameters();

        params.set("key", "value", 2, QueryParameters.Direction.INOUT, 0);

        Assert.assertEquals("key", params.getNameByPosition(0));
        Assert.assertEquals("value", params.getValue("key"));
        Assert.assertEquals(2, params.getType("key").intValue());
        Assert.assertEquals(QueryParameters.Direction.INOUT, params.getDirection("key"));
        Assert.assertEquals(0, params.getPosition("key").intValue());
    }

    /**
     * {@link QueryParameters#set(String, Object, Integer, org.midao.jdbc.core.handlers.model.QueryParameters.Direction)}
     */
    @Test
    public void testSet2() throws Exception {
        QueryParameters params = new QueryParameters();

        params.set("key", "value", 2, QueryParameters.Direction.INOUT);

        Assert.assertEquals("key", params.getNameByPosition(0));
        Assert.assertEquals("value", params.getValue("key"));
        Assert.assertEquals(2, params.getType("key").intValue());
        Assert.assertEquals(QueryParameters.Direction.INOUT, params.getDirection("key"));
        Assert.assertEquals(0, params.getPosition("key").intValue());
    }

    /**
     * {@link QueryParameters#set(String, Object, org.midao.jdbc.core.handlers.model.QueryParameters.Direction)}
     */
    @Test
    public void testSet3() throws Exception {
        QueryParameters params = new QueryParameters();

        params.set("key", "value", QueryParameters.Direction.INOUT);

        Assert.assertEquals("key", params.getNameByPosition(0));
        Assert.assertEquals("value", params.getValue("key"));
        Assert.assertEquals(MjdbcTypes.OTHER, params.getType("key").intValue());
        Assert.assertEquals(QueryParameters.Direction.INOUT, params.getDirection("key"));
        Assert.assertEquals(0, params.getPosition("key").intValue());
    }

    /**
     * {@link QueryParameters#set(String, Object, Integer)}
     */
    @Test
    public void testSet4() throws Exception {
        QueryParameters params = new QueryParameters();

        params.set("key", "value", 2);

        Assert.assertEquals("key", params.getNameByPosition(0));
        Assert.assertEquals("value", params.getValue("key"));
        Assert.assertEquals(2, params.getType("key").intValue());
        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("key"));
        Assert.assertEquals(0, params.getPosition("key").intValue());
    }

    /**
     * {@link QueryParameters#set(String, Object)}
     */
    @Test
    public void testSet5() throws Exception {
        QueryParameters params = new QueryParameters();

        params.set("key", "value");

        Assert.assertEquals("key", params.getNameByPosition(0));
        Assert.assertEquals("value", params.getValue("key"));
        Assert.assertEquals(MjdbcTypes.OTHER, params.getType("key").intValue());
        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("key"));
        Assert.assertEquals(0, params.getPosition("key").intValue());
    }

    @Test
    public void testSetClassName() throws Exception {
        String className = "Superman";
        QueryParameters params = new QueryParameters(superman);

        params.setClassName(className);

        Assert.assertEquals(className, InputUtils.getClassName(params.toMap()));
    }

    @Test
    public void testUpdateType() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(MjdbcTypes.OTHER, params.getType("name").intValue());

        params.updateType("name", 2);

        Assert.assertEquals(2, params.getType("name").intValue());
    }

    @Test
    public void testUpdateDirection() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("name"));

        params.updateDirection("name", QueryParameters.Direction.INOUT);

        Assert.assertEquals(QueryParameters.Direction.INOUT, params.getDirection("name"));
    }

    @Test
    public void testUpdatePosition() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(1, params.getPosition("name").intValue());

        params.updatePosition("name", 3);

        Assert.assertEquals(3, params.getPosition("name").intValue());
    }

    @Test
    public void testUpdateValue() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(superman.getName(), params.getValue("name"));

        params.updateValue("name", "batman");

        Assert.assertEquals("batman", params.getValue("name"));
    }

    @Test
    public void testGetPosition() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(1, params.getPosition("name").intValue());
    }

    @Test
    public void testGetDirection() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(QueryParameters.Direction.IN, params.getDirection("name"));
    }

    @Test
    public void testGetType() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(MjdbcTypes.OTHER, params.getType("name").intValue());
    }

    @Test
    public void testGetValue() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(superman.getName(), params.getValue("name"));
    }

    @Test
    public void testToMap() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);
        Map<String, Object> map = params.toMap();

        Assert.assertEquals(superman.getName(), map.get("name"));
        Assert.assertEquals(superman.getOrigin(), map.get("origin"));
        Assert.assertEquals(superman.getStrength(), map.get("strength"));
    }

    @Test
    public void testKeySet() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        org.junit.Assert.assertArrayEquals(new Object[]{"strength", "name", "origin"}, params.keySet().toArray());
    }

    @Test
    public void testIsOutParameter() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(false, params.isOutParameter("name"));

        params.updateDirection("name", QueryParameters.Direction.INOUT);

        Assert.assertEquals(true, params.isOutParameter("name"));

        params.updateDirection("name", QueryParameters.Direction.OUT);

        Assert.assertEquals(true, params.isOutParameter("name"));

        params.updateDirection("name", QueryParameters.Direction.IN);

        Assert.assertEquals(false, params.isOutParameter("name"));
    }

    @Test
    public void testIsInParameter() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(true, params.isInParameter("name"));

        params.updateDirection("name", QueryParameters.Direction.INOUT);

        Assert.assertEquals(true, params.isInParameter("name"));

        params.updateDirection("name", QueryParameters.Direction.OUT);

        Assert.assertEquals(false, params.isInParameter("name"));

        params.updateDirection("name", QueryParameters.Direction.IN);

        Assert.assertEquals(true, params.isInParameter("name"));
    }

    @Test
    public void testGetNameByPosition() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals("name", params.getNameByPosition(1));
    }

    @Test
    public void testGetValueByPosition() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals("superman", params.getValueByPosition(1));
    }

    @Test
    public void testContainsKey() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(true, params.containsKey("name"));
        Assert.assertEquals(true, params.containsKey("origin"));
        Assert.assertEquals(true, params.containsKey("strength"));
    }

    @Test
    public void testRemove() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(true, params.containsKey("name"));
        Assert.assertEquals(true, params.containsKey("strength"));
        Assert.assertEquals(true, params.containsKey("origin"));

        params.remove("name");
        params.remove("strength");
        params.remove("origin");

        Assert.assertEquals(false, params.containsKey("name"));
        Assert.assertEquals(false, params.containsKey("strength"));
        Assert.assertEquals(false, params.containsKey("origin"));
    }

    @Test
    public void testRemoveOrder() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(1, params.getPosition("name").intValue());
        Assert.assertEquals(0, params.getPosition("strength").intValue());
        Assert.assertEquals(2, params.getPosition("origin").intValue());

        params.remove("strength");

        Assert.assertEquals(0, params.getPosition("name").intValue());
        Assert.assertEquals(1, params.getPosition("origin").intValue());
    }

    @Test
    public void testSize() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(3, params.size());

        params.remove("name");

        Assert.assertEquals(2, params.size());
    }

    @Test
    public void testSetCaseSensitive() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        params.setCaseSensitive(false);

        Assert.assertEquals(3, params.size());

        params.set("NaMe", "batman");

        Assert.assertEquals(3, params.size());

        params.setCaseSensitive(true);

        params.set("NaMe", "batman");

        Assert.assertEquals(4, params.size());
    }

    @Test
    public void testIsCaseSensitive() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        // by default case sensitivity for keys is turned off
        Assert.assertEquals(false, params.isCaseSensitive());
    }

    @Test
    public void testSetReturn() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);
        List<QueryParameters> paramsList = new ArrayList<QueryParameters>();

        params.setReturn(paramsList);

        Assert.assertEquals(paramsList, params.getReturn());
    }

    @Test
    public void testGetReturn() throws Exception {
        testSetReturn();
    }

    @Test
    public void testRemoveReturn() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);
        List<QueryParameters> paramsList = new ArrayList<QueryParameters>();

        params.setReturn(paramsList);

        Assert.assertEquals(paramsList, params.getReturn());

        params.removeReturn();

        Assert.assertEquals(null, params.getReturn());
    }

    @Test
    public void testUpdate() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);
        Object[] expectedValues = new Object[] {superman.getName(), superman.getOrigin(), superman.getStrength()};
        Object[] updateValues = new Object[] {null, "batman", "earth"};
        Object[] updatedValues = new Object[] {100, "batman", "earth"};

        org.junit.Assert.assertArrayEquals(expectedValues, new Object[] {params.getValue("name"), params.getValue("origin"), params.getValue("strength")});

        params.updateDirection("name", QueryParameters.Direction.INOUT);
        params.updateDirection("origin", QueryParameters.Direction.INOUT);
        params.update(updateValues, true);

        org.junit.Assert.assertArrayEquals(updatedValues, new Object[] {params.getValue("strength"), params.getValue("name"), params.getValue("origin")});
    }

    @Test
    public void testUpdateAndClean() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);
        params.set("once_bested_by", "doomsday");

        Object[] updatedValues = new Object[] {100, "batman", "earth"};
        ProcessedInput processedInput = new ProcessedInput("original query");

        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 1, 2);
        processedInput.addParameter("strength", 1, 2);
        processedInput.setSqlParameterValues(Arrays.<Object>asList(updatedValues[1], updatedValues[2], updatedValues[0]));

        Assert.assertEquals(4, params.size());

        params.updateAndClean(processedInput);

        org.junit.Assert.assertArrayEquals(new Object[] {updatedValues[1], updatedValues[2], updatedValues[0]},
                new Object[] {params.getValue("name"), params.getValue("origin"), params.getValue("strength")});

        Assert.assertEquals(3, params.size());
    }

    @Test
    public void testGetValuesArray() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        org.junit.Assert.assertArrayEquals(new Object[] {superman.getStrength(), superman.getName(), superman.getOrigin()},
                params.getValuesArray());
    }

    @Test
    public void testIsOrderSet() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        Assert.assertEquals(true, params.isOrderSet());

        params.remove("strength");

        // trying to update non existent value, that's why position and direction won't be initialized
        params.updateValue("strength", 99);

        Assert.assertEquals(false, params.isOrderSet());
    }

    @Test
    public void testAssertIncorrectOrder() throws Exception {
        QueryParameters params = new QueryParameters(superman.getClass(), superman);

        try {
            params.assertIncorrectOrder();
        } catch (IllegalArgumentException ex) {
            fail(); // exception shouldn't be thrown
        }

        params.remove("strength");

        // trying to update non existent value, that's why position and direction won't be initialized
        params.updateValue("strength", 99);

        try {
            params.assertIncorrectOrder();

            fail(); // exception should be thrown
        } catch (IllegalArgumentException ex) {

        }
    }

    public static class Superhero {
        String name;
        String origin;
        int strength;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public int getStrength() {
            return strength;
        }

        public void setStrength(int strength) {
            this.strength = strength;
        }
    }
}
