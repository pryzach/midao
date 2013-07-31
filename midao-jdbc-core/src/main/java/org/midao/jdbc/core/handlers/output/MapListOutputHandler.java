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

import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.processor.QueryOutputProcessor;

import java.util.List;
import java.util.Map;

/**
 * Converts query output into List of Maps
 *
 * Example:
 * Query output:
 * new Object[]{"jack", "sheriff", 36}
 * new Object[]{"henry", "mechanic", 36}
 * new Object[]{"alison", "agent", 30}
 *
 * After handling we would receive
 * List:
 * 0: Map {"name":"jack", "occupation":"sheriff", "age":36};
 * 1: Map {"name":"henry", "occupation":"mechanic", "age":36}
 * 2: Map {"name":"alison", "occupation":"agent", "age":30}
 */
public class MapListOutputHandler extends AbstractOutputListHandler<Map<String, Object>> {

    /**
     * Creates new MapListOutputHandler instance.
     */
    public MapListOutputHandler() {
        super();
    }

    /**
     * Creates new MapListOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public MapListOutputHandler(QueryOutputProcessor processor) {
    	super(processor);
    }

    /**
     * Converts query output into List of Maps
     *
     * @param outputList Query output
     * @return List of Maps converted from query output
     * @throws org.midao.jdbc.core.exception.MjdbcException
     */
	public List<Map<String, Object>> handle(List<QueryParameters> outputList) throws MjdbcException {
		return this.outputProcessor.toMapList(outputList);
	}

}
