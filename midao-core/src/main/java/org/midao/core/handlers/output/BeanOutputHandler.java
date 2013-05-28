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

import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.processor.QueryOutputProcessor;

import java.sql.SQLException;
import java.util.List;

/**
 * Converts first row of query output into Bean
 */
public class BeanOutputHandler<T> extends AbstractOutputHandler<T> {
	
    /**
     * The Class of beans produced by this handler.
     */
    private final Class<T> type;

    /**
     * Creates new BeanOutputHandler instance.
     *
     * @param type Bean Class description
     */
	public BeanOutputHandler(Class<T> type) {
		super();
		
		this.type = type;
	}

    /**
     * Creates new BeanOutputHandler instance.
     *
     * @param type Bean Class description
     * @param outputProcessor Query output processor
     */
	public BeanOutputHandler(Class<T> type, QueryOutputProcessor outputProcessor) {
		super(outputProcessor);
		
		this.type = type;
	}

    /**
     * Converts first row of query output into Bean
     *
     * @param outputList Query output
     * @return Bean converted from first row of query output
     * @throws MidaoException
     */
	public T handle(List<QueryParameters> outputList) throws MidaoException {
		return (T) this.outputProcessor.toBean(outputList, this.type);
	}

}
