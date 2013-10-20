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

package org.midao.jdbc.core.processor;

import org.midao.jdbc.core.handlers.model.ProcessedInput;

/**
 * Extension of basic {@link org.midao.jdbc.core.processor.QueryInputProcessor} implementation: {@link BasicQueryInputProcessor}.
 *
 * <p>
 *     Only difference from parent {@link BasicQueryInputProcessor} - is that is processes "#{xxx}" instead of ":xxx".
 *     That change would allow to process parameters which can include not only parameter name, but type like: "#{xxx,jdbcType=VARCHAR}"
 * </p>
 *
 * <p>
 *     <strong>
 *     Please be careful as currently only "jdbcType" (varchar/int; types which listed in {@link org.midao.jdbc.core.MjdbcTypes})
 *     and "mode" (in, out, inout; as listed in {@link org.midao.jdbc.core.handlers.model.QueryParameters.Direction}) are supported.
 *     </strong>
 * </p>
 */
public class ExtendedQueryInputProcessor extends BasicQueryInputProcessor {
	private static final char FILL_SYMBOL = '#';

	private static final String PARAMETER_SEPARATOR = "[\"\'&,;()|=+\\-*%/\\<>\\^\\s{}]";

	/**
	 * RegEx for searching for a parameter in SQL String
	 */
	private static final String REGEX_PARAMETER_SEARCH = PARAMETER_SEPARATOR + "[#][{][a-zA-Z0-9_\\-.,=]{1,}?[}](?=" + PARAMETER_SEPARATOR + ")";

	/**
	 * RegEx for searching for text and comment blocks
	 */
	//private static final String REGEX_SKIP_BLOCK_SEARCH = "(['].+?['])|([\"].+?[\"])|([-][-].+?[\n])|([/][*].+?[*][/])";
    private static final String REGEX_SKIP_BLOCK_SEARCH = "(['].+?['])|([\"].+?[\"])|([-][-].+?[\n])|([#][^{].+?[\n])|([/][*].+?[*][/])";

    @Override
    protected String getRegexParameterSearch() {
        return REGEX_PARAMETER_SEARCH;
    }

    @Override
    protected String getRegexSkipBlockSearch() {
        return REGEX_SKIP_BLOCK_SEARCH;
    }

    @Override
    protected String getParameterName(String preProcessedSql, int paramStart, int paramEnd) {
        return preProcessedSql.substring(paramStart + 2, paramEnd - 1).toLowerCase();
    }

    @Override
    protected void addParameter(ProcessedInput processedInput, String parameterDescription, int paramStart, int paramEnd) {
        if (parameterDescription.contains(",") == true) {
            String[] parameterOptions = parameterDescription.toLowerCase().split(",");
            String parameterName = parameterOptions[0];
            String parameterType = null;
            String parameterDirection = null;

            for (int i = 1; i < parameterOptions.length; i++) {
                if (parameterOptions[i].startsWith("jdbctype=") == true) {
                    parameterType = parameterOptions[i].substring(9).toUpperCase();
                }
                if (parameterOptions[i].startsWith("mode=") == true) {
                    parameterDirection = parameterOptions[i].substring(5).toUpperCase();
                }
            }

            processedInput.addParameter(parameterName, paramStart, paramEnd, parameterType, parameterDirection);
        } else {
            processedInput.addParameter(parameterDescription, paramStart, paramEnd);
        }
    }
}
