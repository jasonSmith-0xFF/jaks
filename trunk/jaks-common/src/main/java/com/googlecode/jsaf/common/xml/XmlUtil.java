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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.googlecode.jsaf.common.SquashedException;
import com.googlecode.jsaf.common.io.StreamUtil;

/**
 * <p>A collection of lightweight XML utility methods covering XML-&gt;DOM, DOM-&gt;XML, and XPath.</p>
 * <dl>
 * <dt>A Note on Threaded Performance.</dt>
 * <dd>This class is thread-safe, but significant portions are synchronized. If you are using {@link XmlUtil}
 * across threads, you should consider creating one instance per thread.</dd>
 * </dl>
 * @author Jason Smith
 */
public class XmlUtil 
{
	private DocumentBuilder builder = null;
	
	/**
	 * Gets the {@link DocumentBuilderFactory} used to create the builder for {@link #getBuilder()}.
	 * Override this method to change the settings of the factory. Default is non-validating and namespace-aware.
	 * @return The {@link DocumentBuilderFactory} used to create the builder for {@link #getBuilder()}.
	 */
	protected synchronized DocumentBuilderFactory getDocumentBuilderFactory()
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		
		try 
		{
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
		} 
		catch(final ParserConfigurationException e) 
		{
			//Ignore.
		}
		
		try 
		{
			factory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
		} 
		catch(final ParserConfigurationException e) 
		{
			//Ignore.
		}
		
