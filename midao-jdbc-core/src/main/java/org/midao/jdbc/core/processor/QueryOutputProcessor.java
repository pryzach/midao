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

package org.midao.jdbc.core.processor;

import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

/**
 * Query Output Processor description.
 * This processor is used by {@link org.midao.jdbc.core.handlers.output.OutputHandler} during processing of
 * Query output and converting it into Java classes
 */
public interface QueryOutputProcessor {

    /**
     * Converts query output (first line) into array of values
     *
     * @param paramsList query output(1st line is avoided).
     * @return array of values
     */
    public Object[] toArray(List<QueryParameters> paramsList);

    /**
     * Converts query output into list. Every query output line is converted into array and put into List
     *
     * @param paramsList query output(1st line is avoided).
     * @return array of values
     */
    public List<Object[]> toArrayList(List<QueryParameters> paramsList);

    /**
     * Converts query output(first line) into Java Object.
     *
     * @param paramsList query output(1st line is avoided)
     * @param type Java Class definition from which Object would be created
     * @return filled object
     * @throws org.midao.jdbc.core.exception.MjdbcException
     */
    public <T> T toBean(List<QueryParameters> paramsList, Class<T> type) throws MjdbcException;

    /**
     * Converts query parameters into Java Object.
     *
     * @param params query parameters from which Object would be filled
     * @param type Java Class definition from which Object would be created
     * @return filled object
     * @throws org.midao.jdbc.core.exception.MjdbcException
     */
    public <T> T toBean(QueryParameters params, Class<T> type) throws MjdbcException;

    /**
     * Converts query output into Java Object.
     * Every line would be converted into Java Object and added into List
     *
     * @param paramsList query output(1st line is avoided)
     * @param type Java Class definition from which Object would be created
     * @return List of Map's
     * @throws org.midao.jdbc.core.exception.MjdbcException
     */
    public <T> List<T> toBeanList(List<QueryParameters> paramsList, Class<T> type) throws MjdbcException;

    /**
     * Converts query output(first line) into Map.
     *
     * @param paramsList query output(1st line is avoided)
     * @return filled object
     */
    public Map<String, Object> toMap(List<QueryParameters> paramsList);

    /**
     * Converts query parameters into Map.
     *
     * @param params query parameters from which Object would be filled
     * @return filled object
     */
    public Map<String, Object> toMap(QueryParameters params);

    /**
     * Converts query output into Map.
     * Every line would be converted into Map and added into List
     *
     * @param paramsList query output(1st line is avoided)
     * @return List of Map's
     */
    public List<Map<String, Object>> toMapList(List<QueryParameters> paramsList);

    /**
     * Reads value from @params (by @position) and converts it into value
     * according to type specified in @prop
     *
     * This function is not used outside OutputProcessor. Might be removed in future
     *
     * @param params Query Parameters
     * @param position position which would be read
     * @param prop PropertyDescriptor according to which value would be converted
     * @return converted value
     * @throws org.midao.jdbc.core.exception.MjdbcException
     */
    public Object processValue(QueryParameters params, Integer position, PropertyDescriptor prop) throws MjdbcException;
}
