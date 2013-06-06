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
 * Named InputHandler. Allows accepting List(Map) of Maps as source of values for Query
 */
public class MapListInputHandler extends AbstractNamedListInputHandler<Map<String, Map<String, Object>>> {
	
	private final Map<String, Map<String, Object>> inputParameter;
	private final String encodedSql;
	
	private final String sql;
	private final QueryParameters queryParameters;

    /**
     * Creates new MapListInputHandler instance
     *
     * @param encodedQuery encoded Query
     * @param inputParameter input Map List(Map)
     */
	public MapListInputHandler(String encodedQuery, Map<String, Map<String, Object>> inputParameter) {
		this(MidaoConfig.getDefaultQueryInputProcessor(), encodedQuery, inputParameter);
	}

    /**
     * Creates new MapListInputHandler instance
     *
     * @param processor Query input processor
     * @param encodedQuery encoded Query
     * @param inputParameter input Map List (Map)
     */
	protected MapListInputHandler(QueryInputProcessor processor, String encodedQuery, Map<String, Map<String, Object>> inputParameter) {
		super(processor);
		
		this.validateSqlString(encodedQuery);
		
		Map<String, Object> beanPropertiesMap = null;
		List<Map<String, Object>> beanList = new ArrayList<Map<String, Object>>();
		Map<String, Object> preparedMap = null;
		Map<String, Object> parameter = null;
		ProcessedInput processedInput = null;
		
		this.inputParameter = inputParameter;
		this.encodedSql = encodedQuery;
		
		if (inputParameter != null) {
			for (String parameterName : inputParameter.keySet()) {
				beanPropertiesMap = new HashMap<String, Object>(inputParameter.get(parameterName));
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
