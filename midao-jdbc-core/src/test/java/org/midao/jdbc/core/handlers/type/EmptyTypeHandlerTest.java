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

package org.midao.jdbc.core.handlers.type;

import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
public class EmptyTypeHandlerTest {
    @Mock Connection conn;
    @Mock Statement stmt;
    @Mock Array array;
    @Mock Blob blob;
    @Mock Clob clob;
    //@Mock SQLXML sqlXml;
    @Mock OutputStream output;
    @Mock InputStream input;

    QueryParameters params;
    Object sqlXml;

    @Before
    public void setUp() throws SQLException, IOException, MjdbcException, ClassNotFoundException {
        MockitoAnnotations.initMocks(this);

        sqlXml = Mockito.mock(Class.forName("java.sql.SQLXML"));

        when(stmt.getConnection()).thenReturn(conn);
        when(MappingUtils.invokeFunction(conn, "createBlob", new Class[]{}, new Object[]{})).thenReturn(blob);
        when(MappingUtils.invokeFunction(conn, "createClob", new Class[]{}, new Object[]{})).thenReturn(clob);
        when(MappingUtils.invokeFunction(conn, "createSQLXML", new Class[]{}, new Object[]{})).thenReturn(sqlXml);
        when(MappingUtils.invokeFunction(conn, "createArrayOf", new Class[]{String.class, Object[].class}, new Object[]{any(String.class), any(Object[].class)})).thenReturn(array);

        when(blob.setBinaryStream(1)).thenReturn(output);
        when(clob.setAsciiStream(1)).thenReturn(output);
        //when(sqlXml.setBinaryStream()).thenReturn(output);
        when(MappingUtils.invokeFunction(sqlXml, "setBinaryStream", new Class[]{}, new Object[]{})).thenReturn(output);

        when(input.read(any(byte[].class))).thenReturn(-1);
        when(input.read(any(byte[].class), any(int.class), any(int.class))).thenReturn(-1);

        when(blob.getBinaryStream()).thenReturn(input);
        when(clob.getAsciiStream()).thenReturn(input);
        //when(sqlXml.getBinaryStream()).thenReturn(input);
        when(MappingUtils.invokeFunction(sqlXml, "getBinaryStream", new Class[]{}, new Object[]{})).thenReturn(input);

        params = new QueryParameters();

        params.set("array_list", Arrays.asList("Superman"), MjdbcTypes.ARRAY);
        params.set("blob_byte", "Batman", MjdbcTypes.BLOB);
        params.set("clob_byte", "Wolverine", MjdbcTypes.CLOB);
        params.set("sqlXml_byte", "Magneto", MjdbcTypes.SQLXML);

        params.set("array", array, MjdbcTypes.ARRAY);
        params.set("blob", blob, MjdbcTypes.BLOB);
        params.set("clob", clob, MjdbcTypes.CLOB);
        params.set("sqlXml", sqlXml, MjdbcTypes.SQLXML);
    }

    @Test
    public void testProcessInput() throws Exception {
        new EmptyTypeHandler(new Overrider()).processInput(stmt, params);

        MappingUtils.invokeFunction(verify(conn, never()), "createBlob", new Class[]{}, new Object[]{});
        MappingUtils.invokeFunction(verify(conn, never()), "createClob", new Class[]{}, new Object[]{});
        MappingUtils.invokeFunction(verify(conn, never()), "createSQLXML", new Class[]{}, new Object[]{});
        MappingUtils.invokeFunction(verify(conn, never()), "createArrayOf", new Class[]{String.class, Object[].class}, new Object[]{any(String.class), any(Object[].class)});

        verify(blob, never()).setBinaryStream(1);
        verify(clob, never()).setAsciiStream(1);
        //verify(sqlXml, never()).setBinaryStream();
        MappingUtils.invokeFunction(verify(sqlXml, never()), "setBinaryStream", new Class[]{}, new Object[]{});
    }

    @Test
    public void testAfterExecute() throws Exception {
        QueryParameters processedParams = new QueryParameters(params);
        processedParams.set("array_list", processedParams.getValue("array"));
        processedParams.set("blob_byte", processedParams.getValue("blob"));
        processedParams.set("clob_byte", processedParams.getValue("clob"));
        processedParams.set("sqlXml_byte", processedParams.getValue("sqlXml"));

        new EmptyTypeHandler(new Overrider()).afterExecute(stmt, processedParams, params);

        MappingUtils.invokeFunction(verify(array, never()), "free", new Class[]{}, new Object[]{});
        MappingUtils.invokeFunction(verify(blob, never()), "free", new Class[]{}, new Object[]{});
        MappingUtils.invokeFunction(verify(clob, never()), "free", new Class[]{}, new Object[]{});
        MappingUtils.invokeFunction(verify(sqlXml, never()), "free", new Class[]{}, new Object[]{});
    }

    @Test
    public void testProcessOutput() throws Exception {
        new EmptyTypeHandler(new Overrider()).processOutput(stmt, params);

        verify(array, never()).getArray();
        verify(blob, never()).getBinaryStream();
        verify(clob, never()).getAsciiStream();
        //verify(sqlXml, never()).getBinaryStream();
        MappingUtils.invokeFunction(verify(sqlXml, never()), "getBinaryStream", new Class[]{}, new Object[]{});
    }

    @Test
    public void testProcessOutputList() throws Exception {
        List<QueryParameters> paramsList = new ArrayList<QueryParameters>();
        QueryParameters paramsClone = new QueryParameters(params);

        paramsList.add(new QueryParameters()); // first element is usually technical query parameters
        paramsList.add(params);
        paramsList.add(paramsClone);

        new EmptyTypeHandler(new Overrider()).processOutput(stmt, paramsList);

        verify(array, never()).getArray();
        verify(blob, never()).getBinaryStream();
        verify(clob, never()).getAsciiStream();
        //verify(sqlXml, never()).getBinaryStream();
        MappingUtils.invokeFunction(verify(sqlXml, never()), "getBinaryStream", new Class[]{}, new Object[]{});
    }
}
