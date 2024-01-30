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

import java.util.Iterator;

import org.esciurus.model.opf.EpubFormatException;
import org.esciurus.model.opf.OPFPackage;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * A list of x-metadata entries within a metadata record.
 * 
 * @see MetadataRecord
 *
 */
public class XMetaList extends MetaEntryList<XMetaEntry> {

	/**
	 * Create a new XMetaList.
	 * 
	 * @param tagName the XML tag name corresponding to the entries of this list,
	 *        when reading/writing the XML representation
	 * @param parent the OPF package this metadata list is contained in
	 */
	public XMetaList(String tagName, OPFPackage parent) {

		super(parent, tagName, null);
	}
	
	
	/**
	 * Retrieve an entry of this list by its "name" attribute.
	 * If the entry is not found, and the createIfAbsent attribute is set,
	 * the entry will be created in the list.
	 * 
	 * @param name the name (value of the name attribute) to look for
	 * @param createIfAbsent true if the x-metadata should be created 
	 *        if it is not found
	 * @return the metadata entry found; may be <code>null</code> if
	 *         no entry was found or created
	 */
	public XMetaEntry getMetaByName (String name, boolean createIfAbsent) {
		
		XMetaEntry result = null;
		for (Iterator<XMetaEntry> it = iterator(); it.hasNext(); ){
			XMetaEntry thisEntry = it.next();
			if (name.equals(thisEntry.getName())) {
				result = thisEntry;
				break;
			}
		}
		
		if ((result == null) && createIfAbsent) {
			result = XMetaFactory.createEntry(name);
			this.add(result);
		}
		
		return result;
		
	}
	
	
	

	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#readFromXml(org.w3c.dom.Element)
	 */
	@Override
	public void readFromXml(Element parentElm) throws EpubFormatException {

		NodeList childList = parentElm.getElementsByTagName(getEntryTagName());
		for (int i = 0; i < childList.getLength(); i++) {
			
			Element childElm = (Element) childList.item(i);
			String metaName = readAttributeValue(childElm,XMetaEntry.nameAttr,true);
			
			XMetaEntry newChild = XMetaFactory.createEntry(metaName);
			this.add(newChild);

			newChild.readFromXml(childElm);
			
				
		}

	}
	
	

}
