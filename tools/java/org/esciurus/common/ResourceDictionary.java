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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

/**
 * A key-value dictionary that can be localized for internationalization,
 * reading its resources from property files.
 * 
 * <p>
 * The data for the dictionaries is loaded via Java's standard ResourceBundle
 * mechanism (Java2 i18n features). The dictionary data will typically reside
 * in property files, one for each language. See the documentation for
 * java.util.PropertyResourceBundle for details.</p>
 * 
 * <p>
 * This class uses java.util.ResourceBundle object to read its contents.
 * This is done at run-time as needed. However, a locale-independent 
 * list of keys is cached by the present class to provide better performance.
 * </p>
 * 
 * <p>
 * Further, the present class
 * can (and must) deal with the case that a key, coming e.g. from an
 * external source, might not be known in the dictionary. In this case,
 * the key text itself is used for display purposes.
 * </p>
 * 
 */
public class ResourceDictionary extends Dictionary {

	/**
	 * class base name for the resources that hold the dictionary contents 
	 */
	private String resourceBaseName;

	/*
	 * list of valid keys, loaded once at initialization, independent of locale
	 */
	private Set<String> keys;
	
	/**
	 * prefix of "exceptional" key values that are acceptedwithout being in the dictionary.
	 */
	private String exceptionPrefix;

	
	/**
	 * Creates a new resource dictionary, linked to a resource on the file system.
	 * <p>
	 * Optionally, an "exception prefix" can be specified. The dictionary will
	 * accept all keys starting with this prefix, even if they are not 
	 * actually contained in the dictionary. If no such prefix is desired, set
	 * this parameter to <code>null</code> (NOT to the empty string).
	 * </p>
	 *  
	 * @param baseName The base name (class-like name) of the resource
	 * associated with this dictionary.
	 * 
	 * @param exceptionPrefix The "exception prefix" for this dictionary. Set to
	 * <code>null</code> if none is desired.
	 */
	public ResourceDictionary (String baseName,String exceptionPrefix) {
	
		resourceBaseName = baseName;
		this.exceptionPrefix = exceptionPrefix;
		initKeys();
	
	}
	
	
	private void initKeys() {
		
		// load from default locale
		ResourceBundle defBundle = ResourceBundle.getBundle(resourceBaseName);
		
		keys = new TreeSet<String>();
			
		for(Enumeration<String> ke = defBundle.getKeys();ke.hasMoreElements();) {
			keys.add(ke.nextElement());
		}
		
	}
	                         
	
	/* (non-Javadoc)
	 * @see org.esciurus.common.Dictionary#hasKey(java.lang.String)
	 */
	@Override
	public boolean hasKey(String key) {
		
		boolean result = false;
		
		if (exceptionPrefix != null) result = key.startsWith(exceptionPrefix);
		
		result = result || keys.contains(key);
		
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.common.Dictionary#getDisplayValue(java.lang.String, java.util.Locale)
	 */
	@Override
	public String getDisplayValue (String key, Locale locale) {
		
		String value = key;
		
		if (hasKey(key)) {
			
			ResourceBundle bundle = ResourceBundle.getBundle(resourceBaseName,locale);
			try{
				value = bundle.getString(key);
			}
			catch (MissingResourceException e) {
				//default value: use key, see above
			}
		}
		
		return value;
		
	}
	
	/* (non-Javadoc)
	 * @see org.esciurus.common.Dictionary#getKeys()
	 */
	@Override
	public Iterator<String> getKeys() {
		return keys.iterator();
	}


	
}
