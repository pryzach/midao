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
import org.midao.core.processor.QueryOutputProcessor;

import java.util.List;

/**
 * Converts first row of query output into object array
 */
public class ArrayOutputHandler extends AbstractOutputHandler<Object[]> {

    /**
     * Creates new ArrayOutputHandler instance.
     */
	public ArrayOutputHandler() {
		super();
	}

    /**
     * Creates new ArrayOutputHandler instance.
     *
     * @param outputProcessor Query output processor
     */
	public ArrayOutputHandler(QueryOutputProcessor outputProcessor) {
		super(outputProcessor);
	}

    /**
     * Converts first row of query output into object array
     *
     * @param outputList Query output
     * @return array of object converted from first line of query output
     */
	public Object[] handle(List<QueryParameters> outputList) {
		return this.outputProcessor.toArray(outputList);
	}
	
}
