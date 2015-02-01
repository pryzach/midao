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

package org.midao.jdbc.core.handlers.input.named;

import org.midao.jdbc.core.handlers.input.AbstractInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.processor.QueryInputProcessor;

/**
 * Similar to {@link AbstractNamedInputHandler}, but accepts List of Maps/Beans
 */
public abstract class AbstractNamedListInputHandler<T> extends AbstractInputHandler<T> {

    /**
     * Initializes parent part of NamedInputHandler
     *
     * @param processor Query input processor
     */
    protected AbstractNamedListInputHandler(QueryInputProcessor processor) {
        super(processor);
    }

    /**
     * {@inheritDoc}
     */
    abstract public String getQueryString();

    /**
     * {@inheritDoc}
     */
    abstract public QueryParameters getQueryParameters();
}
