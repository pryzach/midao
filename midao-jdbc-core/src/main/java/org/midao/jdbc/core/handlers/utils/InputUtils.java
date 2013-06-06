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

package org.midao.jdbc.core.handlers.utils;

import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.Map;

/**
 * Collection of utilities used by/for Input handlers/handling
 */
public class InputUtils {
	private static final String MAP_CLASS_NAME = "__className";
	private static final String PARAMETER_PREFIX = ":";
	
    /**
     * Defines order of @parameters based on @processedInput
     *
     * @param processedInput InputHandler processedInput
     * @param parameters Query Parameters ordering of which would be updated
     */
	public static void defineOrder(ProcessedInput processedInput, QueryParameters parameters) {
		String parameterName = null;
		
		for (int i = 0; i < processedInput.getAmountOfParameters(); i++) {
			parameterName = processedInput.getParameterName(i);
			
			if (parameterName != null) {
				parameters.updatePosition(parameterName, i);
			}
		}
	}

    /**
     * InputHandler converts every object into Map.
     * This function returns Class name of object from which this Map was created.
     *
     * @param map Map from which Class name would be returned
     * @return Class name from which Map was built
     */
    public static String getClassName(Map<String, Object> map) {
    	String className = null;
    	
    	if (map.containsKey(MAP_CLASS_NAME) == true) {
    		className = (String) map.get(MAP_CLASS_NAME);
    	}
    	
    	return className;
    }

    /**
     * InputHandler converts every object into Map.
     * Sets Class name of object from which this Map was created.
     *
     * @param map Map which would store Class name
     * @param className Class name
     */
    public static void setClassName(Map<String, Object> map, String className) {

        String processedClassName = className;

        if (processedClassName != null) {
            // it is highly unlikely that class name would contain ".", but we are cleaning it just in case
            processedClassName = processedClassName.replaceAll("\\.", "_");
        }

    	map.put(MAP_CLASS_NAME, processedClassName);
    }

    /**
     * Checks if this Map key is Map Class Name
     *
     * @param key Key which would be checked
     * @return true - if key equals to Map Class Name key
     */
    public static boolean isClassNameKey(String key) {
    	boolean equals = false;
    	
    	if (MAP_CLASS_NAME.equals(key) == true) {
    		equals = true;
    	}
    	
    	return equals;
    }

    /**
     * Used by InputHandlers.
     * Allows combining of few Maps into one Map.
     * In order to avoid "collisions" - Map Class name is user as prefix
     *
     * @param className Map Class name
     * @param key Map key
     * @return unique key
     */
    public static String addClassName(String className, String key) {
    	return className.toLowerCase() + "." + key.toLowerCase();
    }

    /**
     * Used by InputHandlers.
     * Removes prefix(class name) from combined key created by @addClassName.
     *
     * @param classNameKey combined key(class name + key)
     * @return key without class name
     */
    public static String removeClassName(String classNameKey) {
    	String[] splitKey = classNameKey.split("[.]");
    	String resultString = "";
    	
    	for (int i = 1; i < splitKey.length; i++) {
            resultString = resultString.concat(splitKey[i]);
    		
    		if ((i + 1) < splitKey.length) {
                resultString = resultString.concat(".");
    		}
    	}
    	
    	if (resultString.length() == 0) {
    		resultString = classNameKey;
    	}
    	
    	return resultString;
    }

    /**
     * Used in InputHandlers.
     * Returns Parameter Prefix Constant
     *
     * @return Parameter Prefix Constant
     */
    public static String getParameterPrefix() {
    	return PARAMETER_PREFIX;
    }

    /**
     * Used in InputHandlers.
     * Used to process key and add parameter prefix.
     *
     * @param str key which would be processed
     * @return key with parameter prefix
     */
    public static String addParameterPrefix(String str) {
    	return getParameterPrefix() + str;
    }

    /**
     * Used in InputHandlers.
     * Used to process key and remove parameter prefix.
     *
     * @param str key which would be processed
     * @return key without parameter prefix
     */
    public static String removeParameterPrefix(String str) {
    	return str.substring(getParameterPrefix().length());
    }
}
