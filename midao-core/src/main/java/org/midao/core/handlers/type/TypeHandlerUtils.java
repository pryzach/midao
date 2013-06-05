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

import org.midao.core.exception.MidaoException;
import org.midao.core.exception.MidaoSQLException;
import org.midao.core.handlers.utils.MappingUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Utility classes used by TypeHandlers
 */
public class TypeHandlerUtils {
	private static final int EOF = -1;

    /**
     * Buffer size used by {@link #copy(java.io.InputStream, java.io.OutputStream)}
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Converts array of Object into sql.Array
     *
     * @param conn connection for which sql.Array object would be created
     * @param array array of objects
     * @return sql.Array from array of Object
     * @throws SQLException
     */
	public static Object convertArray(Connection conn, Object[] array) throws SQLException {
        Object result = null;
		
		result = createArrayOf(conn, convertJavaClassToSqlType(array.getClass().getComponentType().getSimpleName()), array);
		
		return result;
	}

    /**
     * Converts Collection into sql.Array
     *
     * @param conn connection for which sql.Array object would be created
     * @param array Collection
     * @return sql.Array from Collection
     * @throws SQLException
     */
	public static Object convertArray(Connection conn, Collection<?> array) throws SQLException {
		return convertArray(conn, array.toArray());
	}

    /**
     * Transfers data from byte[] into sql.Blob
     *
     * @param conn connection for which sql.Blob object would be created
     * @param value array of bytes
     * @return sql.Blob from array of bytes
     * @throws SQLException
     */
	public static Object convertBlob(Connection conn, byte[] value) throws SQLException {
        Object result = createBlob(conn);

        result = convertBlob(result, value);
		
		return result;
	}

    /**
     * Transfers data from InputStream into sql.Blob
     *
     * @param conn connection for which sql.Blob object would be created
     * @param input InputStream
     * @return sql.Blob from InputStream
     * @throws SQLException
     */
	public static Object convertBlob(Connection conn, InputStream input) throws SQLException {
		return convertBlob(conn, toByteArray(input));
	}

    /**
     * Transfers data from String into sql.Blob
     *
     * @param conn connection for which sql.Blob object would be created
     * @param value String
     * @return sql.Blob from String
     * @throws SQLException
     */
	public static Object convertBlob(Connection conn, String value) throws SQLException {
		return convertBlob(conn, value.getBytes());
	}

    /**
     * Transfers data from byte[] into sql.Blob
     *
     * @param blob sql.Blob which would be filled
     * @param value array of bytes
     * @return sql.Blob from array of bytes
     * @throws SQLException
     */
	public static Object convertBlob(Object blob, byte[] value) throws SQLException {
		ByteArrayInputStream input = new ByteArrayInputStream(value);
		//OutputStream output = blob.setBinaryStream(1);
        OutputStream output = null;
        try {
            output = (OutputStream) MappingUtils.invokeFunction(blob, "setBinaryStream", new Class[]{long.class}, new Object[]{1});
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }

        try {
			copy(input, output);
			
			output.flush();
			output.close();
		} catch (IOException ex) {
			throw new MidaoSQLException(ex);
		}
		
		return blob;
	}

    /**
     * Transfers data from InputStream into sql.Blob
     *
     * @param blob sql.Blob which would be filled
     * @param input InputStream
     * @return sql.Blob from InputStream
     * @throws SQLException
     */
	public static Object convertBlob(Object blob, InputStream input) throws SQLException {
		return convertBlob(blob, toByteArray(input));
	}

    /**
     * Transfers data from String into sql.Blob
     *
     * @param blob sql.Blob which would be filled
     * @param value String
     * @return sql.Blob from String
     * @throws SQLException
     */
	public static Object convertBlob(Object blob, String value) throws SQLException {
		return convertBlob(blob, value.getBytes());
	}

    /**
     * Transfers data from byte[] into sql.Clob
     *
     * @param conn connection for which sql.Clob object would be created
     * @param value array of bytes
     * @return sql.Clob from array of bytes
     * @throws SQLException
     */
	public static Object convertClob(Connection conn, byte[] value) throws SQLException {
        Object result = (Object) createClob(conn);

        result = convertClob(result, value);
		
		return result;
	}

    /**
     * Transfers data from String into sql.Blob
     *
     * @param conn connection for which sql.Blob object would be created
     * @param value String
     * @return sql.Clob from String
     * @throws SQLException
     */
	public static Object convertClob(Connection conn, String value) throws SQLException {
		return convertClob(conn, value.getBytes());
	}

