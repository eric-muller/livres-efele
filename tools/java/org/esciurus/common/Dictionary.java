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

import java.util.Iterator;
import java.util.Locale;

/**
 * Abstract base class for internationalized dictionaries
 * that translate language-independent keys into 
 * localized displayable values.
 * This may be used for handling controlled vocabularies
 * in data fields.
 * <p>
 * The dictionary contains key-value pairs, both of which are strings.
 * Keys are thought to be unique, language-independent (and possibly internal)
 * identifiers, while the values correspond to displayable texts that might
 * be presented to end users and that might need localization.
 * </p>
 */
public abstract class Dictionary {

	/**
	 * Check whether the dictionary contains a certain key.
	 * 
	 * @param key the key to look for
	 * @return true if the key was found, false otherwise
	 * @see #checkKey(String)
	 */
	public abstract boolean hasKey(String key);

	/**
	 * Check whether a key is contained in this dictionary.
	 * The method does not return a value, but throws
	 * an exception if the key is not found.
	 * 
	 * @param key the key to check
	 * @throws KeyNotInDictionaryException if the key was not found in the dictionary
	 * @see #hasKey(String)
	 */
	public void checkKey(String key) throws KeyNotInDictionaryException {
		
		if (! hasKey(key)) throw new KeyNotInDictionaryException (key); 
		
	}

	
	/**
	 * Gets a displayable (iternationalized) string for a given key.
	 * If the key is not found in the dictionary,
	 * the key text itself will be returned.
	 * 
	 * @param key the key to search for
	 * @param locale the locale of the display
	 * @return the displayable text 
	 */
	public abstract String getDisplayValue(String key, Locale locale);

	/**
	 * Gets a list of all keys in the dictionary
	 * @return the list of keys (as iterator)
	 */
	public abstract Iterator<String> getKeys();

	

}