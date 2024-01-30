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
import java.util.Locale;

import org.esciurus.common.LanguageDictionary;
import org.esciurus.model.opf.EntryFactory;
import org.esciurus.model.opf.OPFPackage;

/**
 * A list of Dublin Core metadata entries.
 * 
 * <p>This class adds functionality for searching the list by 
 * encoding scheme or language code.</p>
 *
 * @param <E> the class of entries of this list
 */
public class DCMetaEntryList<E extends DCMetaEntry> extends MetaEntryList<E> {

	/**
	 * Create a new DCMetaEntryList.
	 * 
	 * @param parent the OPF package in which the metadata is contained
	 * @param tagName the tag name for entries of this list, when represented as XML
	 * @param entryFactory a factory for new list entries
	 */
	public DCMetaEntryList(OPFPackage parent, String tagName,
			EntryFactory<E> entryFactory) {
		super(parent, tagName, entryFactory);
	}
	
	/**
	 * Get the entry of the list that matches a given encoding scheme.
	 * If no such entry is found, or the list is empty, 
	 * the return value will be <code>null</code>.
	 * 
	 * <p>This method is only useful on lists which contain "encoded"
	 * entries, i.e. entries which support the encoding attribute. On lists with
	 * "human readable" entries, <code>null</code> will be returned in all cases.</p>
	 * 
	 * @param encoding the encoding scheme to search for
	 * @return the entry found for thie encoding scheme, or <code>null</code> if none was found
	 * 
	 * @see DCMetaEntry#DCMetaEntry(boolean)
	 */
	public E getByEncoding(String encoding) {
		E result = null;
		
		for (Iterator<E> it = iterator(); it.hasNext();) {
			E thisEntry = it.next();
			if (encoding.equals(thisEntry.getEncoding())) {
				result = thisEntry;
				break;
			}
		}
		
		return result;
	}
	

	/**
	 * Get the entry of the list that best matches a given language code.
	 * This includes matching of all subtags of the language code,
	 * e.g. country and regional identifiers. The returned entry 
	 * need not precisely match the given language; it is only the
	 * best approximation found.
	 * 
	 * <p>If multiple entries are present which equally match the language code,
	 * the first one of these is returned. In particular, if no match for the language
	 * is found at all, the first entry in the list will be returned.
	 * If the list is empty, the return value will be <code>null</code>.</p>
	 * 
	 * <p>This method is only useful on lists which contain "human readable"
	 * entries, i.e. entries which support the language tags. On lists with
	 * "encoded" entries, the first element of the list will be returned in all case.</p>
	 * 
	 * @param language the language code to search for
	 * @return the best match found; or <code>null</code> if the list is empty
	 * 
	 * @see DCMetaEntry#DCMetaEntry(boolean)
	 * @see LanguageDictionary#compareLanguages(String, String)
	 */
	public E getByLanguage(String language) {
		E result = null;
		int bestMatch = -1;
		
		for (Iterator<E> it = iterator(); it.hasNext();) {
			E thisEntry = it.next();
			int thisMatch = LanguageDictionary.compareLanguages(language,thisEntry.getLanguage());
			if (thisMatch > bestMatch) {
				result = thisEntry;
				bestMatch = thisMatch;
			}
		}
		
		return result;
	}

	/**
	 * Get the entry of the list that best matches a given locale.
	 * @see #getByLanguage(String)
	 * 
	 * @param locale the locale to search for
	 * @return the best match found; or <code>null</code> if the list is empty
	 */
	public E getEntryForLocale(Locale locale) {
		return getByLanguage(locale.getLanguage());
	}

	/**
	 * Get the display value of that entry of the list 
	 * that best matches a given locale.
	 * @see #getByLanguage(String)
	 * 
	 * @param locale the locale to search for
	 * @return the display value of the match found; or the empty string if the list is empty.
	 * Never <code>null</code>.
	 */
	public String getValueForLocale(Locale locale) {
		String result = "";
		E entry = getByLanguage(locale.getLanguage());
		if (entry != null) {
			result = entry.getDisplayValue(locale);
		}
		return result;
	}

	
}
