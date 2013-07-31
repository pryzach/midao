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

package org.midao.jdbc.core.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.MappingUtils;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 */
public class BasicQueryOutputProcessorTest {
    QueryOutputProcessor queryOutputProcessor = new BasicQueryOutputProcessor();

    List<QueryParameters> paramsList = new ArrayList<QueryParameters>();

    @Before
    public void setUp() {
        paramsList.add(new QueryParameters()); // first element keeps internal information and is always skipped during processing
    }

    @Test
    public void testToArray() throws Exception {
        QueryParameters params = new QueryParameters();
        Object[] expected = new Object[] {"8", "7", "4"};

        params.set("intelligence", expected[0]);
        params.set("charisma", expected[1]);
        params.set("strength", expected[2]);

        paramsList.add(params);

        Object[] result = queryOutputProcessor.toArray(paramsList);

        Assert.assertEquals(3, result.length);
        org.junit.Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void testToArrayList() throws Exception {
        QueryParameters params = null;
        Object[] expected = new Object[] {"8", "7", "4"};

        params = new QueryParameters();
        params.set("intelligence", expected[0]);
        params.set("charisma", expected[1]);
        params.set("strength", expected[2]);

        paramsList.add(params);

        params = new QueryParameters();
        params.set("perception", expected[0]);
        params.set("agility", expected[2]);
        params.set("luck", expected[1]);

        paramsList.add(params);

        List<Object[]> result = queryOutputProcessor.toArrayList(paramsList);

        Assert.assertEquals(3, result.get(0).length);
        org.junit.Assert.assertArrayEquals(expected, result.get(0));
        Assert.assertEquals(3, result.get(1).length);
        org.junit.Assert.assertArrayEquals(new Object[] {expected[0], expected[2], expected[1]}, result.get(1));
    }

    @Test
    public void testToBean1() throws Exception {
        testToBean("list");
    }

    @Test
    public void testToBean2() throws Exception {
        testToBean("one");
    }

    private void testToBean(String operation) throws Exception {
        QueryParameters params = null;
        int age = 5;
        String name = "whiskers";

        params = new QueryParameters();
        params.set("age", age);
        params.set("name", name);

        paramsList.add(params);

        Cat result = null;

        if ("list".equals(operation) == true) {
            result = queryOutputProcessor.toBean(paramsList, Cat.class);
        } else if ("one".equals(operation) == true) {
            result = queryOutputProcessor.toBean(params, Cat.class);
        } else {
            fail();
        }

        Assert.assertEquals(age, result.getAge());
        Assert.assertEquals(name, result.getName());
    }

    @Test
    public void testToBeanList() throws Exception {
        QueryParameters params1 = null;
        QueryParameters params2 = null;

        int age1 = 5;
        String name1 = "whiskers";

        int age2 = 10;
        String name2 = "lucky";

        params1 = new QueryParameters();
        params1.set("age", age1);
        params1.set("name", name1);

        paramsList.add(params1);

        params2 = new QueryParameters();
        params2.set("age", age2);
        params2.set("name", name2);

        paramsList.add(params2);

        List<Cat> result = null;

        result = queryOutputProcessor.toBeanList(paramsList, Cat.class);

        Assert.assertEquals(age1, result.get(0).getAge());
        Assert.assertEquals(name1, result.get(0).getName());

        Assert.assertEquals(age2, result.get(1).getAge());
        Assert.assertEquals(name2, result.get(1).getName());
    }

    @Test
    public void testToMap1() throws Exception {
        testToMap("list");
    }

    @Test
    public void testToMap2() throws Exception {
        testToMap("one");
    }

    private void testToMap(String operation) throws Exception {
        QueryParameters params = null;
        int age = 5;
        String name = "whiskers";

        params = new QueryParameters();
        params.set("age", age);
        params.set("name", name);

        paramsList.add(params);

        Map<String, Object> result = null;

        if ("list".equals(operation) == true) {
            result = queryOutputProcessor.toMap(paramsList);
        } else if ("one".equals(operation) == true) {
            result = queryOutputProcessor.toMap(params);
        } else {
            fail();
        }

        Assert.assertEquals(age, result.get("age"));
        Assert.assertEquals(name, result.get("name"));
    }

    @Test
    public void testToMapList() throws Exception {
        QueryParameters params1 = null;
        QueryParameters params2 = null;

        int age1 = 5;
        String name1 = "whiskers";

        int age2 = 10;
        String name2 = "lucky";

        params1 = new QueryParameters();
        params1.set("age", age1);
        params1.set("name", name1);

        paramsList.add(params1);

        params2 = new QueryParameters();
        params2.set("age", age2);
        params2.set("name", name2);

        paramsList.add(params2);

        List<Map<String, Object>> result = null;

        result = queryOutputProcessor.toMapList(paramsList);

        Assert.assertEquals(age1, result.get(0).get("age"));
        Assert.assertEquals(name1, result.get(0).get("name"));

        Assert.assertEquals(age2, result.get(1).get("age"));
        Assert.assertEquals(name2, result.get(1).get("name"));
    }

    @Test
    public void testProcessValue() throws Exception {
        long expectedTime = 100;
        java.util.Date resultTime = null;

        QueryParameters params = new QueryParameters();
        params.set("date", new Date(expectedTime));
        params.set("time", new Time(expectedTime + 10));
        params.set("timestamp", new Timestamp(expectedTime + 20));
        params.set("bigdecimal", new BigDecimal("10.10"));
        params.set("bigInteger", new BigInteger("11"));

        PropertyDescriptor props[] = MappingUtils.propertyDescriptors(SQLValues.class);

        Long bigInteger = (Long) queryOutputProcessor.processValue(params, 4, props[1]);

        Assert.assertEquals(bigInteger.longValue(), 11);

        Double bigDecimal = (Double) queryOutputProcessor.processValue(params, 3, props[0]);

        Assert.assertEquals(bigDecimal.doubleValue(), 10,10);

        resultTime = (java.util.Date) queryOutputProcessor.processValue(params, 0, props[3]);

        Assert.assertEquals(resultTime.getTime(), expectedTime);

        resultTime = (java.util.Date) queryOutputProcessor.processValue(params, 1, props[4]);

        Assert.assertEquals(resultTime.getTime(), expectedTime + 10);

        resultTime = (java.util.Date) queryOutputProcessor.processValue(params, 2, props[5]);

        Assert.assertEquals(resultTime.getTime(), expectedTime + 20);

        try {
            queryOutputProcessor.processValue(params, 1, props[5]);
            fail();
        } catch (MjdbcException ex) {
            Assert.assertEquals("Cannot set timestamp: incompatible types, cannot convert java.sql.Time to java.sql.Timestamp", ex.getMessage());
        }
    }

    @Test
    public void testToMapNull() {
        Assert.assertEquals(null, queryOutputProcessor.toMap((QueryParameters) null));
    }

    @Test
    public void testToBeanNull() throws MjdbcException {
        Assert.assertEquals(null, queryOutputProcessor.toBean((QueryParameters) null, Cat.class));
    }

    public static class Cat {
        private int age;
        private String name;

        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SQLValues {
        private java.sql.Date date;
        private java.sql.Time time;
        private java.sql.Timestamp timestamp;
        private Double bigDecimal;
        private Long bigInteger;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Time getTime() {
            return time;
        }

        public void setTime(Time time) {
            this.time = time;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public Double getBigDecimal() {
            return bigDecimal;
        }

        public void setBigDecimal(Double bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public Long getBigInteger() {
            return bigInteger;
        }

        public void setBigInteger(Long bigInteger) {
            this.bigInteger = bigInteger;
        }
    }
}
