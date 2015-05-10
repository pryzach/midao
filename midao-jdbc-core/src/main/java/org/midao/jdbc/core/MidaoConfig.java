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

import org.midao.jdbc.core.exception.ExceptionHandler;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.metadata.MetadataHandler;
import org.midao.jdbc.core.processor.QueryInputProcessor;
import org.midao.jdbc.core.processor.QueryOutputProcessor;
import org.midao.jdbc.core.statement.StatementHandler;
import org.midao.jdbc.core.transaction.TransactionHandler;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * This class allows configure Midao JDBC.
 * <p/>
 * Majority of configuration won't influence existing instances, rather new instances. Please be sure to
 * change configuration before you are creating new instances.
 * <p/>
 * This class creates a lot of ugly dependencies, but allows easy configuration of the module.
 * <p/>
 * In future I am planning to release Spring compatible MjdbcConfig where those default values would be wired
 * from Spring.
 * <p/>
 * <p>This class is Deprecated - please use {@link MjdbcConfig} instead</p>
 */
@Deprecated
public class MidaoConfig {
    /**
     * Returns default {@link org.midao.jdbc.core.processor.QueryInputProcessor} implementation
     *
     * @return default {@link org.midao.jdbc.core.processor.QueryInputProcessor} implementation
     */
    public static QueryInputProcessor getDefaultQueryInputProcessor() {
        return MjdbcConfig.getDefaultQueryInputProcessor();
    }

    /**
     * Specifies default {@link org.midao.jdbc.core.processor.QueryInputProcessor} implementation
     *
     * @param defaultQueryInputProcessor new default {@link org.midao.jdbc.core.processor.QueryInputProcessor} implementation
     */
    public static void setDefaultQueryInputProcessor(QueryInputProcessor defaultQueryInputProcessor) {
        MjdbcConfig.setDefaultQueryInputProcessor(defaultQueryInputProcessor);
    }

    /**
     * Allows to check if (by default) {@link org.midao.jdbc.core.processor.QueryInputProcessor} should use cache
     *
     * @return true - if {@link org.midao.jdbc.core.processor.QueryInputProcessor} should use cache
     */
    public static boolean isQueryInputProcessorUseCache() {
        return MjdbcConfig.isQueryInputProcessorUseCache();
    }

    /**
     * Specifies if (by default) {@link org.midao.jdbc.core.processor.QueryInputProcessor} should use cache
     *
     * @param queryInputProcessorUseCache new default cache usage rule
     */
    public static void setQueryInputProcessorUseCache(boolean queryInputProcessorUseCache) {
        MjdbcConfig.setQueryInputProcessorUseCache(queryInputProcessorUseCache);
    }

    /**
     * Returns default {@link org.midao.jdbc.core.processor.QueryOutputProcessor} implementation
     *
     * @return default {@link org.midao.jdbc.core.processor.QueryOutputProcessor} implementation
     */
    public static QueryOutputProcessor getDefaultQueryOutputProcessor() {
        return MjdbcConfig.getDefaultQueryOutputProcessor();
    }

