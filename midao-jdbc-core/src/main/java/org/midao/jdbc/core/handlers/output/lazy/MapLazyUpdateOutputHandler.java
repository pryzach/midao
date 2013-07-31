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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Converts query output into updateable lazy list of maps
 *
 * @see {@link LazyUpdateOutputHandler}
 */
public class MapLazyUpdateOutputHandler  extends AbstractScrollUpdateLazyOutputHandler implements LazyUpdateOutputHandler<MapLazyUpdateOutputHandler, Map<String, Object>> {
    /**
     * Creates new MapLazyUpdateOutputHandler instance.
     */
    public MapLazyUpdateOutputHandler() {
        this(MjdbcConfig.getDefaultQueryOutputProcessor());
    }

    /**
     * Creates new MapLazyUpdateOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public MapLazyUpdateOutputHandler(QueryOutputProcessor processor) {
        this.processor = processor;
    }

    /**
     * Creates new MapLazyUpdateOutputHandler instance.
     *
     * @param processor Query output processor
     * @param paramsList Query output lazy list
     */
    private MapLazyUpdateOutputHandler(QueryOutputProcessor processor, QueryParametersLazyList paramsList) {
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
    public MapLazyUpdateOutputHandler handle(List<QueryParameters> outputList) throws MjdbcException {
        if (outputList instanceof QueryParametersLazyList) {
            return new MapLazyUpdateOutputHandler(this.processor, (QueryParametersLazyList) outputList);
        } else {
            throw new MjdbcRuntimeException("LazyOutputHandler can be used only together with LazyStatementHandler. \n" +
                    "Please assign LazyStatementHandler to this QueryRunner or create new QueryRunnerService via MjdbcFactory");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateRow(Map<String, Object> row) throws SQLException {
        innerUpdateRow(new QueryParameters(row));
    }

    /**
     * {@inheritDoc}
     */
    public void insertRow(Map<String, Object> row) throws SQLException {
        innerInsertRow(new QueryParameters(row));
    }
}
