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
import org.midao.jdbc.core.processor.QueryOutputProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parent class used to store query result into Map.
 * Column which would be used as Map key is specified via Constructor
 * Value can be Bean or Map.
 */
public abstract class AbstractKeyedOutputHandler<K, V> extends AbstractOutputHandler<Map<K, V>> {

    /**
     * Index of the column which would be used as Key
     */
    protected final int columnIndex;

    /**
     * Name of the column which would be used as Key
     */
    protected final String columnName;

    /**
     * Initializes AbstractKeyedOutputHandler ancestor instance
     *
     * @param columnIndex column index
     * @param columnName column name (priority over column index)
     */
    public AbstractKeyedOutputHandler(int columnIndex, String columnName) {
		super();
		this.columnIndex = columnIndex;
		this.columnName = columnName;
	}

    /**
     * Initializes AbstractKeyedOutputHandler ancestor instance
     *
     * @param outputProcessor Query output processor
     * @param columnIndex column index
     * @param columnName column name (priority over column index)
     */
	public AbstractKeyedOutputHandler(QueryOutputProcessor outputProcessor, int columnIndex, String columnName) {
		super(outputProcessor);
		this.columnIndex = columnIndex;
		this.columnName = columnName;
	}


    /**
     * Reads query result and converts it into Map of Bean/Map
     * Values from column, index/name of which was specified via Constructor, would be used as key
     *
     * @param outputList Query output
     * @return Map of Bean/Map
     * @throws MidaoException
     */
	public Map<K, V> handle(List<QueryParameters> outputList) throws MidaoException {
    	QueryParameters params = null;
    	Map<K, V> result = new HashMap<K, V>();
    	
		for (int i = 1; i < outputList.size(); i++) {
			params = outputList.get(i);
			
			result.put(this.createKey(params), (V) this.createRow(params));
		}
		return result;
	}

    /**
     * Generates key for query output row
     *
     * @param params query output row
     * @return key for query output row
     * @throws MidaoException
     */
    @SuppressWarnings("unchecked")
    protected K createKey(QueryParameters params) throws MidaoException {
        K result = null;

        if (columnName == null) {
            result = (K) params.getValue(params.getNameByPosition(columnIndex));
        } else {
            result = (K) params.getValue(columnName);
        }

        return result;
    }

    /**
     * Converts query output row into internal Java type
     *
     * @param params query output row
     * @return converted row
     * @throws MidaoException
     */
    protected abstract V createRow(QueryParameters params) throws MidaoException;

}
