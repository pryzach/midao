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

package org.midao.jdbc.core.wrappers;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.wrappers.ConnectionProxy;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Proxy;
import java.sql.Connection;

import static org.mockito.Mockito.*;

/**
 */
public class ConnectionProxyTest {
    @Mock Connection conn;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNewInstance() throws Exception {
        Connection result = null;

        result = ConnectionProxy.newInstance(conn);

        Assert.assertEquals(true, Proxy.isProxyClass(result.getClass()));
        Assert.assertEquals(true, result instanceof Connection);
    }

    @Test
    public void testInvoke() throws Exception {
        Connection result = null;

        result = ConnectionProxy.newInstance(conn);

        result.isClosed();
        result.close();

        verify(conn, times(1)).isClosed();
        verify(conn, never()).close();
    }
}
