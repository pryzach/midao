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

import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.processor.QueryOutputProcessor;

import java.util.Map;

/**
 * Converts query output into Map of Maps.
 * <p/>
 * Example:
 * Query output:
 * new Object[]{"jack", "sheriff", 36}
 * new Object[]{"henry", "mechanic", 36}
 * new Object[]{"alison", "agent", 30}
 * <p/>
 * After handling we would receive
 * Map:
 * k: jack v: Map {"name":"jack", "occupation":"sheriff", "age":36};
 * k: henry v: Map {"name":"henry", "occupation":"mechanic", "age":36}
 * k: alison v: Map {"name":"alison", "occupation":"agent", "age":30}
 */
public class KeyedOutputHandler<K> extends AbstractKeyedOutputHandler<K, Map<String, Object>> {

    /**
     * Creates new KeyedOutputHandler instance.
     */
    public KeyedOutputHandler() {
        this(MjdbcConfig.getDefaultQueryOutputProcessor(), 0, null);
    }

    /**
     * Creates new KeyedOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public KeyedOutputHandler(QueryOutputProcessor processor) {
        this(processor, 0, null);
    }

    /**
     * Creates new KeyedOutputHandler instance.
     *
     * @param columnIndex Index of the column which would be used as Key for result Map
     */
    public KeyedOutputHandler(int columnIndex) {
        this(MjdbcConfig.getDefaultQueryOutputProcessor(), columnIndex, null);
    }

    /**
     * Creates new KeyedOutputHandler instance.
     *
     * @param columnName Name of the column which would be used as Key for result Map
     */
    public KeyedOutputHandler(String columnName) {
        this(MjdbcConfig.getDefaultQueryOutputProcessor(), 0, columnName);
    }

    /**
     * Creates new KeyedOutputHandler instance.
     *
     * @param processor   Query output processor
     * @param columnIndex Index of the column which would be used as Key for result Map. Used only if @columnName is null
     * @param columnName  Name of the column which would be used as Key for result Map
     */
    private KeyedOutputHandler(QueryOutputProcessor processor, int columnIndex,
                               String columnName) {

        super(processor, columnIndex, columnName);
    }

    @Override
    protected Map<String, Object> createRow(QueryParameters params) throws MjdbcException {
        return this.outputProcessor.toMap(params);
    }

}
