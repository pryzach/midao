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

package org.midao.jdbc.core.processor;

import org.midao.jdbc.core.handlers.input.named.BaseInputHandlerTest;
import org.midao.jdbc.core.handlers.model.ProcessedInput;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BasicQueryInputProcessorTest extends BaseInputHandlerTest {
    public void testProcessInputSingle() {
        Map<String, Object> processedMap = new HashMap<String, Object>();
        processedMap.put("cat.age", this.cat.getAge());
        processedMap.put("cat.name", this.cat.getName());

        String testEncodedQueryString = null;
        Object[] testParameters = null;
        ProcessedInput processorResult = null;

        processorResult = getQueryInputProcessor().processInput(getEncodedSingleParameterQuery(), processedMap);

        testEncodedQueryString = processorResult.getParsedSql();
        testParameters = processorResult.getSqlParameterValues().toArray();

        assertEquals(processedMap.size(), testParameters.length);

        assertEquals(testEncodedQueryString, this.decodedSingleParameterQuery);
        assertTrue(Arrays.equals(testParameters, this.singleParameterQueryParameters));
    }

    public void testProcessInputMultiple() {
        Map<String, Object> processedMap = new HashMap<String, Object>();
        processedMap.put("cat.age", this.cat.getAge());
        processedMap.put("cat.name", this.cat.getName());

        processedMap.put("dog.age", this.dog.getAge());
        processedMap.put("dog.breed", this.dog.getBreed());
        processedMap.put("dog.weight", this.dog.getWeight());

        String testEncodedQueryString = null;
        Object[] testParameters = null;
        ProcessedInput processorResult = null;

        processorResult = getQueryInputProcessor().processInput(getEncodedMultipleParameterQuery(), processedMap);

        testEncodedQueryString = processorResult.getParsedSql();
        testParameters = processorResult.getSqlParameterValues().toArray();

        assertEquals(processedMap.size(), testParameters.length);

        assertEquals(testEncodedQueryString, this.decodedMultipleParameterQuery);
        assertTrue(Arrays.equals(testParameters, this.multipleParameterQueryParameters));
    }

    public void testUnnamedParameterCheck() {
        assertFalse(getQueryInputProcessor().hasUnnamedParameters("/*+ HINT */ xxx /* comment ? */ :a yyyy :b :c :a zzzzz -- :xx ? XX"));
        assertTrue(getQueryInputProcessor().hasUnnamedParameters("/*+ HINT */ xxx /* comment ? */ :a yyyy ? :c :a zzzzz -- :xx ? XX"));
        assertFalse(getQueryInputProcessor().hasUnnamedParameters("':yy HINT ' xxx ' comment ? ' :a yyyy :b :c :a zzzzz -- :xx XX"));
        assertFalse(getQueryInputProcessor().hasUnnamedParameters("':yy HINT ' xxx \" comment ? \" :a yyyy :b :c :a zzzzz -- :xx XX"));
        assertTrue(getQueryInputProcessor().hasUnnamedParameters("':yy HINT ' xxx ( comment ? ) :a yyyy :b :c :a zzzzz -- :xx XX"));
    }

    public void testProcessInputVarious() {
        ProcessedInput processorResult = null;
        Map<String, Object> processedMap = new HashMap<String, Object>();
        processedMap.put("a", "aa");
        processedMap.put("b", "bb");

        processedMap.put("c", "cc");
        processedMap.put("d", "dd");

        processorResult = getQueryInputProcessor().processInput("xxx :a yyyy :b :c :a zzzzz", processedMap);

        assertEquals("xxx ? yyyy ? ? ? zzzzz", processorResult.getParsedSql());

        processorResult = getQueryInputProcessor().processInput("xxx :a+:b" + '\t' + ":c%10 yyyy ? zzzzz", processedMap);

        assertEquals("xxx ?+?" + '\t' + "?%10 yyyy ? zzzzz", processorResult.getParsedSql());

        processorResult = getQueryInputProcessor().processInput("xxx :a+:b ::x yyyy ? zzzzz", processedMap);

        assertEquals("xxx ?+? ::x yyyy ? zzzzz", processorResult.getParsedSql());

        //this processor should not convert :x:y into parameter x and y and replace them with '??' (unlike Spring Processor)
        processorResult = getQueryInputProcessor().processInput("xxx :a+:b ::x yyyy :x:y ? zzzzz", processedMap);

        assertEquals("xxx ?+? ::x yyyy :x:y ? zzzzz", processorResult.getParsedSql());

        processorResult = getQueryInputProcessor().processInput("xxx :a+:b ::x yyyy :  : x ? zzzzz", processedMap);

        assertEquals("xxx ?+? ::x yyyy :  : x ? zzzzz", processorResult.getParsedSql());
    }

    public void testCommentInSql() {
        ProcessedInput processorResult = null;

        Map<String, Object> processedMap = new HashMap<String, Object>();
        processedMap.put("a", "aa");
        processedMap.put("b", "bb");
        processedMap.put("c", "cc");

        processorResult = getQueryInputProcessor().processInput("/*+ HINT */ xxx /* comment ? */ :a yyyy :b :c :a zzzzz -- :xx XX", processedMap);

        assertEquals("/*+ HINT */ xxx /* comment ? */ ? yyyy ? ? ? zzzzz -- :xx XX", processorResult.getParsedSql());

        processorResult = getQueryInputProcessor().processInput("/*+ HINT */ xxx /* comment ? */ :a yyyy :b :c :a zzzzz /* :xx XX*/", processedMap);

        assertEquals("/*+ HINT */ xxx /* comment ? */ ? yyyy ? ? ? zzzzz /* :xx XX*/", processorResult.getParsedSql());

        processorResult = getQueryInputProcessor().processInput("/*+ HINT */ xxx /* comment :a ? */ :a yyyy :b :c :a zzzzz /* :xx XX*/", processedMap);

        assertEquals("/*+ HINT */ xxx /* comment :a ? */ ? yyyy ? ? ? zzzzz /* :xx XX*/", processorResult.getParsedSql());

        // MySQL style comment which starts with #
        processorResult = getQueryInputProcessor().processInput("/*+ HINT */ xxx /* comment :a ? */ :a yyyy :b :c :a zzzzz # :xx XX", processedMap);

        assertEquals("/*+ HINT */ xxx /* comment :a ? */ ? yyyy ? ? ? zzzzz # :xx XX", processorResult.getParsedSql());
    }

    public void testTextInSql() {
        ProcessedInput processorResult = null;

        Map<String, Object> processedMap = new HashMap<String, Object>();
        processedMap.put("a", "aa");
        processedMap.put("b", "bb");
        processedMap.put("c", "cc");

        processorResult = getQueryInputProcessor().processInput("':yy HINT ' xxx ' comment ? ' :a yyyy :b :c :a zzzzz -- :xx XX", processedMap);

        assertEquals("':yy HINT ' xxx ' comment ? ' ? yyyy ? ? ? zzzzz -- :xx XX", processorResult.getParsedSql());

        processorResult = getQueryInputProcessor().processInput("\":yy HINT \" xxx /* comment ? */ :a yyyy :b :c :a zzzzz /* :xx XX*/", processedMap);

        assertEquals("\":yy HINT \" xxx /* comment ? */ ? yyyy ? ? ? zzzzz /* :xx XX*/", processorResult.getParsedSql());
    }

    protected QueryInputProcessor getQueryInputProcessor() {
        return new BasicQueryInputProcessor();
    }
}
