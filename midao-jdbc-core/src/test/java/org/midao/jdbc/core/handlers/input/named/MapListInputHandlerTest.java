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

import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * @author Zak
 */
public class MapListInputHandlerTest extends BaseInputHandlerTest {
	public void testQueryString() {
		Map<String, Map<String, Object>> parameterValues = new HashMap<String, Map<String, Object>>();
		parameterValues.put("cat", this.catMap);
		parameterValues.put("dog", this.dogMap);
		
		MapListInputHandler inputHandler = new MapListInputHandler(this.getEncodedMultipleParameterQuery(), parameterValues);
		String testEncodedQueryString = inputHandler.getQueryString();
		
		assertEquals(testEncodedQueryString, this.decodedMultipleParameterQuery);
	}
	
	public void testQueryParameters() {
		Map<String, Map<String, Object>> parameterValues = new HashMap<String, Map<String, Object>>();
		parameterValues.put("cat", this.catMap);
		parameterValues.put("dog", this.dogMap);
		
		MapListInputHandler inputHandler = new MapListInputHandler(this.getEncodedMultipleParameterQuery(), parameterValues);
		QueryParameters testParameters = inputHandler.getQueryParameters();
		
		assertNotNull(testParameters);
		assertEquals(testParameters.size(), this.multipleParameterQueryParameters.length);
		
		assertTrue(Arrays.equals(testParameters.getValuesArray(), this.multipleParameterQueryParameters));
	}
	
	public void testNullHandler() {
		try {
			MapListInputHandler inputHandler = new MapListInputHandler(null, null);
			fail();
		} catch (IllegalArgumentException ex) {
			assertNotNull(ex);
		}
	}
	
	public void testNullQueryHandler() {
		try {
			MapListInputHandler inputHandler = new MapListInputHandler(null, new HashMap<String, Map<String, Object>>());
			fail();
		} catch (IllegalArgumentException ex) {
			assertNotNull(ex);
		}
	}
	
	public void testNullParamsHandler() {
		MapListInputHandler inputHandler = new MapListInputHandler(this.simpleQuery, null);
		String testEncodedQueryString = inputHandler.getQueryString();
		QueryParameters testParameters = inputHandler.getQueryParameters();
		
		assertNotNull(testEncodedQueryString);
		assertNotNull(testParameters);
		
		assertEquals(testEncodedQueryString, this.simpleQuery);
		assertEquals(testParameters.size(), 0);
	}
}
