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

package org.midao.core.handlers.input;

import org.midao.core.handlers.utils.InputUtils;
import org.midao.core.processor.QueryInputProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base InputHandler class
 */
public abstract class AbstractInputHandler<T> implements InputHandler<T> {
	private static final String ERROR_SQL_QUERY_NULL = "SQL query cannot be null";
	private static final String ERROR_FOUND_UNNAMED_PARAMETER = "SQL query contains unnamed ('?') query parameter";
	
	protected static final Object[] EMPTY_ARRAY = new Object[0];
	protected static final Integer[] EMPTY_INT_ARRAY = new Integer[0];
	protected final QueryInputProcessor processor;

    /**
     * Creates new AbstractInputHandler instance
     *
     * @param processor
     */
	protected AbstractInputHandler(QueryInputProcessor processor) {
		this.processor = processor;
	}

    /**
     * Merges all Maps into one single map.
     * All maps are merged according to next algorithm:
     * prefix</class name stored in map>.</map key>
     *
     * Fields encodedQuery and addPrefix are not used and might be removed before final release
     *
     * @param encodedQuery Original SQL string
     * @param mapList List of Maps which should be merged
     * @param addPrefix Specifies if prefix should be added to the beginning
     * @return Merged Map
     */
    protected Map<String, Object> mergeMaps(String encodedQuery, List<Map<String, Object>> mapList, boolean addPrefix) {
    	Map<String, Object> mergedMap = new HashMap<String, Object>();
    	String className = null;
    	
    	for (Map<String, Object> map : mapList) {
    		className = InputUtils.getClassName(map);
    		
    		for (String key : map.keySet()) {
    			if (InputUtils.isClassNameKey(key) == false) {
    				
    				if (className != null) {
    					mergedMap.put(InputUtils.addClassName(className.toLowerCase(), key.toLowerCase()), map.get(key));
    				} else {
    					mergedMap.put(key.toLowerCase(), map.get(key));
    				}
    			}
    		}
    	}
    	
    	return mergedMap;
    }

    /**
     * Checks if original SQL string valid.
     * If string contains some unnamed "?" parameters - it is considered unvalid
     *
     * @param originalSql Original SQL String
     */
    protected void validateSqlString(String originalSql) {
    	if (originalSql == null) {
    		throw new IllegalArgumentException(ERROR_SQL_QUERY_NULL);
    	}
    	if (processor.hasUnnamedParameters(originalSql) == true) {
    		throw new IllegalArgumentException(ERROR_FOUND_UNNAMED_PARAMETER);
    	}
    }
    
}
