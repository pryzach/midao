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

import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.utils.MappingUtils;

import java.util.logging.Logger;

/**
 * Midao Logger is responsible for performing all logging operation.
 *
 * If SLF4j found in class loader - it would be used. Otherwise Java Logging would be used
 */
public class MjdbcLogger extends Logger {
    private static Boolean SLF4jAvailable = null;
    private static Boolean SLF4jImplementationAvailable = null;

    private Object slfLogger = null;

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p/>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers true.
     *
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws java.util.MissingResourceException
     *          if the ResourceBundleName is non-null and
     *          no corresponding resource can be found.
     * @param    name    A name for the logger.  This should
     * be a dot-separated name and should normally
     * be based on the package name or class name
     * of the subsystem, such as java.net
     * or javax.swing.  It may be null for anonymous Loggers.
     */
    private MjdbcLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    /**
     * Creates new MjdbcLogger instance
     *
     * @param name class name
     * @return MjdbcLogger instance
     */
    public static MjdbcLogger getLogger(String name) {
        MjdbcLogger mjdbcLogger = new MjdbcLogger(name, null);

        if (isSLF4jAvailable() == true) {
            try {
                mjdbcLogger = new MjdbcLogger(name, null);
                mjdbcLogger.setSlfLogger(MappingUtils.invokeStaticFunction(Class.forName("org.slf4j.LoggerFactory"), "getLogger", new Class[]{String.class}, new Object[]{name}));
            } catch (MjdbcException e) {
                setSLF4jAvailable(false);
            } catch (ClassNotFoundException e) {
                setSLF4jAvailable(false);
            }
        }

        return mjdbcLogger;
    }

    /**
     * Creates new MjdbcLogger instance
     *
     * @param clazz Class description
     * @return MjdbcLogger instance
     */
    public static MjdbcLogger getLogger(Class clazz) {
        MjdbcLogger mjdbcLogger = new MjdbcLogger(clazz.getName(), null);

        if (isSLF4jAvailable() == true) {
            try {
                mjdbcLogger = new MjdbcLogger(clazz.getName(), null);
                mjdbcLogger.setSlfLogger(MappingUtils.invokeStaticFunction(Class.forName("org.slf4j.LoggerFactory"), "getLogger", new Class[] {Class.class}, new Object[]{clazz}));
            } catch (MjdbcException e) {
                setSLF4jAvailable(false);
            } catch (ClassNotFoundException e) {
                setSLF4jAvailable(false);
            }
        }

        return mjdbcLogger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String msg) {
        if (isSLF4jAvailable() == false) {
            super.info(msg);
        } else {
            try {
                MappingUtils.invokeFunction(slfLogger, "info", new Class[]{String.class}, new Object[]{msg});
            } catch (MjdbcException e) {
                setSLF4jAvailable(false);
                super.info(msg);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warning(String msg) {
        if (isSLF4jAvailable() == false) {
            super.warning(msg);
        } else {
            try {
                MappingUtils.invokeFunction(slfLogger, "warn", new Class[]{String.class}, new Object[]{msg});
            } catch (MjdbcException e) {
                setSLF4jAvailable(false);
                super.warning(msg);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void severe(String msg) {
        if (isSLF4jAvailable() == false) {
            super.severe(msg);
        } else {
            try {
                MappingUtils.invokeFunction(slfLogger, "error", new Class[]{String.class}, new Object[]{msg});
            } catch (MjdbcException e) {
                setSLF4jAvailable(false);
                super.severe(msg);
            }
        }
    }

    /**
     * Checks if SLF4j is available (loaded) in JVM
     *
     * @return true - if SLF4j is available for use
     */
    public static boolean isSLF4jAvailable() {
        if (SLF4jAvailable == null) {
            try {
                Class.forName("org.slf4j.Logger");
                setSLF4jAvailable(true);
            } catch (ClassNotFoundException e) {
                setSLF4jAvailable(false);
            }
        }

        return SLF4jAvailable;
    }

    /**
     * Checks if any SLF4j API implementation is available (loaded in JVM)
     *
     * @return true - if SLF4j implementation is available for use
     */
    public static boolean isSLF4jImplementationAvailable() {
        if (SLF4jImplementationAvailable == null) {
            if (isSLF4jAvailable() == true) {
                try {
                    Object obj = MappingUtils.invokeStaticFunction(Class.forName("org.slf4j.LoggerFactory"), "getILoggerFactory", new Class[]{}, new Object[]{});
                    if (obj.getClass().getSimpleName().equals("NOPLoggerFactory") == true) {
                        SLF4jImplementationAvailable = false;
                    } else {
                        SLF4jImplementationAvailable = true;
                    }
                } catch (MjdbcException e) {
                    SLF4jImplementationAvailable = false;
                } catch (ClassNotFoundException e) {
                    SLF4jImplementationAvailable = false;
                }
            } else {
                SLF4jImplementationAvailable = false;
            }
        }

        return SLF4jImplementationAvailable;
    }

    /**
     * Standard Setter function.
     * Informs if SLF4j availability was set from true into false
     *
     * @param slf4jAvailable new SLF4j availability flag
     */
    private static void setSLF4jAvailable(boolean slf4jAvailable) {
        if ((SLF4jAvailable != null && SLF4jAvailable == true) && slf4jAvailable == false) {
            Logger.getAnonymousLogger().warning("Switching off SLF4j availability. Usually means that SLF4j was found but exception was thrown during it's usage");
        }

        SLF4jAvailable = slf4jAvailable;
    }

    /**
     * Sets SLF4j instance to use by this instance of MjdbcLogger
     *
     * @param logger SLF4j Logger instance
     */
    private void setSlfLogger(Object logger) {
        this.slfLogger = logger;
    }
}