		return factory;
	}
	
	/**
	 * Gets the builder. Note that the builder is {@link DocumentBuilder#reset()} on every call to 
	 * {@link #toDocument(InputSource)}, so it is safe to modify its configuration.
	 * @return The document builder.
	 */
	protected synchronized final DocumentBuilder getBuilder()
	{
		if(builder == null)
		{
			try 
			{
				builder = getDocumentBuilderFactory().newDocumentBuilder();
			} 
			catch (ParserConfigurationException e) 
			{
				return SquashedException.raise(e);
			}
		}
		return builder;
	}
	
	/**
	 * Get a new, empty {@link Document}.
	 * @return A new, empty {@link Document}.
	 */
	public synchronized Document newDocument()
	{
		try
		{
			return getBuilder().newDocument();
		}
		finally
		{
			getBuilder().reset();
		}
	}
	
	/**
	 * Parse XML input to a {@link Document}.
	 * @param xml The input XML.
	 * @return The equivalent {@link Document}.
	 * @throws SAXException See {@link DocumentBuilder#parse(InputSource)}.
	 * @throws IOException See {@link IOException}.
	 */
	public synchronized Document toDocument(final InputSource xml) throws SAXException, IOException
	{
		try
		{
			return getBuilder().parse(xml);
		}
		finally
		{
			getBuilder().reset();
		}
	}
	
	/**
	 * Parse XML input to a {@link Document}.
	 * @param xml The input XML.
	 * @return The equivalent {@link Document}.
	 * @throws SAXException See {@link DocumentBuilder#parse(InputSource)}.
	 * @throws IOException See {@link IOException}.
	 */
	public Document toDocument(final Reader xml) throws SAXException, IOException
	{
		return toDocument(new InputSource(xml));
	}
	
	/**
	 * Parse XML input to a {@link Document}.
	 * @param xml The input XML.
	 * @return The equivalent {@link Document}.
	 * @throws SAXException See {@link DocumentBuilder#parse(InputSource)}.
	 * @throws IOException See {@link IOException}.
	 */
	public Document toDocument(final String xml) throws SAXException, IOException
	{
		return toDocument(new StringReader(xml));
	}
	
	/**
	 * Parse XML input to a {@link Document}.
	 * @param xml The input XML.
	 * @return The equivalent {@link Document}.
	 * @throws SAXException See {@link DocumentBuilder#parse(InputSource)}.
	 * @throws IOException See {@link IOException}.
	 */
	public Document toDocument(final InputStream xml) throws SAXException, IOException
	{
		return toDocument(new InputSource(xml));
	}
	
	/**
	 * Serialize a {@link Document} or other DOM node to XML.
	 * @param node The source DOM node.
	 * @return The equivalent XML.
	 * @throws IOException See {@link IOException}.
	 */
	public String toXml(final Node node) throws IOException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		toXml(node, out, StreamUtil.UTF8, true, true);
		return new String(out.toByteArray(), StreamUtil.UTF8);
	}
	
	/**
	 * Serialize a {@link Document} or other DOM node to XML.
	 * @param node The source DOM node.
	 * @param output The target output stream (bytes).
	 * @param encoding The text encoding.
	 * @param declaration {@code true} to include the XML declaration in the output; {@code false} to omit.
	 * @param format {@code true} to format the output for readability; {@code false} for raw output.
	 * @throws IOException See {@link IOException}.
	 */
	public synchronized void toXml(
			final Node node, 
			final OutputStream output, 
			final String encoding, 
			final boolean declaration, 
			final boolean format) throws IOException
	{
        final Document ownerDocument =
        		node instanceof Document
        		?(Document)node
        		:node.getOwnerDocument();
        
		final DOMImplementation domImpl = ownerDocument.getImplementation();
		final DOMImplementationLS domImplLS = (DOMImplementationLS)domImpl.getFeature("LS", "3.0");
        final LSSerializer serializer = domImplLS.createLSSerializer();

		serializer.getDomConfig().setParameter("xml-declaration", declaration); 
        if (format && serializer.getDomConfig().canSetParameter("format-pretty-print", true))
		{
		    serializer.getDomConfig().setParameter("format-pretty-print", true);
		}
		
		final LSOutput lsOutput = domImplLS.createLSOutput();
		lsOutput.setEncoding(encoding);
    	try(final OutputStream bufferedOut = new BufferedOutputStream(output))
        {
    		lsOutput.setByteStream(bufferedOut);
    		serializer.write(node, lsOutput);
        }
	}
	
	/**
	 * Gets a new {@link XPath} instance.
	 * @return A new {@link XPath} instance.
	 */
	protected synchronized XPath getXPath()
	{
		final ClassLoader cl = getClass().getClassLoader();
		if(cl.getResource("net/sf/saxon/xpath/XPathFactoryImpl.class") != null)
		{
			/*
			 * Special logic to swap for Saxon implementation, if available.
			 */
			try 
			{
				return XPathFactory.newInstance(
						XPathFactory.DEFAULT_OBJECT_MODEL_URI, 
						"net.sf.saxon.xpath.XPathFactoryImpl", 
						cl).newXPath();
			} 
			catch (XPathFactoryConfigurationException e) 
			{
				return SquashedException.raise(e);
			}
		}
		else
		{
			return XPathFactory.newInstance().newXPath();
		}
	}
	
	/**
	 * The internal namespace context used in XPath operations.
	 */
	private static class CustomNamespaceContext implements NamespaceContext
	{
		private final Map<String,String> urisByPrefix = new HashMap<String,String>();
		private final Map<String,String> prefixsByUri = new HashMap<String,String>();
		
		/**
		 * Constructor.
		 * @param namespaces Array of namespaces.
		 */
		public CustomNamespaceContext(final Namespace... namespaces)
		{
			for(final Namespace namespace : namespaces)
			{
				urisByPrefix.put(namespace.getPrefix(), namespace.getUri());
				prefixsByUri.put(namespace.getUri(), namespace.getPrefix());
			}
		}
		
		@Override
		public String getNamespaceURI(final String prefix) 
		{
			return urisByPrefix.get(prefix);
		}

		@Override
		public String getPrefix(final String namespaceURI) 
		{
			return prefixsByUri.get(namespaceURI);
		}

		@Override
		public Iterator<String> getPrefixes(final String namespaceURI) 
		{
			return urisByPrefix.keySet().iterator();
		}
	}
	
	/**
	 * Generalized evaluation of an XPath 1.0 expression.
	 * @param returnType The return type. See {@link XPathExpression#evaluate(Object, QName)} and {@link XPathConstants}.
	 * @param node The target node.
	 * @param xpath The XPath expression.
	 * @param namespaces The set of namespaces.
	 * @return The result(s).
	 * @throws XPathExpressionException See {@link XPath#compile(String)} and {@link XPathExpression#evaluate(InputSource, QName)}.
	 */
    @SuppressWarnings("unchecked")
	protected synchronized <T> T select(final QName returnType, final Node node, final String xpath, final Namespace... namespaces) throws XPathExpressionException 
    {
    	final XPath xp = getXPath();
    	xp.setNamespaceContext(new CustomNamespaceContext(namespaces));
    	final XPathExpression expression = getXPath().compile(xpath);
    	return (T)expression.evaluate(node, returnType);
    }
    
    /**
     * Evaluate an XPath expression, returning the result as a {@link node}.
     * @param node The target node.
     * @param xpath The XPath expression.
     * @param namespaces The set of namespaces.
     * @return The result from the XPath expression.
     * @throws XPathExpressionException See {@link #select(QName, Node, String, Namespace...)}.
     */
    public <T extends Node> T selectNode(final Node node, final String xpath, final Namespace... namespaces) throws XPathExpressionException
    {
    	return select(XPathConstants.NODE, node, xpath, namespaces);
    }
    
    /**
     * Evaluate an XPath expression, returning the result as a list of {@link node}s.
     * @param node The target node.
     * @param xpath The XPath expression.
     * @param namespaces The set of namespaces.
     * @return The result from the XPath expression.
     * @throws XPathExpressionException See {@link #select(QName, Node, String, Namespace...)}.
     */
    @SuppressWarnings("unchecked")
	public <T extends Node> List<T> selectNodes(final Node node, final String xpath, final Namespace... namespaces) throws XPathExpressionException
    {
    	final NodeList nodes = (NodeList)select(XPathConstants.NODESET, node, xpath, namespaces);
    	final List<T> results = new ArrayList<T>(nodes.getLength());
    	for(int i=0; i<nodes.getLength(); i++)
    	{
    		results.add((T)nodes.item(i));
    	}
    	return results;
    }
    
   /**
    * Evaluate an XPath expression, returning the result as a string.
    * @param node The target node.
    * @param xpath The XPath expression.
    * @param namespaces The set of namespaces.
    * @return The result from the XPath expression.
    * @throws XPathExpressionException See {@link #select(QName, Node, String, Namespace...)}.
    */
    public String selectString(final Node node, final String xpath, final Namespace... namespaces) throws XPathExpressionException
    {
    	return select(XPathConstants.STRING, node, xpath, namespaces);
    }
    
    /**
     * Evaluate an XPath expression, returning the result as a double.
     * @param node The target node.
     * @param xpath The XPath expression.
     * @param namespaces The set of namespaces.
     * @return The result from the XPath expression.
     * @throws XPathExpressionException See {@link #select(QName, Node, String, Namespace...)}.
     */
    public double selectNumber(final Node node, final String xpath, final Namespace... namespaces) throws XPathExpressionException
    {
    	return select(XPathConstants.NUMBER, node, xpath, namespaces);
    }
    
    /**
     * Evaluate an XPath expression, returning the result as a boolean.
     * @param node The target node.
     * @param xpath The XPath expression.
     * @param namespaces The set of namespaces.
     * @return The result from the XPath expression.
     * @throws XPathExpressionException See {@link #select(QName, Node, String, Namespace...)}.
     */
    public boolean selectBoolean(final Node node, final String xpath, final Namespace... namespaces) throws XPathExpressionException
    {
    	return select(XPathConstants.BOOLEAN, node, xpath, namespaces);
    }
}
