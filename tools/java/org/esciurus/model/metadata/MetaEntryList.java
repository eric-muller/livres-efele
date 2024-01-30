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

import org.esciurus.model.opf.EntryFactory;
import org.esciurus.model.opf.EpubFormatException;
import org.esciurus.model.opf.OPFList;
import org.esciurus.model.opf.OPFPackage;
import org.w3c.dom.Element;


/**
 * A list of metadata entries.
 * 
 * @param <E> the class of metadata entries accepted as elements of the list 
 * 
 */
public class MetaEntryList<E extends MetaEntry> extends OPFList<E>  {

	
	/**
	 * Create a new MetaEntryList.
	 * 
	 * @param parent the OPF package in which the metadata is contained
	 * @param tagName the tag name for entries of this list, when represented as XML
	 * @param entryFactory a factory for new list entries
	 */
	public MetaEntryList(OPFPackage parent, String tagName, EntryFactory<E> entryFactory ) {
		super( tagName,
		       MetadataRecord.getOEBPSFallbackTagName(tagName),
			   entryFactory);
		moveToPackage(parent);
	}
	
	
	/**
	 * Retrieve a displayable value of this list.
	 * This will be composed of displayable values for all list entries,
	 * separated by line breaks.
	 *  
	 * @param locale the locale to use for formatting
	 * @return the displayable value for this list
	 */
	public String getCollatedDisplayValue (Locale locale) {
		
		StringBuffer sb = new StringBuffer();

		for (int idx = 0; idx < size(); idx++) {
			String value = this.get(idx).getDisplayValue(locale);
			sb.append(value);
			if (idx < size()-1) {
				sb.append(", ");
			}
		}
		
		return sb.toString();
		
	}

	@Override
	public void writeToXml(Element element) {
		
		writeEntriesToXml(element);
		
	}

	
	@Override
	public void readFromXml(Element parentElm) throws EpubFormatException {
		
		readEntriesFromXml(parentElm);
				
	}


	
}
