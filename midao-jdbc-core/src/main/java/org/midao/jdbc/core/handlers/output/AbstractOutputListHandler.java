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

import org.midao.jdbc.core.processor.QueryOutputProcessor;

import java.util.List;

/**
 * Base OutputHandler class for List* classes
 */
public abstract class AbstractOutputListHandler<T> extends AbstractOutputHandler<List<T>> {

    /**
     * Initializes AbstractOutputListHandler ancestor instance
     */
	public AbstractOutputListHandler() {
		super();
	}

    /**
     * Initializes AbstractOutputListHandler ancestor instance
     *
     * @param processor Query output processor
     */
	public AbstractOutputListHandler(QueryOutputProcessor processor) {
		super(processor);
	}
}
