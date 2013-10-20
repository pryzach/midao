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

package org.midao.jdbc.core.handlers.xml;

import org.midao.jdbc.core.MjdbcConstants;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.processor.QueryInputProcessor;
import org.midao.jdbc.core.utils.AssertUtils;

import java.util.*;

/**
 * Xml Input/Output handler allows executing queries set in XML and loaded into {@link XmlRepositoryFactory}.
 *
 * For examples of XML query please go to the website (midao.org) or read {@link XmlRepositoryFactory} JavaDocs
 */
public class XmlInputOutputHandler<T> extends AbstractXmlInputOutputHandler<T> {
    private final List<Object> inputValues;
    private final String encodedSql;
    private final String sql;
    private final String name;
    private final Class<T> type;

    private final QueryParameters queryParameters;

    /**
     * Creates new XmlInputOutputHandler instance
     *
     * @param name query name
     */
    public XmlInputOutputHandler(String name) {
        this(name, new Object[0]);
    }

    /**
     * Creates new XmlInputOutputHandler instance
     *
     * @param type input and/or output bean type
     * @param name query name
     */
    public XmlInputOutputHandler(Class<T> type, String name) {
        this(type, name, new Object[0]);
    }

    /**
     * Creates new XmlInputOutputHandler instance
     *
     * @param name query name
     * @param values query values (can be or all primitive, beans/maps but mix their mix: or all primitive or all beans/maps)
     */
    public XmlInputOutputHandler(String name, Object... values) {
        this(HandlersConstants.IBATIS_PROCESSOR, name, values);
    }

    /**
     * Creates new XmlInputOutputHandler instance
     *
     * @param type input and/or output bean type
     * @param name query name
     * @param values query values (can be or all primitive, beans/maps but mix their mix: or all primitive or all beans/maps)
     */
    public XmlInputOutputHandler(Class<T> type, String name, Object... values) {
        this(HandlersConstants.IBATIS_PROCESSOR, type, name, values);
    }

    /**
     * Creates new XmlInputOutputHandler instance
     *
     * @param processor query input processor
     * @param name query name
     * @param values query values (can be or all primitive, beans/maps but mix their mix: or all primitive or all beans/maps)
     */
    public XmlInputOutputHandler(QueryInputProcessor processor, String name, Object... values) {
        this(processor, null, name, values);
    }

    /**
     * Creates new XmlInputOutputHandler instance
     *
     * @param processor query input processor
     * @param type input and/or output bean type
     * @param name query name
     * @param values query values (can be or all primitive, beans/maps but mix their mix: or all primitive or all beans/maps)
     */
    public XmlInputOutputHandler(QueryInputProcessor processor, Class<T> type, String name, Object... values) {
        super(processor);

        AssertUtils.assertNotNull(name);
        AssertUtils.assertNotTrue(name.length() == 0, "Name string cannot be empty");
        AssertUtils.assertNotNull(values, "Values cannot be null, please or send empty array or just don't set value there");

        this.type = type;
        this.name = processName(name);
        this.inputValues = Arrays.asList(values);

        ProcessedInput processedInput = null;
        processedInput = processor.processInput(XmlRepositoryFactory.getQueryString(this.name));

        this.encodedSql = processedInput.getOriginalSql();
        this.sql = processedInput.getParsedSql();

        // filling cloned empty processed input
        int primitiveAmount = primitiveAmount(values);

        if (this.inputValues.size() == 0) {
            // no values are set
            processedInput.fillParameterValues(MjdbcConstants.EMPTY_VALUE_MAP);
        } else if (primitiveAmount == this.inputValues.size()) {
            // every value is primitive, so we are setting them in order received
            AssertUtils.assertNotFalse(this.inputValues.size() == processedInput.getAmountOfParameters(),
                    "In case when primitive values are supplied - amount of values should be equal to amount of parameters \n" +
                    "(if same parameter used twice - value should be specified twice as well)\n" +
                    "In case one parameter used more than once - map might be more convenient solution.");

            processedInput.setSqlParameterValues(this.inputValues);
        } else if (primitiveAmount == 0) {
            // checking for maps and beans
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            Map<String, Object> mergedMap;
            Map<String, Object> map = null;
            Object value = null;

            for (int i = 0; i < this.inputValues.size(); i++) {
                value = this.inputValues.get(i);

                if (value instanceof Map) {
                    // safer, but expensive casting
                    mapList.add(new HashMap<String, Object>((Map) value));

                } else if (MappingUtils.isCustomBean(value) == true) {
                    map = MappingUtils.toMap(value, MappingUtils.propertyDescriptors(value.getClass()));
                    mapList.add(map);

                } else {
                    throw new MjdbcRuntimeException("Only all primitive, or mix of maps and custom beans are accepted");
                }
            }

            mergedMap = mergeMaps(this.encodedSql, mapList, false);

            processedInput.fillParameterValues(mergedMap);
        }

        this.queryParameters = new QueryParameters(processedInput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueryString() {
        return this.sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryParameters getQueryParameters() {
        return this.queryParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getOutputType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Overrider getOverrides() {
        return XmlRepositoryFactory.getOverride(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T handle(List<QueryParameters> outputList) throws MjdbcException {
        OutputHandler<T> outputHandler = XmlRepositoryFactory.getOutputHandler(this);

        return outputHandler.handle(outputList);
    }

    /**
     * Processes name supplied via constructor and adds class prefix if {@link #type} was set via constructor
     *
     * @param name query name (without prefix)
     * @return query name with prefix
     */
    private String processName(String name) {
        String prefix = "";

        if (this.type != null) {
            prefix = this.type.getName() + ".";
        }

        return prefix + name;
    }

    /**
     * Iterates over values and returns amount of primitive values in it.
     * Used to check if values supplied via constructors aren't mixed, as only primitive or complex types are allows
     *
     * @param values values which should be counted
     * @return amount of primitive values
     */
    private int primitiveAmount(Object... values) {
        int result = 0;

        for (int i = 0; i < values.length; i++) {
            if (MappingUtils.isPrimitive(values[i]) == true) {
                result++;
            }
        }

        return result;
    }
}
