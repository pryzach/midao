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

import org.midao.jdbc.core.exception.BaseExceptionHandler;
import org.midao.jdbc.core.exception.ExceptionHandler;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.handlers.type.EmptyTypeHandler;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.metadata.BaseMetadataHandler;
import org.midao.jdbc.core.metadata.MetadataHandler;
import org.midao.jdbc.core.processor.BasicQueryInputProcessor;
import org.midao.jdbc.core.processor.BasicQueryOutputProcessor;
import org.midao.jdbc.core.processor.QueryInputProcessor;
import org.midao.jdbc.core.processor.QueryOutputProcessor;
import org.midao.jdbc.core.statement.LazyStatementHandler;
import org.midao.jdbc.core.statement.StatementHandler;
import org.midao.jdbc.core.transaction.BaseTransactionHandler;
import org.midao.jdbc.core.transaction.TransactionHandler;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

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
 */
public class MjdbcConfig {
    private static final String ERROR_TH_INIT_FAILED = "Error! Every transaction handler should have two public constructors with Connection and DataSource as parameter!";
    private static final String ERROR_MH_INIT_FAILED = "Error! Every metadata handler should have two public constructors with (Connection, useCache) or (DataSource, useCache) as parameter!";
    private static final String ERROR_SH_INIT_FAILED = "Error! Failed to initialize Statement Handler class. Please make sure there is public constructor which accepts Overrider class";
    private static final String ERROR_TyH_INIT_FAILED = "Error! Failed to initialize Type Handler class. Please make sure there is public constructor which accepts Overrider class";
    private static final String ERROR_OR_INIT_FAILED = "Error! Failed to initialize Overrider class... Please make sure default constructor is available!";

    private static MjdbcConfig defaultMjdbcConfig = new MjdbcConfig();

    // Input processor config
    private QueryInputProcessor defaultQueryInputProcessor = new BasicQueryInputProcessor();

    // query parameters processor config
    private boolean queryInputProcessorUseCache = true;

    // Output Handler processor
    private QueryOutputProcessor defaultQueryOutputProcessor = new BasicQueryOutputProcessor();

    // Statement handler config
    private Class<? extends StatementHandler> defaultStatementHandler = LazyStatementHandler.class;

    // Type handler config
    private Class<? extends TypeHandler> defaultTypeHandler = EmptyTypeHandler.class;

    // Transaction handler config
    private Class<? extends TransactionHandler> defaultTransactionHandler = BaseTransactionHandler.class;

    // Exception handler config
    private Class<? extends ExceptionHandler> defaultExceptionHandler = BaseExceptionHandler.class;

    // Metadata handler config
    private Class<? extends MetadataHandler> defaultMetadataHandler = BaseMetadataHandler.class;

    // Overrider class
    private Class<Overrider> defaultOverrider = Overrider.class;

    // Profiler config
    private boolean profilerEnabled = false;
    private String profilerOutputFormat = "Invoked class [%s].\n - Method [%s{}]\n - Args   [%s]\n - Time   [%5.3f] sec ";

    // Lazy query max cache size
    private int defaultLazyCacheMaxSize = 20;

    // default output handlers configuration for xml input/output handlers
    private static String OUTPUT_HANDLER_PACKAGE = "org.midao.jdbc.core.handlers.output.";

    private static Map<String, OutputHandler> defaultOutputHandlers = new HashMap<String, OutputHandler>() {
        {
            put(OUTPUT_HANDLER_PACKAGE + "ArrayListOutputHandler", MjdbcConstants.ARRAY_LIST_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "ArrayOutputHandler", MjdbcConstants.ARRAY_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "ColumnListOutputHandler", MjdbcConstants.COLUMN_LIST_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "KeyedOutputHandler", MjdbcConstants.KEYED_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "MapListOutputHandler", MjdbcConstants.MAP_LIST_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "MapOutputHandler", MjdbcConstants.MAP_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "RowCountOutputHandler", MjdbcConstants.ROW_COUNT_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "ScalarOutputHandler", MjdbcConstants.SCALAR_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "lazy.MapLazyOutputHandler", MjdbcConstants.MAP_LAZY_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "lazy.MapLazyScrollOutputHandler", MjdbcConstants.MAP_LAZY_SCROLL_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "lazy.MapLazyScrollUpdateOutputHandler", MjdbcConstants.MAP_LAZY_SCROLL_UPDATE_OUTPUT_HANDLER);
            put(OUTPUT_HANDLER_PACKAGE + "lazy.MapLazyUpdateOutputHandler", MjdbcConstants.MAP_LAZY_UPDATE_OUTPUT_HANDLER);
        }
    };

    /**
     * Returns default {@link QueryInputProcessor} implementation
     *
     * @return default {@link QueryInputProcessor} implementation
     */
    public static QueryInputProcessor getDefaultQueryInputProcessor() {
        return instance().defaultQueryInputProcessor;
    }

