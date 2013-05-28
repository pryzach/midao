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

package org.midao.spring.processor;

import org.midao.core.handlers.model.ProcessedInput;
import org.midao.core.handlers.input.named.BaseInputHandlerTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * @author Zak
 */
public class SpringQueryParametersProcessorTest extends BaseInputHandlerTest {
	
	public void testProcessInputSingle() {
		SpringQueryInputProcessor inputProcessor = new SpringQueryInputProcessor();
		Map<String, Object> processedMap = new HashMap<String, Object>();
		processedMap.put("cat.age", this.cat.getAge());
		processedMap.put("cat.name", this.cat.getName());
		
		String testEncodedQueryString = null;
		Object[] testParameters = null;
		ProcessedInput processorResult = null;
		
		processorResult = inputProcessor.processInput(this.encodedSingleParameterQuery, processedMap);
		
		testEncodedQueryString = processorResult.getParsedSql();
		testParameters = processorResult.getSqlParameterValues().toArray();
		
		assertEquals(processedMap.size(), testParameters.length);
		
		assertEquals(testEncodedQueryString, this.decodedSingleParameterQuery);
		assertTrue(Arrays.equals(testParameters, this.singleParameterQueryParameters));
	}
	
	public void testProcessInputMultiple() {
		SpringQueryInputProcessor inputProcessor = new SpringQueryInputProcessor();
		Map<String, Object> processedMap = new HashMap<String, Object>();
		processedMap.put("cat.age", this.cat.getAge());
		processedMap.put("cat.name", this.cat.getName());
		
		processedMap.put("dog.age", this.dog.getAge());
		processedMap.put("dog.breed", this.dog.getBreed());
		processedMap.put("dog.weight", this.dog.getWeight());
		
		String testEncodedQueryString = null;
		Object[] testParameters = null;
		ProcessedInput processorResult = null;
		
		processorResult = inputProcessor.processInput(this.encodedMultipleParameterQuery, processedMap);
		
		testEncodedQueryString = processorResult.getParsedSql();
		testParameters = processorResult.getSqlParameterValues().toArray();
		
		assertEquals(processedMap.size(), testParameters.length);
		
		assertEquals(testEncodedQueryString, this.decodedMultipleParameterQuery);
		assertTrue(Arrays.equals(testParameters, this.multipleParameterQueryParameters));
	}
	
	public void testUnnamedParameterCheck() {
		SpringQueryInputProcessor inputProcessor = new SpringQueryInputProcessor();
		
		assertFalse(inputProcessor.hasUnnamedParameters("/*+ HINT */ xxx /* comment ? */ :a yyyy :b :c :a zzzzz -- :xx ? XX"));
		assertTrue(inputProcessor.hasUnnamedParameters("/*+ HINT */ xxx /* comment ? */ :a yyyy ? :c :a zzzzz -- :xx ? XX"));
		assertFalse(inputProcessor.hasUnnamedParameters("':yy HINT ' xxx ' comment ? ' :a yyyy :b :c :a zzzzz -- :xx XX"));
		assertFalse(inputProcessor.hasUnnamedParameters("':yy HINT ' xxx \" comment ? \" :a yyyy :b :c :a zzzzz -- :xx XX"));
		assertTrue(inputProcessor.hasUnnamedParameters("':yy HINT ' xxx ( comment ? ) :a yyyy :b :c :a zzzzz -- :xx XX"));
	}
	
