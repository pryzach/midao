/*
 * Copyright 2013 Zakhar Prykhoda
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

package org.midao.core.db.derby;

import org.midao.core.db.BaseDB;
import org.midao.core.db.DBConstants;
import org.midao.core.handlers.type.TypeHandlerUtils;

import java.sql.*;

public class BaseDerby extends BaseDB {
	protected final String dbName = DBConstants.derby;

	@Override
	protected void setUp() throws Exception {
		establishConnection(dbName);
	}

	@Override
	protected void tearDown() throws Exception {
		closeConnection();
	}

	
    /*
     * DERBY JAVA SQL FUNCTIONS/PROCEDURES
     */
    public static void testInOut(String name, String[] surname, String[] fullName) {
        surname[0] = surname[0].toUpperCase();

        fullName[0] = name + " " + surname[0];
    }

    public static String testFunction(Integer id) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");

        Statement stmt = null;
        stmt = conn.createStatement();

        String query = "SELECT name, id, address FROM students WHERE id = " + id;

        ResultSet rs = stmt.executeQuery(query);
        rs.next();

        return rs.getString(1);
    }

    public static void testProcedureReturn(Integer id, ResultSet[] rs) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        Statement stmt = null;
        String query = null;

        query = "SELECT name, id, address FROM students WHERE id = " + id;

        stmt = conn.createStatement();

        rs[0] = stmt.executeQuery(query);
    }
    
    public static void testProcedureMultipleReturn(Integer id1, Integer id2, ResultSet[] rs1, ResultSet[] rs2) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        Statement stmt = null;
        String query = null;

        query = "SELECT name, id, address FROM students WHERE id = " + id1;

        stmt = conn.createStatement();

        rs1[0] = stmt.executeQuery(query);
        
        query = "SELECT name, id, address FROM students WHERE id = " + id2;

        stmt = conn.createStatement();

        rs2[0] = stmt.executeQuery(query);
    }
    
    public static void testProcedureLarge(java.sql.Clob clobIn, java.sql.Clob[] clobOut, java.sql.Blob blobIn, java.sql.Blob[] blobOut) throws SQLException {
    	Connection conn = DriverManager.getConnection("jdbc:default:connection");
    	Statement stmt = null;
    	
    	Clob newClob = (Clob) TypeHandlerUtils.createClob(conn);
    	newClob.setString(1, "Hello " + clobIn.getSubString(1, (int) clobIn.length()));
    	
    	Blob newBlob = (Blob) TypeHandlerUtils.createBlob(conn);
    	newBlob.setBytes(1, ("Hi " + new String(blobIn.getBytes(1, (int) blobIn.length()))).getBytes());
    	
    	clobOut[0] = newClob;
    	blobOut[0] = newBlob;
    }
    
    public static void testNamed(Integer id, String[] name, String[] address) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");

        Statement stmt = null;
        stmt = con.createStatement();

        String query = "SELECT name, id, address FROM students WHERE id = " + id;

        ResultSet rs = stmt.executeQuery(query);
        rs.next();

        name[0] = rs.getString("name");
        address[0] = rs.getString("address");

        rs.close();
    }
    
    /*
    public static void testProcedureLarge(String stringName, String[] stringResult) throws SQLException {
    	stringResult[0] = "Hello " + stringName;
    	
    	//, byte[] byteName, byte[][] byteResult
    	//byteResult[0] = ("Hello" + new String(byteName)).getBytes();
    }
    */
}