    /**
     * Specifies default {@link QueryInputProcessor} implementation
     *
     * @param defaultQueryInputProcessor new default {@link QueryInputProcessor} implementation
     */
    public static void setDefaultQueryInputProcessor(QueryInputProcessor defaultQueryInputProcessor) {
        instance().defaultQueryInputProcessor = defaultQueryInputProcessor;
    }

    /**
     * Allows to check if (by default) {@link QueryInputProcessor} should use cache
     *
     * @return true - if {@link QueryInputProcessor} should use cache
     */
    public static boolean isQueryInputProcessorUseCache() {
        return instance().queryInputProcessorUseCache;
    }

    /**
     * Specifies if (by default) {@link QueryInputProcessor} should use cache
     *
     * @param queryInputProcessorUseCache new default cache usage rule
     */
    public static void setQueryInputProcessorUseCache(boolean queryInputProcessorUseCache) {
        instance().queryInputProcessorUseCache = queryInputProcessorUseCache;
    }

    /**
     * Returns default {@link QueryOutputProcessor} implementation
     *
     * @return default {@link QueryOutputProcessor} implementation
     */
    public static QueryOutputProcessor getDefaultQueryOutputProcessor() {
        return instance().defaultQueryOutputProcessor;
    }

    /**
     * Specifies default {@link QueryOutputProcessor} implementation
     *
     * @param defaultQueryOutputProcessor new default {@link QueryOutputProcessor} implementation
     */
    public static void setDefaultQueryOutputProcessor(QueryOutputProcessor defaultQueryOutputProcessor) {
        instance().defaultQueryOutputProcessor = defaultQueryOutputProcessor;
    }

