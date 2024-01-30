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

package org.esciurus.model.dictionaries;

import org.esciurus.common.*;

/**
 * Provides access to dictionaries for those
 * data fields where a controlled vocabulary of keys is used.
 * All these dictionaries are global singletons;
 * this class provides methods to retrieve their instances. 
 * 
 * <p>The class contains only static methods and need not be 
 * instantiated.</p>
 *
 */
public final class Dictionaries {

	private static Dictionary marcDict;
	private static Dictionary langDict;
	private static Dictionary typeDict;
	private static Dictionary mimeDict;
	private static Dictionary refTypeDict;
	
	
	/**
	 * Get a dictionary for MARC relator codes.
	 *  
	 * @return the dictionary
	 */
	public static Dictionary getMarcDict() {
		if (marcDict == null) 
			marcDict = new ResourceDictionary("org.esciurus.model.dictionaries.MarcDictionary","oth.");
		
		return marcDict;
	}

	/**
	 * Get a dictionary for DCMI type codes.
	 *  
	 * @return the dictionary
	 */
	public static Dictionary getTypeDict() {
		if (typeDict == null) 
			typeDict = new ResourceDictionary("org.esciurus.model.dictionaries.DcmiTypeDictionary",null);
		
		return typeDict;
	}

	/**
	 * Get a dictionary of MIME media types.
	 * 
	 * @return the dictionary
	 */
	public static Dictionary getMimeDict() {
		if (mimeDict == null) 
			mimeDict = new ResourceDictionary("org.esciurus.model.dictionaries.MimeDictionary",null);
		
		return mimeDict;
	}

	/**
	 * Get a dictionary of reference types 
	 * according to OPF specification / Chicago Manual of Style.
	 * 
	 * @return the dictionary
	 */
	public static Dictionary getRefTypeDict() {
		if (refTypeDict == null) 
			refTypeDict = new ResourceDictionary("org.esciurus.model.dictionaries.RefTypeDictionary","other.");
		
		return refTypeDict;
	}

	/**
	 * Get a dictionary for RFC 3066 language codes.
	 * 
	 * @return the dictionary
	 */
	public static Dictionary getLanguageDict() {
		if (langDict == null) 
			langDict = new LanguageDictionary();
		
		return langDict;
	}

	
}
