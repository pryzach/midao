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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.utils.MappingUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.sql.*;
import java.util.Arrays;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
public class TypeHandlerUtilsTest {
    @Mock Connection conn;
    @Mock Array array;
    @Mock Blob blob;
    @Mock Clob clob;
    //@Mock SQLXML sqlXml;
    @Mock OutputStream output;
    @Mock InputStream input;

    Object sqlXml;

    @Before
    public void setUp() throws ClassNotFoundException {
        MockitoAnnotations.initMocks(this);

        sqlXml = Mockito.mock(Class.forName("java.sql.SQLXML"));
    }

    @Test
    public void testConvertArrayArray() throws Exception {
        Object[] array = new String[]{"Sun"};

        TypeHandlerUtils.convertArray(conn, array);

        //verify(conn, times(1)).createArrayOf("VARCHAR", array);
        MappingUtils.invokeFunction(verify(conn, times(1)), "createArrayOf", new Class[]{String.class, Object[].class}, new Object[]{"VARCHAR", array});
    }

    @Test
    public void testConvertArrayCollection() throws Exception {
        Object[] array = new String[]{"Venus"};

        TypeHandlerUtils.convertArray(conn, Arrays.asList(array));

        //verify(conn, times(1)).createArrayOf("VARCHAR", array);
        MappingUtils.invokeFunction(verify(conn, times(1)), "createArrayOf", new Class[]{String.class, Object[].class}, new Object[]{"VARCHAR", array});
    }

    @Test
    public void testConvertBlobConnByte() throws Exception {
        testConvertBlobPrepare();

        TypeHandlerUtils.convertBlob(conn, "GIF".getBytes());

        //verify(conn, times(1)).createBlob();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createBlob", new Class[]{}, new Object[]{});
        testConvertBlobCheck();
    }

    @Test
    public void testConvertBlobConnInputStream() throws Exception {
        testConvertBlobPrepare();

        TypeHandlerUtils.convertBlob(conn, new ByteArrayInputStream("BMP".getBytes()));

        //verify(conn, times(1)).createBlob();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createBlob", new Class[]{}, new Object[]{});
        testConvertBlobCheck();
    }

    @Test
    public void testConvertBlobConnString() throws Exception {
        testConvertBlobPrepare();

        TypeHandlerUtils.convertBlob(conn, "PCX");

        //verify(conn, times(1)).createBlob();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createBlob", new Class[]{}, new Object[]{});
        testConvertBlobCheck();
    }

    @Test
    public void testConvertBlobByte() throws Exception {
        testConvertBlobPrepare();

        TypeHandlerUtils.convertBlob(blob, "JPEG".getBytes());

        testConvertBlobCheck();
    }

    @Test
    public void testConvertBlobInputStream() throws Exception {
        testConvertBlobPrepare();

        TypeHandlerUtils.convertBlob(blob, new ByteArrayInputStream("PNG".getBytes()));

        testConvertBlobCheck();
    }

    @Test
    public void testConvertBlobString() throws Exception {
        testConvertBlobPrepare();

        TypeHandlerUtils.convertBlob(blob, "TIFF");

        testConvertBlobCheck();
    }

    private void testConvertBlobPrepare() throws SQLException, MidaoException {
        //when(conn.createBlob()).thenReturn(blob);
        when(MappingUtils.invokeFunction(conn, "createBlob", new Class[]{}, new Object[]{})).thenReturn(blob);
        when(blob.setBinaryStream(1)).thenReturn(output);
    }

    private void testConvertBlobCheck() throws Exception {
        verify(blob, times(1)).setBinaryStream(1);
        verify(output).write(any(byte[].class), eq(0), any(int.class));
        verify(output, times(1)).flush();
        verify(output, times(1)).close();
    }

    @Test
    public void testConvertClobConnByte() throws Exception {
        testConvertClobPrepare();

        TypeHandlerUtils.convertClob(conn, "GIF".getBytes());

        //verify(conn, times(1)).createClob();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createClob", new Class[]{}, new Object[]{});
        testConvertClobCheck();
    }

