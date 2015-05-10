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

package org.midao.jdbc.core.handlers.output.lazy;

import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
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
public class MapLazyOutputHandler extends AbstractLazyOutputHandler implements LazyOutputHandler<MapLazyOutputHandler, Map<String, Object>> {

    /**
     * Creates new MapLazyOutputHandler instance.
     */
    public MapLazyOutputHandler() {
        this(MjdbcConfig.getDefaultQueryOutputProcessor());
    }

    /**
     * Creates new MapLazyOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public MapLazyOutputHandler(QueryOutputProcessor processor) {
        this.processor = processor;
    }

    /**
     * Creates new MapLazyOutputHandler instance.
     *
     * @param processor  Query output processor
     * @param paramsList Query output lazy list
     */
    private MapLazyOutputHandler(QueryOutputProcessor processor, QueryParametersLazyList paramsList) {
        this.processor = processor;
        this.queryParams = paramsList.getLazyCacheIterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return innerHasNext();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getNext() {
        Map<String, Object> result = null;
        QueryParameters params = innerGetNext();

        result = processor.toMap(params);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrent() {
        Map<String, Object> result = null;
        QueryParameters params = innerGetCurrent();

        result = processor.toMap(params);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        innerClose();
    }

    /**
     * {@inheritDoc}
     */
    public MapLazyOutputHandler handle(List<QueryParameters> outputList) throws MjdbcException {
        if (outputList instanceof QueryParametersLazyList) {
            return new MapLazyOutputHandler(this.processor, (QueryParametersLazyList) outputList);
        } else {
            throw new MjdbcRuntimeException("LazyOutputHandler can be used only together with LazyStatementHandler. \n" +
                    "Please assign LazyStatementHandler to this QueryRunner or create new QueryRunnerService via MjdbcFactory");
        }
    }
}
