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

package org.esciurus.model.metadata;

import java.util.Locale;

import org.esciurus.model.opf.EpubFormatException;
import org.w3c.dom.Element;


/**
 * Abstract base class of an x-metadata element, within the publication metadata.
 * 
 * <p>
 * This implementation allows for x-metadata entries with advanced logic,
 * rather than only unconstrained textual values.  
 * In the XML representation, data is always 
 * stored in the attributes allowed by the OPF specification (name, scheme, and content).
 * The in-memory representation may be more complex however.
 * This class serves as a base for these implementations.
 * </p>
 * 
 * @see XMetaList
 */
public abstract class XMetaEntry extends MetaEntry {

	
	/**
	 * Retrieve the content of this x-metadata entry
	 * 
	 * @return the content, as textual value
	 */
	protected abstract String getContent();



	/**
	 * Retrieve the name of this x-metadata entry (value of the "name" attribute)
	 * 
	 * @return Returns the name.
	 */
	protected abstract String getName();




	/**
	 * Retrieve the scheme of this x-metadata entry
	 * 
	 * @return Returns the scheme.
	 */
	protected abstract String getScheme();


	
	/**
	 * Fill this x-metadata element with values read from an XML file.
	 * 
	 * @param scheme the "scheme" attribute value read from XML
	 * @param content the "content" attribute value read from XML
	 * @throws EpubFormatException if data format problems are encountered while parsing the input data
	 */
	protected abstract void parseInput(String scheme, String content) throws EpubFormatException;


	@Override
	public String getDisplayValue(Locale locale) {
		
		String result = getName()+": "+getContent();
		return result;
	}

	
	static String nameAttr = "name"; 
	static String schemeAttr = "scheme"; 
	static String contentAttr = "content"; 

	@Override
	public final void writeToXml(Element element) {
		super.writeToXml(element);
		
		writeAttributeValue(element,nameAttr,getName(),true);
		writeAttributeValue(element,contentAttr,getContent(),true);
		writeAttributeValue(element,schemeAttr,getScheme(),false);
		
	}


	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFListEntry#readFromXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public final void readFromXml(Element element) throws EpubFormatException {
		
		super.readFromXml(element);
		
		/* 
		 * name attribute already read by parent list - 
		 * determines what class will be instantiated
		 */
		String inputScheme = readAttributeValue(element,schemeAttr,false,false);
		String inputContent = readAttributeValue(element,contentAttr,true,false);
		parseInput(inputScheme,inputContent);
		
		
	}




	
	
}
