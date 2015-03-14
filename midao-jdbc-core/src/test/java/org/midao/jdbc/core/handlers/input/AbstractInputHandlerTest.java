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

package org.midao.jdbc.core.handlers.input;

import org.junit.Test;
import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.InputUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 */
public class AbstractInputHandlerTest {
    @Test
    public void testMergeMaps() throws Exception {
        TestAbstractInputHandler testInputHandler = new TestAbstractInputHandler();

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("strength", 50);
        map1.put("intellect", 200);
        InputUtils.setClassName(map1, "ironman");

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("strength", 55);
        map2.put("intellect", 120);
        InputUtils.setClassName(map2, "c.america"); // c.america should be saved as c_america

        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("strength", 95);
        map3.put("intellect", 120);
        InputUtils.setClassName(map3, "thor");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("strength", 99999);
        map.put("intellect", 5);
        InputUtils.setClassName(map, "hulk");

        Map<String, Object> avengers = testInputHandler.mergeMaps("", Arrays.asList(map1, map2, map3, map), true);

        Object[] expected = new Object[]{"ironman.strength", "ironman.intellect",
                "c_america.strength", "c_america.intellect",
                "thor.strength", "thor.intellect",
                "hulk.strength", "hulk.intellect"};
        Arrays.sort(expected);

        Object[] returned = avengers.keySet().toArray();
        Arrays.sort(returned);

        org.junit.Assert.assertArrayEquals(expected, returned);
    }

    @Test
    public void testValidateSqlString() throws Exception {
        TestAbstractInputHandler testInputHandler = new TestAbstractInputHandler();

        try {
            testInputHandler.validateSqlString(null);
            fail(); // exception should be thrown, otherwise - fail
        } catch (IllegalArgumentException ex) {
        }

        try {
            testInputHandler.validateSqlString("some acceptable string");
        } catch (IllegalArgumentException ex) {
            fail(); // no exception should be thrown
        }

        try {
            testInputHandler.validateSqlString("SELECT GOLD from BANK where AMOUNT > :expectedGoldAmount AND ADDRESS = ?");
            fail(); // exception should be thrown as there is unnamed parameter
        } catch (IllegalArgumentException ex) {
        }

        try {
            testInputHandler.validateSqlString("SELECT GOLD from BANK where AMOUNT > :expectedGoldAmount AND ADDRESS = :someAddress");
        } catch (IllegalArgumentException ex) {
            fail(); // no exception should be thrown
        }
    }

    public class TestAbstractInputHandler<T> extends AbstractInputHandler<T> {

        protected TestAbstractInputHandler() {
            super(MjdbcConfig.getDefaultQueryInputProcessor());
        }

        public String getQueryString() {
            return null;
        }

        public QueryParameters getQueryParameters() {
            return null;
        }

        @Override
        public Map<String, Object> mergeMaps(String encodedQuery, List<Map<String, Object>> mapList, boolean addPrefix) {
            return super.mergeMaps(encodedQuery, mapList, addPrefix);
        }

        @Override
        public void validateSqlString(String originalSql) {
            super.validateSqlString(originalSql);
        }

    }
}
