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

import java.util.*;

/**
 */
public class ProcessedInputTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConstructorString() {
        String originalSql = "some SQL string";
        ProcessedInput processedInput = new ProcessedInput(originalSql);

        Assert.assertEquals(originalSql, processedInput.getOriginalSql());
        Assert.assertEquals(null, processedInput.getParsedSql());
        Assert.assertEquals(0, processedInput.getSqlParameterNames().size());
        Assert.assertEquals(0, processedInput.getSqlParameterBoundaries().size());
        Assert.assertEquals(0, processedInput.getSqlParameterValues().size());
    }

    @Test
    public void testConstructorProcessedInput() {
        Object[] updatedValues = new Object[] {100, "batman", "earth"};
        ProcessedInput processedInput = new ProcessedInput("original query");

        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 1, 2);
        processedInput.addParameter("strength", 1, 2);
        processedInput.setSqlParameterValues(Arrays.<Object>asList(updatedValues[1], updatedValues[2], updatedValues[0]));

        ProcessedInput processedInputClone = new ProcessedInput(processedInput);

        Assert.assertEquals(processedInput.getOriginalSql(), processedInputClone.getOriginalSql());
        Assert.assertEquals(processedInput.getParsedSql(), processedInputClone.getParsedSql());
        org.junit.Assert.assertArrayEquals(processedInput.getSqlParameterNames().toArray(), processedInputClone.getSqlParameterNames().toArray());
        org.junit.Assert.assertArrayEquals(processedInput.getSqlParameterBoundaries().toArray(), processedInputClone.getSqlParameterBoundaries().toArray());
        org.junit.Assert.assertArrayEquals(processedInput.getSqlParameterValues().toArray(), processedInputClone.getSqlParameterValues().toArray());
        org.junit.Assert.assertEquals(processedInput.getAmountOfParameters().intValue(), processedInput.getSqlParameterTypes().size());
        org.junit.Assert.assertEquals(processedInput.getAmountOfParameters().intValue(), processedInput.getSqlParameterTypes().size());
    }

    @Test
    public void testConstructorParameters() {
        String originalSql = "some SQL string";

        Object[] updatedValues = new Object[] {100, "batman", "earth"};

        java.util.List<String> sqlParameterNames = new ArrayList<String>();
        List<int[]> sqlParameterBoundaries = new ArrayList<int[]>();
        List<Object> sqlParameterValues = new ArrayList<Object>();

        sqlParameterNames.add("name");
        sqlParameterNames.add("origin");
        sqlParameterNames.add("strength");

        sqlParameterBoundaries.add(new int[] {1, 2});
        sqlParameterBoundaries.add(new int[] {3, 4});
        sqlParameterBoundaries.add(new int[] {5, 6});

        sqlParameterValues.add(updatedValues[1]);
        sqlParameterValues.add(updatedValues[2]);
        sqlParameterValues.add(updatedValues[0]);

        ProcessedInput processedInput = new ProcessedInput(originalSql, "", sqlParameterNames, sqlParameterBoundaries, sqlParameterValues);

        Assert.assertEquals(originalSql, processedInput.getOriginalSql());
        Assert.assertEquals("", processedInput.getParsedSql());

        // checking if constructor haven't used the same objects but created clones
        Assert.assertEquals(false, sqlParameterNames == processedInput.getSqlParameterNames());
        Assert.assertEquals(false, sqlParameterBoundaries == processedInput.getSqlParameterBoundaries());
        Assert.assertEquals(false, sqlParameterValues == processedInput.getSqlParameterValues());

        // checking if values are loaded properly
        org.junit.Assert.assertArrayEquals(sqlParameterNames.toArray(), processedInput.getSqlParameterNames().toArray());
        org.junit.Assert.assertArrayEquals(sqlParameterBoundaries.toArray(), processedInput.getSqlParameterBoundaries().toArray());
        org.junit.Assert.assertArrayEquals(sqlParameterValues.toArray(), processedInput.getSqlParameterValues().toArray());
    }

    @Test
    public void testAddParameter() throws Exception {
        ProcessedInput processedInput = new ProcessedInput("original SQL");

        processedInput.addParameter("parameter", 3, 9);

        Assert.assertEquals("parameter", processedInput.getSqlParameterNames().get(0));
        org.junit.Assert.assertArrayEquals(new int[]{3, 9}, processedInput.getSqlParameterBoundaries().get(0));
    }

    @Test
    public void testGetOriginalSql() throws Exception {
        String originalSql = "original SQL";
        ProcessedInput processedInput = new ProcessedInput(originalSql);

        Assert.assertEquals(originalSql, processedInput.getOriginalSql());
    }

    @Test
    public void testGetParsedSql() throws Exception {
        String originalSql = "original SQL";
        String parsedSql = "parsed SQL";
        ProcessedInput processedInput = new ProcessedInput(originalSql);
        processedInput.setParsedSql(parsedSql);

        Assert.assertEquals(originalSql, processedInput.getOriginalSql());
        Assert.assertEquals(parsedSql, processedInput.getParsedSql());
    }

    @Test
    public void testGetSqlParameterNames() throws Exception {
        List<String> sqlParameterNames = new ArrayList<String>();
        sqlParameterNames.add("name");
        sqlParameterNames.add("origin");
        sqlParameterNames.add("strength");

        ProcessedInput processedInput = new ProcessedInput("");

        processedInput.addParameter(sqlParameterNames.get(0), 1, 1);
        processedInput.addParameter(sqlParameterNames.get(1), 1, 1);
        processedInput.addParameter(sqlParameterNames.get(2), 1, 1);

        org.junit.Assert.assertArrayEquals(sqlParameterNames.toArray(), processedInput.getSqlParameterNames().toArray());
    }

    @Test
    public void testGetSqlParameterBoundaries() throws Exception {
        List<int[]> sqlParameterBoundaries = new ArrayList<int[]>();
        sqlParameterBoundaries.add(new int[] {1, 2});
        sqlParameterBoundaries.add(new int[] {3, 4});
        sqlParameterBoundaries.add(new int[] {5, 6});


        ProcessedInput processedInput = new ProcessedInput("");

        processedInput.addParameter("", sqlParameterBoundaries.get(0)[0], sqlParameterBoundaries.get(0)[1]);
        processedInput.addParameter("", sqlParameterBoundaries.get(1)[0], sqlParameterBoundaries.get(1)[1]);
        processedInput.addParameter("", sqlParameterBoundaries.get(2)[0], sqlParameterBoundaries.get(2)[1]);

        org.junit.Assert.assertArrayEquals(sqlParameterBoundaries.toArray(), processedInput.getSqlParameterBoundaries().toArray());
    }

    @Test
    public void testGetSqlParameterValues() throws Exception {
        List<Object> sqlParameterValues = new ArrayList<Object>();

        sqlParameterValues.add("1");
        sqlParameterValues.add("2");
        sqlParameterValues.add("3");

        ProcessedInput processedInput = new ProcessedInput("");
        processedInput.setSqlParameterValues(sqlParameterValues);

        // shouldn't be the same object
        Assert.assertEquals(false, sqlParameterValues == processedInput.getSqlParameterValues());

        org.junit.Assert.assertArrayEquals(sqlParameterValues.toArray(), processedInput.getSqlParameterValues().toArray());
    }

    @Test
    public void testSetParsedSql() throws Exception {
        testGetParsedSql();
    }

    @Test
    public void testSetSqlParameterValues() throws Exception {
        testGetSqlParameterValues();
    }

    @Test
    public void testGetPosition() throws Exception {
        ProcessedInput processedInput = new ProcessedInput("");
        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 3, 4);
        processedInput.addParameter("strength", 5, 6);

        Assert.assertEquals(0, processedInput.getPosition("name").intValue());
        Assert.assertEquals(1, processedInput.getPosition("origin").intValue());
        Assert.assertEquals(2, processedInput.getPosition("strength").intValue());
    }

    @Test
    public void testGetParameterName() throws Exception {
        ProcessedInput processedInput = new ProcessedInput("");
        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 3, 4);
        processedInput.addParameter("strength", 5, 6);

        Assert.assertEquals("name", processedInput.getParameterName(0));
        Assert.assertEquals("origin", processedInput.getParameterName(1));
        Assert.assertEquals("strength", processedInput.getParameterName(2));
    }

    @Test
    public void testGetAmountOfParameters() throws Exception {
        ProcessedInput processedInput = new ProcessedInput("");

        Assert.assertEquals(0, processedInput.getAmountOfParameters().intValue());

        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 3, 4);
        processedInput.addParameter("strength", 5, 6);

        Assert.assertEquals(3, processedInput.getAmountOfParameters().intValue());
    }

    @Test
    public void testIsFilled() throws Exception {
        ProcessedInput processedInput = new ProcessedInput("a");

        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 3, 4);
        processedInput.addParameter("strength", 5, 6);

        Assert.assertEquals(false, processedInput.isFilled());

        processedInput.setParsedSql("b");

        Assert.assertEquals(true, processedInput.isFilled());
    }

    @Test
    public void testFillParameterValues() throws Exception {
        ProcessedInput processedInput = new ProcessedInput("a");

        processedInput.addParameter("name", 1, 2);
        processedInput.addParameter("origin", 3, 4);
        processedInput.addParameter("strength", 5, 6);

        org.junit.Assert.assertArrayEquals(new Object[0], processedInput.getSqlParameterValues().toArray());

        Map<String, Object> superMap = new HashMap<String, Object>();
        superMap.put("name", "superman");
        superMap.put("origin", "krypton");
        superMap.put("strength", 100);
        superMap.put("something_unneeded", 0);

        Assert.assertEquals(3, processedInput.getAmountOfParameters().intValue());

        processedInput.fillParameterValues(superMap);

        Assert.assertEquals(3, processedInput.getAmountOfParameters().intValue());
        org.junit.Assert.assertArrayEquals(new Object[] {superMap.get("name"), superMap.get("origin"), superMap.get("strength")}, processedInput.getSqlParameterValues().toArray());
    }
}
