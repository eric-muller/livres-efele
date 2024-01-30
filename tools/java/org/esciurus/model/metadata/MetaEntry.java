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

import org.esciurus.model.opf.OPFElement;


/**
 * Abstract base class for an entry in the metadata record 
 * (or one of its sublists).
 *
 * @see MetaEntryList
 */
public abstract class MetaEntry extends OPFElement  {

	
	/**
	 * Retrieve a displayable value for this metadata entry,
	 * corresponding to a given locale.
	 * 
	 * @param locale the locale to use for formatting
	 * @return a displayable value for this metadata entry
	 */
	public abstract String getDisplayValue(Locale locale);

	
}
