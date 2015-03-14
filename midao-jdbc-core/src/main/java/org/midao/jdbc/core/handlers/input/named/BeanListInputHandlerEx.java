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

package org.midao.jdbc.core.handlers.input.named;

import org.midao.jdbc.core.handlers.HandlersConstants;

import java.util.Map;

/**
 * Same as {@link org.midao.jdbc.core.handlers.input.named.BeanListInputHandler} with the difference that it uses {@link org.midao.jdbc.core.processor.ExtendedQueryInputProcessor}
 * by default.
 */
public class BeanListInputHandlerEx<T> extends BeanListInputHandler<T> {

    /**
     * Creates new SpringBeanListInputHandler instance
     *
     * @param encodedQuery   encoded Query
     * @param inputParameter input Bean List
     */
    public BeanListInputHandlerEx(String encodedQuery, Map<String, T> inputParameter) {
        super(HandlersConstants.IBATIS_PROCESSOR, encodedQuery, inputParameter);
    }

}
