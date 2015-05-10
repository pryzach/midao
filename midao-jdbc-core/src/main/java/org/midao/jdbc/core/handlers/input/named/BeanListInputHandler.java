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

import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.InputUtils;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.processor.QueryInputProcessor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Named InputHandler. Allows accepting List of Beans as source of values for Query
 */
public class BeanListInputHandler<T> extends AbstractNamedListInputHandler<T> {

    private final Map<String, T> inputParameter;
    private final String encodedSql;

    private final String sql;
    private final QueryParameters queryParameters;

    /**
     * Creates new BeanListInputHandler instance
     *
     * @param encodedQuery   encoded Query
     * @param inputParameter input Bean List
     */
    public BeanListInputHandler(String encodedQuery, Map<String, T> inputParameter) {
        this(MjdbcConfig.getDefaultQueryInputProcessor(), encodedQuery, inputParameter);
    }

    /**
     * Creates new BeanListInputHandler instance
     *
     * @param processor      Query input processor
     * @param encodedQuery   encoded Query
     * @param inputParameter input Bean List
     */
    protected BeanListInputHandler(QueryInputProcessor processor, String encodedQuery, Map<String, T> inputParameter) {
        super(processor);

        this.validateSqlString(encodedQuery);

        Map<String, Object> beanPropertiesMap = null;
        List<Map<String, Object>> beanList = new ArrayList<Map<String, Object>>();
        Map<String, Object> preparedMap = null;
        T parameter = null;
        ProcessedInput processedInput = null;

        this.inputParameter = inputParameter;
        this.encodedSql = encodedQuery;

        if (inputParameter != null) {
            for (String parameterName : inputParameter.keySet()) {
                parameter = inputParameter.get(parameterName);
                PropertyDescriptor[] props = MappingUtils.propertyDescriptors(parameter.getClass());

                beanPropertiesMap = MappingUtils.toMap(parameter, props);
                InputUtils.setClassName(beanPropertiesMap, parameterName);

                beanList.add(beanPropertiesMap);
            }
        }

        // preparing map for processing with query
        preparedMap = this.mergeMaps(encodedQuery, beanList, true);

        if (encodedQuery != null) {
            processedInput = processor.processInput(encodedQuery, preparedMap);

            sql = processedInput.getParsedSql();
            if (processedInput.getSqlParameterValues() != null) {
                this.queryParameters = new QueryParameters(processedInput);
            } else {
                this.queryParameters = HandlersConstants.EMPTY_QUERY_PARAMS;
            }
        } else {
            sql = null;
            this.queryParameters = HandlersConstants.EMPTY_QUERY_PARAMS;
        }
    }

    @Override
    public String getQueryString() {
        return this.sql;
    }

    @Override
    public QueryParameters getQueryParameters() {
        return this.queryParameters;
    }

}
