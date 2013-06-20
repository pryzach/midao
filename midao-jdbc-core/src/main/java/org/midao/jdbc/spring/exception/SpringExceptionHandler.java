package org.midao.jdbc.spring.exception;

import org.midao.jdbc.core.exception.ExceptionHandler;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.exception.MidaoSQLException;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.utils.AssertUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Exception handler implementation inspired by Spring JDBC implementation
 */
public class SpringExceptionHandler implements ExceptionHandler {
    private final String dbName;

    /**
     * Creates new SpringExceptionHandler instance.
     *
     * @param dbName Database name
     */
    public SpringExceptionHandler(String dbName) {
        this.dbName = dbName;
    }

    /**
     * {@inheritDoc}
     */
    public MidaoSQLException convert(Connection conn, SQLException cause, String sql, Object... params) {
        AssertUtils.assertNotNull(cause);

        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.deepToString(params));
        }

        MidaoSQLException convertedException = null;

        try {
            // trying to use JDBC4 exceptions and convert them into Spring SQL Exceptions
            convertedException = translateJDBC4Exception(msg.toString(), cause.getSQLState(),
                    cause.getErrorCode(), cause);
        } catch (MidaoException ex) {
            // possible as JDBC4 classes might not be available if we are running it with Java 5
        }

        if (convertedException == null) {
            // JDBC4 exceptions are not available. Trying to read SQL state prefix and convert it to Spring SQL Exceptions
            convertedException = translateSQLStatePrefix(msg.toString(), cause.getSQLState(),
                    cause.getErrorCode(), cause);
        }

        if (convertedException == null) {
            // JDBC4 and SQL state prefix failed to translate. Using database specific translation
            convertedException = translate(msg.toString(), cause.getSQLState(),
                    cause.getErrorCode(), cause);
        }

        if (convertedException == null) {
            // wasn't able to translate. Creating general exception...
            convertedException = new MidaoSQLException(msg.toString(), cause.getSQLState(),
                    cause.getErrorCode());
        }

        convertedException.setStackTrace(cause.getStackTrace());

        return convertedException;
    }

    /**
     * Checks the interface of SQLException and tries to determine/convert JDBC4 exceptions into
     * Spring SQL Exceptions
     *
     * @param reason a description of the exception
     * @param SQLState an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     * @param cause original SQL Exception
     * @return SQL Exception converted into Spring SQL Exception. Null otherwise
     * @throws MidaoException
     */
    private MidaoSQLException translateJDBC4Exception(String reason, String SQLState, int vendorCode, SQLException cause) throws MidaoException {
        MidaoSQLException result = null;

        if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLTransientException") == true) {

            if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLTransactionRollbackException") == true) {

                result = new ConcurrencyFailureException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLTransientConnectionException") == true) {

                result = new TransientDataAccessResourceException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLTimeoutException") == true) {

                result = new QueryTimeoutException(reason, SQLState, vendorCode);

            }
        } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLNonTransientException") == true) {

            if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLDataException") == true) {

                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLFeatureNotSupportedException") == true) {

                result = new InvalidDataAccessApiUsageException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLIntegrityConstraintViolationException") == true) {

                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLInvalidAuthorizationSpecException") == true) {

                result = new PermissionDeniedDataAccessException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLNonTransientConnectionException") == true) {

                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);

            } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLSyntaxErrorException") == true) {

                result = new BadSqlGrammarException(reason, SQLState, vendorCode);

            }
        } else if (MappingUtils.objectAssignableTo(cause, "java.sql.SQLRecoverableException") == true) {

            result = new RecoverableDataAccessException(reason, SQLState, vendorCode);

        }

        return result;
    }

    /**
     * Checks SQL state and tries to convert it into Spring SQL Exception.
     * This implementation is vendor "free".
     *
     * @param reason a description of the exception
     * @param SQLState an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     * @param cause original SQL Exception
     * @return SQL Exception converted into Spring SQL Exception. Null otherwise
     */
    private MidaoSQLException translateSQLStatePrefix(String reason, String SQLState, int vendorCode, SQLException cause) {
        MidaoSQLException result = null;
        String sqlState = getSqlState(cause);
        String sqlStatePrefix = null;

        if (sqlState != null && sqlState.length() >= 2) {
            sqlStatePrefix = sqlState.substring(0, 2);

            if (SpringExceptionHandlerConstants.SQL_STATE_PREFIX_BAD_SQL_GRAMMAR.contains(sqlStatePrefix) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SQL_STATE_PREFIX_DATA_INTEGRITY_VIOLATION.contains(sqlStatePrefix) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SQL_STATE_PREFIX_DATA_ACCESS_RESOURCE_FAILURE.contains(sqlStatePrefix) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SQL_STATE_PREFIX_TRANSIENT_DATA_ACCESS_RESOURCE_EXCEPTION.contains(sqlStatePrefix) == true) {
                result = new TransientDataAccessResourceException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SQL_STATE_PREFIX_CONCURRENCY_FAILURE.contains(sqlStatePrefix) == true) {
                result = new ConcurrencyFailureException(reason, SQLState, vendorCode);
            }
        }

        return result;
    }

    /**
     * Vendor specific translation. Reads returned errorcode/sqlstate and converts it into Spring SQL exception
     * based on existing lists.
     *
     * @param reason a description of the exception
     * @param SQLState an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     * @param cause original SQL Exception
     * @return SQL Exception converted into Spring SQL Exception. Null otherwise
     */
    private MidaoSQLException translate(String reason, String SQLState, int vendorCode, SQLException cause) {
        MidaoSQLException result = null;
        String sqlState = getSqlState(cause);
        String errorCode = getErrorCode(cause);

        if (dbName.startsWith("DB2") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.DB2_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DB2_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DB2_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DB2_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DB2_ERROR_CODE_TRANSILIENT_DATA_ACCESS_RESOURCE_EXCEPTION.contains(errorCode) == true) {
                result = new TransientDataAccessResourceException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DB2_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION.contains(errorCode) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            }

        } else if (dbName.contains("Derby") == true) {
            // using sql state to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.DERBY_SQL_STATE_BAD_SQL_GRAMMAR.contains(sqlState) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DERBY_SQL_STATE_DUPLICATE_KEY_EXCEPTION.contains(sqlState) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DERBY_SQL_STATE_DATA_INTEGRITY_VIOLATION.contains(sqlState) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DERBY_SQL_STATE_DATA_ACCESS_RESOURCE_FAILURE.contains(sqlState) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DERBY_SQL_STATE_ACQUIRE_LOCK_EXCEPTION.contains(sqlState) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.DERBY_SQL_STATE_DEADLOCK_LOSER_EXCEPTION.contains(sqlState) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("H2") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.H2_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.H2_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.H2_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.H2_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.H2_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION.contains(errorCode) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("HSQL") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.HSQL_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.HSQL_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.HSQL_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.HSQL_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("Informix") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.INFORMIX_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.INFORMIX_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.INFORMIX_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("Microsoft SQL Server") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION.contains(errorCode) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_PERMISSION_DENIED.contains(errorCode) == true) {
                result = new PermissionDeniedDataAccessException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MSSQL_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION.contains(errorCode) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("MySQL") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.MySQL_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MySQL_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MySQL_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MySQL_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MySQL_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION.contains(errorCode) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.MySQL_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION.contains(errorCode) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("Oracle") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION.contains(errorCode) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_INVALID_RESULTSET_ACCESS.contains(errorCode) == true) {
                result = new InvalidResultSetAccessException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION.contains(errorCode) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.ORACLE_ERROR_CODE_CANNOT_SERIALIZE_TRANSACTION.contains(errorCode) == true) {
                result = new CannotSerializeTransactionException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("PostgreSQL") == true) {
            // using sql state to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_BAD_SQL_GRAMMAR.contains(sqlState) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_DUPLICATE_KEY_EXCEPTION.contains(sqlState) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_DATA_INTEGRITY_VIOLATION.contains(sqlState) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_DATA_ACCESS_RESOURCE_FAILURE.contains(sqlState) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_ACQUIRE_LOCK_EXCEPTION.contains(sqlState) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_DEADLOCK_LOSER_EXCEPTION.contains(sqlState) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.POSTGRES_SQL_STATE_CANNOT_SERIALIZE_TRANSACTION.contains(sqlState) == true) {
                result = new CannotSerializeTransactionException(reason, SQLState, vendorCode);
            }

        } else if (dbName.startsWith("Sybase") == true) {
            // using error code to translate vendor specific exception into Spring SQL exception

            if (SpringExceptionHandlerConstants.SYBASE_ERROR_CODE_BAD_SQL_GRAMMAR.contains(errorCode) == true) {
                result = new BadSqlGrammarException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SYBASE_ERROR_CODE_DUPLICATE_KEY_EXCEPTION.contains(errorCode) == true) {
                result = new DuplicateKeyException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SYBASE_ERROR_CODE_DATA_INTEGRITY_VIOLATION.contains(errorCode) == true) {
                result = new DataIntegrityViolationException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SYBASE_ERROR_CODE_DATA_ACCESS_RESOURCE_FAILURE.contains(errorCode) == true) {
                result = new DataAccessResourceFailureException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SYBASE_ERROR_CODE_ACQUIRE_LOCK_EXCEPTION.contains(errorCode) == true) {
                result = new CannotAcquireLockException(reason, SQLState, vendorCode);
            } else if (SpringExceptionHandlerConstants.SYBASE_ERROR_CODE_DEADLOCK_LOSER_EXCEPTION.contains(errorCode) == true) {
                result = new DeadlockLoserDataAccessException(reason, SQLState, vendorCode);
            }

        }

        return result;
    }

    /**
     * Reads SQLException hierarchy and returns SQL state.
     *
     * @param ex top SQL exception
     * @return sql state
     */
    private String getSqlState(SQLException ex) {
        String result = ex.getSQLState();
        SQLException nestedEx = null;

        if (result == null) {
            nestedEx = ex.getNextException();
            if (nestedEx != null) {
                result = nestedEx.getSQLState();
            }
        }

        return result;
    }

    /**
     * Reads SQLException hierarchy and returns error code
     *
     * @param ex top SQL exception
     * @return error code as String
     */
    private String getErrorCode(SQLException ex) {
        String result = null;
        SQLException nestedEx = null;

        if (ex.getErrorCode() != 0) {
            result = Integer.toString(ex.getErrorCode());
        }

        if (result == null) {
            nestedEx = ex.getNextException();
            if (nestedEx != null) {
                result = Integer.toString(nestedEx.getErrorCode());
            }
        }

        return result;
    }
}
