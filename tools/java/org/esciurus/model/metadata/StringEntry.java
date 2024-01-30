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

import org.esciurus.common.Dictionary;
import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.opf.EntryFactory;
import org.esciurus.model.opf.EpubFormatException;
import org.w3c.dom.Element;


/**
 * A metadata entry that consists only of a textual value.
 * This value may be constrained by a controlled vocabulary,
 * given by a dictionary.
 * This is used e.g. for the "type" and "source" metadata elements,
 * among many others.
 * 
 * @see MetadataRecord
 * @see StringEntryList
 *
 */
public class StringEntry extends DCMetaEntry {

	private Dictionary dict;
	private String content;
	
	/**
	 * Create a StringEntry, based on a certain dictionary.
	 * 
	 * @param encoded true if the content of this encoded, 
	 * false if it is human readable
	 * @param dict the dictionary to use for this StringEntry,
	 *      or <code>null</code> if no check against a dictionary
	 *      is desired.
	 *      
	 * @see DCMetaEntry#DCMetaEntry(boolean)
	 */
	public StringEntry(boolean encoded, Dictionary dict) {
		super(encoded);
		this.dict = dict;
	}
	

	private static class StringEntryFactory implements EntryFactory<StringEntry> {

		private Dictionary dict;
		private boolean encoded;
		
		StringEntryFactory(boolean encoded, Dictionary dict) {
			super();
			this.dict=dict;
			this.encoded = encoded;
		}
		
		public StringEntry createEntry() {
			return new StringEntry(encoded,dict);
		}
		
	}

	/**
	 * Create a factory that produces StringEntry entries.
	 * 
	 * @param encoded true if the content of the entries produced will be encoded, 
	 * false if it is human readable
	 * @param dict the dictionary to use for the StringEntry objects 
	 *      created by this factory,
	 *      or <code>null</code> if no check against a dictionary
	 *      is desired.
	 * @return the factory created
	 * @see MetaEntryList (String,String,EntryFactory)
	 */
	public static EntryFactory<StringEntry> getEntryFactory(boolean encoded, Dictionary dict) {		
		return new StringEntryFactory(encoded, dict);
	}


	@Override
	public String getDisplayValue(Locale locale) {
		
		if (content == null) {
			return "";
		}
		else if (dict != null) {
			return dict.getDisplayValue(content,locale);
		}
		else {
			return content;
		}
	}

	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		super.readFromXml(element);
		content = element.getTextContent();		
	}

	@Override
	public void writeToXml(Element element)  {
		super.writeToXml(element);
		element.setTextContent(content);		
	}


	/**
	 * Retrieve the content of this string entry
	 * @return the content of the string entry
	 */
	public String getContent() {
		return content;
	}


	/**
	 * Set the content of this string entry
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}


	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		super.checkConstraints(ticket);
		
		// currently no constraint checks for bare String entries
		
	}

}