	public void testProcessInputVarious() {
		SpringQueryInputProcessor inputProcessor = new SpringQueryInputProcessor();
		ProcessedInput processorResult = null;
		Map<String, Object> processedMap = new HashMap<String, Object>();
		processedMap.put("a", "aa");
		processedMap.put("b", "bb");
		
		processedMap.put("c", "cc");
		processedMap.put("d", "dd");
		
		processedMap.put("x", "xx");
		processedMap.put("y", "yy");
		
		processorResult = inputProcessor.processInput("xxx :a yyyy :b :c :a zzzzz", processedMap);
		
		assertEquals("xxx ? yyyy ? ? ? zzzzz", processorResult.getParsedSql());
		
		processorResult = inputProcessor.processInput("xxx :a+:b" + '\t' + ":c%10 yyyy ? zzzzz", processedMap);
		
		assertEquals("xxx ?+?" + '\t' + "?%10 yyyy ? zzzzz", processorResult.getParsedSql());
		
		processorResult = inputProcessor.processInput("xxx :a+:b ::x yyyy ? zzzzz", processedMap);
		
		assertEquals("xxx ?+? ::x yyyy ? zzzzz", processorResult.getParsedSql());
		
		// this processor should convert :x:y into parameter x and y and replace them with '??'
		processorResult = inputProcessor.processInput("xxx :a+:b ::x yyyy :x:y ? zzzzz", processedMap);
		
		assertEquals("xxx ?+? ::x yyyy ?? ? zzzzz", processorResult.getParsedSql());
		
		processorResult = inputProcessor.processInput("xxx :a+:b ::x yyyy :  : x ? zzzzz", processedMap);
		
		assertEquals("xxx ?+? ::x yyyy :  : x ? zzzzz", processorResult.getParsedSql());
	}
	
	public void testCommentInSql() {
		SpringQueryInputProcessor inputProcessor = new SpringQueryInputProcessor();
		ProcessedInput processorResult = null;
		
		Map<String, Object> processedMap = new HashMap<String, Object>();
		processedMap.put("a", "aa");
		processedMap.put("b", "bb");
		processedMap.put("c", "cc");
		
		processorResult = inputProcessor.processInput("/*+ HINT */ xxx /* comment ? */ :a yyyy :b :c :a zzzzz -- :xx XX", processedMap);
		
		assertEquals("/*+ HINT */ xxx /* comment ? */ ? yyyy ? ? ? zzzzz -- :xx XX", processorResult.getParsedSql());
		
		processorResult = inputProcessor.processInput("/*+ HINT */ xxx /* comment ? */ :a yyyy :b :c :a zzzzz /* :xx XX*", processedMap);
		
		assertEquals("/*+ HINT */ xxx /* comment ? */ ? yyyy ? ? ? zzzzz /* :xx XX*", processorResult.getParsedSql());
		
		processorResult = inputProcessor.processInput("/*+ HINT */ xxx /* comment :a ? */ :a yyyy :b :c :a zzzzz /* :xx XX*", processedMap);
		
		assertEquals("/*+ HINT */ xxx /* comment :a ? */ ? yyyy ? ? ? zzzzz /* :xx XX*", processorResult.getParsedSql());
		
		// MySQL style comment which starts with #
		// Is not supported by Spring Input Processor
		//processorResult = inputProcessor.processInput("/*+ HINT */ xxx /* comment :a ? */ :a yyyy :b :c :a zzzzz # :xx XX", processedMap);
		
		//assertEquals("/*+ HINT */ xxx /* comment :a ? */ ? yyyy ? ? ? zzzzz # :xx XX", processorResult.getParsedSql());
	}
	
	public void testTextInSql() {
		SpringQueryInputProcessor inputProcessor = new SpringQueryInputProcessor();
		ProcessedInput processorResult = null;
		
		Map<String, Object> processedMap = new HashMap<String, Object>();
		processedMap.put("a", "aa");
		processedMap.put("b", "bb");
		processedMap.put("c", "cc");
		
		processorResult = inputProcessor.processInput("':yy HINT ' xxx ' comment ? ' :a yyyy :b :c :a zzzzz -- :xx XX", processedMap);
		
		assertEquals("':yy HINT ' xxx ' comment ? ' ? yyyy ? ? ? zzzzz -- :xx XX", processorResult.getParsedSql());
		
		processorResult = inputProcessor.processInput("\":yy HINT \" xxx /* comment ? */ :a yyyy :b :c :a zzzzz /* :xx XX*/", processedMap);
		
		assertEquals("\":yy HINT \" xxx /* comment ? */ ? yyyy ? ? ? zzzzz /* :xx XX*/", processorResult.getParsedSql());
	}
}
