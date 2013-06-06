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

package org.midao.jdbc.core.handlers.input.query;

import org.midao.jdbc.core.handlers.input.AbstractInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.processor.QueryInputProcessor;

/**
 * Query InputHandler allows accepting QueryParameters as an input.
 * This is useful when you have to call Stored Procedures, as it is possible to specify Direction and Type
 * of Stored Procedure parameters.
 *
 * If Metadata Handler cannot figure Stored Procedure/Function parameters for
 * Named Input Handlers(Bean/Map Input Handlers) - AbstractQueryInputHandler implementation should be used.
 * In such case - please specify Direction and/or Type in QueryParameters
 */
public abstract class AbstractQueryInputHandler<T> extends AbstractInputHandler<T> {

    /**
     * Initializes parent part of QueryInputHandler
     *
     * @param processor Query input processor
     */
	protected AbstractQueryInputHandler(QueryInputProcessor processor) {
		super(processor);
	}

    /**
     * This implementation allows updating input parameters with values returned from Query call.
     * Usually it is used when you have OUT parameters from Query execution and want to update
     * original input with them.
     *
     * Only fields with Direction specified as OUT/INOUT would be updated.
     *
     * Doesn't actually updates input, but creates new instance with updated values.
     *
     * Might be removed in future, as can be replaced with:
     * 1. {@link org.midao.jdbc.core.handlers.input.query.QueryInputHandler#getQueryParameters()}
     * 2. {@link QueryParameters#update(Object[], boolean)}
     *
     * @param outParamValues Array of values returned after Query execution from OUT/INOUT fields
     * @return new instance of input parameters with updated values.
     */
	public abstract QueryParameters update(Object[] outParamValues);

    /**
     * {@inheritDoc}
     */
    abstract public String getQueryString();

    /**
     * {@inheritDoc}
     */
    abstract public QueryParameters getQueryParameters();
}
