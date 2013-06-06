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

package org.midao.jdbc.core.db;

public class DBConstants {
	//private final String derbyUrl = "jdbc:derby:memory:myDB;create=true";
	//private final String derbyDriver = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String derby = "derby";
    public static final String oracle = "oracle";
    public static final String mysql = "mysql";
    public static final String postgres = "postgresql";
    public static final String mssql = "mssql";

	//public static final String derbyDataSourceClass = "org.apache.derby.jdbc.EmbeddedDataSource";
	//public static final String derbyDbName = "memory:testDb";
	
	//public static final String oracleDataSourceClass = "oracle.jdbc.pool.OracleDataSource";
	//public static final String oracleDbName = "xe";
	
	//public static final String mysqlDataSourceClass = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
	//public static final String mysqlDbName = "test";
	
	//public static final String postgresDataSourceClass = "org.postgresql.ds.PGSimpleDataSource";
	//public static final String postgresDbName = "template1";
	
	public static final String CREATE_STUDENT_TABLE_DERBY = "CREATE TABLE students ("
			+ "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," 
			+ "name VARCHAR(24) NOT NULL,"
			+ "address VARCHAR(1024)," + "CONSTRAINT primary_key PRIMARY KEY (id))";
	
	public static final String CREATE_STUDENT_TABLE_ORACLE = "CREATE TABLE students ("
			+ "id NUMBER(11)," 
			+ "name VARCHAR2(24) NOT NULL,"
			+ "address VARCHAR2(1024)," + "PRIMARY KEY (id))";
	
	public static final String CREATE_STUDENT_TABLE_MYSQL = "CREATE TABLE students ("
			+ "id INT NOT NULL AUTO_INCREMENT," 
			+ "name VARCHAR(24) NOT NULL,"
			+ "address VARCHAR(1024), PRIMARY KEY (id))";
	
	public static final String CREATE_STUDENT_TABLE_POSTGRES = "CREATE TABLE students ("
			+ "id serial PRIMARY KEY," 
			+ "name VARCHAR(24) NOT NULL,"
			+ "address VARCHAR(1024))";

    public static final String CREATE_STUDENT_TABLE_MSSQL = "CREATE TABLE students ("
            + "id int IDENTITY(1,1)PRIMARY KEY CLUSTERED,"
            + "name VARCHAR(24) NOT NULL,"
            + "address VARCHAR(1024))";
	
	public static final String CREATE_STUDENT_TABLE_ORACLE_SEQ = "CREATE SEQUENCE student_sq START WITH 1 INCREMENT BY 1";
	public static final String CREATE_STUDENT_TABLE_ORACLE_TRG = "CREATE OR REPLACE TRIGGER student_trg BEFORE INSERT ON students FOR EACH ROW BEGIN SELECT student_sq.nextval INTO :NEW.ID FROM DUAL; END;";
	
	public static final String DROP_STUDENT_TABLE_ORACLE_SEQ = "DROP SEQUENCE student_sq";
	
	public static final String INSERT_STUDENT_TABLE = "INSERT INTO students (name, address) VALUES ('Not me', 'unknown')";
	public static final String INSERT_STUDENT_TABLE_ORACLE = "INSERT INTO students (id, name, address) VALUES (1, 'Not me', 'unknown')";
	public static final String INSERT_STUDENT_TABLE_ORACLE_2 = "INSERT INTO students (id, name, address) VALUES (student_sq.nextval, 'Not me', 'unknown')";
	
	public static final String INSERT_NAMED_STUDENT_TABLE = "INSERT INTO students (name) VALUES (:studentName)";
	public static final String INSERT_NAMED2_STUDENT_TABLE = "INSERT INTO students (name, address) VALUES (:student.name, :student.address)";
	public static final String INSERT_STUDENT_TABLE_W_PARAMS = "INSERT INTO students (name) VALUES (?)";
	public static final String INSERT_STUDENT_TABLE_W_PARAMS_ORACLE = "INSERT INTO students (id, name) VALUES (student_sq.nextval, ?)";
	
	public static final String SELECT_STUDENT_TABLE = "SELECT name FROM students WHERE id = 1";
	public static final String SELECT_NAMED_STUDENT_TABLE = "SELECT name FROM students WHERE id = :id";
	public static final String SELECT_NAMED2_STUDENT_TABLE = "SELECT name FROM students WHERE id = :table.id AND address = :student.address";
	
	public static final String SELECT_NAMED2_STUDENT_TABLE_ORACLE = "SELECT name FROM students WHERE id = :table.id AND address = :student.address";
	public static final String INSERT_NAMED_STUDENT_TABLE_ORACLE = "INSERT INTO students (id, name) VALUES (student_sq.nextval, :studentName)";
	
