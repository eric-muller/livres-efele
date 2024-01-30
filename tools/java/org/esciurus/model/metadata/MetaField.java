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

import org.esciurus.common.ResourceLoader;

/**
 * Metadata fields allowed in the OPF specification.
 * This includes both Dublin Core (DC) metadata 
 * and extra metadata (<meta> tag).
 */
public enum MetaField {
	
	/**
	 * The DC Title field
	 */
	TITLE,

	/**
	 * The DC Creator field
	 */
	CREATOR,
	
	/**
	 * The DC Subject field
	 */
	SUBJECT,
	
	/**
	 * The DC Description field
	 */
	DESCRIPTION,
	
	/**
	 * The DC Publisher field
	 */
	PUBLISHER,
	
	/**
	 * The DC Contributor field
	 */
	CONTRIBUTOR,
	
	/**
	 * The DC Date field
	 */
	DATE,
	
	/**
	 * The DC Type field
	 */
	TYPE,
	
	/**
	 * The DC Format field
	 */
	FORMAT,
	
	/**
	 * The DC Identifier field
	 */
	IDENTIFIER,
	
	/**
	 * The DC Source field
	 */
	SOURCE,
	
	/**
	 * The DC Language field
	 */
	LANGUAGE,
	
	/**
	 * The DC Relation field
	 */
	RELATION,
	
	/**
	 * The DC Coverage field
	 */
	COVERAGE,
	
	/**
	 * The DC Rights field
	 */
	RIGHTS,
	
	/**
	 * The extra metadata field defined in the OPF specification.
	 * This is represented by HTML-like "meta" tags in the XML file.
	 */
	EXTRA;
	
	/**
	 * Test whether this field is a Dublin Core metadata field.
	 * @return true if the field is a Dublin Core field, false if it is the extra metadata field
	 */
	public boolean isDCField() {
		return !this.equals(EXTRA);
	}
	
	private String bundleName = "org.esciurus.model.dictionaries.MetadataBundle";
	
	/**
	 * Get a displayable value for this field, in a specific locale.
	 * 
	 * @param locale the locale to use for formatting
	 * @return the displayable value
	 */
	public String getDisplayName(Locale locale) {
		return ResourceLoader.getEnumDisplayName(this,bundleName,locale);
	}
}
