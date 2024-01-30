/*
 *  Esciurus - a personal electronic library 
 *  Copyright (C) 2007 B. Wolterding
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */

package org.esciurus.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A utility class for initializing
 * and using XML parsers. It hides the parser 
 * object (and associated factories) and allows to read, write, or create
 * XML document objects directly.
 * 
 * <p><em>Pattern:</em> Singleton</p>
 */
public class XmlUtility {

	
	private static XmlUtility singleInstance;
	
	/**
	 * Get an instance (the system-wide single instance)
	 * of the XmlUtility class.
	 * 
	 * @return an instance of XmlUtility
	 */
	public static synchronized XmlUtility getInstance() {
		if (singleInstance==null) {
			singleInstance = new XmlUtility();
		}
		
		return singleInstance;
				
	}
	
	
	private DocumentBuilderFactory builderFactory;
	private TransformerFactory transformerFactory;
	
	
	private XmlUtility() {
		
		builderFactory = DocumentBuilderFactory.newInstance();
		transformerFactory = TransformerFactory.newInstance();
		
		configureFactories();
		
	}

		
	private void configureFactories() {
		
		builderFactory.setValidating(false);
		builderFactory.setNamespaceAware(true);
			
	}
	
	
	private DocumentBuilder createDocumentBuilder() {

		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			
			builder.setEntityResolver(new MyEntityResolver());		
			
			return builder;
		}
		catch (ParserConfigurationException e) {
			
			/* this should REALLY never happen, since options for the parser factory
			 * are fixed. 
			 */
			throw new RuntimeException("error configuring XML parser",e);
			
		}
	}
	
	private class MyEntityResolver implements EntityResolver {
		
		public InputSource resolveEntity(String publicId, String systemId)
		{
			if (systemId.endsWith(".dtd"))
				// this deactivates all DTDs by giving empty XML docs
				return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			else
				// return empty files - deactivate all external entitites
				return new InputSource(new ByteArrayInputStream("".getBytes()));
		}
	}
	
	
	/**
	 * Create an empty XML document.
	 * 
	 * @return the new XML document, empty
	 */
	public Document createEmptyDocument() {
		Document doc  = createDocumentBuilder().newDocument();
		return doc;
	}
	
	
	/**
	 * Read an XML document from an input stream.
	 * 
	 * @param stream thre stream to read from
	 * @return the XML document that has been parsed from the stream
	 * @throws SAXException if an error occurs during XML parsing
	 * @throws IOException if an error occurs in stream I/O
	 */
	public Document readXml(InputStream stream) throws SAXException, IOException {
		
		DocumentBuilder builder = createDocumentBuilder();
		
		Document doc = builder.parse(stream);

		return doc;
		
	}
	
	
	/**
	 * Write an XML document to an output stream.
	 * 
	 * @param doc the XML document to write
	 * @param stream the stream to use for output
	 * @param publicId the public id of the XML document to be written. 
	 * Set to <code>null</code> if none is desired.
	 * @param systemId  the system id of the XML document to be written. 
	 * Set to <code>null</code> if none is desired.
	 * @throws TransformerException if an error occurs in XML processing
	 */
	public void writeXml(Document doc, OutputStream stream, String publicId, String systemId) throws TransformerException {
		
		Transformer tf = transformerFactory.newTransformer();
		
		tf.setOutputProperty(OutputKeys.INDENT,"yes");
		if (publicId != null || systemId != null) {
			tf.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,publicId);
			tf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,systemId);
		}
		
		tf.transform( new DOMSource(doc), new StreamResult(stream) );
				
	}
	
}
