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

package org.midao.jdbc.core.profiler;


import org.midao.jdbc.core.MidaoLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Proxy which is used for Profiling of QueryRunner
 * Uses Java Proxy
 */
public class BaseInvocationHandler implements java.lang.reflect.InvocationHandler {
	private static final MidaoLogger logger = MidaoLogger.getLogger(BaseInvocationHandler.class);
	
	private final Object obj;
	private final String profilerOutputFormat;

    /**
     * Creates new BaseInvocationHandler instance
     *
     * @param obj Object which would be proxied
     * @param profilerOutputFormat profiler output string format description
     */
	public BaseInvocationHandler(Object obj, String profilerOutputFormat) {
		this.obj = obj;
		this.profilerOutputFormat = profilerOutputFormat;
	}

    /**
     * @see {@link BaseInvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])}
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result;
		String className = method.getDeclaringClass().getSimpleName();
		String methodName = method.getName();
		String parameters = Arrays.deepToString(args);
		
		long startInvokeTime = System.currentTimeMillis();
		double executionTime = 0;
		
		try {
			result = method.invoke(obj, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (Exception e) {
			throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
		}
		executionTime = (System.currentTimeMillis() - startInvokeTime) * 1.0 / 1000;
		
		logger.info(String.format(this.profilerOutputFormat, new Object[] {
				className, 
				methodName, 
				parameters, 
				executionTime}));
		
		return result;
	}
}