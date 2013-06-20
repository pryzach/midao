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

package org.midao.jdbc.core.handlers.output;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.model.QueryParametersLazyList;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.handlers.type.UniversalTypeHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;

/**
 */
public class BeanListLazyOutputHandlerTest extends BaseOutputHandlerTest {
    @Mock Statement stmt;
    @Mock ResultSet rs;
    @Mock ResultSetMetaData rsMeta;
    TypeHandler typeHandler = new UniversalTypeHandler(new Overrider());

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        init();
    }

    @Test
    public void testHasNext() throws Exception {
        when(stmt.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getMetaData()).thenReturn(rsMeta);
        when(rsMeta.getColumnCount()).thenReturn(0);

        BeanListLazyOutputHandler<Character> result = new BeanListLazyOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false));

        Assert.assertEquals(true, result.hasNext());
        result.getNext();
        Assert.assertEquals(false, result.hasNext());
    }

    @Test
    public void testGetNext() throws Exception {
        when(stmt.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getMetaData()).thenReturn(rsMeta);
        when(rsMeta.getColumnCount()).thenReturn(1);
        when(rsMeta.getColumnLabel(1)).thenReturn("name");
        when(rs.getObject(1)).thenReturn("Nightcrawler").thenReturn("Lobo");

        BeanListLazyOutputHandler<Character> result = new BeanListLazyOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false));

        Assert.assertEquals(true, result.hasNext());
        Assert.assertEquals("Nightcrawler", result.getNext().getName());
        Assert.assertEquals("Lobo", result.getNext().getName());
        Assert.assertEquals(false, result.hasNext());
    }

    @Test
    public void testClose() throws Exception {
        when(stmt.getResultSet()).thenReturn(rs);

        BeanListLazyOutputHandler<Character> result = new BeanListLazyOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false));

        result.close();

        verify(rs, times(1)).close();
        verify(stmt, times(1)).getMoreResults();
    }

    @Test
    public void testHandle() throws Exception {
        Assert.assertEquals(true, new BeanListLazyOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)) instanceof BeanListLazyOutputHandler);
    }

    @Test
    public void testEmpty() throws MidaoException, SQLException {
        BeanListLazyOutputHandler<Character> result = new BeanListLazyOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false));

        Assert.assertEquals(false, result.hasNext());
    }
}
