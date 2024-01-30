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

import org.esciurus.model.biblio.BibliographicInfo;

/**
 * A factory for x-metadata fields.
 * This static class is used to create XMetaEntry objects
 * while reading the OPF package from XML representation.
 * The actual class of the x-metadata element is 
 * determined by its "name" attribute.
 * 
 * <p>
 * New subclasses of XMetaEntry need to be "registered"
 * with this factory, namely by adding them to the implementation
 * of the <code>createEntry()</code> method.
 * </p>
 * 
 * <p>
 * This very simplistic dispatcher might be replaced in future
 * versions by a more sophisticated extension mechanism. 
 * </p>
 * 
 * @see XMetaEntry
 *
 */
public class XMetaFactory {

	/**
	 * Create a new XMetaEntry object, based on the "name" value
	 * provided. 
	 * @param metaName the "name" attribute value read from the XML representation 
	 * @return a newly created x-metadata entry
	 */
	public static XMetaEntry createEntry(String metaName) {
		XMetaEntry result;
		
		if (BibliographicInfo.xMetaName.equals (metaName)) {
			result = new BibliographicInfo();
		}
		else {
			XTextfield t = new XTextfield();
			t.setName(metaName);
			result = t;
		}
		return result;
		
	}

}
