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

package org.midao.core.handlers.output;

import org.midao.core.handlers.model.QueryParameters;

import java.util.List;

/**
 * Reads specified column of first row of Query output
 */
public class ScalarOutputHandler<T> extends AbstractOutputHandler<T> {

    /**
     * Index of the column which would be used as Key
     */
    private final int columnIndex;

    /**
     * Name of the column which would be used as Key
     */
    private final String columnName;

    /**
     * Creates new ScalarOutputHandler instance.
     */
    public ScalarOutputHandler() {
        this(0, null);
    }

    /**
     * Creates new ScalarOutputHandler instance.
     *
     * @param columnIndex Index of the column which would be returned
     */
    public ScalarOutputHandler(int columnIndex) {
        this(columnIndex, null);
    }

    /**
     * Creates new ScalarOutputHandler instance.
     *
     * @param columnName Name of the column which would be returned
     */
    public ScalarOutputHandler(String columnName) {
        this(0, columnName);
    }

    /**
     * Creates new ScalarOutputHandler instance.
     *
     * @param columnIndex Index of the column which would be returned. Used only if @columnName is null
     * @param columnName Name of the column which would be returned
     */
    private ScalarOutputHandler(int columnIndex, String columnName) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * Reads specified column of first row of Query output
     *
     * @param output Query output
     * @return Value returned from specified (via constructor) column of first row of query output
     */
	public T handle(List<QueryParameters> output) {
		T result = null;
		String parameterName = null;
		Object parameterValue = null;
		
		if (output.size() > 1) {
			if (this.columnName == null) {
				parameterName = output.get(1).getNameByPosition(columnIndex);
				parameterValue = output.get(1).getValue(parameterName);
				
				result = (T) parameterValue;
			} else {
				parameterValue = output.get(1).getValue(this.columnName);
				
				result = (T) parameterValue;
			}
		}
		
		return result;
	}

}

