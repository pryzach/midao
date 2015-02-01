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

import org.midao.jdbc.core.handlers.input.AbstractInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.utils.InputUtils;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.processor.QueryInputProcessor;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * Base InputHandler for Bean and Map InputHandlers
 */
public abstract class AbstractNamedInputHandler<T> extends AbstractInputHandler<T> {

    /**
     * {@inheritDoc}
     */
    public abstract <T> T updateInput(QueryParameters updatedInput);

    /**
     * {@inheritDoc}
     */
    public abstract String getEncodedQueryString();

    /**
     * Initializes parent part of NamedInputHandler
     *
     * @param processor Query input processor
     */
    protected AbstractNamedInputHandler(QueryInputProcessor processor) {
        super(processor);
    }

    /**
     * Updates original map with values from source Map
     * used in {@link #updateInput(org.midao.jdbc.core.handlers.model.QueryParameters)}
     *
     * @param target target Map
     * @param source source Map
     * @return cloned Map with updated values
     */
    protected Map<String, Object> updateMap(Map<String, Object> target, Map<String, Object> source) {
        Map<String, Object> resultMap = new HashMap<String, Object>(target);

        String keyWOClassName = null;

        for (String sourceKey : source.keySet()) {
            keyWOClassName = InputUtils.removeClassName(sourceKey);

            if (target.containsKey(sourceKey) == true) {
                resultMap.put(sourceKey, source.get(sourceKey));
            } else if (target.containsKey(keyWOClassName) == true) {
                resultMap.put(keyWOClassName, source.get(keyWOClassName));
            }
        }

        return resultMap;
    }

    /**
     * Updates bean with values from source.
     *
     * @param object Bean object to update
     * @param source Map which would be read
     * @return cloned bean with updated values
     */
    protected T updateBean(T object, Map<String, Object> source) {
        T clone = copyProperties(object);

        updateProperties(clone, source);

        return clone;
    }

    /**
     * Updates bean by using PropertyDescriptors. Values are read from
     * source.
     * Is used by {@link #updateBean(Object, java.util.Map)}
     *
     * @param target target Bean
     * @param source source Map
     */
    private void updateProperties(T target, Map<String, Object> source) {
        Map<String, PropertyDescriptor> mapTargetProps = MappingUtils.mapPropertyDescriptors(target.getClass());

        for (String sourceKey : source.keySet()) {
            if (mapTargetProps.containsKey(sourceKey) == true) {
                MappingUtils.callSetter(target, mapTargetProps.get(sourceKey), source.get(sourceKey));
            }
        }
    }

    /**
     * Creates empty bean {@link #createEmpty(Class)} and fills it with values
     * from source effectively cloning it
     *
     * @param source source Bean which would be cloned
     * @return cloned Bean
     */
    private T copyProperties(T source) {
        T target = createEmpty(source.getClass());

        Map<String, PropertyDescriptor> mapTargetProps = MappingUtils.mapPropertyDescriptors(target.getClass());
        Map<String, PropertyDescriptor> mapSourceProps = MappingUtils.mapPropertyDescriptors(source.getClass());

        Object value = null;

        for (String propertyName : mapSourceProps.keySet()) {
            value = MappingUtils.callGetter(source, mapSourceProps.get(propertyName));

            MappingUtils.callSetter(target, mapTargetProps.get(propertyName), value);
        }

        return target;
    }

    /**
     * Creates new empty Bean instance
     *
     * @param clazz Class description which would be instantiated
     * @return empty Bean instance
     */
    private T createEmpty(Class<?> clazz) {
        T emptyInstance = null;

        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class is an interface: " + clazz.getName());
        }

        try {
            emptyInstance = (T) clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException(clazz.getName() + ". Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(clazz.getName() + "Is the constructor accessible?", ex);
        }

        return emptyInstance;
    }

    /**
     * {@inheritDoc}
     */
    abstract public String getQueryString();

    /**
     * {@inheritDoc}
     */
    abstract public QueryParameters getQueryParameters();
}
