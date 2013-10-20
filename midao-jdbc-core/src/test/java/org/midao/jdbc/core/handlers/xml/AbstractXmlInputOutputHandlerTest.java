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

import org.midao.jdbc.core.handlers.input.named.BaseInputHandlerTest;

import java.io.ByteArrayInputStream;

/**
 */
public class AbstractXmlInputOutputHandlerTest extends BaseInputHandlerTest {
    @Override
    public void setUp() throws Exception {
        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                ("<?xml version=\"1.0\"?><root><query id='findOne' outputHandler='MapOutputHandler'>" +
                        "SELECT ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}" +
                        "</query></root>").getBytes()
        )));

        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                ("<?xml version=\"1.0\"?><root><query id='org.midao.jdbc.core.handlers.input.named.BaseInputHandlerTest$Cat.findOne' outputHandler='BeanOutputHandler'>" +
                        "SELECT ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}" +
                        "</query></root>").getBytes()
        )));

        XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                ("<?xml version=\"1.0\"?><root><query id='org.midao.jdbc.core.handlers.output.BaseOutputHandlerTest$Character.findOne' outputHandler='BeanOutputHandler'>" +
                        "SELECT ID FROM CATS WHERE AGE=#{age} AND NAME = #{name}" +
                        "</query></root>").getBytes()
        )));
    }
}
