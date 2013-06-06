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

import java.util.List;
import java.util.Map;

/**
 * Converts first row of query output into Map
 */
public class MapOutputHandler extends AbstractOutputHandler<Map<String, Object>> {

    /**
     * Creates new MapOutputHandler instance.
     */
    public MapOutputHandler() {
    	super();
    }

    /**
     * Creates new MapOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public MapOutputHandler(QueryOutputProcessor processor) {
        super(processor);
    }

    /**
     * Converts first row of query output into Map
     *
     * @param outputList Query output
     * @return Map converted from first row of query output
     * @throws MidaoException
     */
	public Map<String, Object> handle(List<QueryParameters> outputList) throws MidaoException {
		return this.outputProcessor.toMap(outputList);
	}
}
