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

import org.midao.jdbc.core.handlers.utils.MappingUtils;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Midao JDBC Constants
 * <p/>
 * Constants from other Constants class might be moved in here
 * <p/>
 * <p>This class is Deprecated - please use {@link MjdbcConstants} instead</p>
 */
@Deprecated
public class MidaoConstants {
    public static final String OVERRIDE_GENERATED_COLUMN_NAMES = "OverrideGenColumnNames";
    public static final String OVERRIDE_CONTROL_PARAM_COUNT = "OverrideControlParamCount";
    public static final String OVERRIDE_LAZY_CACHE_MAX_SIZE = "OverrideLazyCacheMaxSize";
    public static final String OVERRIDE_LAZY_SCROLL_CHANGE_SENSITIVE = "OverrideLazyScrollChangeSensitive";

    public static final String OVERRIDE_INT_GET_GENERATED_KEYS = "OverrideInternalGetGeneratedKeys";
    public static final String OVERRIDE_INT_IS_MANUAL_MODE = "OverrideInternalIsManualMode";
    public static final String OVERRIDE_INT_TYPE_HANDLER = "OverrideInternalTypeHandler";
    public static final String OVERRIDE_INT_JDBC3 = "OverrideInternalJDBC3";

    /**
     * Connection bean descriptors cache.
     * Used in {@link org.midao.jdbc.core.handlers.utils.MappingUtils#invokeConnectionSetter(java.sql.Connection, String, Object)}
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
