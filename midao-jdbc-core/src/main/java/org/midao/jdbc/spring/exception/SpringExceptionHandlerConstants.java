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

package org.midao.jdbc.spring.exception;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Exception handler constants.
 * Keeps all SQL Exception -> Spring SQL Exception correlations.
 */
public class SpringExceptionHandlerConstants {

    // Correlation between SQL State prefixes and Spring SQL Exceptions. It is universal among databases.
    static List<String> SQL_STATE_PREFIX_BAD_SQL_GRAMMAR = Arrays.asList("07", "21", "2A", "37", "42", "65", "S0");
    static List<String> SQL_STATE_PREFIX_DATA_INTEGRITY_VIOLATION = Arrays.asList("01", "02", "22", "23", "27", "44");
    static List<String> SQL_STATE_PREFIX_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("08", "53", "54", "57", "58");
    static List<String> SQL_STATE_PREFIX_TRANSIENT_DATA_ACCESS_RESOURCE_EXCEPTION = Arrays.asList("JW", "JZ", "S1");
    static List<String> SQL_STATE_PREFIX_CONCURRENCY_FAILURE = Arrays.asList("40", "61");

    // DB2 error code to Spring SQL exception correlation
    static List<String> DB2_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("-007", "-029", "-097", "-104", "-109",
            "-115", "-128", "-199", "-204", "-206", "-301", "-408", "-441", "-491");
    static List<String> DB2_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("-803");
    static List<String> DB2_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("-407", "-530", "-531", "-532", "-543",
            "-544", "-545", "-603", "-667");
    static List<String> DB2_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("-904", "-971");
    static List<String> DB2_ERROR_CODE_TRANSILIENT_DATA_ACCESS_RESOURCE_EXCEPTION = Arrays.asList("-1035", "-1218", "-30080", "-30081");
    static List<String> DB2_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("-911", "-913");

    // Derby sql state to Spring SQL exception correlation
    static List<String> DERBY_SQL_STATE_BAD_SQL_GRAMMAR = Arrays.asList("42802", "42821", "42X01", "42X02", "42X03",
            "42X04", "42X05", "42X06", "42X07", "42X08");
    static List<String> DERBY_SQL_STATE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("23505");
    static List<String> DERBY_SQL_STATE_DATA_INTEGRITY_VIOLATION = Arrays.asList("22001", "22005", "23502", "23503",
            "23513", "X0Y32");
    static List<String> DERBY_SQL_STATE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("04501", "08004", "42Y07");
    static List<String> DERBY_SQL_STATE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("40XL1");
    static List<String> DERBY_SQL_STATE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("40001");

    // H2 error code to Spring SQL exception correlation
    static List<String> H2_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("42000", "42001", "42101", "42102", "42111",
            "42112", "42121", "42122", "42132");
    static List<String> H2_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("23001", "23505");
    static List<String> H2_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("22001", "22003", "22012", "22018",
            "22025", "23000", "23002", "23003", "23502", "23503", "23506", "23507", "23513");
    static List<String> H2_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("90046", "90100", "90117",
            "90121", "90126");
    static List<String> H2_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("50200");

    // HSQL error code to Spring SQL exception correlation
    static List<String> HSQL_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("-22", "-28");
    static List<String> HSQL_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("-104");
    static List<String> HSQL_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("-9");
    static List<String> HSQL_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("-80");

    // Informix error code to Spring SQL exception correlation
    static List<String> INFORMIX_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("-201", "-217", "-696");
    static List<String> INFORMIX_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("-239", "-268", "-6017");
    static List<String> INFORMIX_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("-692", "-11030");

    // Microsoft SQL error code to Spring SQL exception correlation
    static List<String> MSSQL_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("156", "170", "207", "208", "209");
    static List<String> MSSQL_ERROR_CODE_PERMISSION_DENIED = Arrays.asList("229");
    static List<String> MSSQL_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("2601", "2627");
    static List<String> MSSQL_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("544", "8114", "8115");
    static List<String> MSSQL_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("4060");
    static List<String> MSSQL_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("1222");
    static List<String> MSSQL_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("1205");

    // MySQL error code to Spring SQL exception correlation
    static List<String> MySQL_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("1054", "1064", "1146");
    static List<String> MySQL_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("1062");
    static List<String> MySQL_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("630", "839", "840", "893", "1169",
            "1215", "1216", "1217", "1451", "1452", "1557");
    static List<String> MySQL_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("1");
    static List<String> MySQL_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("1205");
    static List<String> MySQL_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("1213");

    // Oracle error code to Spring SQL exception correlation
    static List<String> ORACLE_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("900", "903", "904", "917", "936", "942",
            "17006", "6550");
    static List<String> ORACLE_ERROR_CODE_INVALID_RESULTSET_ACCESS = Arrays.asList("17003");
    static List<String> ORACLE_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("1");
    static List<String> ORACLE_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("1400", "1722", "2291", "2292");
    static List<String> ORACLE_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("17002", "17447");
    static List<String> ORACLE_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("54", "30006");
    static List<String> ORACLE_ERROR_CODE_CANNOT_SERIALIZE_TRANSACTION = Arrays.asList("8177");
    static List<String> ORACLE_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("60");

    // PostgreSQL sql state to Spring SQL exception correlation
    static List<String> POSTGRES_SQL_STATE_BAD_SQL_GRAMMAR = Arrays.asList("03000,42000,42601,42602,42622,42804,42P01");
    static List<String> POSTGRES_SQL_STATE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("23505");
    static List<String> POSTGRES_SQL_STATE_DATA_INTEGRITY_VIOLATION = Arrays.asList("23000,23502,23503,23514");
    static List<String> POSTGRES_SQL_STATE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("53000,53100,53200,53300");
    static List<String> POSTGRES_SQL_STATE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("55P03");
    static List<String> POSTGRES_SQL_STATE_CANNOT_SERIALIZE_TRANSACTION = Arrays.asList("40001");
    static List<String> POSTGRES_SQL_STATE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("40P01");

    // Sybase error code to Spring SQL exception correlation
    static List<String> SYBASE_ERROR_CODE_BAD_SQL_GRAMMAR = Arrays.asList("101", "102", "103", "104", "105", "106",
            "107", "108", "109", "110", "111", "112", "113", "116", "120", "121", "123", "207", "208", "213", "257", "512");
    static List<String> SYBASE_ERROR_CODE_DUPLICATE_KEY_EXCEPTION = Arrays.asList("2601", "2615", "2626");
    static List<String> SYBASE_ERROR_CODE_DATA_INTEGRITY_VIOLATION = Arrays.asList("233", "511", "515", "530", "547",
            "2615", "2714");
    static List<String> SYBASE_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE = Arrays.asList("921", "1105");
    static List<String> SYBASE_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION = Arrays.asList("12205");
    static List<String> SYBASE_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION = Arrays.asList("1205");
}
