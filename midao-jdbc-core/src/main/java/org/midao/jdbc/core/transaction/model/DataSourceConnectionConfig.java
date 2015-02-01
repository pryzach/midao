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

package org.midao.jdbc.core.transaction.model;

import java.util.Map;
import java.util.Properties;

/**
 * Model for storing DataSource properties
 */
public class DataSourceConnectionConfig {
    private Boolean readOnly = null;
    private String catalog = null;
    private Map<String, Class<?>> typeMap = null;
    private Integer holdability = null;
    private Properties clientInfo = new Properties();

    /**
     * Sets readonly.
     *
     * @param readOnly new readonly status
     * @see {@link java.sql.Connection#setReadOnly(boolean)}
     */
    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Returns readonly state
     *
     * @return current readonly status
     * @see {@link java.sql.Connection#isReadOnly()}
     */
    public Boolean getReadOnly() {
        return readOnly;
    }

    /**
     * Sets catalog name
     *
     * @param catalog new catalog name
     * @see {@link java.sql.Connection#setCatalog(String)}
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * Returns catalog name
     *
     * @return current catalog name
     * @see {@link java.sql.Connection#getCatalog()}
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Sets type map
     *
     * @param map new types map
     * @see {@link java.sql.Connection#setTypeMap(java.util.Map)}
     */
    public void setTypeMap(Map<String, Class<?>> map) {
        this.typeMap = map;
    }

    /**
     * Returns type map
     *
     * @return current type map
     * @see {@link java.sql.Connection#getTypeMap()}
     */
    public Map<String, Class<?>> getTypeMap() {
        return this.typeMap;
    }

    /**
     * Sets holdability status
     *
     * @param holdability new holdability status
     * @see {@link java.sql.Connection#setHoldability(int)}
     */
    public void setHoldability(Integer holdability) {
        this.holdability = holdability;
    }

    /**
     * Gets holdability status
     *
     * @return current holdability status
     * @see {@link java.sql.Connection#getHoldability()}
     */
    public Integer getHoldability() {
        return holdability;
    }

    /**
     * Sets client information
     *
     * @param clientInfo new client information
     * @see {@link java.sql.Connection#setClientInfo(java.util.Properties)}
     */
    public void setClientInfo(Properties clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Sets client information
     *
     * @param name  name
     * @param value value
     * @see {@link java.sql.Connection#setClientInfo(String, String)}
     */
    public void setClientInfo(String name, String value) {
        this.clientInfo.put(name, value);
    }

    /**
     * Gets client information
     *
     * @return current client information
     * @see {@link java.sql.Connection#getClientInfo()}
     */
    public Properties getClientInfo() {
        return clientInfo;
    }

}
