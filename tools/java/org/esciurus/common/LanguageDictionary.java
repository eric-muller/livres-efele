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
import java.util.Vector;

/**
 * A key-value dictionary for language codes (RFC3066).
 * 
 * The class relies on Java's internal table of language codes and
 * associated names. Hence, not all keys in RFC3066 may be actually supported.
 * 
 */
public class LanguageDictionary extends Dictionary {

	
	private Vector<String> languageCodes;
	
	/**
	 * Code for "undetermined" language as per RFC3066.
	 * This value should only be used in places where giving a non-null
	 * language code is required, but the language is not determined.
	 */
	public static String LANG_UNDETERMINED = "UND";

	
	/**
	 * Create a new language dictionary.
	 */
	public LanguageDictionary () {
	
		initLanguageCodes();
	
	}
	
	private void initLanguageCodes() {

		languageCodes = new Vector<String>();
		String[] knownCodes = Locale.getISOLanguages();
		languageCodes.ensureCapacity(knownCodes.length);
		
		for (int i = 0; i < knownCodes.length; i++){
			languageCodes.add(knownCodes[i]);
		}
		
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.common.Dictionary#hasKey(java.lang.String)
	 */
	@Override
	public boolean hasKey(String key) {
		
		return languageCodes.contains(key);
	
	}
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.common.Dictionary#getDisplayValue(java.lang.String, java.util.Locale)
	 */
	@Override
	public String getDisplayValue (String key, Locale locale) {
		
		Locale keyLanguage = new Locale(key);
		
		if (keyLanguage == null) {
			return key;
		}
		else {
			return keyLanguage.getDisplayLanguage(locale);
		}
					
	}
	
	/* (non-Javadoc)
	 * @see org.esciurus.common.Dictionary#getKeys()
	 */
	@Override
	public Iterator<String> getKeys() {
		return languageCodes.iterator();
	}
	
	
	private static String subtagJoinChar = "-"; 
	
	/**
	 * Compare two language codes, taking all subtags of the code into account.
	 * The result of this method is a non-negative integer; here 0 means "no match"
	 * and higher numbers mean better matches.
	 * <p>
	 * In detail, the number is computed as follows:
	 * <ul> 
	 * <li> For each consecutive matching
	 * subtag in both codes, a value of 4 is added.</li>
	 * <li> If the two codes exactly agree, another 2 is added.</li>
	 * <li> If the first code is a true subset of the second, or vice versa, another 1 is added.</li>
	 * </p> 
	 * <p>
	 * Any parameter being <code>null</code> or the empty string will be handled
	 * as a language code with 0 subtags. That is, its match value is
	 * 1 against any non-empty language code, and 2 against another empty code. 
	 * </p> 
	 * @param lang1 the first language code tom compare
	 * @param lang2 the second language code to compare
	 * @return a nonnegative integer describing the match, as detailed above
	 */
	public static int compareLanguages(String lang1, String lang2) {
		
		String[] tags1, tags2; 
		
		if (lang1==null || lang1.length()==0) {
			tags1 = new String[0];
		}
		else {
			tags1 = lang1.split(subtagJoinChar);
		}
		
		if (lang2==null || lang2.length()==0) {
			tags2 = new String[0];
		}
		else {
			tags2 = lang2.split(subtagJoinChar);
		}

		int result = 0; 
		
		int pos=0;
		while (pos < tags1.length && pos < tags2.length) {
			if (tags1[pos].equalsIgnoreCase(tags2[pos])) {
				result += 4;
				pos ++;
			}
			else {
				break;
			}
		}
		if (pos == tags1.length) result += 1;
		if (pos == tags2.length) result += 1;
		
		
		return result;
	}
	
	
	
}
