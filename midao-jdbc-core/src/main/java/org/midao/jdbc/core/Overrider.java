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

import java.util.HashMap;
import java.util.Map;

/**
 * Overrider class is model responsible for storing information
 * about standard functionality overrides in {@link org.midao.jdbc.core.service.QueryRunnerService},
 * {@link org.midao.jdbc.core.statement.StatementHandler} and {@link org.midao.jdbc.core.handlers.type.TypeHandler}
 */
public class Overrider {
    private Map<String, Object> overrideOnce = new HashMap<String, Object>();
    private Map<String, Object> override = new HashMap<String, Object>();

    /**
     * Creates new Overrider instance
     */
    public Overrider() {
    }

    /**
     * Creates new Overrider instance from input parameter
     *
     * @param overrider overrider which would serve as source for content for new one
     */
    public Overrider(Overrider overrider) {
        this.override = overrider.getOverrideValues();
        this.overrideOnce = overrider.getOverrideOnceValues();
    }

    /**
     * Adds override which would be used only once.
     *
     * @param operation name of the operation
     * @param value     override value
     */
    public void overrideOnce(String operation, Object value) {
        this.overrideOnce.put(operation, value);
    }

    /**
     * Adds override which would be used until removed.
     *
     * @param operation name of the operation
     * @param value     override value
     */
    public void override(String operation, Object value) {
        this.override.put(operation, value);
    }

    /**
     * Removes override.
     *
     * @param operation name of the operation
     */
    public void removeOverride(String operation) {
        if (this.overrideOnce.containsKey(operation) == true) {
            this.overrideOnce.remove(operation);
        }

        if (this.override.containsKey(operation) == true) {
            this.override.remove(operation);
        }
    }

    /**
     * Checks if override is present, but it won't be actually read
     *
     * @param operation name of the operation
     * @return true if override is present
     */
    public boolean hasOverride(String operation) {
        boolean result = false;

        if (this.overrideOnce.containsKey(operation) == true || this.override.containsKey(operation) == true) {
            result = true;
        }

        return result;
    }

    /**
     * Return override value
     *
     * @param operation name of the operation
     * @return override value
     */
    public Object getOverride(String operation) {
        Object result = null;

        if (this.overrideOnce.containsKey(operation) == true) {
            result = this.overrideOnce.get(operation);
            this.overrideOnce.remove(operation);
        } else if (this.override.containsKey(operation) == true) {
            result = this.override.get(operation);
        }

        return result;
    }

    /**
     * Returned copy of override once values.
     * Might be used to construct copy of this instance
     *
     * @return override once values
     */
    private Map<String, Object> getOverrideOnceValues() {
        return new HashMap<String, Object>(this.overrideOnce);
    }

    /**
     * Returned copy of override values.
     * Might be used to construct copy of this instance
     *
     * @return override values
     */
    private Map<String, Object> getOverrideValues() {
        return new HashMap<String, Object>(this.override);
    }
}
