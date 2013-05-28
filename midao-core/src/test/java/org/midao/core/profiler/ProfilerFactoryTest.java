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

import junit.framework.Assert;
import org.junit.Test;
import org.midao.core.MidaoConfig;
import org.midao.core.MidaoLogger;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class ProfilerFactoryTest {
    @Test
    public void testNewInstance() throws Exception {
        List list = null;

        MidaoConfig.setProfilerEnabled(true);
        list = (List) ProfilerFactory.newInstance(new ArrayList());

        if (MidaoLogger.isSLF4jAvailable() == true ) {
            if (MidaoLogger.isSLF4jImplementationAvailable() == true) {
                Assert.assertEquals(true, Proxy.isProxyClass(list.getClass()));
            } else {
                Assert.assertEquals(false, Proxy.isProxyClass(list.getClass()));
            }
        } else {
            Assert.assertEquals(true, Proxy.isProxyClass(list.getClass()));
        }

        MidaoConfig.setProfilerEnabled(false);
        list = (List) ProfilerFactory.newInstance(new ArrayList());
        Assert.assertEquals(false, Proxy.isProxyClass(list.getClass()));
    }
}