    @Test
    public void testConvertClobConnInputStream() throws Exception {
        testConvertClobPrepare();

        TypeHandlerUtils.convertClob(conn, new ByteArrayInputStream("BMP".getBytes()));

        //verify(conn, times(1)).createClob();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createClob", new Class[]{}, new Object[]{});
        testConvertClobCheck();
    }

    @Test
    public void testConvertClobConnString() throws Exception {
        testConvertClobPrepare();

        TypeHandlerUtils.convertClob(conn, "PCX");

        //verify(conn, times(1)).createClob();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createClob", new Class[]{}, new Object[]{});
        testConvertClobCheck();
    }

    @Test
    public void testConvertClobByte() throws Exception {
        testConvertClobPrepare();

        TypeHandlerUtils.convertClob(clob, "JPEG".getBytes());

        testConvertClobCheck();
    }

    @Test
    public void testConvertClobInputStream() throws Exception {
        testConvertClobPrepare();

        TypeHandlerUtils.convertClob(clob, new ByteArrayInputStream("PNG".getBytes()));

        testConvertClobCheck();
    }

    @Test
    public void testConvertClobString() throws Exception {
        testConvertClobPrepare();

        TypeHandlerUtils.convertClob(clob, "TIFF");

        testConvertClobCheck();
    }

    private void testConvertClobPrepare() throws SQLException, MidaoException {
        //when(conn.createClob()).thenReturn(clob);
        when(MappingUtils.invokeFunction(conn, "createClob", new Class[]{}, new Object[]{})).thenReturn(clob);
        when(clob.setAsciiStream(1)).thenReturn(output);
    }

    private void testConvertClobCheck() throws Exception {
        verify(clob, times(1)).setAsciiStream(1);
        verify(output).write(any(byte[].class), eq(0), any(int.class));
        verify(output, times(1)).flush();
        verify(output, times(1)).close();
    }

    @Test
    public void testConvertSqlXmlConnByte() throws Exception {
        testConvertSqlXmlPrepare();

        TypeHandlerUtils.convertSqlXml(conn, "GIF".getBytes());

        //verify(conn, times(1)).createSQLXML();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createSQLXML", new Class[]{}, new Object[]{});
        testConvertSqlXmlCheck();
    }

    @Test
    public void testConvertSqlXmlConnInputStream() throws Exception {
        testConvertSqlXmlPrepare();

        TypeHandlerUtils.convertSqlXml(conn, new ByteArrayInputStream("BMP".getBytes()));

        //verify(conn, times(1)).createSQLXML();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createSQLXML", new Class[]{}, new Object[]{});
        testConvertSqlXmlCheck();
    }

    @Test
    public void testConvertSqlXmlConnString() throws Exception {
        testConvertSqlXmlPrepare();

        TypeHandlerUtils.convertSqlXml(conn, "PCX");

        //verify(conn, times(1)).createSQLXML();
        MappingUtils.invokeFunction(verify(conn, times(1)), "createSQLXML", new Class[]{}, new Object[]{});
        testConvertSqlXmlCheck();
    }

    @Test
    public void testConvertSqlXmlByte() throws Exception {
        testConvertSqlXmlPrepare();

        TypeHandlerUtils.convertSqlXml(sqlXml, "JPEG".getBytes());

        testConvertSqlXmlCheck();
    }

    @Test
    public void testConvertSqlXmlInputStream() throws Exception {
        testConvertSqlXmlPrepare();

        TypeHandlerUtils.convertSqlXml(sqlXml, new ByteArrayInputStream("PNG".getBytes()));

        testConvertSqlXmlCheck();
    }

    @Test
    public void testConvertSqlXmlString() throws Exception {
        testConvertSqlXmlPrepare();

        TypeHandlerUtils.convertSqlXml(sqlXml, "TIFF");

        testConvertSqlXmlCheck();
    }

