/*
 * Copyright (C) 2012 by Jason Smith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.jsaf.common.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.googlecode.jaks.common.xml.Namespace;
import com.googlecode.jaks.common.xml.XmlUtil;
import com.googlecode.jsaf.common.io.StreamUtil;

/**
 * Tests for {@link XmlUtil}.
 * @author Jason Smith
 */
public class TestXmlUtil extends Assert
{
	/** Instance of {@link XmlUtil} for tests. */
	protected final XmlUtil xml = new XmlUtil();
	
	/**
	 * Verify that I can parse a {@link Reader} into a {@link Document}.
	 * @throws Exception See {@link Exception}.
	 */
	@Test 
	public void testToDocumentWithReader() throws Exception
	{
		assertEquals("Unexpected tag name.", 
				"test", 
				xml.toDocument(new StringReader("<test/>")).getDocumentElement().getTagName());
	}
	
	/**
	 * Verify that I can parse a {@link String} into a {@link Document}.
	 * @throws Exception See {@link Exception}.
	 */
	@Test 
	public void testToDocumentWithString() throws Exception
	{
		assertEquals("Unexpected tag name.", 
				"test", 
				xml.toDocument("<test/>").getDocumentElement().getTagName());
	}
	
	/**
	 * Verify that I can parse a {@link InputStream} into a {@link Document}.
	 * @throws Exception See {@link Exception}.
	 */
	@Test 
	public void testToDocumentWithInputStream() throws Exception
	{
		assertEquals("Unexpected tag name.", 
				"test", 
				xml.toDocument(
						new ByteArrayInputStream(
								"<?xml version='1.0' encoding='UTF-8'?>\n<test/>".getBytes(StreamUtil.UTF8))).getDocumentElement().getTagName());
	}
	
	/**
	 * Verify that I can serialize a {@link Document} to XML.
	 * @throws IOException See {@link IOException}.
	 */
	@Test
	public void testToXml() throws IOException
	{
		final Document doc = xml.newDocument();
		doc.appendChild(doc.createElement("test"));

		String[] lines = xml.toXml(doc).split("\\r?\\n");
		assertEquals("Expecting result to contain 2 lines.",
				2,
				lines.length);
		assertTrue("XML declaration did not match.", 
				lines[0].matches("^<\\?xml\\s.*\\?>$"));
		assertEquals("XML body is different than expected.", 
				"<test/>", 
				lines[1]);
	}
	
	/**
	 * Verify I can get a number back from an XPath.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSelectNumber() throws Exception
	{
		final Document doc = xml.toDocument("<x><y>a</y><y>b</y><y>a</y></x>");
		assertEquals("Didn't get back the expected number of nodes. Curious.",
				2L,
				Math.round(xml.selectNumber(doc, "count(/x/y[.='a'])")));
	}
	
	/**
	 * Verify I can get a {@code true} back from an XPath.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSelectBooleanTrue() throws Exception
	{
		final Document doc = xml.toDocument("<x>a</x>");
		assertTrue("I was expecting maybe true.", xml.selectBoolean(doc, "/x[.='a']"));
	}
	
	/**
	 * Verify I can get a {@code false} back from an XPath.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSelectBooleanFalse() throws Exception
	{
		final Document doc = xml.toDocument("<x>a</x>");
		assertFalse("I was expecting maybe false.", xml.selectBoolean(doc, "/x[.='b']"));
	}
	
	/**
	 * Verify I can get back a string from an XPath.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSelectString() throws Exception
	{
		final Document doc = xml.toDocument("<x>Hello.</x>");
		assertEquals("XPath evaluation did not return the value I was expecting.",
				"Hello.",
				xml.selectString(doc, "/*/text()"));
	}
	
	/**
	 * Verify I can get back a {@link node} from the evaluation.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSelectNode() throws Exception
	{
		final Document doc = xml.toDocument("<x>Hello.</x>");
		final Element element = xml.selectNode(doc, "/*");
		assertNotNull("Didn't find the element.", element);
		assertEquals("Element didn't contain the text I was expecting.",
				"Hello.",
				element.getTextContent());
	}
	
	/**
	 * Verify I can get back a list of {@link node}s from the evaluation.
	 * @throws Exception See {@link Exception}.
	 */
	public void testSelectNodes() throws Exception
	{
		final Document doc = xml.toDocument("<x><y>a</y><y>b</y><y>c</y></x>");
		final List<Element> results = xml.selectNodes(doc, "/x/y");
		assertEquals("Got the wrong number of results back.",
				3,
				results.size());
		assertEquals("Maybe they came back out of order?",
				"a",
				results.get(0).getTextContent());
	}
	
	/**
	 * Verify I can get back a list of {@link node}s from the evaluation, where the document
	 * has a non-empty default namespace.
	 * @throws Exception See {@link Exception}.
	 */
	public void testSelectNodesWithNamespace() throws Exception
	{
		final Document doc = xml.toDocument("<x xmlns='http://blah'><y>a</y><y>b</y><y>c</y></x>");
		final List<Element> results = xml.selectNodes(doc, "/ns:x/ns:y", new Namespace("http://blah", "ns"));
		assertEquals("Got the wrong number of results back.",
				3,
				results.size());
		assertEquals("Maybe they came back out of order?",
				"a",
				results.get(0).getTextContent());
	}
}
