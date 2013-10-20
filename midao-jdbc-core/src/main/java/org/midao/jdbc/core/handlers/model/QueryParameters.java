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

package org.midao.jdbc.core.handlers.model;

import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.utils.InputUtils;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.utils.AssertUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Class which is responsible for values transferring in and out from QueryRunner.
 * Currently is used as parameters for query and storing query execution output.
 *
 * Some of the functions might be moved into QueryParametersUtils to make this close clean model.
 *
 * By default Key is case insensitive. If needed to make instance case sensitive please use {@link #setCaseSensitive(boolean)}
 */
public class QueryParameters {

    /**
     * Parameter Direction enumeration.
     */
	public enum Direction {
		IN, OUT, INOUT, RETURN
	}
	
	private static final Integer DEFAULT_TYPE = MjdbcTypes.OTHER;

	private static final String QUERY_PARAMS_RETURN = "__return$$";
	
	private static final String ERROR_INCORRECT_LENGTH = "Error! Incorrect length of updated values";
	private static final String ERROR_ORDER_NOT_INIT = "Error! Order is not initialized properly";
	
	private final Map<String, Object> values = new HashMap<String, Object>();
	private final Map<String, Integer> types = new HashMap<String, Integer>();
	private final Map<String, Direction> direction = new HashMap<String, Direction>();
    // one parameter can be used few times, but it will share same value and direction
    private final List<String> order = new ArrayList<String>();

	private boolean isCaseSensitive = false;

    /**
     * Creates new QueryParameters instance
     */
    public QueryParameters() {
    }

    /**
     * Creates new QueryParameters instance and fills it with values from Map
     * Only values would be imported. Direction/Position/Type would be filled with default values
     *
     * @param map source of values for new QueryParameters
     */
	public QueryParameters(Map<String, Object> map) {
		if (map != null) {
			this.importValues(map);
		}
	}

    /**
     * Creates new QueryParameters instance and fills it with values from Bean
     * Only values would be imported. Direction/Position/Type would be filled with default values
     *
     * @param clazz Bean object description class
     * @param bean source of values for new QueryParameters
     */
	public QueryParameters(Class<?> clazz, Object bean) {
		Map<String, Object> beanPropertiesMap = null;
		
		if (bean != null) {
			PropertyDescriptor[] props = MappingUtils.propertyDescriptors(bean.getClass());

			beanPropertiesMap = MappingUtils.toMap(bean, props);
		
			this.importValues(beanPropertiesMap);
		}
	}

    /**
     * Creates new QueryParameters instance and fills it with data from @parameters
     *
     * @param parameters QueryParameters object which would be "cloned"
     */
	public QueryParameters(QueryParameters parameters) {
		
		if (parameters != null) {
			for (String key : parameters.keySet()) {
				this.set(key, parameters.getValue(key), parameters.getType(key), parameters.getDirection(key));
			}

            // updating order
            String key = null;
            for (int i = 0; i < parameters.orderSize(); i++) {
                key = parameters.getNameByPosition(i);

                if (key != null) {
                    this.updatePosition(key, i);
                }
            }
		}
	}

    /**
     * Creates new QueryParameters instance and fills it with data from ProcessedInput
     * Values and position. Type and Direction would be set with default values
     *
     * @param processedInput ProcessedInput object which would be used read
     */
	public QueryParameters(ProcessedInput processedInput) {
		if (processedInput.getAmountOfParameters() > 0) {
			String parameterName = null;
            String parameterTypeName = null;
            Integer parameterType = null;
            String parameterDirectionName = null;
            Direction parameterDirection = null;

			for (int i = 0; i < processedInput.getAmountOfParameters(); i++) {
				parameterName = processedInput.getParameterName(i);
                parameterTypeName = null;
                parameterDirectionName = null;

				this.set(parameterName, processedInput.getSqlParameterValues().get(i));
				this.updatePosition(parameterName, i);

                try {
                    parameterTypeName = processedInput.getSqlParameterTypes().get(i);
                    if (parameterTypeName != null) {
                        parameterType = (Integer) MappingUtils.returnStaticField(MjdbcTypes.class, parameterTypeName);
                        this.updateType(parameterName, parameterType);
                    }
                } catch (MjdbcException ex) {
                    throw new MjdbcRuntimeException("Could not set type: " + parameterTypeName, ex);
                }

                try {
                    parameterDirectionName = processedInput.getSqlParameterDirections().get(i);
                    if (parameterDirectionName != null) {
                        parameterDirection = Direction.valueOf(parameterDirectionName);
                        this.updateDirection(parameterName, parameterDirection);
                    }
                } catch (Exception ex) {
                    throw new MjdbcRuntimeException("Could not set direction: " + parameterDirection, ex);
                }
            }
		}
	}

    /**
     * Creates new QueryParameters instance and fills it with data from @params
     * Only values would be set. Direction/Position/Type would be filled with default values
     *
     * @param params "array" of values which would be used to fill new Instance
     */
	public QueryParameters(Object... params) {
		String parameterName = null;
		
		if ( (params == null) || (params.length == 1 && params[0] == null) ) {
			// We've got QueryParameters(null). Treating it as QueryParameters()
		} else {
			for (int i = 0; i < params.length; i++) {
				parameterName = new Integer(i).toString();

				this.set(parameterName, params[i]);
				this.updatePosition(parameterName, i);
			}
		}
	}

    /**
     * Imports values from Map. If such key already exists - value would be rewritten and Type/Position/Direction
     * would be reset to default value.
     *
     * @param map Map which would be imported
     */
    public void importValues(Map<String, Object> map) {
        for (String key : map.keySet()) {
            this.set(key, map.get(key));
        }
    }

    /**
     * Setter function of QueryParameters
     *
     * @param key Key
     * @param value Value
     * @param type SQL Type
     * @param direction Direction (used for Stored Procedures calls)
     * @param position Position of parameter in Query
     * @return this instance of QueryRunner
     */
	public QueryParameters set(String key, Object value, Integer type, Direction direction, Integer position) {
		this.updateValue(key, value);
		this.updateType(key, type);
		this.updateDirection(key, direction);
		this.updatePosition(key, position);
		
		return this;
	}

    /**
     * Setter function of QueryParameters
     *
     * @param key Key
     * @param value Value
     * @param type SQL Type
     * @param direction Direction (used for Stored Procedures calls)
     * @return this instance of QueryRunner
     */
	public QueryParameters set(String key, Object value, Integer type, Direction direction) {
		return this.set(key, value, type, direction, this.orderSize());
	}

    /**
     * Setter function of QueryParameters
     *
     * @param key Key
     * @param value Value
     * @param direction Direction (used for Stored Procedures calls)
     * @return this instance of QueryRunner
     */
	public QueryParameters set(String key, Object value, Direction direction) {
		return this.set(key, value, DEFAULT_TYPE, direction);
	}

    /**
     * Setter function of QueryParameters
     *
     * @param key Key
     * @param value Value
     * @param type SQL Type
     * @return this instance of QueryRunner
     */
	public QueryParameters set(String key, Object value, Integer type) {
		return this.set(key, value, type, Direction.IN);
	}

    /**
     * Setter function of QueryParameters
     *
     * @param key Key
     * @param value Value
     * @return this instance of QueryRunner
     */
	public QueryParameters set(String key, Object value) {
		return this.set(key, value, DEFAULT_TYPE, Direction.IN);
	}

    /**
     * Useful in cases if QueryParameter was constructed from Bean and we need to save class name
     *
     * @param className Class name
     * @return this instance of QueryParameters
     */
	public QueryParameters setClassName(String className) {
		InputUtils.setClassName(this.values, className);
		
		return this;
	}

    /**
     * Updates type of specified key
     *
     * @param key Key
     * @param type SQL Type
     * @return this instance of QueryParameters
     */
	public QueryParameters updateType(String key, Integer type) {
		this.types.put(processKey(key), type);
		
		return this;
	}

    /**
     * Updates direction of specified key
     *
     * @param key Key
     * @param direction Direction
     * @return this instance of QueryParameters
     */
	public QueryParameters updateDirection(String key, Direction direction) {
		this.direction.put(processKey(key), direction);
		
		return this;
	}

    /**
     * Updates position of specified key
     *
     * @param key Key
     * @param position Position
     * @return this instance of QueryParameters
     */
	public QueryParameters updatePosition(String key, Integer position) {
        while (this.order.size() < position + 1) {
            this.order.add(null);
        }

        this.order.set(position, processKey(key));

        return this;
	}

    /**
     * Updates value of specified key
     *
     * @param key Key
     * @param value Value
     * @return this instance of QueryParameters
     */
    public QueryParameters updateValue(String key, Object value) {
        this.values.put(processKey(key), value);

        return this;
    }

    /**
     * Returns position of specified key
     *
     * @param key Key
     * @return this instance of QueryParameters
     */
	public Integer getFirstPosition(String key) {
        int position = -1;
        String processedKey = processKey(key);

        if (this.values.containsKey(processedKey) == true) {
            position = this.order.indexOf(processedKey);
        }
		return position;
	}

    /**
     * Returns list of positions of specified key
     *
     * @param key Key
     * @return this instance of QueryParameters
     */
    public List<Integer> getOrderList(String key) {
        List<Integer> result = new ArrayList<Integer>();
        String processedKey = processKey(key);
        String orderKey = null;

        for (int i = 0; i < this.order.size(); i++) {
            orderKey = this.order.get(i);

            if (orderKey != null && orderKey.equals(processedKey) == true) {
                result.add(i);
            }
        }

        return result;
    }

    public boolean usedOnce(String key) {
        boolean result = false;
        String processedKey = processKey(key);

        if (this.order.contains(processedKey) == true) {
            if (this.order.indexOf(processedKey) == this.order.lastIndexOf(processedKey)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Returns direction of specified key
     *
     * @param key Key
     * @return this instance of QueryParameters
     */
	public Direction getDirection(String key) {
		return this.direction.get(processKey(key));
	}

    /**
     * Returns type of specified key
     *
     * @param key Key
     * @return this instance of QueryParameters
     */
	public Integer getType(String key) {
		return this.types.get(processKey(key));
	}

    /**
     * Returns value of specified key
     *
     * @param key Key
     * @return this instance of QueryParameters
     */
	public Object getValue(String key) {
		return this.values.get(processKey(key));
	}

    /**
     * Returns values converted to Map
     *
     * @return Map of values
     */
	public Map<String, Object> toMap() {
		return new HashMap<String, Object>(this.values);
	}

    /**
     * Returns key set
     *
     * @return key set
     */
	public Set<String> keySet() {
		return this.values.keySet();
	}

    /**
     * Checks is specified key is OUT parameter.
     * OUT and INOUT parameter would be considered as OUT parameter
     *
     * @param key Key
     * @return true - if key is OUT parameter
     */
	public boolean isOutParameter(String key) {
		boolean isOut = false;
		
		if (this.getDirection(processKey(key)) == Direction.INOUT || this.getDirection(processKey(key)) == Direction.OUT) {
			isOut = true;
		}
		
		return isOut;
	}

    /**
     * Checks is specified key is IN parameter.
     * IN and INOUT parameter would be considered as IN parameter
     *
     * @param key Key
     * @return true - if key is IN parameter
     */
	public boolean isInParameter(String key) {
		boolean isIn = false;
		
		if (this.getDirection(processKey(key)) == Direction.INOUT || this.getDirection(processKey(key)) == Direction.IN) {
			isIn = true;
		}
		
		return isIn;
	}

    /**
     * Returns Key by searching key assigned to that position
     *
     * @param position Position which would be searched
     * @return Key
     */
	public String getNameByPosition(Integer position) {
		String name = null;

        name = this.order.get(position);

		return name;
	}

    /**
     * Returns value by searching key assigned to that position
     *
     * @param position Position which would be searched
     * @return Value
     * @throws NoSuchFieldException
     */
	public Object getValueByPosition(Integer position) throws NoSuchFieldException {
		String name = null;
		Object value = null;
		
		name = this.getNameByPosition(position);
		
		if (name != null) {
			value = this.getValue(name);
		} else {
			throw new NoSuchFieldException();
		}
		
		return value;
	}

    /**
     * Checks if this instance of QueryParameters contains specified Key.
     * Check is performed for Key/Value pair only as it considered only source of keys
     *
     * @param key Key
     * @return true if key is present in this instance
     */
	public boolean containsKey(String key) {
		return this.values.containsKey(processKey(key));
	}

    /**
     * Removes specified key
     *
     * @param key Key which would be removed
     */
    public void remove(String key) {
        String processedKey = processKey(key);
        String orderKey = null;

        // removing key set for all positions
        for (int i = 0; i < this.order.size(); i++) {
            orderKey = this.order.get(i);

            if (orderKey != null && orderKey.equals(processedKey) == true) {
                this.order.remove(i);
            }
        }

        this.types.remove(processedKey);
        this.direction.remove(processedKey);
        this.values.remove(processedKey);
    }

    /**
     * Resets current order configuration
     */
    public void clearOrder() {
        this.order.clear();
    }

    /**
     * Returns amount of elements(values) set into this QueryParameter instance
     * Used, by default, to get/set values/types/directions
     *
     * @return amount of elements(values)
     */
	public int size() {
		return this.values.size();
	}

    /**
     * Returns amount of elements(values) assigned with position/order
     * Used, by default, internally to identify order size (can be different than key size)
     *
     * @return amount of elements in order
     */
    public int orderSize() {
        return this.order.size();
    }

    /**
     * Turns on/off case sensitivity for Keys
     *
     * @param newValue new value
     */
    public void setCaseSensitive(boolean newValue) {
        this.isCaseSensitive = newValue;
    }

    /**
     * Returns if current instance has case sensitivity set for Keys
     *
     * @return true - if current instance is case sensitive
     */
    public boolean isCaseSensitive() {
        return this.isCaseSensitive;
    }

    /**
     * Utility function.
     * Allows storing (processed) Query output
     *
     * @param queryOutput Query output
     */
    public void setReturn(Object queryOutput) {
        this.set(QUERY_PARAMS_RETURN, queryOutput);
    }

    /**
     * Utility function.
     * Returns previously stored (processed) Query output
     *
     * @return Query output
     */
    public Object getReturn() {
        Object result = null;

        if (this.containsKey(QUERY_PARAMS_RETURN) == true) {
            result = this.getValue(QUERY_PARAMS_RETURN);
        }

        return result;
    }

    /**
     * Utility function.
     * Allows removing previously set (processed) Query output
     */
    public void removeReturn() {
        if (this.containsKey(QUERY_PARAMS_RETURN) == true) {
            this.remove(QUERY_PARAMS_RETURN);
        }
    }

    /**
     * Utility function.
     * Updates this instance values from array @newValues
     * Allows updating only fields which are OUT/INOUT parameters
     *
     * @param newValues array of updated values
     * @param updateOutOnly check if update OUT/INOUT parameters only
     */
	public void update(Object[] newValues, boolean updateOutOnly) {
		AssertUtils.assertNotNull(newValues);
		
		if (newValues.length != this.values.size()) {
			throw new IllegalArgumentException(ERROR_INCORRECT_LENGTH);
		}
		
		this.assertIncorrectOrder();
		
		String parameterName = null;
		for (int i = 0; i < newValues.length; i++) {
            parameterName = this.getNameByPosition(i);
			
			if (updateOutOnly == false || isOutParameter(parameterName) == true) {
				parameterName = this.getNameByPosition(i);
			
				this.updateValue(parameterName, newValues[i]);
			}
		}
	}

    /**
     * Utility function.
     * Updates this instance of Query Parameters with values and positions from
     * ProcessedInput.
     *
     * This instance of QueryParameters should have at least all keys present in Processed Input. If key is present
     * in ProcessedInput but is not present in this instance of QueryParameters - it will be added but ignored. This
     * issue might be resolved with using {@link #updateValue(String, Object)}
     *
     * All keys which are present in this instance but not present in Processed Input - would be removed.
     *
     * @param processedInput Processed Input which would be used to update this instance
     */
    public void updateAndClean(ProcessedInput processedInput) {
        // stores list of processed parameter names. all other names should be removed
        List<String> processedParameters = new ArrayList<String>();

        if (processedInput.getAmountOfParameters() > 0) {
            String parameterName = null;

            for (int i = 0; i < processedInput.getAmountOfParameters(); i++) {
                parameterName = processedInput.getParameterName(i);
                processedParameters.add(parameterName);

                this.updateValue(parameterName, processedInput.getSqlParameterValues().get(i));
                this.updatePosition(parameterName, i);
            }
        }
        
        List<String> removeKeyList = new ArrayList<String>();

        for (String key : this.keySet()) {
            if (processedParameters.contains(key) == false) {
                removeKeyList.add(key);
            }
        }
        
        for (String key : removeKeyList) {
        	this.remove(key);
        }
    }

    /**
     * Utility function.
     * Returns array of values
     *
     * @return array of values
     */
	public Object[] getValuesArray() {
		this.assertIncorrectOrder();
		
		Object[] params = new Object[this.order.size()];
		
		String parameterName = null;
		for (int i = 0; i < this.order.size(); i++) {
			parameterName = this.getNameByPosition(i);
			
			params[i] = this.getValue(parameterName); 
		}
		
		return params;
	}

    /**
     * Utility function.
     * Is used to check if Order(position for all keys) was set
     *
     * @return true if order is defined for all keys
     */
	public boolean isOrderSet() {
		boolean result = true;
		
		if (this.values.size() != this.order.size()) {
			result = false;
		}
		
		return result;
	}

    /**
     * Utility function.
     * Is used to check if Order(position for all keys) was set
     * Uses {@link #isOrderSet()}
     *
     * Throws exception if order is incorrect
     */
	public void assertIncorrectOrder() {
		if (this.isOrderSet() == false) {
			throw new IllegalArgumentException(ERROR_ORDER_NOT_INIT);
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        QueryParameters params = this;

        result.append(params.getClass().getSimpleName());
        result.append(params.isCaseSensitive() == true ? " CS " : " CI ");
        result.append("{");

        if (params.values != null) {
            for (String parameterName : params.values.keySet()) {
                result.append(" {[" + parameterName + "]");

                if (params.types != null && params.types.containsKey(parameterName) == true) {
                    result.append(" Ty: [").append(params.types.get(parameterName) + "] ");
                }
                if (params.order != null && params.order.contains(parameterName) == true) {
                    result.append(" Or: [").append(params.order.indexOf(parameterName) + "] ");
                }
                if (params.direction != null && params.direction.containsKey(parameterName) == true) {
                    result.append(" Di: [").append(params.direction.get(parameterName) + "] ");
                }

                result.append(" Va: [").append(params.values.get(parameterName) + "]");

                result.append("}");
            }

        }

        result.append(" }");

        return result.toString();
    }

    /**
     * Utility function.
     * Processes key and converts it's case if required
     *
     * @param key Key
     * @return Processed Key
     */
    private String processKey(String key) {
        AssertUtils.assertNotNull(key, "Key cannot be null");

        String result = null;

        if (this.isCaseSensitive() == false) {
            result = key.toLowerCase(Locale.ENGLISH);
        } else {
            result = key;
        }

        return result;
    }
}
