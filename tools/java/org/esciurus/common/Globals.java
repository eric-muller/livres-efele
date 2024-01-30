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
 * Captures global properties of the Esciurus system,
 * such as the release number.
 * 
 * <p>This class provides static methods exclusively.</p> 
 *
 */
public class Globals {

	private static String propertyName = "Globals.properties";
	private static String releaseKey   = "esciurus.release";
	
	/**
	 * Get the release id for Esciurus.
	 * 
	 * <p>The release id is read from a properties file,
	 * where it is automatically inserted during the build process.</p>
	 * 
	 * @return the release id (e.g. "0.2.1")
	 */
	public static String getReleaseId() {
		return ResourceLoader.getInstance().getPropertyString(propertyName,releaseKey);
	}
	
}
