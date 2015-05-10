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

import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.processor.QueryOutputProcessor;

/**
 * Base OutputHandler class
 */
public abstract class AbstractOutputHandler<T> implements OutputHandler<T> {
    protected QueryOutputProcessor outputProcessor = MjdbcConfig.getDefaultQueryOutputProcessor();

    /**
     * Initializes AbstractOutputHandler ancestor instance
     */
    AbstractOutputHandler() {
    }

    /**
     * Initializes AbstractOutputHandler ancestor instance
     *
     * @param outputProcessor Query output processor
     */
    AbstractOutputHandler(QueryOutputProcessor outputProcessor) {
        this.outputProcessor = outputProcessor;
    }

}
