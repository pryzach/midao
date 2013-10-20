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

import org.midao.jdbc.core.MjdbcConfig;
import org.midao.jdbc.core.Overrider;
import org.midao.jdbc.core.exception.MjdbcRuntimeException;
import org.midao.jdbc.core.handlers.model.ProcessedInput;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.utils.AssertUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML Repository factory is responsible for reading and caching queries from XML.
 *
 * <p>
 *     Allows: query/update execution, any available output handler (inc. lazy), generate keys, control parameter count.
 * </p>
 * <p>
 *     Planned: call execution, statement/type handler per query, refresh mechanism in case of file change
 * </p>
 *
 * Example XML with queries:<br/>
 * <br/>
 * Update (generate keys):
 * <p>
 * &lt?xml version="1.0"?&gt&ltroot&gt&ltupdate id='insertStudent' generateKeys='true' outputHandler='MapOutputHandler'&gt<br/>
 *   INSERT INTO students (name, address) VALUES ('Not me', 'unknown')<br/>
 * &lt/update&gt&lt/root&gt<br/>
 * </p>
 *
 * Select:
 * <p>
 * &lt?xml version="1.0"?&gt&ltroot&gt&ltquery id='findStudent' outputHandler='MapOutputHandler'&gt<br/>
 *   SELECT name FROM students WHERE id = #{id,jdbcType=INTEGER,mode=in}<br/>
 * &lt/query&gt&lt/root&gt<br/>
 * </p>
 *
 * Execution:
 *
 * <p>
 * XmlInputOutputHandler<Class> handler = new XmlInputOutputHandler<Class>(Class.class, (name), (values));<br/>
 * runner.execute(handler);<br/>
 * </p>
 */
public class XmlRepositoryFactory {
    private static boolean autoRefresh = false;
    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder documentBuilder = null;

    private static Overrider defaultOverride = new Overrider();

    private static Map<String, String> xmlQueryString = new HashMap<String, String>();
    private static Map<String, Overrider> xmlOverrideMap = new HashMap<String, Overrider>();

    // initialize default values via static constructor. Later that class is prototyped for other overrides
    static {
        defaultOverride.override(XmlParameters.operationType, "query");
        defaultOverride.override(XmlParameters.outputHandler, "org.midao.jdbc.core.handlers.output.MapListOutputHandler");
    }

    /**
     * Reads XML file and returns it is DOM XML Document instance.
     * Can be used in conjunction with {@link #addAll(org.w3c.dom.Document)}
     *
     * @param file file which should be read
     * @return {@link Document} instance
     */
    public static Document getDocument(File file) {
        Document result = null;

        try {
            if (documentBuilder == null) {
                documentBuilder = builderFactory.newDocumentBuilder();
            }

            result = documentBuilder.parse(file);
        } catch (ParserConfigurationException ex) {
            throw new MjdbcRuntimeException(ex);
        } catch (SAXException ex) {
            throw new MjdbcRuntimeException(ex);
        } catch (IOException ex) {
            throw new MjdbcRuntimeException(ex);
        }

        return result;
    }

    /**
     * Reads XML stream and returns it is DOM XML Document instance.
     * Can be used in conjunction with {@link #addAll(org.w3c.dom.Document)}
     *
     * @param inputStream stream which should be read
     * @return {@link Document} instance
     */
    public static Document getDocument(InputStream inputStream) {
        Document result = null;

        try {
            if (documentBuilder == null) {
                documentBuilder = builderFactory.newDocumentBuilder();
            }

            result = documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException ex) {
            throw new MjdbcRuntimeException(ex);
        } catch (SAXException ex) {
            throw new MjdbcRuntimeException(ex);
        } catch (IOException ex) {
            throw new MjdbcRuntimeException(ex);
        }

        return result;
    }

    /**
     * Adds/updates one specific query from XML file into Repository
     *
     * @param document document which would be read
     * @param name query which should be loaded
     */
    public static void add(Document document, String name) {
        Element element = findElement(document, name);

        AssertUtils.assertNotNull(element, "Failed to find element by name: " + name);

        add(element);
    }

