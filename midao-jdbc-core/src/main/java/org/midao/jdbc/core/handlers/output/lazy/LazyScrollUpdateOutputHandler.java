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
 * <p>
 * If underlying JDBC Driver doesn't support scrollable/updateable {@link java.sql.ResultSet} - exception would be thrown.
 * In order to check if JDBC Driver supports such functionality - please read relevant JDBC driver documentation
 * and/or create POC (Proof of concept).
 * </p>
 * <p>
 * Scroll/Update supports only one ResultSet due to the fact that some JDBC Drivers close previous ResultSet before returning next one.
 * In cases where multiple ResultSets need to be handled - please use cached or standard(non-scrollable/updateable) lazy output.
 * </p>
 */
public interface LazyScrollUpdateOutputHandler<T, S> extends LazyScrollOutputHandler<T, S>, LazyUpdateOutputHandler<T, S> {
    // nothing to add here
}
