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

package org.midao.core.processor;

import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.handlers.utils.MappingUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Default output processor used by OutputHandlers
 */
public class BasicQueryOutputProcessor implements QueryOutputProcessor {
	
    /**
     * If no corresponding bean property was found - this value is assigned.
     * Used in mapColumnsToProperties.
     */
    protected static final int PROPERTY_NOT_FOUND = -1;
    
    /**
     * If value from DB returned is null - it will be replaced
     * with default value(for specified types) from this map.
     */
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

    /**
     * Allows mapping columns from DB into Class.
     * Used in cases when column/parameter name returned from DB differs from Class property name.
     */
    private final Map<String, String> columnToPropertyOverrides;

    static {
        primitiveDefaults.put(Integer.TYPE, Integer.valueOf(0));
        primitiveDefaults.put(Short.TYPE, Short.valueOf((short) 0));
        primitiveDefaults.put(Byte.TYPE, Byte.valueOf((byte) 0));
        primitiveDefaults.put(Float.TYPE, Float.valueOf(0f));
        primitiveDefaults.put(Double.TYPE, Double.valueOf(0d));
        primitiveDefaults.put(Long.TYPE, Long.valueOf(0L));
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Character.TYPE, Character.valueOf((char) 0));
    }

    /**
     * Creates new BasicQueryOutputProcessor instance
     */
    public BasicQueryOutputProcessor() {
        this(new HashMap<String, String>());
    }

    /**
     * Creates new BasicQueryOutputProcessor instance
     *
     * @param columnToPropertyOverrides by default columns from SQL query are put into Map/Bean according to it's name.
     *                                  By specifying overrides - processor would use them to map fields accordingly.
     */
    public BasicQueryOutputProcessor(Map<String, String> columnToPropertyOverrides) {
        super();
        if (columnToPropertyOverrides == null) {
            throw new IllegalArgumentException("columnToPropertyOverrides map cannot be null");
        }
        this.columnToPropertyOverrides = columnToPropertyOverrides;
    }

    /**
     * {@inheritDoc}
     */
	public Object[] toArray(List<QueryParameters> paramsList) {
		Object[] result = new Object[0];
		QueryParameters singleParam = null;
		String parameterName = null;
		
		if (paramsList.size() > 1) {
			
			singleParam = paramsList.get(1);
			
			result = new Object[singleParam.size()];
			
			for (int i = 0; i < singleParam.size(); i++) {
				parameterName = singleParam.getNameByPosition(i);
				
				result[i] = singleParam.getValue(parameterName);
			}
		}

		return result;
	}

    /**
     * {@inheritDoc}
     */
	public List<Object[]> toArrayList(List<QueryParameters> paramsList) {
		List<Object[]> result = new ArrayList<Object[]>();
		QueryParameters singleParam = null;
		Object[] singleResult = null;
		String parameterName = null;
		
		for (int i = 1; i < paramsList.size(); i++) {
			singleParam = paramsList.get(i);
			
			singleResult = new Object[singleParam.size()];
			
			for (int j = 0; j < singleParam.size(); j++) {
				parameterName = singleParam.getNameByPosition(j);
				
				singleResult[j] = singleParam.getValue(parameterName);
			}
			
			result.add(singleResult);
		}
		
		return result;
	}

    /**
     * {@inheritDoc}
     */
	public <T> T toBean(List<QueryParameters> paramsList, Class<T> type) throws MidaoException {
		T result = null;
		
		if (paramsList.size() > 1) {
			result = this.toBean(paramsList.get(1), type);
		}
		
		return result;
	}

    /**
     * {@inheritDoc}
     */
	public <T> T toBean(QueryParameters params, Class<T> type) throws MidaoException {
		T result = null;
		int[] columnToProperty = null;
		PropertyDescriptor[] props = MappingUtils.propertyDescriptors(type);
		
		columnToProperty = this.mapColumnsToProperties(params, props);
		result = this.createBean(params, type, props, columnToProperty);
		
		return result;
	}

    /**
     * {@inheritDoc}
     */
	public <T> List<T> toBeanList(List<QueryParameters> paramsList, Class<T> type) throws MidaoException {
		List<T> result = new ArrayList<T>();
		T bean = null;
		int[] columnToProperty = null;
		PropertyDescriptor[] props = MappingUtils.propertyDescriptors(type);
		QueryParameters singleParam = null;
		
		for (int i = 1; i < paramsList.size(); i++) {
			singleParam = paramsList.get(i);
			columnToProperty = this.mapColumnsToProperties(singleParam, props);
			bean = this.createBean(singleParam, type, props, columnToProperty);
			result.add(bean);
		}
		
		return result;
	}

    /**
     * {@inheritDoc}
     */
	public Map<String, Object> toMap(List<QueryParameters> paramsList) {
		Map<String, Object> result = null;
		
		if (paramsList.size() > 1) {
			result = this.toMap(paramsList.get(1));
		} else {
			result = new HashMap<String, Object>();
		}

		return result;
	}

    /**
     * {@inheritDoc}
     */
	public Map<String, Object> toMap(QueryParameters params) {
		return params.toMap();
	}

    /**
     * {@inheritDoc}
     */
	public List<Map<String, Object>> toMapList(List<QueryParameters> paramsList) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		if (paramsList.size() > 1) {
			for (int i = 1; i < paramsList.size(); i++) {
				result.add(paramsList.get(i).toMap());
			}
		}

		return result;
	}

    /**
     * {@inheritDoc}
     */
    public Object processValue(QueryParameters params, Integer position, PropertyDescriptor prop) throws MidaoException {
    	String parameterName = params.getNameByPosition(position);
    	Object value = params.getValue(parameterName);
    	Class<?> propType = prop.getPropertyType();
    	Object result = null;
    	
        if ( propType.isPrimitive() != true && value == null ) {
            result = null;
        } else {
            // convert types for some popular ones
            if (value instanceof java.util.Date) {
                final String targetType = propType.getName();
                if ("java.sql.Date".equals(targetType)) {
                	result = new java.sql.Date(((java.util.Date) value).getTime());
                } else if ("java.sql.Time".equals(targetType)) {
                	result = new java.sql.Time(((java.util.Date) value).getTime());
                } else if ("java.sql.Timestamp".equals(targetType)) {
                	result = new java.sql.Timestamp(((java.util.Date) value).getTime());
                }
            } else {
            	result = value;
            }
        }

        if (this.isCompatibleType(value, propType) == false) {
          throw new MidaoException(
              "Cannot set " + prop.getName() + ": incompatible types, cannot convert "
              + value.getClass().getName() + " to " + propType.getName());
              // value cannot be null here because isCompatibleType allows null
        }    	
    	return result;
    }

    /**
     * Columns from @params are mapped to class fields @props (by name).
     * During processing @columnToPropertyOverrides is used to check overrides
     *
     * @param params Query output columns of which would be mapped to @props
     * @param props Java class property descriptor
     * @return array for every element from @params corresponding element in @props
     */
    protected int[] mapColumnsToProperties(QueryParameters params,
                                           PropertyDescriptor[] props) {

        int paramsSize = params.size();
        int[] columnToProperty = new int[paramsSize];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 0; col < paramsSize; col++) {
            String columnName = params.getNameByPosition(col);

            String propertyName = columnToPropertyOverrides.get(columnName);
            if (propertyName == null) {
                propertyName = columnName;
            }
            for (int i = 0; i < props.length; i++) {

                if (propertyName.equalsIgnoreCase(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }

    /**
     * Creates and fills Java Object with values from @params
     * Is used by toBean* functions
     *
     * @param params value which used to fill Java Object
     * @param type Java Class definition which would be instantiated
     * @param props Java Class property descriptor which would be used to set values(via setter)
     * @param columnToProperty mapping column number to Java Class property
     * @return filled Java Object
     * @throws MidaoException
     */
    private <T> T createBean(QueryParameters params, Class<T> type,
                             PropertyDescriptor[] props, int[] columnToProperty) throws MidaoException {

        T bean = MappingUtils.newInstance(type);

        for (int i = 0; i < columnToProperty.length; i++) {

            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }

            PropertyDescriptor prop = props[columnToProperty[i]];
            Class<?> propType = prop.getPropertyType();

            Object value = this.processValue(params, i, prop);

            if (propType != null && value == null && propType.isPrimitive()) {
                value = primitiveDefaults.get(propType);
            }

            MappingUtils.callSetter(bean, prop, value);
        }

        return bean;
    }

    /**
     * Checks if @value is compatible with @type
     *
     * @param value value which is checked
     * @param type Java Class compatability with which is checked
     * @return true/false
     */
    private boolean isCompatibleType(Object value, Class<?> type) {
        // Do object check first, then primitives
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;

        } else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        }
        return false;

    }
}
