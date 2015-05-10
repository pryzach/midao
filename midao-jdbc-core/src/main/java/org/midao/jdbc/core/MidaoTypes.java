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

package org.midao.jdbc.core;

/**
 * Internal {@link java.sql.Types} implementation to avoid dependency
 * on JDBC version and allow compile under both Java 5 and 6.
 * <p/>
 * Please be aware that {@link java.sql.Types#ROWID}, {@link java.sql.Types#NCHAR}, {@link java.sql.Types#NVARCHAR},
 * {@link java.sql.Types#LONGNVARCHAR}, {@link java.sql.Types#NCLOB}, {@link java.sql.Types#SQLXML} would be supported
 * only by Java 6 (JDBC 4.0) and should not be invoked during work with Java 5 (JDBC 3.0).
 * <p/>
 * <p>This class is Deprecated - please use {@link MjdbcTypes} instead</p>
 */
@Deprecated
public class MidaoTypes {
    /**
     * @see {@link java.sql.Types#BIT}
     */
    public final static int BIT = -7;

    /**
     * @see {@link java.sql.Types#TINYINT}
     */
    public final static int TINYINT = -6;

    /**
     * @see {@link java.sql.Types#SMALLINT}
     */
    public final static int SMALLINT = 5;

    /**
     * @see {@link java.sql.Types#INTEGER}
     */
    public final static int INTEGER = 4;

    /**
     * @see {@link java.sql.Types#BIGINT}
     */
    public final static int BIGINT = -5;

    /**
     * @see {@link java.sql.Types#FLOAT}
     */
    public final static int FLOAT = 6;

    /**
     * @see {@link java.sql.Types#REAL}
     */
    public final static int REAL = 7;

    /**
     * @see {@link java.sql.Types#DOUBLE}
     */
    public final static int DOUBLE = 8;

    /**
     * @see {@link java.sql.Types#NUMERIC}
     */
    public final static int NUMERIC = 2;

    /**
     * @see {@link java.sql.Types#DECIMAL}
     */
    public final static int DECIMAL = 3;

    /**
     * @see {@link java.sql.Types#CHAR}
     */
    public final static int CHAR = 1;

    /**
     * @see {@link java.sql.Types#VARCHAR}
     */
    public final static int VARCHAR = 12;

    /**
     * @see {@link java.sql.Types#LONGVARCHAR}
     */
    public final static int LONGVARCHAR = -1;

    /**
     * @see {@link java.sql.Types#DATE}
     */
    public final static int DATE = 91;

    /**
     * @see {@link java.sql.Types#TIME}
     */
    public final static int TIME = 92;

    /**
     * @see {@link java.sql.Types#TIMESTAMP}
     */
    public final static int TIMESTAMP = 93;

    /**
     * @see {@link java.sql.Types#BINARY}
     */
    public final static int BINARY = -2;

    /**
     * @see {@link java.sql.Types#VARBINARY}
     */
    public final static int VARBINARY = -3;

    /**
     * @see {@link java.sql.Types#LONGVARBINARY}
     */
    public final static int LONGVARBINARY = -4;

    /**
     * @see {@link java.sql.Types#NULL}
     */
    public final static int NULL = 0;

    /**
     * @see {@link java.sql.Types#OTHER}
     */
    public final static int OTHER = 1111;

    /**
     * @see {@link java.sql.Types#JAVA_OBJECT}
     */
    public final static int JAVA_OBJECT = 2000;

    /**
     * @see {@link java.sql.Types#DISTINCT}
     */
    public final static int DISTINCT = 2001;

    /**
     * @see {@link java.sql.Types#STRUCT}
     */
    public final static int STRUCT = 2002;

    /**
     * @see {@link java.sql.Types#ARRAY}
     */
    public final static int ARRAY = 2003;

    /**
     * @see {@link java.sql.Types#BLOB}
     */
    public final static int BLOB = 2004;

    /**
     * @see {@link java.sql.Types#CLOB}
     */
    public final static int CLOB = 2005;

    /**
     * @see {@link java.sql.Types#REF}
     */
    public final static int REF = 2006;

    /**
     * @see {@link java.sql.Types#DATALINK}
     */
    public final static int DATALINK = 70;

    /**
     * @see {@link java.sql.Types#BOOLEAN}
     */
    public final static int BOOLEAN = 16;

    //------------------------- JDBC 4.0 -----------------------------------
    // Please don't use those for Java 1.5

    /**
     * @see {@link java.sql.Types#ROWID}
     */
    public final static int ROWID = -8;

    /**
     * @see {@link java.sql.Types#NCHAR}
     */
    public static final int NCHAR = -15;

    /**
     * @see {@link java.sql.Types#NVARCHAR}
     */
    public static final int NVARCHAR = -9;

    /**
     * @see {@link java.sql.Types#LONGNVARCHAR}
     */
    public static final int LONGNVARCHAR = -16;

    /**
     * @see {@link java.sql.Types#NCLOB}
     */
    public static final int NCLOB = 2011;

    /**
     * @see {@link java.sql.Types#SQLXML}
     */
    public static final int SQLXML = 2009;

    /**
     * Making sure that nobody would initialize this class (the same way as {@link java.sql.Types}
     */
    private MidaoTypes() {

    }
}
