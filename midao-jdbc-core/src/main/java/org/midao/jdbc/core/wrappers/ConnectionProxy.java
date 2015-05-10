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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * SQL Connection Proxy.
 * <p/>
 * Is used by {@link org.midao.jdbc.core.transaction.TransactionHandler} to return Proxy instead of real Connection.
 *
 * @see {@link org.midao.jdbc.core.transaction.TransactionHandler#getConnection()}
 */
public class ConnectionProxy implements java.lang.reflect.InvocationHandler {
    private final Connection conn;

    /**
     * Creates new SQL Connection Proxy instance
     *
     * @param conn SQL Connection
     * @return Proxy SQL Connection
     */
    public static Connection newInstance(Connection conn) {
        return (Connection) java.lang.reflect.Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class},
                new ConnectionProxy(conn));
        /*
        return (Connection) java.lang.reflect.Proxy.newProxyInstance(conn.getClass().getClassLoader(), conn.getClass().getInterfaces(),
                new ConnectionProxy(conn));
                */
    }

    /**
     * Creates new ConnectionProxy instance
     *
     * @param conn SQL Connection
     */
    public ConnectionProxy(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String methodName = method.getName();

        if ("close".equals(methodName) == true) {
            // nothing should be
        } else {

            try {
                result = method.invoke(conn, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
            }
        }

        return result;
    }
}
