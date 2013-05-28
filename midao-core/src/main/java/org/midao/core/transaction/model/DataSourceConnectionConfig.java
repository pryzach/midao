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

package org.midao.core.transaction.model;

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
     * @see {@link java.sql.Connection#setReadOnly(boolean)}
     *
     * @param readOnly new readonly status
     */
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

    /**
     * Returns readonly state
     *
     * @see {@link java.sql.Connection#isReadOnly()}
     *
     * @return current readonly status
     */
	public Boolean getReadOnly() {
		return readOnly;
	}

    /**
     * Sets catalog name
     *
     * @see {@link java.sql.Connection#setCatalog(String)}
     *
     * @param catalog new catalog name
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * Returns catalog name
     *
     * @see {@link java.sql.Connection#getCatalog()}
     *
     * @return current catalog name
     */
	public String getCatalog() {
		return catalog;
	}

    /**
     * Sets type map
     *
     * @see {@link java.sql.Connection#setTypeMap(java.util.Map)}
     *
     * @param map new types map
     */
    public void setTypeMap(Map<String, Class<?>> map) {
        this.typeMap = map;
    }

    /**
     * Returns type map
     *
     * @see {@link java.sql.Connection#getTypeMap()}
     *
     * @return current type map
     */
	public Map<String, Class<?>> getTypeMap() {
		return this.typeMap;
	}

    /**
     * Sets holdability status
     *
     * @see {@link java.sql.Connection#setHoldability(int)}
     *
     * @param holdability new holdability status
     */
    public void setHoldability(Integer holdability) {
        this.holdability = holdability;
    }

    /**
     * Gets holdability status
     *
     * @see {@link java.sql.Connection#getHoldability()}
     *
     * @return current holdability status
     */
	public Integer getHoldability() {
		return holdability;
	}

    /**
     * Sets client information
     *
     * @see {@link java.sql.Connection#setClientInfo(java.util.Properties)}
     *
     * @param clientInfo new client information
     */
    public void setClientInfo(Properties clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Sets client information
     *
     * @see {@link java.sql.Connection#setClientInfo(String, String)}
     *
     * @param name name
     * @param value value
     */
    public void setClientInfo(String name, String value) {
        this.clientInfo.put(name, value);
    }

    /**
     * Gets client information
     *
     * @see {@link java.sql.Connection#getClientInfo()}
     *
     * @return current client information
     */
	public Properties getClientInfo() {
		return clientInfo;
	}

}
