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
import java.util.Map;

/**
 * Converts query output into List of Maps
 *
 * @see {@link LazyOutputHandler}
 */
public class MapListLazyOutputHandler implements LazyOutputHandler<MapListLazyOutputHandler, Map<String, Object>> {
    private QueryOutputProcessor processor;

    private QueryParametersLazyList queryParams;
    private int currentIndex;

    /**
     * Creates new MapListLazyOutputHandler instance.
     */
    public MapListLazyOutputHandler() {
        this(MidaoConfig.getDefaultQueryOutputProcessor());
    }

    /**
     * Creates new MapListLazyOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public MapListLazyOutputHandler(QueryOutputProcessor processor) {
        this.processor = processor;
    }

    /**
     * Creates new MapListLazyOutputHandler instance.
     *
     * @param processor Query output processor
     * @param paramsList Query output lazy list
     */
    private MapListLazyOutputHandler(QueryOutputProcessor processor, QueryParametersLazyList paramsList) {
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
    public Map<String, Object> getNext() {
        Map<String, Object> result = null;
        QueryParameters params = queryParams.get(currentIndex + 1);

        result = processor.toMap(params);

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
    public MapListLazyOutputHandler handle(List<QueryParameters> outputList) throws MidaoException {
        if (outputList instanceof QueryParametersLazyList) {
            return new MapListLazyOutputHandler(this.processor, (QueryParametersLazyList) outputList);
        } else {
            throw new MidaoRuntimeException("LazyOutputHandler can be used only together with LazyStatementHandler. \n" +
                    "Please assign LazyStatementHandler to this QueryRunner or create new QueryRunnerService via MidaoFactory");
        }
    }
}
