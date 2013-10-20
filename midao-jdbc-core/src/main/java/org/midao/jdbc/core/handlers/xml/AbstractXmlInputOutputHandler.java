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

package org.midao.jdbc.core.handlers.xml;

import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.input.AbstractInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.processor.QueryInputProcessor;

import java.util.List;

/**
 * Base implementation of XML input handler
 */
public abstract class AbstractXmlInputOutputHandler<T> extends AbstractInputHandler<T> implements XmlHandler, OutputHandler<T> {

    /**
     * Initializes parent part of AbstractXmlInputOutputHandler
     *
     * @param processor Query input processor
     */
    protected AbstractXmlInputOutputHandler(QueryInputProcessor processor) {
        super(processor);
    }

    /**
     * {@inheritDoc}
     */
    public abstract String getQueryString();

    /**
     * {@inheritDoc}
     */
    public abstract QueryParameters getQueryParameters();

    /**
     * Returns query name used for this handler
     *
     * @return query name
     */
    public abstract String getName();

    /**
     * Returns output type set for this handler.
     * Output type is set via constructor
     *
     * @return output type if present. null otherwise
     */
    public abstract Class<T> getOutputType();

    /**
     * {@inheritDoc}
     */
    public abstract Overrider getOverrides();

    /**
     * {@inheritDoc}
     */
    public abstract T handle(List<QueryParameters> outputList) throws MjdbcException;
}