    /**
     * Adds/updates specific query (from {@link Element} into Repository
     *
     * @param element element which would be read
     */
    public static void add(Element element) {
        AssertUtils.assertNotNull(element);

        ProcessedInput processedInput = null;
        Overrider overrider = new Overrider(defaultOverride);

        overrider.override(XmlParameters.operationType, element.getTagName());

        if (element.hasAttribute(XmlParameters.outputHandler) == true) {
            overrider.override(XmlParameters.outputHandler, element.getAttribute(XmlParameters.outputHandler));
        }

        if (element.hasAttribute(XmlParameters.controlParamCount) == true) {
            overrider.override(XmlParameters.controlParamCount, Boolean.valueOf(element.getAttribute(XmlParameters.controlParamCount)));
        }

        if (element.hasAttribute(XmlParameters.generateKeys) == true) {
            overrider.override(XmlParameters.generateKeys, element.getAttribute(XmlParameters.generateKeys));
        }

        if (element.hasAttribute(XmlParameters.generateKeysColumns) == true) {
            overrider.override(XmlParameters.generateKeysColumns, element.getAttribute(XmlParameters.generateKeysColumns).split(","));
        }

        String name = element.getAttribute("id");
        String encodedQuery = element.getTextContent();

        xmlQueryString.put(name, encodedQuery);
        xmlOverrideMap.put(name, overrider);
    }

    /**
     * Adds all queries from XML file into Repository
     *
     * @param document document which would be read
     */
    public static void addAll(Document document) {
        List<Element> elementList = getElements(document);

        List<String> elementIds = getElementsId(elementList);

        addAll(elementList);
    }

    /**
     * Adds all queries (from {@link Element} into Repository
     *
     * @param elementList list of elements which would be read
     */
    public static void addAll(List<Element> elementList) {
        for (Element element : elementList) {
            add(element);
        }
    }

    /**
     * Removes specified query from the Repository
     *
     * @param name query name
     */
    public static void remove(String name) {
        xmlQueryString.remove(name);
        xmlOverrideMap.remove(name);
    }

    /**
     * Removes all queries in the XML file from the Repository
     *
     * @param document document which would be used as source for query names to remove from repository
     */
    public static void removeAll(Document document) {
        List<String> elementsId = null;

        elementsId = getElementsId(document);

        removeAll(elementsId);
    }

    /**
     * Removes all queries (specified in {@link List} of {@link String}} from the Repository
     *
     * @param elementsId list of queries (as list of queries names) which should be removed from the Repository
     */
    public static void removeAll(List<String> elementsId) {
        for (String elementId : elementsId) {
            remove(elementId);
        }
    }

    /**
     * Reloads queries from the XML document
     *
     * @param document document which would be reloaded in the Repository
     */
    public static void refresh(Document document) {
        refresh(document, document);
    }

    /**
     * Removes old queries and adds new ones from XML documents and into Repository
     *
     * @param cachedDocument document which would be the source for queries to remove from Repository
     * @param updatedDocument document which would be the source for queries to add to Repository
     */
    public static void refresh(Document cachedDocument, Document updatedDocument) {
        removeAll(cachedDocument);
        addAll(updatedDocument);
    }

    /**
     * Returns if Repository set to allow auto-refresh (reload queries if file was modified)
     * Feature is not implemented for version 0.9.5 and should be implemented later
     *
     * @return true - if auto-refresh was enabled
     */
    public static boolean isAutoRefresh() {
        return autoRefresh;
    }

    /**
     * Sets if Repository should allow auto-refresh (reload queries if file was modified)
     * Feature is not implemented for version 0.9.5 and should be implemented later
     *
     * @param autoRefresh new auto-refresh setting
     */
    public static void setAutoRefresh(boolean autoRefresh) {
        XmlRepositoryFactory.autoRefresh = autoRefresh;
    }

    /**
     * Package private function used by {@link XmlInputOutputHandler} to retrieve query string from repository
     *
     * @param name query name
     * @return sql string (if present)
     * @throws MjdbcRuntimeException if query name wasn't loaded to repository
     */
    static String getQueryString(String name) {
        String result = null;

        if (xmlQueryString.containsKey(name) == false) {
            throw new MjdbcRuntimeException("Failed to find query by name: " + name + ". Please ensure that relevant file was loaded into repository");
        }

        result = xmlQueryString.get(name);

        return result;
    }

    /**
     * Package private function used by {@link XmlInputOutputHandler} to retrieve query string from repository
     *
     * @param inputHandler Xml input/output handler
     * @return override values (if present)
     * @throws MjdbcRuntimeException if query name wasn't loaded to repository
     */
    static Overrider getOverride(AbstractXmlInputOutputHandler inputHandler) {
        AssertUtils.assertNotNull(inputHandler);

        Overrider result = null;

        if (xmlOverrideMap.containsKey(inputHandler.getName()) == false) {
            throw new MjdbcRuntimeException("Failed to find query by name: " + inputHandler.getName() + ". Please ensure that relevant file was loaded into repository");
        }

        result =  xmlOverrideMap.get(inputHandler.getName());

        return result;
    }