	public static final String SELECT_STUDENT_TABLE_W_PARAMS = "SELECT name FROM students WHERE id = ?";

    public static final String DERBY_PROCEDURE_INOUT = "CREATE PROCEDURE TEST_INOUT (IN NAME varchar(50), INOUT SURNAME varchar(50), OUT FULLNAME varchar(50)) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 0 EXTERNAL NAME 'org.midao.jdbc.core.db.derby.BaseDerby.testInOut'";
    public static final String ORACLE_PROCEDURE_INOUT = "CREATE PROCEDURE TEST_INOUT (NAME IN varchar2, SURNAME IN OUT varchar2, FULLNAME OUT varchar2) AS BEGIN SURNAME := UPPER(SURNAME); FULLNAME := NAME || ' ' || SURNAME; END;";
    public static final String MYSQL_PROCEDURE_INOUT = "CREATE PROCEDURE TEST_INOUT (IN NAME varchar(50), INOUT SURNAME varchar(50), OUT FULLNAME varchar(50)) BEGIN SET SURNAME = UPPER(SURNAME); SET FULLNAME = CONCAT(NAME, ' ', SURNAME); END;";
    public static final String POSTGRES_PROCEDURE_INOUT = "CREATE OR REPLACE FUNCTION TEST_INOUT (NAME IN varchar, SURNAME INOUT varchar, FULLNAME OUT varchar) AS $$ BEGIN SURNAME := UPPER(SURNAME); FULLNAME := CONCAT(NAME, ' ', SURNAME); END; $$ LANGUAGE plpgsql;";
    public static final String MSSQL_PROCEDURE_INOUT = "CREATE PROCEDURE TEST_INOUT @NAME varchar(50), @SURNAME varchar(50) OUTPUT, @FULLNAME varchar(50) OUTPUT AS SET @SURNAME = UPPER(@SURNAME); SET @FULLNAME = CONCAT(@NAME, ' ', @SURNAME);";
    public static final String CALL_PROCEDURE_INOUT = "{call TEST_INOUT(:name, :surname, :fullname)}";
    public static final String DROP_PROCEDURE_INOUT = "DROP PROCEDURE TEST_INOUT";
    
    public static final String DERBY_PROCEDURE_NAMED = "CREATE PROCEDURE TEST_NAMED (IN ID integer, OUT NAME varchar(50), OUT ADDRESS varchar(50)) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 0 EXTERNAL NAME 'org.midao.jdbc.core.db.derby.BaseDerby.testNamed'";
    public static final String MYSQL_PROCEDURE_NAMED = "CREATE PROCEDURE TEST_NAMED (IN P_ID int, OUT P_NAME varchar(50), OUT P_ADDRESS varchar(50)) \n" +
    		"BEGIN\n" +
    		"SELECT name, address INTO P_NAME, P_ADDRESS FROM students WHERE ID = P_ID;\n" +
    		"END;\n";
    public static final String ORACLE_PROCEDURE_NAMED = "CREATE OR REPLACE PROCEDURE TEST_NAMED (P_ID IN NUMBER, P_NAME OUT varchar2, P_ADDRESS OUT varchar2) AS\n" +
    		"BEGIN\n" +
    		"SELECT name, address INTO P_NAME, P_ADDRESS FROM students WHERE ID = P_ID;\n" +
    		"END;\n";
    public static final String POSTGRES_PROCEDURE_NAMED = "CREATE OR REPLACE FUNCTION TEST_NAMED (P_ID IN INTEGER, P_NAME OUT varchar, P_ADDRESS OUT varchar) AS $$ " +
    		"BEGIN\n" +
    		"SELECT name, address INTO P_NAME, P_ADDRESS FROM students WHERE ID = P_ID;\n" +
    		"END; $$ LANGUAGE plpgsql;";
    public static final String MSSQL_PROCEDURE_NAMED = "CREATE PROCEDURE TEST_NAMED (@P_ID int, @P_NAME varchar(50) OUTPUT, @P_ADDRESS varchar(50) OUTPUT) AS \n" +
            "BEGIN\n" +
            "SELECT @P_NAME = name, @P_ADDRESS = address FROM students WHERE ID = @P_ID;\n" +
            "END\n";
    public static final String CALL_PROCEDURE_NAMED = "{call TEST_NAMED(:id, :name, :address)}";
    public static final String MSSQL_CALL_PROCEDURE_NAMED = "{call dbo.TEST_NAMED(:id, :name, :address)}";
    public static final String DROP_PROCEDURE_NAMED = "DROP PROCEDURE TEST_NAMED";

