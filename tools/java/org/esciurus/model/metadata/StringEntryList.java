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

import org.esciurus.common.Dictionary;
import org.esciurus.common.KeyNotInDictionaryException;
import org.esciurus.model.opf.OPFPackage;


/**
 * A list of simple string entries within the metadata record.
 * Values of these entries may be checked against a controlled vocabulary.
 * 
 * @see MetadataRecord
 */
public class StringEntryList extends DCMetaEntryList<StringEntry>  {

	
	private Dictionary dictionary;
	
	/**
	 * Create a new string entry list.
	 * 
	 * @param parent the OPF package in which the metadata is contained
	 * @param encoded true if the content of the entries produced is encoded, 
	 * false if it is human readable

	 * @param dict the dictionary to use for entries of this list,
	 *      or <code>null</code> if no check against a dictionary
	 *      is desired.
	 * @param tagName the XML tag name for the entries of this list, used 
	 *    when reading/writing XML files
	 */
	public StringEntryList(OPFPackage parent, boolean encoded, Dictionary dict, String tagName) {
		super(parent, tagName, StringEntry.getEntryFactory(encoded, dict));
		this.dictionary=dict;
	}
	
	/**
	 * Get the dictionary associated with this list.
	 * 
	 * @return the dictionary; may be <code>null</code>
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	
	/**
	 * Add a new string entry with specified content to the list.
	 * Content (keys) will be checked against the dictionary.
	 * 
	 * @param key the content (key) to use for the new entry
	 * @return the StringEntry object correpsonding to the new entry
	 * @throws KeyNotInDictionaryException if the specified key is not found in the dictionary
	 * 
	 */
	public StringEntry addContentStrict(String key) throws KeyNotInDictionaryException {
		if (dictionary != null) dictionary.checkKey(key);
		
		return addContent(key);
	}
	

	/**
	 * Add a new string entry with specified content to the list.
	 * Content will <em>not</em> be checked against the dictionary.
	 * 
	 * @param content the content to use for the new entry
	 * @return the StringEntry object correpsonding to the new entry
	 * 
	 * @see #addContentStrict(String)
	 */
	public StringEntry addContent(String content) {
		
		StringEntry se = createNewEntry();
		se.setContent(content);
		add(se);
		return se;
	}

}
