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

package org.esciurus.model.ocf;

/**
 * Information about a file in a container structure.
 * This includes its path relative to the container root,
 * its absoulte location on the files system,
 * and its MIME media type.
 * 
 * <p><em>Note:</em> All relative paths are assumed to be in the format required
 * by the OCF specification; 
 * in particular, they use a slash ("/") as
 * path delimiter, independent of the operating system platform.</p>
 * 
 * <p><em>Pattern:</em> Immutable</p>
 * 
 */
public class FileInfo implements Comparable<FileInfo> {

	private String filePath;
	private String mediaType;

	// path separator in OCF IRIs
	private static char ocfSeparator = '/';
	//characters forbidden in file names, as defined in the OCF specification
	private static char[] forbiddenChars = {'"','*',':','<','>','?','\\'};
	

	/**
	 * Create a new FileInfo object based on a relative path.
	 * 
	 * @param filePath the path of the file within the container
	 * @param mediaType the MIME media type of the file
	 * @throws FileSyntaxException if the relative path does not meet 
	 * the file name standards of the OCF specification
	 */
	public FileInfo (String filePath, String mediaType) throws FileSyntaxException {

		this(filePath,null,mediaType);
	}
	
	/**
	 * Create a new FileInfo object based on a relative path 
	 * and a base path, both within the container.
	 * 
	 * @param relativePath the relative path of the file
	 * @param basePath the base path within the container
	 * @param mediaType the MIME media type of the file
	 * @throws FileSyntaxException if the relative path does not meet 
	 * the file name standards of the OCF specification
	 */
	public FileInfo (String relativePath, String basePath, String mediaType) throws FileSyntaxException {

		if (relativePath == null || relativePath.length()==0) {
			throw new FileSyntaxException("file path cannot be empty");
		}
		
		String newFilePath = "";
		if (basePath!=null){
			newFilePath = basePath;
			if (newFilePath.lastIndexOf(ocfSeparator) != newFilePath.length()-1 ) {
				newFilePath += ocfSeparator;
			}
		}
		newFilePath += relativePath;
		checkSyntax(newFilePath);
		this.filePath = newFilePath;
		this.mediaType = mediaType;		
	}

	private static void checkSyntax(String path) throws FileSyntaxException {
		
		for (int i = 0; i < forbiddenChars.length; i++) {
			if (path.indexOf(forbiddenChars[i]) >= 0) {
				throw new FileSyntaxException("disallowed character ["+forbiddenChars[i]+"] in path name: "+path);
			}
		}
		
		if (path.indexOf("./") >= 0 || path.endsWith(".")) {
			throw new FileSyntaxException("illegal file name, ends with dot: "+path);			
		}

		if (path.length() > 65535) {
			throw new FileSyntaxException("file name too long");						
		}
		
	}


	/**
	 * Retrieve the MIME media type of this file.
	 * 
	 * @return Returns the MIME media type.
	 */
	public String getMediaType() {
		return mediaType;
	}


	/**
	 * Retrieve the path of this file relative to the container root.
	 * 
	 * @return Returns the relative path of the file.
	 */
	public String getFilePath() {
		return filePath;
	}
	
	
	/**
	 * Check whether this object represents is a directory.
	 * This is determined by testing whether the relative path ends in "/";
	 * the method does not access the file system in order to verify
	 * whether a file or directory entry is present.
	 * 
	 * @return true if the relative path name corresponds to a directory,
	 * false if it corresponds to a file
	 */
	public boolean isDirectory() {
		return filePath.endsWith(Character.toString(ocfSeparator));
	}
	
	

	/**
	 * Compare this object to another FileInfo object.
	 * Twoo FileInfo objects are considered equal if their
	 * relative paths match after case normalization.
	 * (This is in line with the OCF specification, which demands that
	 * file names within the same directory must not map
	 * to the same string following case normaliation.)
	 *  
	 * @param anotherFileInfo the FileInfo object to compare to
	 * @return 0 if the argument FileInfo is equal to this FileInfo; 
	 * a value less than 0 if this FileInfo is sorted before the argument FileInfo (judging by relative path in lexicographic order); 
	 * and a value greater than 0 if this FileInfo  sorted after the FileInfo argument.
	 */
	public int compareTo(FileInfo anotherFileInfo) {
	
		// file names must be unique even after case normalization
		return this.filePath.compareToIgnoreCase(anotherFileInfo.filePath);
	}

	

	@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		// file names must be unique even after case normalization
		if (arg0 instanceof FileInfo) {
			return this.filePath.equalsIgnoreCase(((FileInfo)arg0).filePath);
		}
		else if (arg0 instanceof String) {
			return this.filePath.equalsIgnoreCase((String)arg0);
		}
		else {
			return false;
		}
		
	}


	/**
	 * Retrieve the filename part of this object.
	 * That is, extract the part of the relative path after the last "/".
	 * 
	 * @return the filename part of the relative path, or the empty string
	 * if the relative path corresponds to a directory (i.e. the path ends in "/"). 
	 */
	public String getFilenamePart() {
		String result;
		
		int separatorPos = filePath.lastIndexOf(ocfSeparator);
		if (separatorPos >= 0) {
			result = filePath.substring(separatorPos+1);
		}
		else {
			result = filePath;
		}
		return result;
	}
	
	/**
	 * Retrieve the directory part of the relative path.
	 * That is, extract the part of the relative path before and
	 * including the last "/". If the relative path corresponds
	 * to a directory, i.e. it ends in "/", then the entire
	 * relative path is returned. 
	 * 
	 * @return the directory part of the relative path
	 */
	public String getDirectoryPart() {
		String result;
		
		int separatorPos = filePath.lastIndexOf(ocfSeparator);
		if (separatorPos >= 0) {
			result = filePath.substring(0,separatorPos+1);
		}
		else {
			result = "";
		}
		return result;
	}
}