    /**
     * Specifies default {@link org.midao.jdbc.core.processor.QueryOutputProcessor} implementation
     *
     * @param defaultQueryOutputProcessor new default {@link org.midao.jdbc.core.processor.QueryOutputProcessor} implementation
     */
    public static void setDefaultQueryOutputProcessor(QueryOutputProcessor defaultQueryOutputProcessor) {
        MjdbcConfig.setDefaultQueryOutputProcessor(defaultQueryOutputProcessor);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.statement.StatementHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultStatementHandler(Class)}
     *
     * @param overrider {@link org.midao.jdbc.core.Overrider} instance (from {@link QueryRunner#getOverrider()}
     * @return new {@link org.midao.jdbc.core.statement.StatementHandler} implementation instance
     */
    public static StatementHandler getDefaultStatementHandler(Overrider overrider) {
        return MjdbcConfig.getDefaultStatementHandler(overrider);
    }

    /**
     * Sets default {@link org.midao.jdbc.core.statement.StatementHandler} implementation
     *
     * @param defaultStatementHandler new default {@link org.midao.jdbc.core.statement.StatementHandler} implementation
     */
    public static void setDefaultStatementHandler(Class<? extends StatementHandler> defaultStatementHandler) {
        MjdbcConfig.setDefaultStatementHandler(defaultStatementHandler);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultTypeHandler(Class)}
     *
     * @param overrider {@link org.midao.jdbc.core.Overrider} instance (from {@link QueryRunner#getOverrider()}
     * @return new {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation instance
     */
    public static TypeHandler getDefaultTypeHandler(Overrider overrider) {
        return MjdbcConfig.getDefaultTypeHandler(overrider);
    }

    /**
     * Sets default {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation
     *
     * @param defaultTypeHandler new default {@link org.midao.jdbc.core.handlers.type.TypeHandler} implementation
     */
    public static void setDefaultTypeHandler(Class<? extends TypeHandler> defaultTypeHandler) {
        MjdbcConfig.setDefaultTypeHandler(defaultTypeHandler);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.transaction.TransactionHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultTransactionHandler(Class)}
     *
     * @param conn SQL Connection
     * @return new {@link org.midao.jdbc.core.transaction.TransactionHandler} implementation instance
     */
    public static TransactionHandler getDefaultTransactionHandler(Connection conn) {
        return MjdbcConfig.getDefaultTransactionHandler(conn);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.transaction.TransactionHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultTransactionHandler(Class)}
     *
     * @param ds SQL DataSource
     * @return new {@link org.midao.jdbc.core.transaction.TransactionHandler} implementation instance
     */
    public static TransactionHandler getDefaultTransactionHandler(DataSource ds) {
        return MjdbcConfig.getDefaultTransactionHandler(ds);
    }

    /**
     * Sets default {@link org.midao.jdbc.core.transaction.TransactionHandler} implementation
     *
     * @param defaultTransactionHandler new default {@link org.midao.jdbc.core.transaction.TransactionHandler} implementation
     */
    public static void setDefaultTransactionHandler(Class<? extends TransactionHandler> defaultTransactionHandler) {
        MjdbcConfig.setDefaultTransactionHandler(defaultTransactionHandler);
    }

    /**
     * Returns default {@link org.midao.jdbc.core.exception.ExceptionHandler} implementation
     *
     * @return default {@link org.midao.jdbc.core.exception.ExceptionHandler} implementation
     */
    public static ExceptionHandler getDefaultExceptionHandler(String dbName) {
        return MjdbcConfig.getDefaultExceptionHandler(dbName);
    }

    /**
     * Sets default {@link org.midao.jdbc.core.exception.ExceptionHandler} implementation
     *
     * @param defaultExceptionHandler new default {@link org.midao.jdbc.core.exception.ExceptionHandler} implementation
     */
    public static void setDefaultExceptionHandler(Class<? extends ExceptionHandler> defaultExceptionHandler) {
        MjdbcConfig.setDefaultExceptionHandler(defaultExceptionHandler);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.metadata.MetadataHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultMetadataHandler(Class)}
     *
     * @param conn SQL Connection
     * @return new {@link org.midao.jdbc.core.metadata.MetadataHandler} implementation instance
     */
    public static MetadataHandler getDefaultMetadataHandler(Connection conn) {
        return MjdbcConfig.getDefaultMetadataHandler(conn);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.metadata.MetadataHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultMetadataHandler(Class)}
     *
     * @param ds SQL DataSource
     * @return new {@link org.midao.jdbc.core.metadata.MetadataHandler} implementation instance
     */
    public static MetadataHandler getDefaultMetadataHandler(DataSource ds) {
        return MjdbcConfig.getDefaultMetadataHandler(ds);
    }

    /**
     * Sets default {@link org.midao.jdbc.core.metadata.MetadataHandler} implementation
     *
     * @param defaultMetadataHandler new default {@link org.midao.jdbc.core.metadata.MetadataHandler} implementation
     */
    public static void setDefaultMetadataHandler(Class<? extends MetadataHandler> defaultMetadataHandler) {
        MjdbcConfig.setDefaultMetadataHandler(defaultMetadataHandler);
    }

    /**
     * Creates new {@link org.midao.jdbc.core.Overrider} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultOverrider(Class)}
     *
     * @return new {@link org.midao.jdbc.core.Overrider} instance
     */
    public static Overrider getDefaultOverrider() {
        return MjdbcConfig.getDefaultOverrider();
    }

    /**
     * Sets default {@link org.midao.jdbc.core.Overrider} implementation
     *
     * @param defaultOverrider new default {@link org.midao.jdbc.core.Overrider} implementation
     */
    public static void setDefaultOverrider(Class<Overrider> defaultOverrider) {
        MjdbcConfig.setDefaultOverrider(defaultOverrider);
    }

    /**
     * Returns if Profiler is enabled
     *
     * @return true - if Profiler is enabled
     */
    public static boolean isProfilerEnabled() {
        return MjdbcConfig.isProfilerEnabled();
    }

    /**
     * Specifies if Profiler should be enabled
     *
     * @param profilerEnabled new Profiler state
     */
    public static void setProfilerEnabled(boolean profilerEnabled) {
        MjdbcConfig.setProfilerEnabled(profilerEnabled);
    }

    /**
     * Returns Profiler output format
     *
     * @return current Profiler output format
     */
    public static String getProfilerOutputFormat() {
        return MjdbcConfig.getProfilerOutputFormat();
    }

    /**
     * Sets new Profiler output format
     *
     * @param profilerOutputFormat new Profiler output format
     */
    public static void setProfilerOutputFormat(String profilerOutputFormat) {
        MjdbcConfig.setProfilerOutputFormat(profilerOutputFormat);
    }

    /**
     * Returns current lazy cache max size
     *
     * @return current lazy cache max size
     */
    public static int getDefaultLazyCacheMaxSize() {
        return MjdbcConfig.getDefaultLazyCacheMaxSize();
    }

    /**
     * Sets new lazy cache max size
     *
     * @param lazyCacheMaxSize new lazy cache max size
     */
    public static void setDefaultLazyCacheMaxSize(int lazyCacheMaxSize) {
        MjdbcConfig.setDefaultLazyCacheMaxSize(lazyCacheMaxSize);
    }
}
