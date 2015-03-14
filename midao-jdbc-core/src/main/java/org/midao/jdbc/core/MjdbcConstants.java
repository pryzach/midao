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

import org.midao.jdbc.core.handlers.output.*;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyScrollOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyScrollUpdateOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyUpdateOutputHandler;
import org.midao.jdbc.core.handlers.utils.MappingUtils;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Midao JDBC Constants
 * <p/>
 * Constants from other Constants class might be moved in here
 */
public class MjdbcConstants {
    public static final String OVERRIDE_GENERATED_COLUMN_NAMES = "__OverrideGenColumnNames";
    public static final String OVERRIDE_CONTROL_PARAM_COUNT = "__OverrideControlParamCount";
    public static final String OVERRIDE_LAZY_CACHE_MAX_SIZE = "__OverrideLazyCacheMaxSize";
    public static final String OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE = "__OverrideLazyScrollChangeSensitive";

    public static final String OVERRIDE_INT_GET_GENERATED_KEYS = "__OverrideInternalGetGeneratedKeys";
    public static final String OVERRIDE_INT_IS_MANUAL_MODE = "__OverrideInternalIsManualMode";
    public static final String OVERRIDE_INT_TYPE_HANDLER = "__OverrideInternalTypeHandler";
    public static final String OVERRIDE_INT_JDBC3 = "__OverrideInternalJDBC3";
    public static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
    public static final Map<String, Object> EMPTY_VALUE_MAP = new HashMap<String, Object>();

    public static final ArrayListOutputHandler ARRAY_LIST_OUTPUT_HANDLER = new ArrayListOutputHandler();
    public static final ArrayOutputHandler ARRAY_OUTPUT_HANDLER = new ArrayOutputHandler();
    public static final ColumnListOutputHandler COLUMN_LIST_OUTPUT_HANDLER = new ColumnListOutputHandler();
    public static final KeyedOutputHandler KEYED_OUTPUT_HANDLER = new KeyedOutputHandler();
    public static final MapListOutputHandler MAP_LIST_OUTPUT_HANDLER = new MapListOutputHandler();
    public static final MapOutputHandler MAP_OUTPUT_HANDLER = new MapOutputHandler();
    public static final RowCountOutputHandler ROW_COUNT_OUTPUT_HANDLER = new RowCountOutputHandler();
    public static final ScalarOutputHandler SCALAR_OUTPUT_HANDLER = new ScalarOutputHandler();
    public static final MapLazyOutputHandler MAP_LAZY_OUTPUT_HANDLER = new MapLazyOutputHandler();
    public static final MapLazyScrollOutputHandler MAP_LAZY_SCROLL_OUTPUT_HANDLER = new MapLazyScrollOutputHandler();
    public static final MapLazyScrollUpdateOutputHandler MAP_LAZY_SCROLL_UPDATE_OUTPUT_HANDLER = new MapLazyScrollUpdateOutputHandler();
    public static final MapLazyUpdateOutputHandler MAP_LAZY_UPDATE_OUTPUT_HANDLER = new MapLazyUpdateOutputHandler();

    /**
     * Connection bean descriptors cache.
     * Used in {@link MappingUtils#invokeConnectionSetter(java.sql.Connection, String, Object)}
     */
    public static Map<String, PropertyDescriptor> connectionBeanDescription = new HashMap<String, PropertyDescriptor>() {
        {
            PropertyDescriptor[] connectionDescription = MappingUtils.propertyDescriptors(Connection.class);

            for (PropertyDescriptor property : connectionDescription) {
                put(property.getName(), property);
            }
        }
    };

}
