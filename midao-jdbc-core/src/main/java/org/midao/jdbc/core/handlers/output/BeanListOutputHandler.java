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

/**
 * Converts query output into list of beans
 */
public class BeanListOutputHandler<T> extends AbstractOutputListHandler<T> {
	
    /**
     * The Class of beans produced by this handler.
     */
    private final Class<T> type;

    /**
     * Creates new BeanListOutputHandler instance.
     *
     * @param type Bean Class description
     */
	public BeanListOutputHandler(Class<T> type) {
		super();
		
		this.type = type;
	}

    /**
     * Creates new BeanListOutputHandler instance.
     *
     * @param type Bean Class description
     * @param processor Query output processor
     */
	public BeanListOutputHandler(Class<T> type, QueryOutputProcessor processor) {
		super(processor);
		
		this.type = type;
	}

    /**
     * Converts query output into list of beans
     *
     * @param outputList Query output
     * @return List of beans converted from query output
     * @throws MidaoException
     */
	public List<T> handle(List<QueryParameters> outputList) throws MidaoException {
		return this.outputProcessor.toBeanList(outputList, this.type);
	}

}
