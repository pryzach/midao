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

package org.midao.jdbc.core.handlers.output.lazy;

import org.midao.jdbc.core.handlers.model.LazyCacheIterator;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.processor.QueryOutputProcessor;

/**
 */
public abstract class AbstractLazyOutputHandler {
    protected QueryOutputProcessor processor;

    protected LazyCacheIterator<QueryParameters> queryParams;

    protected QueryParameters innerGetCurrent() {
        QueryParameters params = queryParams.getCurrent();

        return params;
    }

    protected boolean innerHasNext() {
        return queryParams.hasNext();
    }

    protected QueryParameters innerGetNext() {
        QueryParameters params = queryParams.getNext();

        return params;
    }

    protected void innerClose() {
        this.queryParams.getSource().close();
    }
}
