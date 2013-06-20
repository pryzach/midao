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

package org.midao.jdbc.core.utils;

import junit.framework.Assert;
import org.junit.Test;
import org.midao.jdbc.core.exception.MidaoSQLException;

import java.sql.SQLException;

import static org.junit.Assert.fail;

/**
 */
public class AssertUtilsTest {
    private String exceptionMessage = "Catch me";

    @Test(expected = Exception.class)
    public void testAssertNotNull() {
        AssertUtils.assertNotNull(null);
    }

    @Test
    public void testAssertNotNullMessage() {
        try {
            AssertUtils.assertNotNull(null, exceptionMessage);
        } catch (Exception ex) {
            Assert.assertEquals(exceptionMessage, ex.getMessage());
        }
    }

    @Test
    public void testAssertNotNullException() throws Exception {
        SQLException exception = new MidaoSQLException(exceptionMessage);

        try {
            AssertUtils.assertNotNull(null, exception);
        } catch (MidaoSQLException ex) {
            Assert.assertEquals(exceptionMessage, ex.getMessage());
        } catch (SQLException ex) {
            fail();
        }
    }
}
