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

import org.midao.jdbc.core.MjdbcConstants;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class is used by InputHandlers to store processed input SQL parameters
 *
 * Some of the functions might be moved into ProcessedInputUtils to make this close clean model.
 */
public class ProcessedInput {
    private static List<String> emptyList = new ArrayList<String>();

	private final String originalSql;
	private String parsedSql;
	private List<String> sqlParameterNames;
	private List<int[]> sqlParameterBoundaries;
    private List<String> sqlParameterTypes;
    private List<String> sqlParameterDirections;
	
	private List<Object> sqlParameterValues;

    /**
     * Creates new QueryParameters instance
     *
     * @param originalSql original (unprocessed) SQL string
     */
	public ProcessedInput(String originalSql) {
		this.originalSql = originalSql;
		this.parsedSql = null;
        this.sqlParameterNames = new ArrayList<String>();
        this.sqlParameterBoundaries = new ArrayList<int[]>();
        this.sqlParameterValues = new ArrayList<Object>();
        this.sqlParameterTypes = new ArrayList<String>();
        this.sqlParameterDirections = new ArrayList<String>();
	}

    /**
     * Creates new QueryParameters instance and loads values from ProcessedInput
     *
     * @param processedInput Processed Input which would be "cloned"
     */
	public ProcessedInput(ProcessedInput processedInput) {
		this.originalSql = processedInput.getOriginalSql();
		this.parsedSql = processedInput.getParsedSql();
		
		if (processedInput.getSqlParameterNames() != null) {
			this.sqlParameterNames = new ArrayList<String>(processedInput.getSqlParameterNames());
		} else {
			this.sqlParameterNames = null;
		}

		if (processedInput.getSqlParameterBoundaries() != null) {
			this.sqlParameterBoundaries = new ArrayList<int[]>(processedInput.getSqlParameterBoundaries());
		} else {
			this.sqlParameterBoundaries = null;
		}
		
		if (processedInput.getSqlParameterValues() != null) {
			this.sqlParameterValues = new ArrayList<Object>(processedInput.getSqlParameterValues());
		} else {
			this.sqlParameterValues = null;
		}

        if (processedInput.getSqlParameterTypes() != null) {
            this.sqlParameterTypes = new ArrayList<String>(processedInput.getSqlParameterTypes());
        } else {
            this.sqlParameterTypes = null;
        }

        if (processedInput.getSqlParameterDirections() != null) {
            this.sqlParameterDirections = new ArrayList<String>(processedInput.getSqlParameterDirections());
        } else {
            this.sqlParameterDirections = null;
        }
	}

    /**
     * Creates new QueryParameters instance
     *
     * @param originalSql original (unprocessed) SQL string
     * @param parsedSql cleaned (processed) SQL string
     * @param sqlParameterNames list of parameters in original SQL string
     * @param sqlParameterBoundaries list of parameter boundaries in original SQL string
     * @param sqlParameterValues list of parameters values
     * @param sqlParameterTypes list of parameters types
     * @param sqlParameterDirections list of parameters directions
     */
    public ProcessedInput(String originalSql, String parsedSql, List<String> sqlParameterNames, List<int[]> sqlParameterBoundaries, List<Object> sqlParameterValues, List<String> sqlParameterTypes, List<String> sqlParameterDirections) {
        this.originalSql = originalSql;
        this.parsedSql = parsedSql;
        this.sqlParameterNames = new ArrayList<String>(sqlParameterNames);
        this.sqlParameterBoundaries = new ArrayList<int[]>(sqlParameterBoundaries);
        this.sqlParameterValues = new ArrayList<Object>(sqlParameterValues);
        this.sqlParameterTypes = new ArrayList<String>(sqlParameterTypes);
        this.sqlParameterDirections = new ArrayList<String>(sqlParameterDirections);
    }

    /**
     * Creates new QueryParameters instance
     *
     * @param originalSql original (unprocessed) SQL string
     * @param parsedSql cleaned (processed) SQL string
     * @param sqlParameterNames list of parameters in original SQL string
     * @param sqlParameterBoundaries list of parameter boundaries in original SQL string
     * @param sqlParameterValues list of parameter values
     */
    public ProcessedInput(String originalSql, String parsedSql, List<String> sqlParameterNames, List<int[]> sqlParameterBoundaries, List<Object> sqlParameterValues) {
        this(originalSql, parsedSql, sqlParameterNames, sqlParameterBoundaries, sqlParameterValues, MjdbcConstants.EMPTY_STRING_LIST, MjdbcConstants.EMPTY_STRING_LIST);
    }

