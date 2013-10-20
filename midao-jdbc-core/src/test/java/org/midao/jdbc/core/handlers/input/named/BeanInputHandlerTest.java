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

/*
 * @author Zak
 */
public class BeanInputHandlerTest extends BaseInputHandlerTest {
	public void testQueryString() {
		BeanInputHandler<Cat> inputHandler = new BeanInputHandler<Cat>(this.getEncodedSingleParameterQuery(), this.cat, "cat");
		String testEncodedQueryString = inputHandler.getQueryString();
		
		assertEquals(testEncodedQueryString, this.decodedSingleParameterQuery);
	}
	
	public void testQueryParameters() {
		BeanInputHandler<Cat> inputHandler = new BeanInputHandler<Cat>(this.getEncodedSingleParameterQuery(), this.cat, "cat");
		QueryParameters testParameters = inputHandler.getQueryParameters();
		
		assertNotNull(testParameters);
		assertEquals(testParameters.size(), this.singleParameterQueryParameters.length);
		
		assertTrue(Arrays.equals(testParameters.getValuesArray(), this.singleParameterQueryParameters));
	}
	
	public void testShortNamedQueryString() {
		BeanInputHandler<Cat> inputHandler = new BeanInputHandler<Cat>(this.getEncodedShortParameterQuery(), this.cat);
		String testEncodedQueryString = inputHandler.getQueryString();
		
		assertEquals(testEncodedQueryString, this.decodedShortParameterQuery);
	}
	
	public void testNullHandler() {
		try {
			BeanInputHandler<Cat> inputHandler = new BeanInputHandler<Cat>(null, null);
			fail();
		} catch (IllegalArgumentException ex) {
			assertNotNull(ex);
		}
	}
	
	public void testNullQueryHandler() throws IllegalArgumentException {
		try {
			BeanInputHandler<Cat> inputHandler = new BeanInputHandler<Cat>(null, this.cat);
			fail();
		} catch (IllegalArgumentException ex) {
			assertNotNull(ex);
		}
	}
	
	public void testNullParamsHandler() {
		BeanInputHandler<Cat> inputHandler = new BeanInputHandler<Cat>(this.simpleQuery, null);
		String testEncodedQueryString = inputHandler.getQueryString();
		QueryParameters testParameters = inputHandler.getQueryParameters();
		
		assertNotNull(testEncodedQueryString);
		assertNotNull(testParameters);
		
		assertEquals(testEncodedQueryString, this.simpleQuery);
		assertEquals(testParameters.size(), 0);
	}
}
