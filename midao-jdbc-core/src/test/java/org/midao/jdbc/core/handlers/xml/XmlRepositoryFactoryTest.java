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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 */
public class XmlRepositoryFactoryTest {

    @Mock Element element;

    String xmlQuery = "<?xml version=\"1.0\"?><root><query id='findOne' outputHandler='MapOutputHandler'>" +
            "SELECT ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}" +
            "</query></root>";
    protected Map<String, Object> catMap = new HashMap<String, Object>() {
        {put("age", 1);put("name", "whiskers");}
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDocumentStream() throws Exception {
        Document document = XmlRepositoryFactory.getDocument(new ByteArrayInputStream(xmlQuery.getBytes()));

        Assert.assertNotNull(document);
    }

    @Test
    public void testGetDocumentStreamMultiThreadedUsage() throws Exception {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                XmlRepositoryFactory.getDocument(new ByteArrayInputStream(xmlQuery.getBytes()));
            }
        };

        // org.xml.sax.SAXException: FWK005 parse may not be called while parsing
        // little crazy test, but due very big difference in possible CPU performance - sometimes high number of threads needed to actually reliably get that exception.
        //  test is limited by 500 milliseconds (below) before this test would be shut down.
        for (long i = 0; i < 10; i++) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    for (long j = 0; j < 100; j++) {
                        (new Thread(runnable)).start();
                    }

                }
            })).start();
        }

        Thread.sleep(500);
    }

    @Test
    public void testGetDocumentFile() throws Exception {
        /*
        File temp = File.createTempFile("example", ".xml");

        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        writer.write(xmlQuery);
        writer.flush();
        writer.close();

        Document document = XmlRepositoryFactory.getDocument(temp);

        temp.delete();

        Assert.assertNotNull(document);
        */
    }

    @Test
    public void testAddName() throws Exception {
        XmlRepositoryFactory.add(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                (xmlQuery).getBytes()
        )), "findOne");

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testAddElement() throws Exception {
        when(element.getAttribute(eq("id"))).thenReturn("findOne");
        when(element.getTextContent()).thenReturn("some SQL query");

        XmlRepositoryFactory.add(element);

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testAddAllDocument() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testAddAllElementList() throws Exception {
        when(element.getAttribute(eq("id"))).thenReturn("findOne");
        when(element.getTextContent()).thenReturn("some SQL query");

        XmlRepositoryFactory.addAll(Arrays.asList(new Element[] {element}));

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testRemove() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));

        XmlRepositoryFactory.remove("findOne");

        try {
            XmlRepositoryFactory.getQueryString("findOne");
            fail();
        } catch (MjdbcRuntimeException ex) {
            // exception should be thrown
        }
    }

    @Test
    public void testRemoveAllDocument() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));

        XmlRepositoryFactory.removeAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        try {
            XmlRepositoryFactory.getQueryString("findOne");
            fail();
        } catch (MjdbcRuntimeException ex) {
            // exception should be thrown
        }
    }

    @Test
    public void testRemoveAllElementList() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));

        when(element.getAttribute(eq("findOne"))).thenReturn("findOne");
        when(element.getTextContent()).thenReturn("some SQL query");

        XmlRepositoryFactory.removeAll(Arrays.asList("findOne"));

        try {
            XmlRepositoryFactory.getQueryString("findOne");
            fail();
        } catch (MjdbcRuntimeException ex) {
            // exception should be thrown
        }
    }

    @Test
    public void testRefreshCachedUpdated() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertEquals("SELECT ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}", XmlRepositoryFactory.getQueryString("findOne"));

        Document document = XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                ("<?xml version=\"1.0\"?><root><query id='findOne' outputHandler='MapOutputHandler'>" +
                        "SELECTED ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}" +
                        "</query></root>").getBytes()
        ));

        XmlRepositoryFactory.refresh(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )), document);

        Assert.assertEquals("SELECTED ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}", XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testRefresh() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertEquals("SELECT ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}", XmlRepositoryFactory.getQueryString("findOne"));

        Document document = XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                ("<?xml version=\"1.0\"?><root><query id='findOne' outputHandler='MapOutputHandler'>" +
                        "SELECTED ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}" +
                        "</query></root>").getBytes()
        ));

        XmlRepositoryFactory.refresh(document);

        Assert.assertEquals("SELECTED ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}", XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testIsAutoRefresh() throws Exception {
        // nothing to test yet
    }

    @Test
    public void testSetAutoRefresh() throws Exception {
        // nothing to test yet
    }

    @Test
    public void testGetQueryString() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        Assert.assertNotNull(XmlRepositoryFactory.getQueryString("findOne"));
    }

    @Test
    public void testGetOverride() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", catMap);

        Assert.assertNotNull(XmlRepositoryFactory.getOverride(handler));
    }

    @Test
    public void testGetOutputHandler() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                xmlQuery.getBytes()
        )));

        XmlInputOutputHandler handler = new XmlInputOutputHandler("findOne", catMap);

        Assert.assertNotNull(XmlRepositoryFactory.getOutputHandler(handler));
    }
}
