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

/**
 * Lazy scrollable output handler handles output from {@link org.midao.jdbc.core.statement.LazyStatementHandler} and allows lazy
 * loading of data from it.
 *
 * <p>
 * If underlying JDBC Driver doesn't support scrollable {@link java.sql.ResultSet} - exception would be thrown.
 * In order to check if JDBC Driver supports such functionality - please read relevant JDBC driver documentation
 * and/or create POC (Proof of concept).
 * </p>
 * <p>
 * Scroll supports only one ResultSet due to the fact that some JDBC Drivers close previous ResultSet before returning next one.
 * In cases where multiple ResultSets need to be handled - please use cached or standard(non-scrollable) lazy output.
 * </p>
 */
public interface LazyScrollOutputHandler<T, S> extends LazyOutputHandler<T, S> {

    /**
     * Checks if lazy list has previous element
     *
     * @return true if list has previous element
     */
    public boolean hasPrev();

    /**
     * Returns previous element from lazy list
     *
     * @return previous element from list. Null is returned if no value is present
     */
    public S getPrev();

    /**
     * Moves cursor to specified position.
     * In order to read value after cursor reposition - {@link org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler#getCurrent()} can be used.
     *
     * <p>
     * In order to move to first position - use "1". In order to move to last - use "-1"
     * </p>
     *
     * @param row absolute row number to which cursor should be moved
     * @return
     */
    public boolean moveTo(int row);

    /**
     * Moves cursor relatively to current position. Positive values will move cursor forward, negative - backward.
     * In order to read value after cursor reposition - {@link org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler#getCurrent()} can be used.
     *
     * <p>
     * In order to receive current position - {@link #position()} can be used
     * </p>
     *
     * @param rows relative row number to which cursor should be moved.
     * @return
     */
    public boolean moveRelative(int rows);

    /**
     * Returns current cursor position.
     *
     * @return currect cursor position.
     */
    public int position();
}
