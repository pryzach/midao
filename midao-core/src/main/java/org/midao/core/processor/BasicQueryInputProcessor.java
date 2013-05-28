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

package org.midao.core.processor;

import org.midao.core.MidaoConfig;
import org.midao.core.handlers.model.ProcessedInput;
import org.midao.core.handlers.utils.InputUtils;
import org.midao.core.utils.AssertUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic {@link QueryInputProcessor} implementation.
 */
public class BasicQueryInputProcessor implements QueryInputProcessor {
	private static final int DEFAULT_CACHE_LIMIT = 256;
	private static final int PAD_LIMIT = 8192;
	private static final char FILL_SYMBOL = '#';
	private static final String SQL_PARAMETER = "?";
	
	// Caching all processed input based on SQL Query
	@SuppressWarnings("serial")
	private final Map<String, ProcessedInput> processedInputCache = new LinkedHashMap<String, ProcessedInput>(
			DEFAULT_CACHE_LIMIT, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, ProcessedInput> eldest) {
			return size() > DEFAULT_CACHE_LIMIT;
		}
	};
	
	//private static final String PARAMETER_SEPARATOR = "[\"\'\\:\\&\\,\\;\\(\\)\\|\\=\\+\\-\\*\\%\\/\\\\<\\>\\^\\ ]";
	private static final String PARAMETER_SEPARATOR = "[\"\'&,;()|=+\\-*%/\\<>\\^\\s{}]";
	//private static final String PARAMETER_SEPARATOR = "";
	
	/**
	 * RegEx for searching for a parameter in SQL String
	 */
	private static final String REGEX_PARAMETER_SEARCH = PARAMETER_SEPARATOR + "[" + InputUtils.getParameterPrefix() + "][a-zA-Z0-9_\\-.]{1,}?(?=" + PARAMETER_SEPARATOR + ")";
	
	private static final String REGEX_UNNAMED_PARAMETER_SEARCH = PARAMETER_SEPARATOR + "[?]" + "(?=" + PARAMETER_SEPARATOR + ")";
	
	/**
	 * RegEx for searching for text and comment blocks
	 */
	private static final String REGEX_SKIP_BLOCK_SEARCH = "(['].+?['])|([\"].+?[\"])|([-][-].+?[\n])|([#].+?[\n])|([/][*].+?[*][/])";

    /**
     * {@inheritDoc}
     */
	public ProcessedInput processInput(String originalSql, Map<String, Object> params) {
		ProcessedInput processedInput = getProcessedInputsFromCache(originalSql);
		String preProcessedSql = null;

		if (processedInput.getParsedSql() == null) {
			// removing comments and text blocks
			preProcessedSql = removeBlocks(originalSql);
			
			// parsing sql(without comments and text blocks)
			processedInput = parseSqlString(preProcessedSql, processedInput);
		}

		putProcessedInputToCache(processedInput);

		AssertUtils.assertNotNull(processedInput.getParsedSql());

		// set sql parameter values
		processedInput.fillParameterValues(params);

		return processedInput;
	}

    /**
     * {@inheritDoc}
     */
	public boolean hasUnnamedParameters(String originalSql) {
		boolean hasUnnamedParameters = false;
		String preProcessedSql = null;

		// removing comments and text blocks
		preProcessedSql = removeBlocks(originalSql);

        // adding whitespace in the end to allow proper processing if "?" would be the last symbol
        preProcessedSql = preProcessedSql + " ";
			
		// parsing sql(without comments and text blocks)
		Pattern regexPattern = null;
		Matcher regexMatcher = null;
		
		regexPattern = Pattern.compile(REGEX_UNNAMED_PARAMETER_SEARCH, Pattern.CASE_INSENSITIVE);
		regexMatcher = regexPattern.matcher(preProcessedSql);
		
		if (regexMatcher.find() == true) {
			hasUnnamedParameters = true;
		}
		
		return hasUnnamedParameters;
	}

    /**
     * Performs actual input SQL/parameters processing
     * Used by {@link #processInput(String, java.util.Map)}
     *
     * @param preProcessedSql SQL string with removed blocks (comments etc.)
     * @param processedInput Processed Input object which would be filled
     * @return filled ProcessedInput object
     */
	private ProcessedInput parseSqlString(String preProcessedSql, ProcessedInput processedInput) {
		ProcessedInput resultProcessedInput = new ProcessedInput(processedInput);
		
		String originalSql = resultProcessedInput.getOriginalSql();
		StringBuilder parsedSql = new StringBuilder(preProcessedSql.length()); 
		
		Pattern regexPattern = null;
		Matcher regexMatcher = null;
		
		regexPattern = Pattern.compile(REGEX_PARAMETER_SEARCH, Pattern.CASE_INSENSITIVE);
		
		// adding whitespace, as parameter might be in the end of the string, but regex is checking for separator on both ends of parameter.
		regexMatcher = regexPattern.matcher(preProcessedSql + " ");
		
		int paramStart = 0;
		int paramEnd = 0;
		int prevParamEnd = 0;
		String paramName = null;

		while (regexMatcher.find() == true) {
			paramStart = regexMatcher.start() + 1;
			paramEnd = regexMatcher.end();
			paramName = preProcessedSql.substring(paramStart + 1, paramEnd).toLowerCase();
			
			parsedSql.append(originalSql.substring(prevParamEnd, paramStart));
			
			parsedSql.append(SQL_PARAMETER);
			
			resultProcessedInput.addParameter(paramName, paramStart, paramEnd);
			
			prevParamEnd = paramEnd;
		}
		
		parsedSql.append(originalSql.substring(prevParamEnd, preProcessedSql.length()));
		
		resultProcessedInput.setParsedSql(parsedSql.toString());
		
		return resultProcessedInput;
	}

    /**
     * Removes blocks - such as comments and other possible blocks
     *
     * @param originalSql original SQL
     * @return SQL string with removed(they will be filled with {@link #FILL_SYMBOL} blocks
     */
	private String removeBlocks(String originalSql) {
		StringBuilder cleanedSql = new StringBuilder(originalSql.length()); 
		
		Pattern regexPattern = null;
		Matcher regexMatcher = null;
		
		regexPattern = Pattern.compile(REGEX_SKIP_BLOCK_SEARCH, Pattern.CASE_INSENSITIVE);
		regexMatcher = regexPattern.matcher(originalSql + "\n");
		
		int prevBlockEnd = 0;
		int blockStart = 0;
		int blockEnd = 0;
		String block = null;

		while (regexMatcher.find() == true) {
			blockStart = regexMatcher.start();
			blockEnd = Math.min(originalSql.length(), regexMatcher.end());
			
			block = originalSql.substring(blockStart, blockEnd);
			
			cleanedSql.append(originalSql.substring(prevBlockEnd, blockStart));
			
			prevBlockEnd = blockEnd;
			
			cleanedSql.append(fill(FILL_SYMBOL, block.length()));
		}
		
		cleanedSql.append(originalSql.substring(prevBlockEnd, originalSql.length()));
		
		return cleanedSql.toString();
	}

    /**
     * Returns string of specified length filled with specified symbol
     *
     * @param symbol character with which String would be filled
     * @param times amount of times character would be copied
     * @return filled String
     */
	private String fill(char symbol, int times) {
		char[] result = new char[times];
		
		
		for (int i = 0; i < times; i++) {
			result[i] = symbol;
		}
		
		return new String(result);
	}

    /**
     * Returns processed input from Cache
     *
     * @param originalSql original SQL
     * @return empty/cached ProcessedInput instance
     */
	private ProcessedInput getProcessedInputsFromCache(String originalSql) {
		ProcessedInput processedInput = null;

		synchronized (this.processedInputCache) {
			processedInput = this.processedInputCache.get(originalSql);
			if (processedInput == null || MidaoConfig.isQueryInputProcessorUseCache() == false) {
				processedInput = new ProcessedInput(originalSql);
			} else {
				processedInput = new ProcessedInput(processedInput);
			}
		}
		
		return processedInput;
	}

    /**
     * Puts ProcessedInput instance into Cache. Original SQL would be served as key
     *
     * @param processedInput ProcessedInput instance which would be cached
     */
	private void putProcessedInputToCache(ProcessedInput processedInput) {
		
		if (MidaoConfig.isQueryInputProcessorUseCache() == true) {
			ProcessedInput clonedProcessedInput = new ProcessedInput(processedInput);
			synchronized (this.processedInputCache) {

				// no need to keep parameters in the memory. removing just in case
				clonedProcessedInput.setSqlParameterValues(null);
				this.processedInputCache.put(processedInput.getOriginalSql(), clonedProcessedInput);
			}
		}
	}

}
