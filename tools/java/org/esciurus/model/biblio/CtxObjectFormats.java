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

package org.esciurus.model.biblio;

import java.util.Locale;

import org.esciurus.common.ResourceLoader;
import org.esciurus.model.opf.EpubFormatException;


/**
 * Formats of a publication (or its bibliographic information).
 * These values are in direct correspondence with format identifiers
 * for the OpenURL ContextObject format. 
 */
public enum CtxObjectFormats {

	/**
	 * A book or conference proceedings volume
	 */
	BOOK,
	
	/**
	 * A scientific journal
	 */
	JOURNAL,
	
	/**
	 * A dissertation
	 */
	DISSERTATION,
	
	/**
	 * A patent
	 */
	PATENT;

	private static String formatPrefix = "info:ofi/fmt:kev:mtx:";
	
	/**
	 * Get the ContextObject format identifier for this format.
	 * More precisely, this format identifier is for the
	 * KEV serialization, Matrix constraint language, as defined
	 * in the OpenURL specification. 
	 * 
	 * <p>Note: Currently these values are directly derived from
	 * the <em>Java identifiers</em> of the format constants.
	 * </p>
	 * @return the ContextObject format identifier
	 */
	public String getFormatIdentifier() {
		return formatPrefix+this.name().toLowerCase();
	}
	
	private static String bundleName = "org.esciurus.model.dictionaries.BiblioBundle";
	
	/**
	 * Get a displayable description for this publication format.
	 * 
	 * @param locale the locale to use for formatting
	 * @return a displayable string for this format
	 */
	public String getDisplayName(Locale locale) {
		return ResourceLoader.getEnumDisplayName(this,bundleName,locale);
	}

	/**
	 * Retrieve the publication format from a ContextObject 
	 * format identifier.
	 * 
	 * @param kevFmt the ContextObject format identifier
	 * @return the format value corresponding to this identifier
	 * @throws EpubFormatException if the format identifier is not recognized
	 * 	as a legal value
	 * 
	 * @see #getFormatIdentifier()
	 */
	public static CtxObjectFormats fromFormatIdentifier(String kevFmt) throws EpubFormatException {
		CtxObjectFormats result = null;
		
		if (kevFmt.startsWith(formatPrefix)) {
			String suffix = kevFmt.substring(formatPrefix.length());
			result = valueOf(suffix.toUpperCase()); 
		}
		else {
			throw new EpubFormatException ("format identifier has bad prefix: "+kevFmt);
		}
		
		return result;
	}
	
}
