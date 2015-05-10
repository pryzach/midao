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

package org.midao.jdbc.core.handlers.input;

import org.midao.jdbc.core.handlers.model.QueryParameters;

/**
 * Input Handler is responsible for handling query input parameters (SQL string and named parameters)
 */
public interface InputHandler<T> {

    /**
     * Returns parsed SQL String (without named parameters)
     *
     * @return parsed SQL String
     */
    public String getQueryString();

    /**
     * Returns named parameters and their values
     *
     * @return QueryParameters filled with parameters and their values
     */
    public QueryParameters getQueryParameters();

}
