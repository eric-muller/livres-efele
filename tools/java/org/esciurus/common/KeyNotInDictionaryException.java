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

/**
 * This exception is thrown if a requested key was not found
 * in the vocabulary of a dictionary.
 * 
 * @see Dictionary
 *
 */
public class KeyNotInDictionaryException extends Exception {


	/**
	 * Create a new exception, associated with a specific key
	 * that was not found in the dictionary.
	 * 
	 * @param key the bad key
	 */
	public KeyNotInDictionaryException(String key) {
		super("unknown key: ["+key+"]");
	}

}
