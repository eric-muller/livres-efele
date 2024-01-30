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
 * Captures platform-specific information.
 * 
 * <p>This class provides static methods exclusively.</p>
 *
 */
public class Platform {

	/**
	 * Platform type. 
	 * This captures thelevel of granularity on which Esciurus functions
	 * are customized per platform.
	 */
	public enum Type {
		/**
		 * Linux platform (any variant, any desktop environment) 
		 */
		LINUX,
		
		/**
		 * Windows platform (any variant) 
		 */
		WINDOWS,
		
		/**
		 * Apple Macintosh platform. Per availability of Java 5.0, 
		 * this will apply to Mac OS X and higher only. 
		 */
		MAC,
		
		/**
		 * Other platforms not captured in the other constant values. 
		 */
		OTHER
	}
	
	/**
	 * Get the type of platform the application is running on.
	 * 
	 * @return the platform type
	 */
	public static Type getType() {
		String plaf = System.getProperty("os.name").toLowerCase();
		
		Type result = Type.OTHER;
		
		if (plaf.startsWith("linux")) {
			result = Type.LINUX;
		}
		else if (plaf.startsWith("windows")) {
			result = Type.WINDOWS;
		}
		else if (plaf.startsWith("mac os")) {
			result = Type.MAC;
		}
		return result;
	}
	
}
