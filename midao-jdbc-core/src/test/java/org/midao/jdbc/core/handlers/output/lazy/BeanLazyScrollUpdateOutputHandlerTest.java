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

import org.junit.Test;
import org.midao.jdbc.core.handlers.model.QueryParametersLazyList;

/**
 */
public class BeanLazyScrollUpdateOutputHandlerTest extends BaseLazyOutputHandlerTest {
    @Test
    public void testHasNext() throws Exception {
        innerTestHasPrepare();
        innerTestHasNext(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testGetNext() throws Exception {
        innerTestGetPrepare();
        innerTestGetNext(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testGetCurrent() throws Exception {
        innerTestGetPrepare();
        innerTestGetCurrent(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testClose() throws Exception {
        innerTestClosePrepare();

        innerTestClose(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testHandle() throws Exception {
        innerTestHandle(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)) instanceof BeanLazyScrollUpdateOutputHandler);
    }

    @Test
    public void testEmpty() throws Exception {
        innerTestEmpty(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testHasPrev() throws Exception {
        innerTestHasPrepare();
        innerTestHasPrev(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testGetPrev() throws Exception {
        innerTestGetPrepare();
        innerTestGetPrev(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testMoveTo() throws Exception {
        innerTestGetPrepare();
        innerTestMoveTo(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testMoveRelative() throws Exception {
        innerTestGetPrepare();
        innerTestMoveRelative(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testPosition() throws Exception {
        innerTestGetPrepare();
        innerTestPosition(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(new QueryParametersLazyList(stmt, typeHandler, false)));
    }

    @Test
    public void testUpdateRow() throws Exception {
        testTestUpdatePrepare();

        QueryParametersLazyList queryParamsLazyList = new QueryParametersLazyList(stmt, typeHandler, false);
        queryParamsLazyList.setType(QueryParametersLazyList.Type.UPDATE_SCROLL);

        testTestUpdateRow(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(queryParamsLazyList), zoe);
    }

    @Test
    public void testInsertRow() throws Exception {
        testTestUpdatePrepare();

        QueryParametersLazyList queryParamsLazyList = new QueryParametersLazyList(stmt, typeHandler, false);
        queryParamsLazyList.setType(QueryParametersLazyList.Type.UPDATE_SCROLL);

        testTestInsertRow(new BeanLazyScrollUpdateOutputHandler<Character>(Character.class).handle(queryParamsLazyList), zoe);
    }
}
