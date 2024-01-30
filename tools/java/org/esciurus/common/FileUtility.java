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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A utility class for certain file I/O operations.
 *
 */
public class FileUtility {

	private static int bufferSize=8192;
	

	/**
	 * Copy data between two streams. Data is read from the input stream
	 * (until its end is reached) and copied to the output stream.
	 * 
	 * @param inStream the input stream to read from
	 * @param outStream the output stream to write to
	 * @throws IOException if an error occurs in stream I/O
	 */
	public static void copyStream (InputStream inStream, OutputStream outStream) throws IOException {
		
		byte[] buffer = new byte[bufferSize];
		int len;
		while ((len = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, len);
		}
	}

	
	/**
	 * Copy a file on the file system.
	 * 
	 * @param source the source file to be copied
	 * @param target the target of the copy
	 * @throws IOException if an error occurs in file I/O
	 */
	public static void copyFile (File source, File target) throws IOException {
		
		FileInputStream  in  = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(target);
		
		copyStream(in,out);

		out.close();
		in.close();
	}
	
	
	/**
	 * Browse a directory on the file system (recursively if specified).
	 * For each file found in the directory, the appropriate 
	 * method of the <code>visitor</code>
	 * object is invoked.
	 * 
	 * @param directory the base directory to browse
	 * @param recursive true if subdirectories should be processed in a recursive fashion
	 * @param visitor is called for each file found 
	 * @throws IOException if an error occurs in file I/O
	 */
	public static void browseFiles(File directory, boolean recursive, FileVisitor visitor) throws IOException {
		
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + directory.getAbsolutePath());
		}
		
		File[] dirContents = directory.listFiles();
		
		for (int i = 0; i < dirContents.length; ++i) {
			File dirEntry = dirContents[i];
			if (dirEntry.isDirectory()) {
				if (recursive) {
					visitor.enterDirectory(dirEntry);
					browseFiles(dirEntry,recursive,visitor);
					visitor.leaveDirectory(dirEntry);
				}
			} else {
				visitor.visitFile(dirEntry);
			}
		}
		
	}
	
	
	
	/**
	 * Delete a directory and all its contents, including subdirectories.
	 * 
	 * @param directory the directory to delete
	 * @throws IOException if an error in file I/O occurs
	 */
	public static void deleteDirectory(File directory) throws IOException {

		DelDirVisitor visitor = new DelDirVisitor(directory);
		
		browseFiles(directory,true,visitor);
		
		directory.delete();
	}
	
	/**
     * Get the extension of a file.
     * 
     * @param file the file to query
     * @return the file extension of this file; may be the empty string
     * if the file name does not contain an extension
     */  
    public static String getExtension(File file) {
        String ext = "";
        String fname = file.getName();
        int i = fname.lastIndexOf('.');

        if (i > 0 &&  i < fname.length() - 1) {
            ext = fname.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
}