    /**
     * Transfers data from InputStream into sql.Clob
     *
     * Using default locale. If different locale is required see
     * {@link #convertClob(java.sql.Connection, String)}
     *
     * @param conn connection for which sql.Clob object would be created
     * @param input InputStream
     * @return sql.Clob from InputStream
     * @throws SQLException
     */
	public static Object convertClob(Connection conn, InputStream input) throws SQLException {
		return convertClob(conn, toByteArray(input));
	}

    /**
     * Transfers data from byte[] into sql.Clob
     *
     * @param clob sql.Clob which would be filled
     * @param value array of bytes
     * @return sql.Clob from array of bytes
     * @throws SQLException
     */
	public static Object convertClob(Object clob, byte[] value) throws SQLException {
		ByteArrayInputStream input = new ByteArrayInputStream(value);
		//OutputStream output = clob.setAsciiStream(1);
        OutputStream output = null;
        try {
            output = (OutputStream) MappingUtils.invokeFunction(clob, "setAsciiStream", new Class[]{long.class}, new Object[]{1});
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }
		
		try {
			copy(input, output);
			
			output.flush();
			output.close();
		} catch (IOException ex) {
			throw new MidaoSQLException(ex);
		}
		
		return clob;
	}

    /**
     * Transfers data from String into sql.Clob
     *
     * @param clob sql.Clob which would be filled
     * @param value String
     * @return sql.Clob from String
     * @throws SQLException
     */
	public static Object convertClob(Object clob, String value) throws SQLException {
		return convertClob(clob, value.getBytes());
	}

    /**
     * Transfers data from InputStream into sql.Clob
     *
     * Using default locale. If different locale is required see
     * {@link #convertClob(java.sql.Connection, String)}
     *
     * @param clob sql.Clob which would be filled
     * @param input InputStream
     * @return sql.Clob from InputStream
     * @throws SQLException
     */
	public static Object convertClob(Object clob, InputStream input) throws SQLException {
		return convertClob(clob, toByteArray(input));
	}

    /**
     * Converts Java Class name into SQL Type name
     *
     * @param simpleClassName Java Class name
     * @return SQL Type name
     * @throws SQLException
     */
	public static String convertJavaClassToSqlType(String simpleClassName) throws SQLException {
		if ("String".equals(simpleClassName) == true) {
			return "VARCHAR";
		}
		
		throw new SQLException(String.format("Could not convert java class %s", simpleClassName));
	}

    /**
     * Transfers data from byte[] into sql.SQLXML
     *
     * @param conn connection for which sql.SQLXML object would be created
     * @param value array of bytes
     * @return sql.SQLXML from array of bytes
     * @throws SQLException
     */
	public static Object convertSqlXml(Connection conn, byte[] value) throws SQLException {
        Object result = createSQLXML(conn);

        result = convertSqlXml(result, value);

		return result;
	}


    /**
     * Transfers data from String into sql.SQLXML
     *
     * @param conn connection for which sql.SQLXML object would be created
     * @param value String
     * @return sql.SQLXML from String
     * @throws SQLException
     */
	public static Object convertSqlXml(Connection conn, String value) throws SQLException {
		return convertSqlXml(conn, value.getBytes());
	}

    /**
     * Transfers data from InputStream into sql.SQLXML
     *
     * Using default locale. If different locale is required see
     * {@link #convertSqlXml(java.sql.Connection, String)}
     *
     * @param conn connection for which sql.SQLXML object would be created
     * @param input InputStream
     * @return sql.SQLXML from InputStream
     * @throws SQLException
     */
	public static Object convertSqlXml(Connection conn, InputStream input) throws SQLException {
		return convertSqlXml(conn, toByteArray(input));
	}

