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

import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.processor.QueryOutputProcessor;

import java.util.List;

/**
 * Converts query output into list of object array
 */
public class ArrayListOutputHandler extends AbstractOutputListHandler<Object[]> {

    /**
     * Creates new ArrayListOutputHandler instance.
     */
    public ArrayListOutputHandler() {
        super();
    }

    /**
     * Creates new ArrayListOutputHandler instance.
     *
     * @param processor Query output processor
     */
    public ArrayListOutputHandler(QueryOutputProcessor processor) {
        super(processor);
    }

    /**
     * Converts query output into list of object array
     *
     * @param outputList Query output
     * @return List of object array converted from query output
     */
    public List<Object[]> handle(List<QueryParameters> outputList) {
        return this.outputProcessor.toArrayList(outputList);
    }

}
