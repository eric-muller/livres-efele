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

import java.io.File;
import java.io.IOException;

/**
 * Interface for a callback object that is used
 * for iterating through directories on the file system.
 * 
 * @see FileUtility#browseFiles(File, boolean, FileVisitor)
 *
 */
public interface FileVisitor {

	/**
	 * Method to be called for each file found in a directory.
	 * @param file the file to be handled
	 * @throws IOException if an error in file I/O occurs
	 */
	public abstract void visitFile(File file) throws IOException ;

	/**
	 * Method to be called when a subdirectory is entererd.
	 * @param directory the subdirectory in question
	 * @throws IOException if an error in file I/O occurs
	 */
	public abstract void enterDirectory(File directory) throws IOException ;

	/**
	 * Method to be called when a subdirectory is left.
	 * @param directory the subdirectory in question
	 * @throws IOException if an error in file I/O occurs
	 */
	public abstract void leaveDirectory(File directory) throws IOException ;

}
