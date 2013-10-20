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

package org.midao.jdbc.core.handlers.xml;

import junit.framework.Assert;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.BaseOutputHandlerTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 */
public class XmlInputOutputHandlerTest extends AbstractXmlInputOutputHandlerTest {

    public void testGetQueryString1() throws Exception {
        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", this.catMap);
        String testEncodedQueryString = handler.getQueryString();

        assertEquals(testEncodedQueryString, this.decodedSingleParameterQuery);
    }

    public void testGetQueryString2() throws Exception {
        XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne", this.cat);
        String testEncodedQueryString = handler.getQueryString();

        assertEquals(testEncodedQueryString, this.decodedSingleParameterQuery);
    }

    public void testGetQueryParameters1() throws Exception {
        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", this.catMap);
        QueryParameters testParameters = handler.getQueryParameters();

        assertNotNull(testParameters);
        assertEquals(testParameters.size(), this.singleParameterQueryParameters.length);

        assertTrue(Arrays.equals(testParameters.getValuesArray(), this.singleParameterQueryParameters));
    }

    public void testGetQueryParameters2() throws Exception {
        XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne", this.cat);
        QueryParameters testParameters = handler.getQueryParameters();

        assertNotNull(testParameters);
        assertEquals(testParameters.size(), this.singleParameterQueryParameters.length);

        assertTrue(Arrays.equals(testParameters.getValuesArray(), this.singleParameterQueryParameters));
    }

    public void testIncorrentName1() {
        try {
            XmlInputOutputHandler handler = new XmlInputOutputHandler("dontknow", this.catMap);
            // exception should be thrown
            fail();
        } catch (MjdbcRuntimeException ex) {

        }
    }

    public void testIncorrentName2() {
        try {
            XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "downknow", this.cat);
            // exception should be thrown
            fail();
        } catch (MjdbcRuntimeException ex) {

        }
    }

    public void testIncorrectValues1() {
        try {
            XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", null);
            // exception should be thrown
            fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testIncorrectValues2() {
        try {
            XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne", null);
            // exception should be thrown
            fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testEmptyValue1() {
        try {
            XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne");
            // we expect 2 values but haven't supplied them. exception should be thrown
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testEmptyValue2() {
        try {
            XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne");
            // we expect 2 values but haven't supplied them. exception should be thrown
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetName1() throws Exception {
        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", this.catMap);

        Assert.assertEquals("findOne", handler.getName());
    }

    public void testGetName2() throws Exception {
        XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne", this.cat);

        Assert.assertEquals("org.midao.jdbc.core.handlers.input.named.BaseInputHandlerTest$Cat.findOne", handler.getName());
    }

    public void testGetOutputType1() throws Exception {
        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", this.catMap);

        Assert.assertEquals(null, handler.getOutputType());
    }

    public void testGetOutputType2() throws Exception {
        XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne", this.cat);

        Assert.assertEquals(Cat.class, handler.getOutputType());
    }

    public void testGetOverrides1() throws Exception {
        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", this.catMap);

        Assert.assertEquals("MapOutputHandler", handler.getOverrides().getOverride(XmlParameters.outputHandler));
        Assert.assertEquals("query", handler.getOverrides().getOverride(XmlParameters.operationType));
    }

    public void testGetOverrides2() throws Exception {
        XmlInputOutputHandler<Cat> handler = new XmlInputOutputHandler<Cat>(Cat.class, "findOne", this.cat);

        Assert.assertEquals("BeanOutputHandler", handler.getOverrides().getOverride(XmlParameters.outputHandler));
        Assert.assertEquals("query", handler.getOverrides().getOverride(XmlParameters.operationType));
    }

    public void testHandle1() throws Exception {
        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", this.catMap);

        List<QueryParameters> paramsList = new ArrayList<QueryParameters>();
        paramsList.add(new QueryParameters());
        paramsList.add(new QueryParameters(this.catMap));

        Map<String, Object> result = (Map<String, Object>) handler.handle(paramsList);

        Assert.assertEquals(this.catMap.get("age"), result.get("age"));
        Assert.assertEquals(this.catMap.get("name"), result.get("name"));
    }

    public void testHandle2() throws Exception {
        XmlInputOutputHandler<BaseOutputHandlerTest.Character> handler = new XmlInputOutputHandler<BaseOutputHandlerTest.Character>(BaseOutputHandlerTest.Character.class, "findOne", this.cat);

        List<QueryParameters> paramsList = new ArrayList<QueryParameters>();
        paramsList.add(new QueryParameters());
        paramsList.add(new QueryParameters().set("age", this.cat.getAge()).set("name", this.cat.getName()));

        BaseOutputHandlerTest.Character result = (BaseOutputHandlerTest.Character) handler.handle(paramsList);

        Assert.assertEquals(this.cat.getAge(), result.getAge().intValue());
        Assert.assertEquals(this.cat.getName(), result.getName());
    }
}
