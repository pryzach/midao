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

import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.InputUtils;
import org.midao.jdbc.core.processor.QueryInputProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Query InputHandler allows accepting QueryParameters as an input.
 * This is useful when you have to call Stored Procedures, as it is possible to specify Direction and Type
 * of Stored Procedure parameters.
 *
 * If Metadata Handler cannot figure Stored Procedure/Function parameters for
 * Named Input Handlers(Bean/Map Input Handlers) - this InputHandler should be used.
 * In such case - please specify Direction and/or Type in QueryParameters
 */
public class QueryInputHandler extends AbstractQueryInputHandler<QueryParameters> {
	
	private final QueryParameters inputParameter;
	private final String encodedSql;
	
	private final String sql;
	private final String parameterName;
	private final QueryParameters queryParameters;

    /**
     * Creates new QueryInputHandler instance
     *
     * @param encodedQuery Original SQL string
     * @param inputParameter QueryParameters input
     */
	public QueryInputHandler(String encodedQuery, QueryParameters inputParameter) {
		this(encodedQuery, inputParameter, null);
	}

    /**
     * Creates new QueryInputHandler instance
     *
     * @param encodedQuery Original SQL string
     * @param inputParameter QueryParameters input
     * @param parameterName Class name of the @inputParameter. If it is specified - it can be referenced by it's name.
     *                      Example: class name: Animal. Key: Name. In SQL string it can be references as ":animal.name"
     */
	public QueryInputHandler(String encodedQuery, QueryParameters inputParameter, String parameterName) {
		this(MidaoConfig.getDefaultQueryInputProcessor(), encodedQuery, inputParameter, parameterName);
	}
	
	protected QueryInputHandler(QueryInputProcessor processor, String encodedQuery, QueryParameters inputParameter, String parameterName) {
		super(processor);
		
		this.validateSqlString(encodedQuery);
		
		Map<String, Object> beanPropertiesMap = null;
		List<Map<String, Object>> beanList = new ArrayList<Map<String, Object>>();
		Map<String, Object> preparedMap = null;
		ProcessedInput processedInput = null;
		
		this.inputParameter = inputParameter;
		this.encodedSql = encodedQuery;
		
		this.parameterName = parameterName;
		
		if (inputParameter != null) {
			beanPropertiesMap = new HashMap<String, Object>(inputParameter.toMap());
			InputUtils.setClassName(beanPropertiesMap, this.parameterName);

			beanList.add(beanPropertiesMap);
		}
        
        // preparing map for processing with query
        preparedMap = this.mergeMaps(encodedQuery, beanList, true);
        
		if (encodedQuery != null) {
			processedInput = processor.processInput(encodedQuery, preparedMap);
			
			this.sql = processedInput.getParsedSql();
			if (processedInput.getSqlParameterValues() != null) {
                this.queryParameters = new QueryParameters(this.inputParameter);
                this.queryParameters.updateAndClean(processedInput);
			} else {
				this.queryParameters = HandlersConstants.EMPTY_QUERY_PARAMS;
			}
		} else {
			this.sql = null;
			this.queryParameters = HandlersConstants.EMPTY_QUERY_PARAMS;
			processedInput = null;
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
	
	@Override
	public QueryParameters update(Object[] outParamValues) {
		QueryParameters parameters = null;
		if (this.queryParameters != null) {
			parameters = new QueryParameters(this.queryParameters);
			parameters.update(outParamValues, true);
		}
		
		return parameters;
	}
}
