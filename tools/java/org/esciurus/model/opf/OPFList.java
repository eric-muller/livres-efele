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

package org.esciurus.model.opf;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A list of package parts, which includes functionality to read
 * and write this list from an to XML representation.
 *
 * @param <E> the class of pacakge parts accepted as entries of this list 
 */
public abstract class OPFList<E extends PackagePart> extends PackagePartList<E> {

	
	private String entryTagName;
	private String entryTagName2;
	private EntryFactory<E> entryFactory;
	
	/**
	 * Create a new OPF list.
	 * 
	 * @param entryTagName the XML tag (element name) used for entries of this list in XML representation
	 * @param entryTagName2 an alternate XML tag which is also accepted during read procedures (used for backward compatibility with OEBPS 1.2)
	 * @param entryFactory a factory that creates new entries for the list
	 */
	public OPFList (String entryTagName, 
			String entryTagName2, EntryFactory<E> entryFactory ) {
		super();
		this.entryTagName = entryTagName;
		this.entryTagName2 = entryTagName2;
		this.entryFactory = entryFactory;
		
	}

	/**
	 * @return Returns the entryTagName.
	 */
	protected String getEntryTagName() {
		return entryTagName;
	}


	
	/**
	 * Create a new entry of the list.
	 * This uses the factory passed to the constructor upon creation of this list.
	 * 
	 * @return a new entry of the list (but not yet added to the list)
	 * 
	 * @see #OPFList(String, String, EntryFactory)
	 * @see #addNewEntry()
	 */
	protected E createNewEntry() {
		return entryFactory.createEntry();
	}

	
	/**
	 * Create a new entry and add it to the list.
	 * 
	 * @return the new entry, after being added to the list
	 */
	public E addNewEntry() {
		E newEntry = createNewEntry();
		this.add(newEntry);
		return newEntry;
	}
	
	
	/**
	 * Write the entries of the list to XML representation.
	 * Every entry is written to its own XML element.
	 * 
	 * @param parentNode the parent node to which the entries are appended as children
	 */
	protected void writeEntriesToXml(Node parentNode) {

		Document doc = parentNode.getOwnerDocument();

		for (Iterator<E> it = this.iterator(); it.hasNext();) {
			
			Element elm = doc.createElement(entryTagName);
			parentNode.appendChild(elm);
			it.next().writeToXml(elm);

		}
		
	}

	/**
	 * Read the entries of the list from XML representation.
	 * This retrieves all child elements of the XML element given
	 * which correspond to the XML tag name specific to this list,
	 * and adds these as new elements to the list.
	 *  
	 * @param elm the XML element to read the children from 
	 * @throws EpubFormatException if a problem is encountered with the input data
	 */
	protected void readEntriesFromXml(Element elm) throws EpubFormatException {
		readTagFromXml(elm,entryTagName);
		if (entryTagName2 != null) readTagFromXml(elm,entryTagName2);
	}
	
	
	private void readTagFromXml(Element elm, String tagName) throws EpubFormatException {
	
		NodeList childList = elm.getElementsByTagName(tagName);
		
		for (int i = 0; i < childList.getLength(); i++) {
			
			Element childElm = (Element) childList.item(i);

			E newEntry = createNewEntry();
			this.add(newEntry);
			
			newEntry.readFromXml(childElm);
			
			
		}
	}


	
}
