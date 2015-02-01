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

package org.midao.jdbc.core.handlers;

import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.handlers.output.RowCountOutputHandler;
import org.midao.jdbc.core.processor.ExtendedQueryInputProcessor;
import org.midao.jdbc.core.processor.QueryInputProcessor;
import org.midao.jdbc.spring.processor.SpringQueryInputProcessor;

/**
 * Collection of constants used by Handlers.
 * Might be moved into different class before final release
 */
public class HandlersConstants {
    public static final String ERROR_PARAMETER_NOT_FOUND = "Parameter: %s wasn't found in values map";
    public static final String ERROR_WRONG_PARAMETER_COUNT = "Error! Incorrect parameters count. Expected: %d got %d";

    public static final OutputHandler<Integer> UPDATE_ROW_COUNT_HANDLER = new RowCountOutputHandler<Integer>();
    public static final QueryParameters EMPTY_QUERY_PARAMS = new QueryParameters();

    public static final QueryInputProcessor SPRING_PROCESSOR = new SpringQueryInputProcessor();
    public static final QueryInputProcessor IBATIS_PROCESSOR = new ExtendedQueryInputProcessor();

    public static final String STMT_UPDATE_COUNT = "updateCount";
}
