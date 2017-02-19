package com.programyourhome.huebridgesimulator.config;

import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Test class for validating the style compliancy of the XSD.
 * Using these style rules will make the XSD more consistent and readable.
 *
 * Please note: since the XSD files can/will contain a namespace for the XSD naming scope 'xsd:', they should
 * be included in the XPath expressions. A workaround for this seems to be to start with the root of the XSD file '/schema'.
 */
public class XsdStyleCompliancyTest {

    private static final String XSD_LOCATION = "/config.xsd";

    private Document document;
    private XPath xpath;

    @Before
    public void beforeClass() throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        this.document = documentBuilder.parse(XsdStyleCompliancyTest.class.getResourceAsStream(XSD_LOCATION));
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        this.xpath = xPathfactory.newXPath();
    }

    @Test
    public void testXsdStyleCompliance() throws Exception {
        this.assertNoUseOfAll();
        this.assertNoMinMaxOnSequenceChoice();
        this.assertMinAndMaxOccursOneChoiceChild();
        this.assertUseMinMaxOnElement();
        this.assertAttributeOrder();
    }

    private void assertNoUseOfAll() throws Exception {
        final XPathExpression expression = this.xpath.compile("/schema//all");
        final NodeList nodes = (NodeList) expression.evaluate(this.document, XPathConstants.NODESET);
        if (nodes.getLength() > 0) {
            // Use this construct to be able to create an error message with an item from the list.
            Assert.fail(this.errorMessage("Do not use the 'all' tag.", nodes.item(0)));
        }
    }

    private void assertNoMinMaxOnSequenceChoice() throws Exception {
        for (final Element element : this.getXPathElements("/schema//sequence | /schema//choice")) {
            Assert.assertFalse(this.errorMessage("Do not use the attribute 'minOccurs' on <sequence> and <choice>", element),
                    element.hasAttribute("minOccurs"));
            Assert.assertFalse(this.errorMessage("Do not use the attribute 'maxOccurs' on <sequence> and <choice>", element),
                    element.hasAttribute("maxOccurs"));
        }
    }

    private void assertMinAndMaxOccursOneChoiceChild() throws Exception {
        for (final Element element : this.getXPathElements("/schema//choice")) {
            for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                if (element.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                    final Element choiceChild = (Element) element.getChildNodes().item(i);
                    if (choiceChild.hasAttribute("minOccurs")) {
                        Assert.assertTrue(this.errorMessage("Always use 'minOccurs=1' on the child of a choice element.", element),
                                choiceChild.getAttribute("minOccurs").equals("1"));
                    }
                    if (choiceChild.hasAttribute("maxOccurs")) {
                        Assert.assertTrue(this.errorMessage("Always use 'maxOccurs=1' on the child of a choice element.", element),
                                choiceChild.getAttribute("maxOccurs").equals("1"));
                    }
                }
            }
        }
    }

    private void assertUseMinMaxOnElement() throws Exception {
        // Top level <element> tags (which are XML root tag candidates) are not allowed to have a minOccurs or maxOccurs,
        // since there is no other possibility than exactly one XML root element.
        for (final Element element : this.getXPathElements("/schema/element//element | /schema/complexType//element")) {
            Assert.assertTrue(this.errorMessage("The attribute 'minOccurs' is required for every <element>", element), element.hasAttribute("minOccurs"));
            Assert.assertTrue(this.errorMessage("The attribute 'maxOccurs' is required for every <element>", element), element.hasAttribute("maxOccurs"));
        }
    }

    /**
     * We have to use SAX for this, since DOM does not save the order of attributes.
     */
    private void assertAttributeOrder() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser saxParser = factory.newSAXParser();

        final DefaultHandler handler = new DefaultHandler() {
            private Locator locator;

            @Override
            public void setDocumentLocator(final Locator locator) {
                this.locator = locator;
            }

            @Override
            public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
                if (qName.contains("element")) {
                    final List<String> allowedOrders = Arrays.asList("name", "name|minOccurs|maxOccurs", "name|type|minOccurs|maxOccurs");
                    final String attributeOrder = String.join("|", XmlUtil.namesAsList(attributes));
                    Assert.assertTrue("XSD style error: The attributes are not in the right order. Offending tag at line "
                            + this.locator.getLineNumber() + ": " + qName, allowedOrders.contains(attributeOrder));
                }
            }
        };
        saxParser.parse(XsdStyleCompliancyTest.class.getResourceAsStream(XSD_LOCATION), handler);
    }

    /**
     * Provide a list of elements that is the result of the given xpath expression.
     */
    private List<Element> getXPathElements(final String xPathString) throws Exception {
        final XPathExpression expression = this.xpath.compile(xPathString);
        final NodeList nodes = (NodeList) expression.evaluate(this.document, XPathConstants.NODESET);
        return XmlUtil.asList(nodes);
    }

    private String errorMessage(final String message, final Node node) throws Exception {
        return "XSD style error: " + message + ". Offending node:\n" + XmlUtil.elementToString(node);
    }

}