    /**
     * Creates new {@link StatementHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultStatementHandler(Class)}
     *
     * @param overrider {@link Overrider} instance (from {@link org.midao.jdbc.core.QueryRunner#getOverrider()}
     * @return new {@link StatementHandler} implementation instance
     */
    public static StatementHandler getDefaultStatementHandler(Overrider overrider) {
        StatementHandler result = null;
        Constructor<? extends StatementHandler> constructor = null;

        Class<? extends StatementHandler> clazz = instance().defaultStatementHandler;

        try {
            constructor = clazz.getConstructor(Overrider.class);
            result = constructor.newInstance(overrider);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_SH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Sets default {@link StatementHandler} implementation
     *
     * @param defaultStatementHandler new default {@link StatementHandler} implementation
     */
    public static void setDefaultStatementHandler(Class<? extends StatementHandler> defaultStatementHandler) {
        instance().defaultStatementHandler = defaultStatementHandler;
    }

    /**
     * Creates new {@link TypeHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultTypeHandler(Class)}
     *
     * @param overrider {@link Overrider} instance (from {@link org.midao.jdbc.core.QueryRunner#getOverrider()}
     * @return new {@link TypeHandler} implementation instance
     */
    public static TypeHandler getDefaultTypeHandler(Overrider overrider) {
        TypeHandler result = null;
        Constructor<? extends TypeHandler> constructor = null;

        Class<? extends TypeHandler> clazz = instance().defaultTypeHandler;

        try {
            constructor = clazz.getConstructor(Overrider.class);
            result = constructor.newInstance(overrider);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_TyH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Sets default {@link TypeHandler} implementation
     *
     * @param defaultTypeHandler new default {@link TypeHandler} implementation
     */
    public static void setDefaultTypeHandler(Class<? extends TypeHandler> defaultTypeHandler) {
        instance().defaultTypeHandler = defaultTypeHandler;
    }

    /**
     * Creates new {@link TransactionHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultTransactionHandler(Class)}
     *
     * @param conn SQL Connection
     * @return new {@link TransactionHandler} implementation instance
     */
    public static TransactionHandler getDefaultTransactionHandler(Connection conn) {
        TransactionHandler result = null;
        Constructor<?> constructor = null;

        Class<?> clazz = instance().defaultTransactionHandler;

        try {
            constructor = clazz.getConstructor(Connection.class);
            result = (TransactionHandler) constructor.newInstance(conn);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Creates new {@link TransactionHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultTransactionHandler(Class)}
     *
     * @param ds SQL DataSource
     * @return new {@link TransactionHandler} implementation instance
     */
    public static TransactionHandler getDefaultTransactionHandler(DataSource ds) {
        TransactionHandler result = null;
        Constructor<?> constructor = null;

        Class<?> clazz = instance().defaultTransactionHandler;

        try {
            constructor = clazz.getConstructor(DataSource.class);
            result = (TransactionHandler) constructor.newInstance(ds);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Sets default {@link TransactionHandler} implementation
     *
     * @param defaultTransactionHandler new default {@link TransactionHandler} implementation
     */
    public static void setDefaultTransactionHandler(Class<? extends TransactionHandler> defaultTransactionHandler) {
        instance().defaultTransactionHandler = defaultTransactionHandler;
    }

    /**
     * Returns default {@link ExceptionHandler} implementation
     *
     * @return default {@link ExceptionHandler} implementation
     */
    public static ExceptionHandler getDefaultExceptionHandler(String dbName) {
        ExceptionHandler result = null;
        Constructor<?> constructor = null;

        Class<?> clazz = instance().defaultExceptionHandler;

        try {
            constructor = clazz.getConstructor(String.class);
            result = (ExceptionHandler) constructor.newInstance(dbName);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_TH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Sets default {@link ExceptionHandler} implementation
     *
     * @param defaultExceptionHandler new default {@link ExceptionHandler} implementation
     */
    public static void setDefaultExceptionHandler(Class<? extends ExceptionHandler> defaultExceptionHandler) {
        instance().defaultExceptionHandler = defaultExceptionHandler;
    }

    /**
     * Creates new {@link MetadataHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultMetadataHandler(Class)}
     *
     * @param conn SQL Connection
     * @return new {@link MetadataHandler} implementation instance
     */
    public static MetadataHandler getDefaultMetadataHandler(Connection conn) {
        MetadataHandler result = null;
        Constructor<?> constructor = null;

        Class<?> clazz = instance().defaultMetadataHandler;

        try {
            constructor = clazz.getConstructor(Connection.class, boolean.class);
            result = (MetadataHandler) constructor.newInstance(conn, false);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Creates new {@link MetadataHandler} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultMetadataHandler(Class)}
     *
     * @param ds SQL DataSource
     * @return new {@link MetadataHandler} implementation instance
     */
    public static MetadataHandler getDefaultMetadataHandler(DataSource ds) {
        MetadataHandler result = null;
        Constructor<?> constructor = null;

        Class<?> clazz = instance().defaultMetadataHandler;

        try {
            constructor = clazz.getConstructor(DataSource.class, boolean.class);
            result = (MetadataHandler) constructor.newInstance(ds, false);
        } catch (SecurityException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (NoSuchMethodException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (IllegalArgumentException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        } catch (InvocationTargetException e) {
            throw new MjdbcRuntimeException(ERROR_MH_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Sets default {@link MetadataHandler} implementation
     *
     * @param defaultMetadataHandler new default {@link MetadataHandler} implementation
     */
    public static void setDefaultMetadataHandler(Class<? extends MetadataHandler> defaultMetadataHandler) {
        instance().defaultMetadataHandler = defaultMetadataHandler;
    }

    /**
     * Creates new {@link Overrider} implementation instance based on default statement handler
     * implementation class set via {@link #setDefaultOverrider(Class)}
     *
     * @return new {@link Overrider} instance
     */
    public static Overrider getDefaultOverrider() {
        Overrider result = null;

        Class<Overrider> clazz = instance().defaultOverrider;

        try {
            result = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new MjdbcRuntimeException(ERROR_OR_INIT_FAILED, e);
        } catch (IllegalAccessException e) {
            throw new MjdbcRuntimeException(ERROR_OR_INIT_FAILED, e);
        }

        return result;
    }

    /**
     * Sets default {@link Overrider} implementation
     *
     * @param defaultOverrider new default {@link Overrider} implementation
     */
    public static void setDefaultOverrider(Class<Overrider> defaultOverrider) {
        instance().defaultOverrider = defaultOverrider;
    }

    /**
     * Returns if Profiler is enabled
     *
     * @return true - if Profiler is enabled
     */
    public static boolean isProfilerEnabled() {
        return instance().profilerEnabled;
    }

    /**
     * Specifies if Profiler should be enabled
     *
     * @param profilerEnabled new Profiler state
     */
    public static void setProfilerEnabled(boolean profilerEnabled) {
        instance().profilerEnabled = profilerEnabled;
    }

    /**
     * Returns Profiler output format
     *
     * @return current Profiler output format
     */
    public static String getProfilerOutputFormat() {
        return instance().profilerOutputFormat;
    }

    /**
     * Sets new Profiler output format
     *
     * @param profilerOutputFormat new Profiler output format
     */
    public static void setProfilerOutputFormat(String profilerOutputFormat) {
        instance().profilerOutputFormat = profilerOutputFormat;
    }

    /**
     * Returns current lazy cache max size
     *
     * @return current lazy cache max size
     */
    public static int getDefaultLazyCacheMaxSize() {
        return instance().defaultLazyCacheMaxSize;
    }

    /**
     * Sets new lazy cache max size
     *
     * @param lazyCacheMaxSize new lazy cache max size
     */
    public static void setDefaultLazyCacheMaxSize(int lazyCacheMaxSize) {
        instance().defaultLazyCacheMaxSize = lazyCacheMaxSize;
    }

    public static String getOutputHandlerPackage() {
        return OUTPUT_HANDLER_PACKAGE;
    }

    public static Map<String, OutputHandler> getDefaultOutputHandlers() {
        return defaultOutputHandlers;
    }

    private static MjdbcConfig instance() {
        return MjdbcConfig.defaultMjdbcConfig;
    }

}