    /**
     * Transfers data from byte[] into sql.SQLXML
     *
     * @param sqlXml sql.SQLXML which would be filled
     * @param value array of bytes
     * @return sql.SQLXML from array of bytes
     * @throws SQLException
     */
	public static Object convertSqlXml(Object sqlXml, byte[] value) throws SQLException {
		ByteArrayInputStream input = new ByteArrayInputStream(value);
		//OutputStream output = sqlXml.setBinaryStream();
        OutputStream output = null;
        try {
            output = (OutputStream) MappingUtils.invokeFunction(sqlXml, "setBinaryStream", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }
		
		try {
			copy(input, output);
			
			output.flush();
			output.close();
		} catch (IOException ex) {
			throw new MidaoSQLException(ex);
		}
		
		return sqlXml;
	}

    /**
     * Transfers data from String into sql.SQLXML
     *
     * @param sqlXml sql.SQLXML which would be filled
     * @param value array of bytes
     * @return sql.SQLXML from String
     * @throws SQLException
     */
	public static Object convertSqlXml(Object sqlXml, String value) throws SQLException {
		return convertSqlXml(sqlXml, value.getBytes());
	}

    /**
     * Transfers data from InputStream into sql.SQLXML
     *
     * Using default locale. If different locale is required see
     * {@link #convertSqlXml(Object, String)}
     *
     * @param sqlXml sql.SQLXML which would be filled
     * @param input InputStream
     * @return sql.SQLXML from InputStream
     * @throws SQLException
     */
	public static Object convertSqlXml(Object sqlXml, InputStream input) throws SQLException {
		return convertSqlXml(sqlXml, toByteArray(input));
	}

    /**
     * Transfers data from sql.Blob into byte[]
     *
     * @param blob sql.Blob which would be read
     * @param close informs if sql.Blob should be closed after a read
     * @return array of bytes from sql.Blob
     * @throws SQLException
     */
	public static byte[] readBlob (Object blob, boolean close) throws SQLException {
		byte[] result = null;
		InputStream input = null;
		
		//input = blob.getBinaryStream();
        try {
            input = (InputStream) MappingUtils.invokeFunction(blob, "getBinaryStream", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }

		result = toByteArray(input);
		
		if (close == true) {
			TypeHandlerUtils.closeQuietly(input);
		}
		
		return result;
	}

    /**
     * Transfers data from sql.Blob into byte[]
     * sql.Blob is closed after read
     *
     * @param blob sql.Blob which would be read
     * @return array of bytes from sql.Blob
     * @throws SQLException
     */
	public static byte[] readBlob (Object blob) throws SQLException {
		return readBlob(blob, true);		
	}

    /**
     * Transfers data from sql.Clob into byte[]
     *
     * @param clob sql.Clob which would be read
     * @param close informs if sql.Clob should be closed after a read
     * @return array of bytes from sql.Clob
     * @throws SQLException
     */
	public static byte[] readClob(Object clob, boolean close) throws SQLException {
		byte[] result = null;
		InputStream input = null;
		
		//input = clob.getAsciiStream();
        try {
            input = (InputStream) MappingUtils.invokeFunction(clob, "getAsciiStream", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }

		result = toByteArray(input);
		
		if (close == true) {
			TypeHandlerUtils.closeQuietly(input);
		}
		
		return result;
	}

    /**
     * Transfers data from sql.Clob into byte[]
     * sql.Clob is closed after read
     *
     * @param clob sql.Clob which would be read
     * @return array of bytes from sql.Clob
     * @throws SQLException
     */
	public static byte[] readClob(Object clob) throws SQLException {
		return readClob(clob, true);
	}

    /**
     * Transfers data from sql.SQLXML into byte[]
     *
     * @param sqlXml sql.SQLXML which would be read
     * @param close informs if sql.Clob should be closed after a read
     * @return array of bytes from sql.SQLXML
     * @throws SQLException
     */
	public static byte[] readSqlXml(Object sqlXml, boolean close) throws SQLException {
		byte[] result = null;
		InputStream input = null;
		
		//input = sqlXml.getBinaryStream();
        try {
            input = (InputStream) MappingUtils.invokeFunction(sqlXml, "getBinaryStream", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException(ex);
        }

		result = toByteArray(input);
		
		if (close == true) {
			TypeHandlerUtils.closeQuietly(input);
		}
		
		return result;
	}

    /**
     * Transfers data from sql.SQLXML into byte[]
     * sql.SQLXML is closed after read
     *
     * @param sqlXml sql.SQLXML which would be read
     * @return array of bytes from sql.SQLXML
     * @throws SQLException
     */
	public static byte[] readSqlXml(Object sqlXml) throws SQLException {
		return readSqlXml(sqlXml, true);
	}

    /**
     * Transfers data from InputStream into byte array
     *
     * @param input InputStream
     * @return array of bytes from InputStream
     * @throws SQLException
     */
    public static byte[] toByteArray(InputStream input) throws SQLException {
    	byte[] result = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        try {
			copy(input, output);
			result = output.toByteArray();
		} catch (IOException ex) {
			throw new MidaoSQLException(ex);
		}
        
        TypeHandlerUtils.closeQuietly(output);
        
        return result;
    }

    public static String toString(Reader reader) throws SQLException {
        String result = null;
        StringBuilder output = new StringBuilder();

        try {
            copy(reader, output);
            result = output.toString();
        } catch (IOException ex) {
            throw new MidaoSQLException(ex);
        }

        return result;
    }

    /**
     * Closes InputStream while catching any exceptions
     *
     * @param input InputStream
     * @throws SQLException
     */
    public static void closeQuietly(InputStream input) {
    	try {
    		input.close();
    	} catch (IOException ex) {
    		// keeping it quiet
    	}
    }

    /**
     * Closes OutputStream while catching any exceptions
     *
     * @param output OutputStream
     */
    public static void closeQuietly(OutputStream output) {
    	try {
    		output.close();
    	} catch (IOException ex) {
            // keeping it quiet
    	}
    }

    /**
     * Transfers data from InputStream into OutputStream
     * Uses {@link #DEFAULT_BUFFER_SIZE} to define buffer size
     *
     * @param input InputStream which would be read
     * @param output OutputStream which would be filled
     * @return amount of bytes transferred
     * @throws IOException
     */
    public static long copy(InputStream input, OutputStream output)
            throws IOException {
        return copy(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Transfers data from Reader into StringBuilder
     * Uses {@link #DEFAULT_BUFFER_SIZE} to define buffer size
     *
     * @param input Reader which would be read
     * @param output StringBuilder which would be filled
     * @return amount of bytes transferred
     * @throws IOException
     */
    public static long copy(Reader input, StringBuilder output)
            throws IOException {
        return copy(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    public static Object createBlob(Connection conn) throws MidaoSQLException {
        Object result = null;

        try {
            result = MappingUtils.invokeFunction(conn, "createBlob", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException("createBlob is not supported by JDBC Driver", ex);
        }

        return result;
    }

    public static Object createClob(Connection conn) throws MidaoSQLException {
        Object result = null;

        try {
            result = MappingUtils.invokeFunction(conn, "createClob", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException("createClob is not supported by JDBC Driver", ex);
        }

        return result;
    }

    public static Object createSQLXML(Connection conn) throws MidaoSQLException {
        Object result = null;

        try {
            result = MappingUtils.invokeFunction(conn, "createSQLXML", new Class[]{}, new Object[]{});
        } catch (MidaoException ex) {
            throw new MidaoSQLException("createSQLXML is not supported by JDBC Driver", ex);
        }

        return result;
    }

    public static Object createArrayOf(Connection conn, String typeName, Object[] elements) throws MidaoSQLException {
        Object result = null;

        try {
            result = MappingUtils.invokeFunction(conn, "createArrayOf", new Class[]{String.class, Object[].class}, new Object[]{typeName, elements});
        } catch (MidaoException ex) {
            throw new MidaoSQLException("createArrayOf is not supported by JDBC Driver", ex);
        }

        return result;
    }

    /**
     * Transfers data from InputStream into OutputStream
     *
     * @param input InputStream which would be read
     * @param output OutputStream which would be filled
     * @param buffer buffer which is used for read/write operations
     * @return amount of bytes transferred
     * @throws IOException
     */
    private static long copy(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long bytesTransferred = 0;
        int bytesRead = 0;
        while (EOF != (bytesRead = input.read(buffer))) {
            output.write(buffer, 0, bytesRead);
            bytesTransferred += bytesRead;
        }
        return bytesTransferred;
    }

    /**
     * Transfers data from Reader into StringBuilder
     *
     * @param input Reader which would be read
     * @param output StringBuilder which would be filled
     * @param buffer buffer which is used for read/write operations
     * @return amount of bytes transferred
     * @throws IOException
     */
    private static long copy(Reader input, StringBuilder output, char[] buffer)
            throws IOException {
        long bytesTransferred = 0;
        int bytesRead = 0;
        while (EOF != (bytesRead = input.read(buffer))) {
            output.append(buffer, 0, bytesRead);
            bytesTransferred += bytesRead;
        }
        return bytesTransferred;
    }
  }
