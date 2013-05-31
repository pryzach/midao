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

package org.midao.core.handlers.type;

import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.BLOB;
import oracle.sql.CLOB;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.MidaoTypes;
import org.midao.core.Overrider;
import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.utils.MappingUtils;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class OracleTypeHandlerTest {
    @Mock OracleConnection conn;
    @Mock Statement stmt;
    @Mock ARRAY array;
    @Mock BLOB blob;
    @Mock CLOB clob;
    @Mock OutputStream output;
    @Mock InputStream input;

    QueryParameters params;

    @Before
    public void setUp() throws SQLException, IOException, MidaoException {
        MockitoAnnotations.initMocks(this);

        when(stmt.getConnection()).thenReturn(conn);
        when(conn.createARRAY(any(String.class), any(Object[].class))).thenReturn(array);
        when(MappingUtils.invokeFunction(conn, "createBlob", new Class[]{}, new Object[]{})).thenReturn(blob);
        when(MappingUtils.invokeFunction(conn, "createClob", new Class[]{}, new Object[]{})).thenReturn(clob);

        when(blob.setBinaryStream(1)).thenReturn(output);
        when(clob.setAsciiStream(1)).thenReturn(output);

        when(input.read(any(byte[].class))).thenReturn(-1);
        when(input.read(any(byte[].class), any(int.class), any(int.class))).thenReturn(-1);

        when(blob.getBinaryStream()).thenReturn(input);
        when(clob.getAsciiStream()).thenReturn(input);

        params = new QueryParameters();

        params.set("array_list", Arrays.asList("Superman"), MidaoTypes.ARRAY);
        params.set("blob_byte", "Batman", MidaoTypes.BLOB);
        params.set("clob_byte", "Wolverine", MidaoTypes.CLOB);

        params.set("array", array, MidaoTypes.ARRAY);
        params.set("blob", blob, MidaoTypes.BLOB);
        params.set("clob", clob, MidaoTypes.CLOB);
    }

    @Test
    public void testProcessInput() throws Exception {
        QueryParameters paramsClone = new QueryParameters(params);

        // cannot test CLOB and BLOB creation and write operation as they are instantiated using static method createTemporary
        paramsClone.remove("blob_byte");
        paramsClone.remove("clob_byte");

        new OracleTypeHandler(new Overrider()).processInput(stmt, paramsClone);

        verify(conn, times(1)).createARRAY(any(String.class), any(Object[].class));

        //verify(conn, times(1)).createClob();
        //verify(conn, times(1)).createBlob();

        //verify(blob, times(1)).setBinaryStream(1);
        //verify(clob, times(1)).setAsciiStream(1);
    }

    @Test
    public void testAfterExecute() throws Exception {
        QueryParameters processedParams = new QueryParameters(params);
        processedParams.set("array_list", processedParams.getValue("array"));
        processedParams.set("blob_byte", processedParams.getValue("blob"));
        processedParams.set("clob_byte", processedParams.getValue("clob"));
        processedParams.set("sqlXml_byte", processedParams.getValue("sqlXml"));

        new OracleTypeHandler(new Overrider()).afterExecute(stmt, processedParams, params);

        verify(array, times(1)).free();
        verify(blob, times(1)).freeTemporary();
        verify(clob, times(1)).freeTemporary();
    }

    @Test
    public void testProcessOutput() throws Exception {
        new OracleTypeHandler(new Overrider()).processOutput(stmt, params);

        verify(array, times(1)).getArray();
        verify(blob, times(1)).getBinaryStream();
        verify(clob, times(1)).getAsciiStream();
    }

    @Test
    public void testProcessOutputList() throws Exception {
        List<QueryParameters> paramsList = new ArrayList<QueryParameters>();
        QueryParameters paramsClone = new QueryParameters(params);

        paramsList.add(new QueryParameters()); // first element is usually technical query parameters
        paramsList.add(params);
        paramsList.add(paramsClone);

        new OracleTypeHandler(new Overrider()).processOutput(stmt, paramsList);

        verify(array, times(2)).getArray();
        verify(blob, times(2)).getBinaryStream();
        verify(clob, times(2)).getAsciiStream();
    }
}
