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

package org.midao.jdbc.core.handlers.output.lazy;

import org.junit.Assert;
import org.junit.Before;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.handlers.output.BaseOutputHandlerTest;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.handlers.type.UniversalTypeHandler;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 */
public class BaseLazyOutputHandlerTest extends BaseOutputHandlerTest {
    @Mock
    Statement stmt;
    @Mock
    ResultSet rs;
    @Mock
    ResultSetMetaData rsMeta;
    TypeHandler typeHandler = new UniversalTypeHandler(new Overrider());

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        init();
    }

    protected void innerTestHasPrepare() throws SQLException {
        when(stmt.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getMetaData()).thenReturn(rsMeta);
        when(rsMeta.getColumnCount()).thenReturn(0);
    }

    protected void innerTestHasNext(LazyOutputHandler handler) throws Exception {
        Assert.assertEquals(true, handler.hasNext());
        handler.getNext();
        Assert.assertEquals(false, handler.hasNext());
    }

    protected void innerTestHasPrev(LazyScrollOutputHandler handler) throws Exception {
        Assert.assertEquals(true, handler.hasNext());
        handler.getNext();
        Assert.assertEquals(false, handler.hasNext());
        Assert.assertEquals(true, handler.hasPrev());
        handler.getPrev();
        Assert.assertEquals(false, handler.hasPrev());
    }

    protected void innerTestGetPrepare() throws SQLException {
        when(stmt.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getMetaData()).thenReturn(rsMeta);
        when(rsMeta.getColumnCount()).thenReturn(1);
        when(rsMeta.getColumnLabel(1)).thenReturn("name");
        when(rs.getObject(1)).thenReturn("Nightcrawler").thenReturn("Lobo");
    }

    protected void innerTestGetNext(LazyOutputHandler handler) throws Exception {
        Map<String, Object> row = null;
        Character character = null;
        Object nextValue = null;

        Assert.assertEquals(true, handler.hasNext());

        nextValue = handler.getNext();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Nightcrawler", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Nightcrawler", row.get("name"));
        }

        nextValue = handler.getNext();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Lobo", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Lobo", row.get("name"));
        }
    }

    protected void innerTestGetCurrent(LazyOutputHandler handler) throws Exception {
        Map<String, Object> row = null;
        Character character = null;
        Object nextValue = null;

        Assert.assertEquals(true, handler.hasNext());

        handler.getNext();
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Nightcrawler", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Nightcrawler", row.get("name"));
        }

        handler.getNext();
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Lobo", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Lobo", row.get("name"));
        }
    }

    protected void innerTestGetPrev(LazyScrollOutputHandler handler) throws Exception {
        Map<String, Object> row = null;
        Character character = null;
        Object prevValue = null;

        Assert.assertEquals(true, handler.hasNext());

        handler.getNext();
        handler.getNext();

        // have to invoke additional time to move cursor to end
        handler.getNext();

        prevValue = handler.getPrev();

        if (prevValue instanceof Character) {
            character = (Character) prevValue;
            Assert.assertEquals("Lobo", character.getName());
        } else {
            row = (Map<String, Object>) prevValue;
            Assert.assertEquals("Lobo", row.get("name"));
        }

        prevValue = handler.getPrev();

        if (prevValue instanceof Character) {
            character = (Character) prevValue;
            Assert.assertEquals("Nightcrawler", character.getName());
        } else {
            row = (Map<String, Object>) prevValue;
            Assert.assertEquals("Nightcrawler", row.get("name"));
        }
    }

    protected void innerTestMoveTo(LazyScrollOutputHandler handler) throws Exception {
        Map<String, Object> row = null;
        Character character = null;
        Object nextValue = null;

        handler.moveTo(1);
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Nightcrawler", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Nightcrawler", row.get("name"));
        }

        handler.moveTo(2);
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Lobo", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Lobo", row.get("name"));
        }
    }

    protected void innerTestMoveRelative(LazyScrollOutputHandler handler) throws Exception {
        Map<String, Object> row = null;
        Character character = null;
        Object nextValue = null;

        handler.moveRelative(1);
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Nightcrawler", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Nightcrawler", row.get("name"));
        }

        handler.moveRelative(1);
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Lobo", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Lobo", row.get("name"));
        }

        handler.moveRelative(-1);
        nextValue = handler.getCurrent();

        if (nextValue instanceof Character) {
            character = (Character) nextValue;
            Assert.assertEquals("Nightcrawler", character.getName());
        } else {
            row = (Map<String, Object>) nextValue;
            Assert.assertEquals("Nightcrawler", row.get("name"));
        }
    }

    protected void innerTestPosition(LazyScrollOutputHandler handler) throws Exception {
        Map<String, Object> row = null;
        Character character = null;
        Object nextValue = null;

        handler.moveRelative(1);

        Assert.assertEquals(1, handler.position());

        handler.moveRelative(1);

        Assert.assertEquals(2, handler.position());

        handler.moveRelative(-1);

        Assert.assertEquals(1, handler.position());
    }

    protected void innerTestClosePrepare() throws SQLException {
        when(stmt.getResultSet()).thenReturn(rs);
    }

    protected void innerTestClose(LazyOutputHandler handler) throws Exception {
        handler.close();

        verify(rs, times(1)).close();
        verify(stmt, times(1)).getMoreResults();
    }

    protected void innerTestHandle(boolean instanceOfCheck) throws Exception {
        Assert.assertEquals(true, instanceOfCheck);
    }

    protected void innerTestEmpty(LazyOutputHandler handler) throws Exception {
        Assert.assertEquals(false, handler.hasNext());
    }

    protected void testTestUpdatePrepare() throws SQLException {
        when(rs.getRow()).thenReturn(1);
        when(stmt.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(false).thenReturn(false);
        when(rs.getMetaData()).thenReturn(rsMeta);
        when(rsMeta.getColumnCount()).thenReturn(3);
    }

    protected <T, S> void testTestUpdateRow(LazyUpdateOutputHandler<T, S> handler, S row) throws Exception {
        handler.updateRow(row);

        verify(rs, times(1)).updateObject(eq("occupation"), any());
        verify(rs, times(1)).updateObject(eq("age"), any());
        verify(rs, times(1)).updateObject(eq("name"), any());
        verify(rs, times(1)).updateRow();
    }

    protected <T, S> void testTestInsertRow(LazyUpdateOutputHandler<T, S> handler, S row) throws Exception {
        handler.insertRow(row);

        verify(rs, times(1)).updateObject(eq("occupation"), any());
        verify(rs, times(1)).updateObject(eq("age"), any());
        verify(rs, times(1)).updateObject(eq("name"), any());
        verify(rs, times(1)).moveToInsertRow();
        verify(rs, times(1)).insertRow();
    }
}
