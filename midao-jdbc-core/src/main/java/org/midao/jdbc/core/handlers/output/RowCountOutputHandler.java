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

package org.midao.jdbc.core.handlers.output;

import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.List;

/**
 * Class which allows to receive amount of records updated.
 * Can be used in {@link org.midao.jdbc.core.AbstractQueryRunner#update(org.midao.jdbc.core.handlers.input.InputHandler, OutputHandler)}
 */
public class RowCountOutputHandler<T extends Number> extends AbstractOutputHandler<T> {

    /**
     * Creates new RowCountOutputHandler instance.
     */
    public RowCountOutputHandler() {
        super();
    }

    /**
     * Returns amount of records updated (value is calculated by JDBC Driver)
     *
     * @param outputList Query output
     * @return
     */
	public T handle(List<QueryParameters> outputList) {
		Number result = 0;
		
		if (outputList == null || outputList.isEmpty() == true) {
			throw new IllegalArgumentException("Error! Output should always contain at least one element");
		}
		
		QueryParameters stmtParams = outputList.get(0);
		
		if (stmtParams.containsKey(HandlersConstants.STMT_UPDATE_COUNT) == false) {
			throw new IllegalArgumentException("Error! Expected to get update count, but key wasn't found!");
		}
		
		result = (Integer) stmtParams.getValue(HandlersConstants.STMT_UPDATE_COUNT);
		
		return (T) result;
	}
}