    /**
     * Package private function used by {@link XmlInputOutputHandler} to retrieve output handler from repository
     *
     * @param inputHandler Xml input/output handler
     * @return override values (if present)
     * @throws MjdbcRuntimeException if query name wasn't loaded to repository
     */
    static OutputHandler getOutputHandler(AbstractXmlInputOutputHandler inputHandler) {
        AssertUtils.assertNotNull(inputHandler);

        OutputHandler outputHandler = null;
        String outputHandlerClassName = (String) getOverride(inputHandler).getOverride(XmlParameters.outputHandler);

        if (outputHandlerClassName.contains(".") == false) {
            outputHandlerClassName = MjdbcConfig.getOutputHandlerPackage() + outputHandlerClassName;
        }

        if (MjdbcConfig.getDefaultOutputHandlers().containsKey(outputHandlerClassName) == true) {
            outputHandler = MjdbcConfig.getDefaultOutputHandlers().get(outputHandlerClassName);
        } else {
            // if it is not in list of non-bean output handlers - trying to create new instance of bean handler
            outputHandler = createBeanOutputHandler(inputHandler, outputHandlerClassName);
        }

        return outputHandler;
    }

    /**
     * Reads XML document and returns it content as list of query names
     *
     * @param document document which would be read
     * @return list of queries names
     */
    private static List<String> getElementsId(Document document) {
        List<String> result = new ArrayList<String>();

        List<Element> elementList = getElements(document);

        result = getElementsId(elementList);

        return result;
    }

    /**
     * Reads list of queries (as {@link Element} and returns it as list of query names
     *
     * @param elementList list of {@link Element}
     * @return list of query names
     */
    private static List<String> getElementsId(List<Element> elementList) {
        List<String> result = new ArrayList<String>();

        for (Element element : elementList) {
            result.add(element.getAttribute("id"));
        }

        return result;
    }

    /**
     * Reads XML document and returns it content as list of queries (as {@link Element})
     *
     * @param document document which would be read
     * @return list of queries (as {@link Element})
     */
    private static List<Element> getElements(Document document) {
        List<Element> result = new ArrayList<Element>();

        Element root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        Node node = null;
        Element nodeElement = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);

            if (node instanceof Element) {
                nodeElement = (Element) node;

                if (nodeElement.hasAttribute("id") == false) {
                    throw new MjdbcRuntimeException("Found XML element without ID attribute. Parsing is interrupted");
                } else {
                    result.add(nodeElement);
                }
            }
        }

        return result;
    }

    /**
     * Searches XML document and returns specified query as {@link Element}
     *
     * @param document document which would be read
     * @param name query name to search
     * @return query as {@link Element}
     */
    private static Element findElement(Document document, String name) {
        Element result = null;

        List<Element> elementList = getElements(document);

        for (Element element : elementList) {
            if (element.getAttribute("id").equalsIgnoreCase(name) == true) {
                result = element;
                break;
            }
        }

        return result;
    }

    /**
     * Unlike standard output handlers - beans output handlers require bean type via constructor, hence cannot be prototyped.
     * This function is invoked to create new instance of Bean output handler
     *
     * @param inputHandler XML input/output handler for which {@link OutputHandler} is constructed
     * @param className Bean output handler class name
     * @return Bean output handler instance
     */
    private static OutputHandler createBeanOutputHandler(AbstractXmlInputOutputHandler inputHandler, String className) {
        OutputHandler result = null;

        Constructor constructor = null;
        try {
            Class clazz = Class.forName(className);
            Class outputClass = inputHandler.getOutputType();

            AssertUtils.assertNotNull(outputClass,
                    "Since output handler was used which handles Beans - Bean.class should be set via constructor of XmlInputOutputHandler");

            constructor = clazz.getConstructor(Class.class);
            result = (OutputHandler) constructor.newInstance(outputClass);
        } catch (Exception ex) {
            throw new MjdbcRuntimeException("Cannot initialize bean output handler: " + className +
                    "\nIf you are trying to use non-standard output handler - please add it to allowed map " +
                    "\nvia getDefaultOutputHandlers of MjdbcConfig class", ex);
        }

        return result;
    }
}
