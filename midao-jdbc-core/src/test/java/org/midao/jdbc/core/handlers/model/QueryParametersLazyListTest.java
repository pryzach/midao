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
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.handlers.type.UniversalTypeHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 */
public class QueryParametersLazyListTest {
    @Mock
    ResultSet rs;
    @Mock
    Statement stmt;
    @Mock
    ResultSetMetaData rsMeta;
    TypeHandler typeHandler = new UniversalTypeHandler(new Overrider());
    QueryParametersLazyList queryParametersLazyList;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(stmt.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getMetaData()).thenReturn(rsMeta);
        when(rsMeta.getColumnCount()).thenReturn(1);
        when(rsMeta.getColumnLabel(1)).thenReturn("name");
        when(rs.getObject(1)).thenReturn("Nightcrawler").thenReturn("Lobo");

        queryParametersLazyList = new QueryParametersLazyList(stmt, typeHandler, false, -1);
    }

    @Test
    public void testSetMaxCacheSize() throws Exception {
        queryParametersLazyList.get(1);

        // we receive header as first element
        Assert.assertEquals(2, queryParametersLazyList.sizeCached());

        queryParametersLazyList.setMaxCacheSize(1);

        queryParametersLazyList.get(2);

        // header is always as first element and is never "evicted"
        Assert.assertEquals(2, queryParametersLazyList.sizeCached());
    }

    @Test
    public void testIsEmpty() throws Exception {
        Assert.assertEquals(false, queryParametersLazyList.isEmpty());
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testSize() throws Exception {
        queryParametersLazyList.size();
    }

    @Test
    public void testSizeCached() throws Exception {
        // we receive header as first element
        Assert.assertEquals(1, queryParametersLazyList.sizeCached());

        queryParametersLazyList.get(1);
        queryParametersLazyList.get(2);

        // we receive header as first element
        Assert.assertEquals(3, queryParametersLazyList.sizeCached());
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testToArray() throws Exception {
        queryParametersLazyList.toArray();
    }

    @Test
    public void testToArrayCached() throws Exception {
        Object[] result = queryParametersLazyList.toArrayCached();

        // we receive header as first element
        Assert.assertEquals(1, result.length);

        queryParametersLazyList.get(1);
        queryParametersLazyList.get(2);

        result = queryParametersLazyList.toArrayCached();

        // we receive header as first element
        Assert.assertEquals(3, result.length);
        Assert.assertEquals("Nightcrawler", ((QueryParameters) result[1]).getValue("name"));
        Assert.assertEquals("Lobo", ((QueryParameters) result[2]).getValue("name"));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testToArrayType() throws Exception {
        queryParametersLazyList.toArray(new String[0]);
    }

    @Test
    public void testToArrayCachedType() throws Exception {
        QueryParameters[] result = queryParametersLazyList.toArrayCached(new QueryParameters[0]);

        // we receive header as first element
        Assert.assertEquals(1, result.length);

        queryParametersLazyList.get(1);
        queryParametersLazyList.get(2);

        result = queryParametersLazyList.toArrayCached(new QueryParameters[0]);

        // we receive header as first element
        Assert.assertEquals(3, result.length);
        Assert.assertEquals("Nightcrawler", result[1].getValue("name"));
        Assert.assertEquals("Lobo", result[2].getValue("name"));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testSubList() throws Exception {
        queryParametersLazyList.subList(0, 2);
    }

    @Test
    public void testSubListCached() throws Exception {
        List<QueryParameters> result = null;

        try {
            result = queryParametersLazyList.subListCached(1, 3);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // everything is correct - those values are not in cache yet.
        }

        queryParametersLazyList.get(1);
        queryParametersLazyList.get(2);

        // next attempt
        result = queryParametersLazyList.subListCached(1, 3);

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Nightcrawler", result.get(0).getValue("name"));
        Assert.assertEquals("Lobo", result.get(1).getValue("name"));
    }

    @Test
    public void testGet() throws Exception {
        Assert.assertEquals("Nightcrawler", queryParametersLazyList.get(1).getValue("name"));
        Assert.assertEquals("Lobo", queryParametersLazyList.get(2).getValue("name"));
        Assert.assertEquals(null, queryParametersLazyList.get(3));
    }

    @Test
    public void testClose() throws Exception {
        queryParametersLazyList.close();

        verify(rs, times(1)).close();
        verify(stmt, times(1)).close();
    }

    @Test
    public void testIterator() throws Exception {
        Iterator<QueryParameters> iter = queryParametersLazyList.iterator();
        iter.next();

        Assert.assertEquals("Nightcrawler", iter.next().getValue("name"));
        Assert.assertEquals("Lobo", iter.next().getValue("name"));
    }

    @Test
    public void testSet() throws Exception {
        QueryParameters paramsOriginal1 = null;
        QueryParameters paramsOriginal2 = null;
        QueryParameters paramsSet = new QueryParameters();

        paramsOriginal1 = queryParametersLazyList.get(1);
        paramsOriginal2 = queryParametersLazyList.set(1, paramsSet);

        Assert.assertEquals(paramsOriginal1, paramsOriginal2);
        Assert.assertEquals(paramsSet, queryParametersLazyList.get(1));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testListIterator() throws Exception {
        queryParametersLazyList.listIterator();
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testListIteratorIndex() throws Exception {
        queryParametersLazyList.listIterator(1);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testAdd() throws Exception {
        queryParametersLazyList.add(null);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testAddIndex() throws Exception {
        queryParametersLazyList.add(1, null);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testAddAll() throws Exception {
        queryParametersLazyList.addAll(Arrays.<QueryParameters>asList(new QueryParameters[0]));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testAddAllIndex() throws Exception {
        queryParametersLazyList.addAll(1, Arrays.<QueryParameters>asList(new QueryParameters[0]));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testRemove() throws Exception {
        queryParametersLazyList.remove(null);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testRemoveIndex() throws Exception {
        queryParametersLazyList.remove(1);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testRemoveAll() throws Exception {
        queryParametersLazyList.removeAll(Arrays.<QueryParameters>asList(new QueryParameters[0]));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testRetainAll() throws Exception {
        queryParametersLazyList.retainAll(Arrays.<QueryParameters>asList(new QueryParameters[0]));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testClear() throws Exception {
        queryParametersLazyList.clear();
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testContains() throws Exception {
        queryParametersLazyList.contains(null);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testContainsAll() throws Exception {
        queryParametersLazyList.containsAll(Arrays.<QueryParameters>asList(new QueryParameters[0]));
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testIndexOf() throws Exception {
        queryParametersLazyList.indexOf(null);
    }

    @Test(expected = MjdbcRuntimeException.class)
    public void testLastIndexOf() throws Exception {
        queryParametersLazyList.lastIndexOf(null);
    }
}
