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

class DelDirVisitor implements FileVisitor {
	
	
	private String canonicalBasePath;
	
	DelDirVisitor(File baseDir) throws IOException {
		canonicalBasePath = baseDir.getCanonicalPath();
	}
	
	public void visitFile(File file) throws IOException {
		if (file.getCanonicalPath().startsWith(canonicalBasePath)){
			file.delete();
		}
	}
	
	public void enterDirectory(File directory) {
		// nothing to do
		
	}
	
	public void leaveDirectory(File directory) throws IOException {
		if (directory.getCanonicalPath().startsWith(canonicalBasePath)){
			directory.delete();
		}			
	}
	
}
