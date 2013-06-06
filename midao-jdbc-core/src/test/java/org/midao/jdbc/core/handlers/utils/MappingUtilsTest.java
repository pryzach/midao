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

package org.midao.jdbc.core.handlers.utils;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 */
public class MappingUtilsTest {
    @Mock Connection conn;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToMap() throws Exception {
        int age = 5;
        String name = "whiskers";

        Cat cat = new Cat();
        cat.setAge(age);
        cat.setName(name);

        Map<String, Object> map = MappingUtils.toMap(cat, MappingUtils.propertyDescriptors(cat.getClass()));

        Assert.assertEquals(cat.getAge(), map.get("age"));
        Assert.assertEquals(cat.getName(), map.get("name"));
    }

    @Test
    public void testPropertyDescriptors() throws Exception {
        PropertyDescriptor props[] = MappingUtils.propertyDescriptors(Cat.class);

        Assert.assertEquals("age", props[0].getName());
        Assert.assertEquals("name", props[2].getName());
    }

    @Test
    public void testMapPropertyDescriptors() throws Exception {
        Map<String, PropertyDescriptor> mapProps = MappingUtils.mapPropertyDescriptors(Cat.class);

        Assert.assertEquals("age", mapProps.get("age").getName());
        Assert.assertEquals("name", mapProps.get("name").getName());
    }

    @Test
    public void testCallGetter() throws Exception {
        int age = 5;
        String name = "whiskers";

        Cat cat = new Cat();
        cat.setAge(age);
        cat.setName(name);

        Assert.assertEquals(age, MappingUtils.callGetter(cat, MappingUtils.propertyDescriptors(cat.getClass())[0]));
        Assert.assertEquals(name, MappingUtils.callGetter(cat, MappingUtils.propertyDescriptors(cat.getClass())[2]));
    }

    @Test
    public void testCallSetter() throws Exception {
        int age = 5;
        String name = "whiskers";

        Cat cat = new Cat();

        MappingUtils.callSetter(cat, MappingUtils.propertyDescriptors(cat.getClass())[0], age);
        MappingUtils.callSetter(cat, MappingUtils.propertyDescriptors(cat.getClass())[2], name);

        Assert.assertEquals(age, cat.getAge());
        Assert.assertEquals(name, cat.getName());
    }

    @Test
    public void testConvertResultSet() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData rsmd = mock(ResultSetMetaData.class);

        when(rsmd.getColumnCount()).thenReturn(1);
        when(rsmd.getColumnLabel(1)).thenReturn("name");
        when(rs.getMetaData()).thenReturn(rsmd);

        String name1 = "Bruce Lee";
        String name2 = "Jackie Chan";
        String name3 = "Jet Lee";

        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getObject(1)).thenReturn(name1).thenReturn(name2).thenReturn(name3);

        List<QueryParameters> paramsList = MappingUtils.convertResultSet(rs);

        Assert.assertEquals(3, paramsList.size());
        Assert.assertEquals(name1, paramsList.get(0).getValue("name"));
        Assert.assertEquals(name2, paramsList.get(1).getValue("name"));
        Assert.assertEquals(name3, paramsList.get(2).getValue("name"));
    }

    @Test
    public void testNewInstance() throws Exception {
        Cat cat = MappingUtils.newInstance(Cat.class);

        Assert.assertEquals(true, cat != null && cat instanceof Cat);
    }

    @Test
    public void testInvokeConnectionSetter() throws MidaoException {
        MappingUtils.invokeConnectionSetter(conn, "readOnly", false);
        MappingUtils.invokeConnectionSetter(conn, "catalog", "");
        MappingUtils.invokeConnectionSetter(conn, "typeMap", null);
        MappingUtils.invokeConnectionSetter(conn, "holdability", 0);
        MappingUtils.invokeConnectionSetter(conn, "clientInfo", null);

        MappingUtils.invokeFunction(verify(conn, times(1)), "setReadOnly", new Class[]{boolean.class}, new Object[]{any(boolean.class)});
        MappingUtils.invokeFunction(verify(conn, times(1)), "setCatalog", new Class[]{String.class}, new Object[]{""});
        MappingUtils.invokeFunction(verify(conn, times(1)), "setTypeMap", new Class[]{Map.class}, new Object[]{any(Map.class)});
        MappingUtils.invokeFunction(verify(conn, times(1)), "setHoldability", new Class[]{int.class}, new Object[]{any(int.class)});
        MappingUtils.invokeFunction(verify(conn, times(1)), "setClientInfo", new Class[]{Properties.class}, new Object[]{any(Properties.class)});
    }

    @Test
    public void testInvokeFunction() throws MidaoException {
        Assert.assertEquals(true, MappingUtils.invokeFunction("", "isEmpty", new Class[]{}, new Object[]{}));
        Assert.assertEquals(false, MappingUtils.invokeFunction("bla", "isEmpty", new Class[]{}, new Object[]{}));
    }

    @Test
    public void testInvokeStaticFunction() throws MidaoException {
        Assert.assertEquals("1", MappingUtils.invokeStaticFunction(String.class, "valueOf", new Class[]{int.class}, new Object[]{1}));
        Assert.assertEquals("3", MappingUtils.invokeStaticFunction(String.class, "valueOf", new Class[]{int.class}, new Object[]{3}));
    }

    @Test
    public void testHasFunction() {
        Assert.assertEquals(true, MappingUtils.hasFunction("", "length", new Class[]{}));
        Assert.assertEquals(false, MappingUtils.hasFunction("", "bla", new Class[]{}));
    }

    @Test
    public void testObjectImplements() {
        Assert.assertEquals(true, MappingUtils.objectImplements(conn, "java.sql.Connection"));
        Assert.assertEquals(true, MappingUtils.objectImplements(conn, "org.mockito.cglib.proxy.Factory"));
        Assert.assertEquals(false, MappingUtils.objectImplements(conn, "com.some.bla.bla"));
    }

    @Test
    public void testObjectExtends() {
        Assert.assertEquals(true, MappingUtils.objectExtends(new Integer(0), "java.lang.Number"));
        Assert.assertEquals(false, MappingUtils.objectExtends(new Integer(0), "java.lang.Comparable"));
    }

    @Test
    public void testReturnStaticField() throws MidaoException {
        Assert.assertEquals(Integer.SIZE, MappingUtils.returnStaticField(Integer.class, "SIZE"));
        Assert.assertEquals(Integer.MAX_VALUE, MappingUtils.returnStaticField(Integer.class, "MAX_VALUE"));
    }

    @Test
    public void testObjectInstanceOf() {
        Assert.assertEquals(true, MappingUtils.objectInstanceOf("", "java.lang.String"));
        Assert.assertEquals(true, MappingUtils.objectInstanceOf(new Integer(0), "java.lang.Integer"));
        Assert.assertEquals(false, MappingUtils.objectInstanceOf(new Integer(0), "java.lang.Number"));
    }

    @Test
    public void testObjectAssignableTo() throws MidaoException {
        Assert.assertEquals(true, MappingUtils.objectAssignableTo("", "java.lang.String"));
        Assert.assertEquals(true, MappingUtils.objectAssignableTo(new Integer(0), "java.lang.Integer"));
        Assert.assertEquals(true, MappingUtils.objectAssignableTo(new Integer(0), "java.lang.Number"));
        Assert.assertEquals(false, MappingUtils.objectAssignableTo(new Integer(0), "java.lang.Double"));
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
}
