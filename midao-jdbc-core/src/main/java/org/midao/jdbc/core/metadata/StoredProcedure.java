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

package org.midao.jdbc.core.metadata;

import org.midao.jdbc.core.utils.AssertUtils;

/**
 * Stored Procedure description class used by Metadata Handler
 * {@link org.midao.jdbc.core.metadata.BaseMetadataHandler}
 */
public class StoredProcedure implements Comparable<StoredProcedure> {
	private final String catalog;
	private final String schema;
	private final String procedureName;

    /**
     * Creates new StoredProcedure instance
     *
     * @param catalog Database Catalog name
     * @param schema Database Schema name
     * @param procedureName Stored Procedure/Function name
     */
	StoredProcedure(String catalog, String schema, String procedureName) {
		this.catalog = catalog;
		this.schema = schema;
		this.procedureName = procedureName;
	}
	
	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getProcedureName() {
		return procedureName;
	}

	/*
	 * Special implementation. Ignores null values and is case insensitive
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		StoredProcedure procedure = null;
		
		if (obj instanceof StoredProcedure) {
			procedure = (StoredProcedure) obj;

            AssertUtils.assertNotNull(procedure.getProcedureName(), "Procedure name cannot be null");

            if (equalsNull(getCatalog(), procedure.getCatalog()) == true) {
				if (equalsNull(getSchema(), procedure.getSchema()) == true) {
					if (equalsNull(getProcedureName(), procedure.getProcedureName()) == true) {
						result = true;
					}
				}
			}
		}
		
		return result;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = catalog != null ? catalog.hashCode() : 0;
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
        return result;
    }

    /**
     * Comparable implementation.
     * Compares with other instance of StoredProcedure to define order.
     * Returns 0 - if equal, 1/-1 bigger/smaller.
     *
     * @param o Stored Procedure instance which would be compared against this isntance
     * @return 0 - if equal, 1/-1 bigger/smaller.
     */
	public int compareTo(StoredProcedure o) {
		int result = -2;
		
		result = order(getCatalog(), o.getCatalog());
		result = (result == 0 ? order(getSchema(), o.getSchema()) : result);
		result = (result == 0 ? order(getProcedureName(), o.getProcedureName()) : result);
		
		return result;
	}
	
	@Override
	public String toString() {
		return this.getCatalog() + "." + this.getSchema() + "." + this.getProcedureName();
	}

    /**
     * Compares two values. Treats null as equal and is case insensitive
     *
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return true if two values equals
     */
	private boolean equalsNull(String value1, String value2) {
		return ( value1 == value2 || value1 == null || (value1 != null && value1.equalsIgnoreCase(value2)) ); 
	}

    /**
     * Compares two values (allows null values) to define order
     * Returns 0 if they are equals. 1/-1 if bigger/smaller
     *
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return 0 - if they are equals. 1/-1 if bigger/smaller
     */
	private int order(String value1, String value2) {
		int result = -1;
		
		if (value1 == value2) {
			result = 0;
		} else if (value1 != null) {
            if (value2 != null) {
			    result = value1.compareTo(value2);
            } else {
                result = 1;
            }
		} else {
			result = -1;
		}
		
		return result;
	}
}
