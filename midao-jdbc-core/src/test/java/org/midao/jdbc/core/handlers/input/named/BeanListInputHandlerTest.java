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
public class BeanListInputHandlerTest extends BaseInputHandlerTest {
	public void testQueryString() {
		Map<String, Pet> parameterValues = new HashMap<String, Pet>();
		parameterValues.put("cat", this.cat);
		parameterValues.put("dog", this.dog);
		
		BeanListInputHandler<Pet> inputHandler = new BeanListInputHandler<Pet>(this.encodedMultipleParameterQuery, parameterValues);
		String testEncodedQueryString = inputHandler.getQueryString();
		
		assertEquals(testEncodedQueryString, this.decodedMultipleParameterQuery);
	}
	
	public void testQueryParameters() {
		Map<String, Pet> parameterValues = new HashMap<String, Pet>();
		parameterValues.put("cat", this.cat);
		parameterValues.put("dog", this.dog);
		
		BeanListInputHandler<Pet> inputHandler = new BeanListInputHandler<Pet>(this.encodedMultipleParameterQuery, parameterValues);
		QueryParameters testParameters = inputHandler.getQueryParameters();
		
		assertNotNull(testParameters);
		assertEquals(testParameters.size(), this.multipleParameterQueryParameters.length);
		
		assertTrue(Arrays.equals(testParameters.getValuesArray(), this.multipleParameterQueryParameters));
	}
	
	public void testNullHandler() {
		try {
			BeanListInputHandler<Pet> inputHandler = new BeanListInputHandler<Pet>(null, null);
			fail();
		} catch (IllegalArgumentException ex) {
			assertNotNull(ex);
		}
	}
	
	public void testNullQueryHandler() {
		try {
			BeanListInputHandler<Pet> inputHandler = new BeanListInputHandler<Pet>(null, new HashMap<String, Pet>());
			fail();
		} catch (IllegalArgumentException ex) {
			assertNotNull(ex);
		}
	}
	
	public void testNullParamsHandler() {
		BeanListInputHandler<Pet> inputHandler = new BeanListInputHandler<Pet>(this.simpleQuery, null);
		String testEncodedQueryString = inputHandler.getQueryString();
		QueryParameters testParameters = inputHandler.getQueryParameters();
		
		assertNotNull(testEncodedQueryString);
		assertNotNull(testParameters);
		
		assertEquals(testEncodedQueryString, this.simpleQuery);
		assertEquals(testParameters.size(), 0);
	}
}
