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

package org.midao.core.profiler;

import org.midao.core.MidaoConfig;
import org.midao.core.MidaoLogger;

/**
 * Profiler Factory allows wrapping instance into Profiling Proxy.
 */
public class ProfilerFactory {

    /**
     * Function wraps Object into Profiling Java Proxy.
     * Used to wrap QueryRunner instance with Java Proxy
     *
     * @param obj Object which would be wrapped into Profiling Proxy
     * @return Java Proxy with wrapped input object
     */
	public static Object newInstance(Object obj) {
		
		if (MidaoLogger.isSLF4jAvailable() == true && MidaoLogger.isSLF4jImplementationAvailable() == false) {
			// Logging depends on slf4j. If it haven't found any logging system
			// connected - it is turned off.
			// In such case there is no need to output profiling information as
			// it won't be printed out.
			return obj;
		} else {
			if (MidaoConfig.isProfilerEnabled() == true) {
				return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
						new BaseInvocationHandler(obj, MidaoConfig.getProfilerOutputFormat()));
			} else {
				return obj;
			}
		}
	}

}