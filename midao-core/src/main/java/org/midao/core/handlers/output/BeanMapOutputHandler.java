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

import org.midao.core.MidaoConfig;
import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.processor.QueryOutputProcessor;

import java.sql.SQLException;

/**
 * Converts query output into Map of Beans.
 *
 * Example:
 * Query output:
 * new Object[]{"jack", "sheriff", 36}
 * new Object[]{"henry", "mechanic", 36}
 * new Object[]{"alison", "agent", 30}
 *
 * If we will create class Character and specify first column as key we would receive:
 * Map:
 * k: jack v: Character {"jack", "sheriff", 36};
 * k: henry v: Character {"henry", mechanic, 36}
 * k: alison v: Character {"alison", agent, 30}
 */
public class BeanMapOutputHandler<K, V> extends AbstractKeyedOutputHandler<K, V> {

    /**
     * Bean Class description
     */
    private final Class<V> type;

    /**
     * Creates new BeanMapOutputHandler instance.
     *
     * @param type Bean Class description
     */
    public BeanMapOutputHandler(Class<V> type) {
        this(type, MidaoConfig.getDefaultQueryOutputProcessor(), 0, null);
    }

    /**
     * Creates new BeanMapOutputHandler instance.
     *
     * @param type Bean Class description
     * @param processor Query output processor
     */
    public BeanMapOutputHandler(Class<V> type, QueryOutputProcessor processor) {
        this(type, processor, 0, null);
    }

    /**
     * Creates new BeanMapOutputHandler instance.
     *
     * @param type Bean Class description
     * @param columnIndex Index of the column which would be used as Key for result Map
     */
    public BeanMapOutputHandler(Class<V> type, int columnIndex) {
        this(type, MidaoConfig.getDefaultQueryOutputProcessor(), columnIndex, null);
    }

    /**
     * Creates new BeanMapOutputHandler instance.
     *
     * @param type Bean Class description
     * @param columnName Name of the column which would be used as Key for result Map
     */
    public BeanMapOutputHandler(Class<V> type, String columnName) {
        this(type, MidaoConfig.getDefaultQueryOutputProcessor(), 0, columnName);
    }

    /**
     * Creates new BeanMapOutputHandler instance.
     *
     * @param type Bean Class description
     * @param processor Query output processor
     * @param columnIndex Index of the column which would be used as Key for result Map. Used only if @columnName is null
     * @param columnName Name of the column which would be used as Key for result Map
     */
    private BeanMapOutputHandler(Class<V> type, QueryOutputProcessor processor,
            int columnIndex, String columnName) {
    	
   		super(processor, columnIndex, columnName);
        
        this.type = type;
    }

    /**
     * Converts query output into Map of Beans.
     *
     * @param params query output row
     * @return Map of Beans converted from query output
     * @throws MidaoException
     */
    @Override
    protected V createRow(QueryParameters params) throws MidaoException {
        return this.outputProcessor.toBean(params, type);
    }

}
