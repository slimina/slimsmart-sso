/*
 * All rights Reserved, tianwei7518.
 * Copyright(C) 2014-2015
 * 2014年2月20日 
 */

package cn.slimsmart.sso.client.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public final class XmlUtil {

	private final static Log LOG = LogFactory.getLog(XmlUtil.class);

	public static XMLReader getXmlReader() {
		try {
			return XMLReaderFactory.createXMLReader();
		} catch (final SAXException e) {
			throw new RuntimeException("Unable to create XMLReader", e);
		}
	}

	public static List<String> getTextForElements(final String xmlAsString, final String element) {
		final List<String> elements = new ArrayList<String>(2);
		final XMLReader reader = getXmlReader();

		final DefaultHandler handler = new DefaultHandler() {

			private boolean foundElement = false;

			private StringBuilder buffer = new StringBuilder();

			public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
				if (localName.equals(element)) {
					this.foundElement = true;
				}
			}

			public void endElement(final String uri, final String localName, final String qName) throws SAXException {
				if (localName.equals(element)) {
					this.foundElement = false;
					elements.add(this.buffer.toString());
					this.buffer = new StringBuilder();
				}
			}

			public void characters(char[] ch, int start, int length) throws SAXException {
				if (this.foundElement) {
					this.buffer.append(ch, start, length);
				}
			}
		};

		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);

		try {
			reader.parse(new InputSource(new StringReader(xmlAsString)));
		} catch (final Exception e) {
			LOG.error(e, e);
			return null;
		}

		return elements;
	}

	public static String getTextForElement(final String xmlAsString, final String element) {
		final XMLReader reader = getXmlReader();
		final StringBuilder builder = new StringBuilder();

		final DefaultHandler handler = new DefaultHandler() {

			private boolean foundElement = false;

			public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
				if (localName.equals(element)) {
					this.foundElement = true;
				}
			}

			public void endElement(final String uri, final String localName, final String qName) throws SAXException {
				if (localName.equals(element)) {
					this.foundElement = false;
				}
			}

			public void characters(char[] ch, int start, int length) throws SAXException {
				if (this.foundElement) {
					builder.append(ch, start, length);
				}
			}
		};

		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);

		try {
			reader.parse(new InputSource(new StringReader(xmlAsString)));
		} catch (final Exception e) {
			LOG.error(e, e);
			return null;
		}

		return builder.toString();
	}

}