    public static final String DERBY_FUNCTION = "CREATE FUNCTION TEST_FUNC (ID integer) RETURNS varchar(30) PARAMETER STYLE JAVA LANGUAGE JAVA EXTERNAL NAME 'org.midao.jdbc.core.db.derby.BaseDerby.testFunction'";
    public static final String MYSQL_FUNCTION = "CREATE FUNCTION TEST_FUNC (p_ID int) RETURNS varchar(30) BEGIN DECLARE return_name VARCHAR(30); SELECT name INTO return_name FROM students WHERE id = p_ID; return (return_name); END;";
    public static final String ORACLE_FUNCTION = "CREATE OR REPLACE FUNCTION TEST_FUNC (p_ID in NUMBER) RETURN varchar2 AS return_name VARCHAR(30); BEGIN SELECT name INTO return_name FROM students WHERE id = p_ID; return return_name; END;";
    public static final String POSTGRES_FUNCTION = "CREATE OR REPLACE FUNCTION TEST_FUNC (p_ID in INTEGER) RETURNS varchar AS $$ DECLARE return_name VARCHAR(30); BEGIN SELECT name INTO return_name FROM students WHERE id = p_ID; return return_name; END;  $$ LANGUAGE plpgsql;";
    public static final String MSSQL_FUNCTION = "CREATE FUNCTION TEST_FUNC (@p_ID int) RETURNS varchar(255) AS BEGIN DECLARE @return_name VARCHAR(30); SELECT @return_name = name FROM students WHERE id = @p_ID; return @return_name; END";
    public static final String CALL_FUNCTION = "{:name = call TEST_FUNC(:id)}";
    public static final String ORACLE_CALL_FUNCTION = "{CALL :name := TEST_FUNC(:id)}";
    public static final String DROP_FUNCTION = "DROP FUNCTION TEST_FUNC";

    public static final String DERBY_PROCEDURE_RETURN = "CREATE PROCEDURE TEST_PROC_RETURN (IN ID int) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 1 EXTERNAL NAME 'org.midao.jdbc.core.db.derby.BaseDerby.testProcedureReturn'";
    public static final String MYSQL_PROCEDURE_RETURN = "CREATE PROCEDURE TEST_PROC_RETURN (IN p_ID int) BEGIN SELECT name, id, address FROM students WHERE id = p_ID; END;";
    public static final String ORACLE_PROCEDURE_RETURN = "CREATE OR REPLACE FUNCTION TEST_PROC_RETURN (p_ID in NUMBER) RETURN SYS_REFCURSOR AS cursor_ref SYS_REFCURSOR; BEGIN OPEN cursor_ref FOR SELECT NAME FROM students WHERE ID = p_ID; return cursor_ref; END;";
    public static final String ORACLE_CALL_PROCEDURE_RETURN = "{CALL :cursor := TEST_PROC_RETURN(:id)}";
    public static final String POSTGRES_CALL_PROCEDURE_RETURN = "{:cursor = call TEST_PROC_RETURN(:id)}";
    public static final String POSTGRES_PROCEDURE_RETURN = "CREATE OR REPLACE FUNCTION TEST_PROC_RETURN (p_ID in INTEGER) RETURNS refcursor AS $$ DECLARE cursor_ref refcursor; BEGIN OPEN cursor_ref FOR SELECT NAME FROM students WHERE ID = p_ID; return cursor_ref; END;  $$ LANGUAGE plpgsql;";
    public static final String MSSQL_PROCEDURE_RETURN = "CREATE PROCEDURE TEST_PROC_RETURN (@p_ID int) AS BEGIN SELECT name, id, address FROM students WHERE id = @p_ID; END";
    public static final String CALL_PROCEDURE_RETURN = "{call TEST_PROC_RETURN(:id)}";
    public static final String DROP_PROCEDURE_RETURN = "DROP PROCEDURE TEST_PROC_RETURN";
    
    public static final String DERBY_PROCEDURE_MULTIPLE_RETURN = "CREATE PROCEDURE TEST_PROC_MULTIPLE_RETURN (IN ID1 int, IN ID2 int) PARAMETER STYLE JAVA LANGUAGE JAVA DYNAMIC RESULT SETS 2 EXTERNAL NAME 'org.midao.jdbc.core.db.derby.BaseDerby.testProcedureMultipleReturn'";
    public static final String CALL_PROCEDURE_MULTIPLE_RETURN = "{CALL TEST_PROC_MULTIPLE_RETURN(:id1, :id2)}";
    public static final String DROP_PROCEDURE_MULTIPLE_RETURN = "DROP PROCEDURE TEST_PROC_MULTIPLE_RETURN";
    
