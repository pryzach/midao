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

package org.midao.jdbc.core.handlers.output;

import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts specified column of query output into List
 */
public class ColumnListOutputHandler<T> extends AbstractOutputListHandler<T> {

    /**
     * Index of the column which would be used as Key
     */
    private final int columnIndex;

    /**
     * Name of the column which would be used as Key
     */
    private final String columnName;

    /**
     * Creates new ColumnListOutputHandler instance.
     */
    public ColumnListOutputHandler() {
        this(0, null);
    }

    /**
     * Creates new ColumnListOutputHandler instance.
     *
     * @param columnIndex Index of the column which would be converted into List
     */
    public ColumnListOutputHandler(int columnIndex) {
        this(columnIndex, null);
    }

    /**
     * Creates new ColumnListOutputHandler instance.
     *
     * @param columnName Name of the column which would be converted into List
     */
    public ColumnListOutputHandler(String columnName) {
        this(0, columnName);
    }

    /**
     * Creates new ColumnListOutputHandler instance.
     *
     * @param columnIndex Index of the column which would be converted into List. Used only if @columnName is null
     * @param columnName Name of the column which would be used as Key for result Map
     */
    private ColumnListOutputHandler(int columnIndex, String columnName) {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * Converts specified (via constructor) column into List
     *
     * @param outputList Query output
     * @return Query output column as List
     * @throws MidaoException
     */
	public List<T> handle(List<QueryParameters> outputList) throws MidaoException {
		List<T> result = new ArrayList<T>();
		String parameterName = null;
		Object parameterValue = null;
		
		for (int i = 1; i < outputList.size(); i++) {
			if (this.columnName == null) {
				parameterName = outputList.get(i).getNameByPosition(this.columnIndex);
				parameterValue = outputList.get(i).getValue(parameterName);
				
				result.add((T) parameterValue);
			} else {
				parameterValue = outputList.get(i).getValue(this.columnName);
				
				result.add((T) parameterValue);
			}
			
		}
		
		return result;
	}

}
