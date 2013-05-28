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

package org.midao.core.exception;

import junit.framework.Assert;
import org.junit.Test;
import org.midao.core.MidaoConfig;
import org.midao.core.handlers.output.MapOutputHandler;
import org.mockito.Mock;

import java.sql.SQLException;

public class BaseExceptionHandlerTest extends ExceptionHandlerTest {

    BaseExceptionHandler baseExceptionHandler = new BaseExceptionHandler();
    @Mock BaseExceptionHandler mockBaseExceptionHandler;
	
    @Test
	public void testBaseExceptionHandler() throws SQLException {
    	MidaoConfig.setDefaultExceptionHandler(baseExceptionHandler);
    	
    	try {
    		runner.query("SQL generates Exception", new MapOutputHandler());
    	} catch (Exception ex) {
    		Assert.assertEquals(" Query: SQL generates Exception Parameters: [QueryParameters CI { }]", ex.getMessage());
    	}
	}

    @Override
    protected ExceptionHandler getExceptionHandler() {
        return mockBaseExceptionHandler;
    }
}
