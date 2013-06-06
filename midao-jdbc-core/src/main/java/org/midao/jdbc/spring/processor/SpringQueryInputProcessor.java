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

package org.midao.jdbc.spring.processor;

import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.utils.InputUtils;
import org.midao.jdbc.core.processor.QueryInputProcessor;
import org.midao.jdbc.core.utils.AssertUtils;

import java.util.*;

/**
 * Spring SQL string processor. It is meant to be 100% compatible with the one present in Spring JDBC.
 * In order to achieve this parts of Spring code was used in here.
 *
 * If this would create licencing issues - it might be moved into separate package in future
 */
public class SpringQueryInputProcessor implements QueryInputProcessor {
	private static final String QUERY_PARAMETER = "?";
	private static final int DEFAULT_CACHE_LIMIT = 256;
	
	/** Cache of original SQL String to ParsedSql representation */
	@SuppressWarnings("serial")
	private final Map<String, ProcessedInput> processedInputCache = new LinkedHashMap<String, ProcessedInput>(
			DEFAULT_CACHE_LIMIT, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, ProcessedInput> eldest) {
			return size() > DEFAULT_CACHE_LIMIT;
		}
	};
	
	private static final String REGEX_PARAMETER_SEARCH = "[" + InputUtils.getParameterPrefix() + "][a-zA-Z_-]{1,}?[\\.]";


	/**
	 * Set of characters that qualify as parameter separators,
	 * indicating that a parameter name in a SQL String has ended.
	 */
	private static final char[] PARAMETER_SEPARATORS =
			new char[] {'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};

	/**
	 * Set of characters that qualify as comment or quotes starting characters.
	 */
	private static final String[] START_SKIP =
			new String[] {"'", "\"", "--", "/*"};

	/**
	 * Set of characters that at are the corresponding comment or quotes ending characters.
	 */
	private static final String[] STOP_SKIP =
			new String[] {"'", "\"", "\n", "*/"};

    /**
     * Creates new SpringQueryInputProcessor instance
     */
    public SpringQueryInputProcessor() {
    }

    /**
     * {@inheritDoc}
     */
    public ProcessedInput processInput(String query, Map<String, Object> params) {
        ProcessedInput processedInput = getProcessedInputsFromCache(query);
        String parsedSql = null;

        if (processedInput.getParsedSql() == null) {
            processedInput = processSqlStatement(query);

            parsedSql = generateParsedSql(processedInput);
            processedInput.setParsedSql(parsedSql);
        }

        putProcessedInputToCache(processedInput);

        AssertUtils.assertNotNull(processedInput.getParsedSql());
        AssertUtils.assertNotNull(processedInput.getSqlParameterNames());

        // set sql parameter values
        processedInput.fillParameterValues(params);

        return processedInput;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasUnnamedParameters(String originalSql) {
        boolean hasUnnamedParameters = false;

        char[] statement = originalSql.toCharArray();

        int i = 0;
        while (i < statement.length) {
            int skipToPosition = i;
            while (i < statement.length) {
                skipToPosition = skipCommentsAndQuotes(statement, i);
                if (i == skipToPosition) {
                    break;
                } else {
                    i = skipToPosition;
                }
            }
            if (i >= statement.length) {
                break;
            }
            char c = statement[i];
            if (c == '?') {
                hasUnnamedParameters = true;
                break;
            }
            i++;
        }

        return hasUnnamedParameters;
    }
	
	/**
	 * Skip over comments and quoted names present in an SQL statement
	 * @param statement character array containing SQL statement
	 * @param position current position of statement
	 * @return next position to process after any comments or quotes are skipped
	 */
	private static int skipCommentsAndQuotes(char[] statement, int position) {
		for (int i = 0; i < START_SKIP.length; i++) {
			if (statement[position] == START_SKIP[i].charAt(0)) {
				boolean match = true;
				for (int j = 1; j < START_SKIP[i].length(); j++) {
					if (!(statement[position + j] == START_SKIP[i].charAt(j))) {
						match = false;
						break;
					}
				}
				if (match) {
					int offset = START_SKIP[i].length();
					for (int m = position + offset; m < statement.length; m++) {
						if (statement[m] == STOP_SKIP[i].charAt(0)) {
							boolean endMatch = true;
							int endPos = m;
							for (int n = 1; n < STOP_SKIP[i].length(); n++) {
								if (m + n >= statement.length) {
									// last comment not closed properly
									return statement.length;
								}
								if (!(statement[m + n] == STOP_SKIP[i].charAt(n))) {
									endMatch = false;
									break;
								}
								endPos = m + n;
							}
							if (endMatch) {
								// found character sequence ending comment or quote
								return endPos + 1;
							}
						}
					}
					// character sequence ending comment or quote not found
					return statement.length;
				}

			}
		}
		return position;
	}

	/**
	 * Parse the SQL statement and locate any placeholders or named parameters.
	 * Named parameters are substituted for a JDBC placeholder.
	 * 
	 * @param sql
	 *            the SQL statement
	 * @return the parsed statement, represented as ParsedSql instance
	 */
	private static ProcessedInput processSqlStatement(final String sql) {
		Map<String, String> parsedSqlResult = new HashMap<String, String>();

		Set<String> namedParameters = new HashSet<String>();
		String sqlToUse = sql;
		List<ParameterHolder> parameterList = new ArrayList<ParameterHolder>();

		char[] statement = sql.toCharArray();
		int namedParameterCount = 0;
		int unnamedParameterCount = 0;
		int totalParameterCount = 0;

		int escapes = 0;
		int i = 0;
		while (i < statement.length) {
			int skipToPosition = i;
			while (i < statement.length) {
				skipToPosition = skipCommentsAndQuotes(statement, i);
				if (i == skipToPosition) {
					break;
				} else {
					i = skipToPosition;
				}
			}
			if (i >= statement.length) {
				break;
			}
			char c = statement[i];
			if (c == ':' || c == '&') {
				int j = i + 1;
				if (j < statement.length && statement[j] == ':' && c == ':') {
					// Postgres-style "::" casting operator - to be skipped.
					i = i + 2;
					continue;
				}
				String parameter = null;
				if (j < statement.length && c == ':' && statement[j] == '{') {
					// :{x} style parameter
					while (j < statement.length && !('}' == statement[j])) {
						j++;
						if (':' == statement[j] || '{' == statement[j]) {
							throw new IllegalArgumentException("Parameter name contains invalid character '"
									+ statement[j] + "' at position " + i + " in statement " + sql);
						}
					}
					if (j >= statement.length) {
						throw new IllegalArgumentException("Non-terminated named parameter declaration at position "
								+ i + " in statement " + sql);
					}
					if (j - i > 3) {
						parameter = sql.substring(i + 2, j);
						namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
						totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j + 1, parameter);
					}
					j++;
				} else {
					while (j < statement.length && !isParameterSeparator(statement[j])) {
						j++;
					}
					if (j - i > 1) {
						parameter = sql.substring(i + 1, j);
						namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
						totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j, parameter);
					}
				}
				i = j - 1;
			} else {
				if (c == '\\') {
					int j = i + 1;
					if (j < statement.length && statement[j] == ':') {
						// this is an escaped : and should be skipped
						sqlToUse = sqlToUse.substring(0, i - escapes) + sqlToUse.substring(i - escapes + 1);
						escapes++;
						i = i + 2;
						continue;
					}
				}
				if (c == '?') {
					unnamedParameterCount++;
					totalParameterCount++;
				}
			}
			i++;
		}
		ProcessedInput processedInput = new ProcessedInput(sql);
		
		for (ParameterHolder ph : parameterList) {
			processedInput.addParameter(ph.getParameterName(), ph.getStartIndex(), ph.getEndIndex());
		}
		
		return processedInput;
	}
	
	/**
	 * Parse the SQL statement and locate any placeholders or named parameters.
	 * Named parameters are substituted for a JDBC placeholder and any select list
	 * is expanded to the required number of placeholders.  Select lists may contain
	 * an array of objects and in that case the placeholders will be grouped and
	 * enclosed with parantheses.  This allows for the use of "expression lists" in
	 * the SQL statement like:<br/>
	 * select id, name, state from table where (name, age) in (('John', 35), ('Ann', 50))
	 * <p>The parameter values passed in are used to determine the number of
	 * placeholder to be used for a select list. Select lists should be limited
	 * to 100 or fewer elements. A larger number of elements is not guaramteed to
	 * be supported by the database and is strictly vendor-dependent.
     *
	 * @param processedInput processed Input which stores original SQL and parsed SQL parameters
	 * @return the SQL statement with substituted parameters
	 */
	private String generateParsedSql(ProcessedInput processedInput) {
		String originalSql = processedInput.getOriginalSql();
		StringBuilder actualSql = new StringBuilder();
		List paramNames = processedInput.getSqlParameterNames();
		int lastIndex = 0;
		for (int i = 0; i < paramNames.size(); i++) {
			int[] indexes = processedInput.getSqlParameterBoundaries().get(i);
			int startIndex = indexes[0];
			int endIndex = indexes[1];
			actualSql.append(originalSql.substring(lastIndex, startIndex));
			actualSql.append("?");
			lastIndex = endIndex;
		}
		actualSql.append(originalSql.substring(lastIndex, originalSql.length()));
		return actualSql.toString();
	}

    /**
     * Searches cache by original SQL as key and returned cached ProcessedInput instance
     *
     * @param originalSql original SQL string
     * @return cached ProcessedInput, null otherwise
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
			return processedInput;
		}
	}

    /**
     * Puts ProcessedInput instance into cache. Later can be retrieved by searching using original SQL string
     *
     * @param processedInput ProcessedInput instance which should be stored into Cache
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

    /**
     * Checks if specified symbol is parameter separator
     *
     * @param c symbol which would be checked
     * @return true if symbol as parameter separator
     */
	private static boolean isParameterSeparator(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		}
		for (char separator : PARAMETER_SEPARATORS) {
			if (c == separator) {
				return true;
			}
		}
		return false;
	}

    /**
     * Adds named parameter into list
     *
     * @param parameterList parameter list to which parameter would be added
     * @param totalParameterCount total parameters count. would be increased by one after invocation
     * @param escapes amount of escapes
     * @param i start position of parameter
     * @param j end position of parameter
     * @param parameter actual parameter
     * @return updated total parameters count
     */
	private static int addNamedParameter(List<ParameterHolder> parameterList, int totalParameterCount, int escapes, int i, int j,
			String parameter) {
		parameterList.add(new ParameterHolder(parameter, i - escapes, j - escapes));
		totalParameterCount++;
		return totalParameterCount;
	}

    /**
     * Adds named parameter into List
     *
     * @param namedParameters List of named parameters which should be updated
     * @param namedParameterCount total named parameters count. would be increased by one if it is "new" parameter
     * @param parameter named parameter
     * @return updated(if it is "new" parameter) total named parameters count
     */
	private static int addNewNamedParameter(Set<String> namedParameters, int namedParameterCount, String parameter) {
		if (!namedParameters.contains(parameter)) {
			namedParameters.add(parameter);
			namedParameterCount++;
		}
		return namedParameterCount;
	}

    /**
     * Model class to store parameter properties: name, start position, end position
     */
	private static class ParameterHolder {
		private String parameterName;
		private int startIndex;
		private int endIndex;

		public ParameterHolder(String parameterName, int startIndex, int endIndex) {
			super();
			this.parameterName = parameterName;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public String getParameterName() {
			return parameterName;
		}

		public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}
	}
}