    /**
     * Adds parameter into list of input SQL parameters
     *
     * @param parameterName parameter name
     * @param parameterStart character position at which parameter starts
     * @param parameterEnd character position at which parameter ends
     * @param parameterType parameter type
     * @param parameterDirection parameter direction
     */
    public void addParameter(String parameterName, int parameterStart, int parameterEnd, String parameterType, String parameterDirection) {
        if (this.sqlParameterNames == null) {
            this.sqlParameterNames = new ArrayList<String>();
            this.sqlParameterBoundaries = new ArrayList<int[]>();
        }

        this.sqlParameterNames.add(parameterName);
        this.sqlParameterBoundaries.add(new int[]{parameterStart, parameterEnd});
        this.sqlParameterTypes.add(parameterType);
        this.sqlParameterDirections.add(parameterDirection);
    }

    /**
     * Adds parameter into list of input SQL parameters
     *
     * @param parameterName Parameter name
     * @param parameterStart Character position at which parameter starts
     * @param parameterEnd Character position at which parameter ends
     */
	public void addParameter(String parameterName, int parameterStart, int parameterEnd) {
        addParameter(parameterName, parameterStart, parameterEnd, null, null);
	}

    /**
     * @return original (unprocessed) SQL
     */
	public String getOriginalSql() {
		return originalSql;
	}

    /**
     * @return parsed SQL
     */
	public String getParsedSql() {
		return parsedSql;
	}

    /**
     * @return list of parameter names
     */
	public List<String> getSqlParameterNames() {
		return sqlParameterNames;
	}

    /**
     * @return list of parameter boundaries (in original SQL string)
     */
	public List<int[]> getSqlParameterBoundaries() {
		return sqlParameterBoundaries;
	}

    /**
     * @return list of parameters value
     */
	public List<Object> getSqlParameterValues() {
		return sqlParameterValues;
	}

    /**
     * @return list of parameters type
     */
    public List<String> getSqlParameterTypes() {
        return sqlParameterTypes;
    }

    /**
     * @return list of parameters direction
     */
    public List<String> getSqlParameterDirections() {
        return sqlParameterDirections;
    }

    /**
     * Sets parsed SQL
     *
     * @param parsedSql parsed SQL
     */
	public void setParsedSql(String parsedSql) {
		this.parsedSql = parsedSql;
	}

    /**
     * Sets list of parameter values
     * @param sqlParameterValues list of parameter values
     */
	public void setSqlParameterValues(List<Object> sqlParameterValues) {
        if (sqlParameterValues != null) {
		    this.sqlParameterValues = new ArrayList<Object> (sqlParameterValues);
        } else {
            this.sqlParameterValues = null;
        }
	}

    /**
     * Returns position(order) of specified parameter name
     *
     * @param parameterName parameter name which would be searched
     * @return position of parameter, null if it wasn't found in list of parameter names
     */
	public Integer getPosition(String parameterName) {
		Integer position = null;
		
		for (int i = 0; i < this.sqlParameterNames.size(); i++) {
			if (this.sqlParameterNames.get(i).equals(parameterName) == true) {
				position = i;
				break;
			}
		}
		
		return position;
	}

    /**
     * Returns parameter name by specifying it's position
     *
     * @param position position of parameter
     * @return name of parameter, null if list of names is empty
     */
	public String getParameterName(Integer position) {
		String name = null;
		
		if (this.sqlParameterNames != null) {
			name = this.sqlParameterNames.get(position);
		}
		
		return name;
	}

    /**
     * Returns amount of parameters specified in this instance of ProcessedInput
     *
     * @return amount of parameters
     */
	public Integer getAmountOfParameters() {
		Integer size = 0;
		
		if (this.sqlParameterNames != null) {
			size = this.sqlParameterNames.size();
		}
		return size;
	}

    /**
     * Utility function
     * Checks is original SQL was parsed and parameter names/values are set
     *
     * @return true - if this instance of Processed Input is filled
     */
    public boolean isFilled() {
        boolean isFilled = false;

        if (this.originalSql != null && this.originalSql.length() > 0) {
            if (this.parsedSql != null && this.parsedSql.length() > 0) {
                if (this.sqlParameterNames != null) {
                    if (this.sqlParameterValues != null) {
                        isFilled = true;
                    }
                }
            }
        }

        return isFilled;
    }

    /**
     * Utility function.
     * Fills this ProcessedInput with values.
     * This function iterates over parameter names list and loads corresponding value from MAp
     *
     * @param valuesMap Map of values which would be loaded
     */
    public void fillParameterValues(Map<String, Object> valuesMap) {
        AssertUtils.assertNotNull(valuesMap, "Value map cannot be null");

        if (this.sqlParameterNames != null) {

            String parameterName = null;
            this.sqlParameterValues = new ArrayList<Object>();

            // using for instead of iterator because this way I control position
            // I am getting.
            for (int i = 0; i < this.sqlParameterNames.size(); i++) {
                parameterName = this.sqlParameterNames.get(i);

                if (valuesMap.containsKey(parameterName) == true) {
                    this.sqlParameterValues.add(valuesMap.get(parameterName));
                } else {
                    throw new IllegalArgumentException(String.format(HandlersConstants.ERROR_PARAMETER_NOT_FOUND, parameterName));
                }
            }
        }
    }
	
}
