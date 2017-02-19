package com.programyourhome.huebridgesimulator.config;

import java.io.ByteArrayOutputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;

/**
 * To be able to easily loop over a NodeList.
 *
 * Based on:
 * http://stackoverflow.com/questions/19589231/can-i-iterate-through-nodelist-using-foreach-in-java
 * Custom extension: use generics to get a list of the desired subtype of Node.
 */
public final class XmlUtil {

    private XmlUtil() {
    }

    public static <T extends Node> List<T> asList(final NodeList n) {
        return n.getLength() == 0 ? Collections.<T> emptyList() : new NodeListWrapper<T>(n);
    }

    private static final class NodeListWrapper<T extends Node> extends AbstractList<T> implements RandomAccess {
        private final NodeList list;

        public NodeListWrapper(final NodeList l) {
            this.list = l;
        }

        // User responsibility that this matches.
        @Override
        @SuppressWarnings("unchecked")
        public T get(final int index) {
            return (T) this.list.item(index);
        }

        @Override
        public int size() {
            return this.list.getLength();
        }
    }

    public static List<String> namesAsList(final Attributes attributes) {
        final List<String> namesList = new ArrayList<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            namesList.add(attributes.getQName(i));
        }
        return namesList;
    }

    public static String elementToString(final Node node) throws Exception {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final Document document = factory.newDocumentBuilder().newDocument();
        final Node importedNode = document.importNode(node, true);
        document.appendChild(importedNode);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(bos));
        return bos.toString("UTF-8");
    }
}