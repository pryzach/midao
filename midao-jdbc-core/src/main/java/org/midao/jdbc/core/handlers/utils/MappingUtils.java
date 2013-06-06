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

package org.midao.jdbc.core.handlers.utils;

import org.midao.jdbc.core.MidaoConstants;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.utils.AssertUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of utilities used to map values from one type into another (Bean->Map, Map->Bean)
 */
public class MappingUtils {

    /**
     * Converts Java Object into Map<String, Object>.
     * Keys and Values are read from PropertyDescriptors
     *
     * @param inputParameter Object which would be converted into Map
     * @param propertyDescriptors Array of Class PropertyDescriptors
     * @return Map<String, Object> with values from Object
     */
    public static Map<String, Object> toMap(Object inputParameter, PropertyDescriptor[] propertyDescriptors) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	Object propertyValue = null;
    	
    	for (PropertyDescriptor property : propertyDescriptors) {
    		propertyValue = callGetter(inputParameter, property);
    		
    		if ("class".equals(property.getName()) == false) {
    			resultMap.put(property.getName(), propertyValue);
    		}
    	}
    	
    	return resultMap;
    }

    /**
     * Reads property descriptors of class
     *
     * @param clazz Class for which we are getting property descriptors
     * @return Array of Class PropertyDescriptors
     */
    public static PropertyDescriptor[] propertyDescriptors(Class<?> clazz) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);

        } catch (IntrospectionException ex) {
            throw new IllegalArgumentException(
                "Bean introspection failed: " + ex.getMessage());
        }

        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Reads property descriptors of class and puts them into Map.
     * Key for map is read from property descriptor.
     *
     * @param clazz Class for which Property Descriptors would be read
     * @return Map of Property Descriptors for specified class
     */
    public static Map<String, PropertyDescriptor> mapPropertyDescriptors(Class<?> clazz) {
    	PropertyDescriptor[] properties = propertyDescriptors(clazz);
    	Map<String, PropertyDescriptor> mappedProperties = new HashMap<String, PropertyDescriptor>();
    	
    	for (PropertyDescriptor property : properties) {
    		if ("class".equals(property.getName()) == false) {
    			mappedProperties.put(property.getName(), property);
    		}
    	}
    	
    	return mappedProperties;
    }

    /**
     * Invokes Property Descriptor Getter and returns value returned by that function.
     *
     * @param target Object Getter of which would be executed
     * @param prop Property Descriptor which would be used to invoke Getter
     * @return Value returned from Getter
     */
    public static Object callGetter(Object target, PropertyDescriptor prop) {

    	Object result = null;
        Method getter = prop.getReadMethod();
        
        if (getter == null) {
            throw new RuntimeException("No read method for bean property "
                    + target.getClass() + " " + prop.getName());
        }
        try {
        	result = getter.invoke(target, new Object[0]);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Couldn't invoke method: " + getter,
                    e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Couldn't invoke method with 0 arguments: " + getter, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't invoke method: " + getter,
                    e);
        }

        return result;
    }

    /**
     * Invokes Property Descriptor Setter and sets value @value into it.
     *
     * @param target Object Getter of which would be executed
     * @param prop Property Descriptor which would be used to invoke Getter
     * @param value Value which should be set into @target
     */
    public static void callSetter(Object target, PropertyDescriptor prop, Object value) {

        Method setter = prop.getWriteMethod();

        if (setter == null) {
            return;
        }

        try {
        	
            setter.invoke(target, new Object[]{value});
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Cannot set " + prop.getName() + ": " + e.getMessage(), e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "Cannot set " + prop.getName() + ": " + e.getMessage(), e);

        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                "Cannot set " + prop.getName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Converts java.sql.ResultSet into List of QueryParameters.
     * Used for caching purposes to allow ResultSet to be closed and disposed.
     *
     * @param rs ResultSet values from which would be read
     * @return List of QueryParameters (one for each row)
     * @throws SQLException propagates SQLException sent from ResultSet
     */
	public static List<QueryParameters> convertResultSet(ResultSet rs) throws SQLException {
		List<QueryParameters> result = new ArrayList<QueryParameters>();
		String columnName = null;
		
		while (rs.next() == true) {
			QueryParameters params = new QueryParameters();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			for (int i = 1; i <= cols; i++) {
				
	            columnName = rsmd.getColumnLabel(i);
	            if (null == columnName || 0 == columnName.length()) {
	              columnName = rsmd.getColumnName(i);
	            }
				
	            params.set(columnName, rs.getObject(i));
	            params.updatePosition(columnName, i - 1);
			}
			
			result.add(params);
		}

        return result;	
	}

    /**
     * Creates new Instance of class specified.
     * Default Constructor should be visible in order to create new Instance
     *
     * @param clazz Class which should be instantiated
     * @return Empty Object
     * @throws SQLException in case of instantiation error
     */
    public static <T> T newInstance(Class<T> clazz) throws MidaoException {
        try {
            return clazz.newInstance();

        } catch (InstantiationException ex) {
            throw new MidaoException(
                "Failed to create instance: " + clazz.getName() + " - " + ex.getMessage());

        } catch (IllegalAccessException ex) {
            throw new MidaoException(
                    "Failed to create instance: " + clazz.getName() + " - " + ex.getMessage());
        }
    }

    /**
     * Invocation of {@link java.sql.Connection} setter functions
     * This function provides flexibility which required to use {@link java.sql.Connection}
     * with different Java versions: 5/6 etc.
     *
     * @param conn SQL Connection
     * @param functionName SQL Connection parameter name
     * @param value value which would be set
     * @throws MidaoException if property wasn't found
     */
    public static void invokeConnectionSetter(Connection conn, String functionName, Object value) throws MidaoException {
        if (MidaoConstants.connectionBeanDescription.containsKey(functionName) == true) {
            MappingUtils.callSetter(conn, MidaoConstants.connectionBeanDescription.get(functionName), value);
        } else {
            throw new MidaoException(String.format("Property %s wasn't found", functionName));
        }
    }

    /**
     * Invokes class function using Reflection
     *
     * @param object Instance which function would be invoked
     * @param functionName function name
     * @param parameters function parameters (array of Class)
     * @param values function values (array of Object)
     * @return function return
     * @throws MidaoException in case function doesn't exists
     */
    public static Object invokeFunction(Object object, String functionName, Class[] parameters, Object[] values) throws MidaoException {
        Object result = null;

        try {
            Method method = object.getClass().getMethod(functionName, parameters);
            method.setAccessible(true);
            result = method.invoke(object, values);
        } catch (Exception ex) {
            throw new MidaoException(ex);
        }

        return result;
    }

    /**
     * Invokes class function using Reflection
     *
     * @param clazz Class which function would be invoked
     * @param functionName function name
     * @param parameters function parameters (array of Class)
     * @param values function values (array of Object)
     * @return function return
     * @throws MidaoException in case function doesn't exists
     */
    public static Object invokeStaticFunction(Class clazz, String functionName, Class[] parameters, Object[] values) throws MidaoException {
        Object result = null;

        try {
            Method method = clazz.getMethod(functionName, parameters);
            method.setAccessible(true);
            result = method.invoke(null, values);
        } catch (Exception ex) {
            throw new MidaoException(ex);
        }

        return result;
    }

    /**
     * Checks if Instance has specified function
     *
     * @param object Instance which function would be checked
     * @param functionName function name
     * @param parameters function parameters (array of Class)
     * @return true if function is present in Instance
     */
    public static boolean hasFunction(Object object, String functionName, Class[] parameters) {
        boolean result = false;

        try {
            Method method = object.getClass().getMethod(functionName, parameters);

            if (method != null) {
                result = true;
            }
        } catch (NoSuchMethodException ex) {
            result = false;
        }

        return result;
    }

    /**
     * Checks if Instance implements specified Interface
     * {@link Class#isAssignableFrom(Class)} is not used as Interface might not be available
     * and String representation can only be used
     *
     * @param object Instance which would be checked
     * @param interfaceClass Interface with which it would be checked
     * @return true if Instance implements specified Interface
     */
    public static boolean objectImplements(Object object, String interfaceClass) {
        AssertUtils.assertNotNull(object);

        boolean result = false;

        Class[] interfaces = object.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getName().equals(interfaceClass) == true) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Checks if Instance extends specified Class
     * {@link Class#isAssignableFrom(Class)} is not used as Class might not be available
     * and String representation can only be used
     *
     * @param object Instance which would be checked
     * @param superClassName Class with which it would be checked
     * @return true if Instance extends specified Parent
     */
    public static boolean objectExtends(Object object, String superClassName) {
        AssertUtils.assertNotNull(object);

        boolean result = false;

        Class superClass = object.getClass().getSuperclass();

        if (superClass.getName().equals(superClassName) == true) {
            result = true;
        }

        return result;
    }

    /**
     * Checks if instance is of specified class
     *
     * @param object Instance which would be checked
     * @param className Class name with which it would be checked
     * @return true if Instance is of specified class
     */
    public static boolean objectInstanceOf(Object object, String className) {
        AssertUtils.assertNotNull(object);

        boolean result = false;

        Class clazz = object.getClass();

        if (clazz.getName().equals(className) == true) {
            result = true;
        }

        return result;
    }

    /**
     * Checks if instance can be cast to specified Class
     *
     * @param object Instance which would be checked
     * @param className Class name with which it would be checked
     * @return true if Instance can be cast to specified class
     * @throws MidaoException
     */
    public static boolean objectAssignableTo(Object object, String className) throws MidaoException {
        AssertUtils.assertNotNull(object);

        boolean result = false;

        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new MidaoException(ex);
        }

        result = clazz.isAssignableFrom(object.getClass());

        return result;
    }

    /**
     * Returns class static field value
     * Is used to return Constants
     *
     * @param clazz Class static field of which would be returned
     * @param fieldName field name
     * @return field value
     * @throws MidaoException if field is not present or access is prohibited
     */
    public static Object returnStaticField(Class clazz, String fieldName) throws MidaoException {
        Object result = null;

        Field field = null;
        try {
            field = clazz.getField(fieldName);
            result = field.get(null);
        } catch (NoSuchFieldException ex) {
            throw new MidaoException(ex);
        } catch (IllegalAccessException ex) {
            throw new MidaoException(ex);
        }

        return result;
    }
}
