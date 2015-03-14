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

import org.midao.jdbc.core.handlers.output.OutputHandler;

/**
 * Lazy output handler handles output from {@link org.midao.jdbc.core.statement.LazyStatementHandler} and allows lazy
 * loading of data from it.
 * <p/>
 * <p>Useful in cases when you have to fetch a lot of data, but not all of it will be actually needed. For example
 * if user searched for 100 pages but will be able to browse few pages only.</p>
 * <p/>
 * <p>Please be informed that while using Lazy output handler you are responsible for manual resource handling.
 * Please do not forget to use {@link org.midao.jdbc.core.service.QueryRunnerService#commit()} and
 * {@link #close()} after you finish with it's usage</p>
 * <p/>
 * <p>
 * Example usage:
 * QueryRunnerService runner = MjdbcFactory.getQueryRunner(ds, null, LazyStatementHandler.class);
 * runner.setManualTransactionMode(true);
 * ...
 * LazyOutputHandler output = ...
 * ...
 * output.close();
 * runner.commit(); // use it even if you only executed query
 * </p>
 * <p/>
 * <p><i>Please be aware that functionality might be changed and most likely will be extended in future. Features which
 * might be added in future: scrollability(not only forward) and updatability</i></p>
 */
public interface LazyOutputHandler<T, S> extends OutputHandler<T> {

    /**
     * Checks if lazy list has next element
     *
     * @return true if list has next element
     */
    public boolean hasNext();

    /**
     * Returns next element from lazy list
     *
     * @return next element from list. Null is returned if no value is present
     */
    public S getNext();

    /**
     * Returns current element from lazy list
     *
     * @return current element from list. Null is returned if no value is present
     */
    public S getCurrent();

    /**
     * Function closes all resources used by this Lazy output handler instance.
     * Should be invoked every time you finished with reading data, otherwise resource leaks and/or errors might happen.
     */
    public void close();

}