    public static final String DERBY_PROCEDURE_LARGE = "CREATE PROCEDURE TEST_PROC_LARGE (IN clobIn CLOB, OUT clobOut CLOB, IN blobIn BLOB, OUT blobOut BLOB) PARAMETER STYLE JAVA LANGUAGE JAVA no sql EXTERNAL NAME 'org.midao.jdbc.core.db.derby.BaseDerby.testProcedureLarge'";//, IN BLOB_IN BLOB, OUT BLOB_OUT BLOB
    public static final String MYSQL_PROCEDURE_LARGE = "CREATE PROCEDURE TEST_PROC_LARGE (IN clobIn TEXT, OUT clobOut TEXT, IN blobIn BLOB, OUT blobOut BLOB) BEGIN SET clobOut = CONCAT('Hello ', CONVERT(clobIn USING utf8)); SET blobOut = CONCAT('Hi ', CONVERT(blobIn USING utf8)); END;";
    public static final String MSSQL_PROCEDURE_LARGE = "CREATE PROCEDURE TEST_PROC_LARGE @CLOB_IN varchar(max), @CLOB_OUT varchar(max) OUTPUT, @BLOB_IN varbinary(max), @BLOB_OUT varbinary(max) OUTPUT AS BEGIN SET @CLOB_OUT = CONCAT('Hello ', @CLOB_IN); SET @BLOB_OUT = CAST(CONCAT('Hi ', @BLOB_IN) AS varbinary); END";
    public static final String CALL_PROCEDURE_LARGE = "{call TEST_PROC_LARGE(:clobIn, :clobOut, :blobIn, :blobOut)}";//, :blobIn, :blobOut
    public static final String DROP_PROCEDURE_LARGE = "DROP PROCEDURE TEST_PROC_LARGE";
    
    public static final String POSTGRES_PROCEDURE_LARGE = "CREATE OR REPLACE FUNCTION TEST_PROC_LARGE (clobIn IN text, clobOut OUT text, blobIn IN bytea, blobOut OUT bytea) AS $$\n" +
						"BEGIN\n " +
						"clobOut = 'Hello ' || clobIn; " + 
						"blobOut = decode('Hi ' || encode(blobIn, 'escape'), 'escape'); " + 
						"END; $$ LANGUAGE plpgsql;";
    
    public static final String ORACLE_PROCEDURE_LARGE = "CREATE OR REPLACE PROCEDURE TEST_PROC_LARGE (clobIn in CLOB, clobOut out CLOB, blobIn in BLOB, blobOut out BLOB) IS\n" +
    			"clobChar VARCHAR2(12);\n" +
    			"blobChar VARCHAR2(12);\n" +
    			"tempBlob BLOB;\n" +
    		"BEGIN\n" +
    			"clobChar := 'Hello ' || CAST(clobIn AS VARCHAR2);\n" +
    			"clobOut := CAST(clobChar AS CLOB);\n" +
    			"blobChar := 'Hi ' || UTL_RAW.CAST_TO_VARCHAR2(blobIn);\n" +
    			"DBMS_LOB.CREATETEMPORARY(blobOut, TRUE);\n" +
    			"DBMS_LOB.OPEN(blobOut, DBMS_LOB.LOB_READWRITE);\n" +
    			"DBMS_LOB.WRITE(blobOut, LENGTH(blobChar), 1, utl_raw.cast_to_raw(blobChar));\n" +
    		"END;\n";
    
    public static final String ORACLE_PACKAGE_PROCEDURE_NAMED = "CREATE OR REPLACE PACKAGE tests AS \n" +
    		"CREATE OR REPLACE PROCEDURE CHRIS.TEST_NAMED (P_ID IN NUMBER(10), P_NAME OUT varchar2(50), P_ADDRESS OUT varchar2(50));\n" +
    		"END;\n" +
    		"CREATE OR REPLACE PACKAGE BODY tests AS \n" +
    		"CREATE OR REPLACE PROCEDURE CHRIS.TEST_NAMED (P_ID IN NUMBER(10), P_NAME OUT varchar2(50), P_ADDRESS OUT varchar2(50)) IS\n" +
    		"clobChar VARCHAR2(12);\n" +
    		"BEGIN\n" +
    		"SELECT name, address INTO P_NAME, P_ADDRESS FROM students WHERE ID = P_ID;\n" +
    		"END;\n" + 
    		"END;\n";
    
	public static final String DROP_STUDENT_TABLE = "DROP TABLE students";
	
}
