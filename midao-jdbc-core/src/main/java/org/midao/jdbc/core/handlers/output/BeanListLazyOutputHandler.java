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

import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.exception.MidaoRuntimeException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.model.QueryParametersLazyList;
import org.midao.jdbc.core.processor.QueryOutputProcessor;

import java.util.List;

/**
 * Converts query output into lazy list of beans
 *
 * @see {@link LazyOutputHandler}
 */
public class BeanListLazyOutputHandler<S> implements LazyOutputHandler<BeanListLazyOutputHandler, S> {
    private QueryOutputProcessor processor;

    private final Class<S> type;
    private QueryParametersLazyList queryParams;
    private int currentIndex;

    /**
     * Creates new BeanListLazyOutputHandler instance.
     *
     * @param type Bean Class description
     */
    public BeanListLazyOutputHandler(Class<S> type) {
        this(type, MidaoConfig.getDefaultQueryOutputProcessor());
    }

    /**
     * Creates new BeanListLazyOutputHandler instance.
     *
     * @param type Bean Class description
     * @param processor Query output processor
     */
    public BeanListLazyOutputHandler(Class<S> type, QueryOutputProcessor processor) {
        this.type = type;
        this.processor = processor;
    }

    /**
     * Creates new BeanListLazyOutputHandler instance.
     *
     * @param type Bean Class description
     * @param processor Query output processor
     * @param paramsList Query output lazy list
     */
    private BeanListLazyOutputHandler(Class<S> type, QueryOutputProcessor processor, QueryParametersLazyList paramsList) {
        this.type = type;
        this.processor = processor;
        this.queryParams = paramsList;
        this.currentIndex = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        boolean result = false;
        QueryParameters params = queryParams.get(currentIndex + 1);

        if (params != null) {
            result = true;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public S getNext() {
        S result = null;
        QueryParameters params = queryParams.get(currentIndex + 1);

        try {
            result = processor.toBean(params, this.type);
        } catch (MidaoException ex) {
            throw new MidaoRuntimeException(ex);
        }

        if (result != null) {
            currentIndex++;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        this.queryParams.close();
    }

    /**
     * {@inheritDoc}
     */
    public BeanListLazyOutputHandler handle(List<QueryParameters> outputList) throws MidaoException {
        if (outputList instanceof QueryParametersLazyList) {
            return new BeanListLazyOutputHandler(this.type, this.processor, (QueryParametersLazyList) outputList);
        } else {
            throw new MidaoRuntimeException("LazyOutputHandler can be used only together with LazyStatementHandler. \n" +
                    "Please assign LazyStatementHandler to this QueryRunner or create new QueryRunnerService via MidaoFactory");
        }
    }
}
