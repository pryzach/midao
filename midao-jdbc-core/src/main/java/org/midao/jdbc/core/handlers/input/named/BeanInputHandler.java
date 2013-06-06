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
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.processor.QueryInputProcessor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Named InputHandler. Allows accepting Bean as source of values for Query
 */
public class BeanInputHandler<T> extends AbstractNamedInputHandler<T> {
	
	private final T inputParameter;
	private final String encodedSql;
	private final String sql;
	
	private final String parameterName;
	private final QueryParameters queryParameters;

    /**
     * Creates new BeanInputHandler instance
     *
     * @param encodedQuery encoded Query
     * @param inputParameter input Bean
     */
	public BeanInputHandler(String encodedQuery, T inputParameter) {
		this(encodedQuery, inputParameter, null);
	}

    /**
     * Creates new BeanInputHandler instance
     *
     * @param encodedQuery encoded Query
     * @param inputParameter input Bean
     * @param parameterName name of the bean. can be referenced as </parameterName>.</beanfiled>. Example: animal.name
     */
	public BeanInputHandler(String encodedQuery, T inputParameter, String parameterName) {
		this(MidaoConfig.getDefaultQueryInputProcessor(), encodedQuery, inputParameter, parameterName);
	}

    /**
     * Creates new BeanInputHandler instance
     *
     * @param processor Query input processor
     * @param encodedQuery encoded Query
     * @param inputParameter input Bean
     * @param parameterName name of the bean. can be referenced as </parameterName>.</beanfiled>. Example: animal.name
     */
	protected BeanInputHandler(QueryInputProcessor processor, String encodedQuery, T inputParameter, String parameterName) {
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
			PropertyDescriptor[] props = MappingUtils.propertyDescriptors(inputParameter.getClass());

			beanPropertiesMap = MappingUtils.toMap(inputParameter, props);
			InputUtils.setClassName(beanPropertiesMap, this.parameterName);

			beanList.add(beanPropertiesMap);
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

	@Override
	public String getEncodedQueryString() {
		return this.encodedSql;
	}
	
	@Override
	public T updateInput(QueryParameters updatedInput) {
		return this.updateBean(this.inputParameter, updatedInput.toMap());
	}
}