    private void testConvertSqlXmlPrepare() throws SQLException, MidaoException {
        //when(conn.createSQLXML()).thenReturn(sqlXml);
        when(MappingUtils.invokeFunction(conn, "createSQLXML", new Class[]{}, new Object[]{})).thenReturn(sqlXml);
        //when(sqlXml.setBinaryStream()).thenReturn(output);
        when(MappingUtils.invokeFunction(sqlXml, "setBinaryStream", new Class[]{}, new Object[]{})).thenReturn(output);
    }

    private void testConvertSqlXmlCheck() throws Exception {
        //verify(sqlXml, times(1)).setBinaryStream();
        MappingUtils.invokeFunction(verify(sqlXml, times(1)), "setBinaryStream", new Class[]{}, new Object[]{});
        verify(output).write(any(byte[].class), eq(0), any(int.class));
        verify(output, times(1)).flush();
        verify(output, times(1)).close();
    }

    @Test
    public void testConvertJavaClassToSqlType() throws Exception {
        Assert.assertEquals("VARCHAR", TypeHandlerUtils.convertJavaClassToSqlType("String"));
    }

    @Test
    public void testReadBlobClose() throws Exception {
        testReadBlob(true);
    }

    @Test
    public void testReadBlob() throws Exception {
        testReadBlob(false);
    }

    private void testReadBlob(boolean close) throws Exception {
        byte[] result = null;
        String data = "LZW";

        when(blob.getBinaryStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));

        if (close == true) {
            result = TypeHandlerUtils.readBlob(blob);
        } else {
            result = TypeHandlerUtils.readBlob(blob, false);
        }

        Assert.assertEquals(data, new String(result));
    }

    @Test
    public void testReadClobClose() throws Exception {
        testReadClob(true);
    }

    @Test
    public void testReadClob() throws Exception {
        testReadClob(false);
    }

    private void testReadClob(boolean close) throws Exception {
        byte[] result = null;
        String data = "ZIP";

        when(clob.getAsciiStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));

        if (close == true) {
            result = TypeHandlerUtils.readClob(clob);
        } else {
            result = TypeHandlerUtils.readClob(clob, false);
        }

        Assert.assertEquals(data, new String(result));
    }

    @Test
    public void testReadSqlXmlClose() throws Exception {
        testReadSqlXml(true);
    }

    @Test
    public void testReadSqlXml() throws Exception {
        testReadSqlXml(false);
    }

    private void testReadSqlXml(boolean close) throws Exception {
        byte[] result = null;
        String data = "tar.gz";

        //when(sqlXml.getBinaryStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));
        when(MappingUtils.invokeFunction(sqlXml, "getBinaryStream", new Class[]{}, new Object[]{})).thenReturn(new ByteArrayInputStream(data.getBytes()));

        if (close == true) {
            result = TypeHandlerUtils.readSqlXml(sqlXml);
        } else {
            result = TypeHandlerUtils.readSqlXml(sqlXml, false);
        }

        Assert.assertEquals(data, new String(result));
    }

    @Test
    public void testToByteArray() throws Exception {
        String data = "ace";
        byte[] result = null;

        result = TypeHandlerUtils.toByteArray(new ByteArrayInputStream(data.getBytes()));

        Assert.assertEquals(data, new String(result));
    }

    @Test
    public void testToString() throws Exception{
        String data = "tar.gz";
        String result = "";

        result = TypeHandlerUtils.toString(new StringReader(data));

        Assert.assertEquals(data, result);
    }

    @Test
    public void testCloseQuietlyInput() throws Exception {
        Mockito.doThrow(new IOException()).when(input).close();

        try {
            TypeHandlerUtils.closeQuietly(input);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testCloseQuietlyOutput() throws Exception {
        Mockito.doThrow(new IOException()).when(output).close();

        try {
            TypeHandlerUtils.closeQuietly(output);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testCopy() throws Exception {
        String data = "rar";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());

        TypeHandlerUtils.copy(inputStream, outputStream);

        Assert.assertEquals(data, new String(outputStream.toByteArray()));
    }
}
