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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.esciurus.common.FileUtility;
import org.esciurus.common.FileVisitor;
import org.esciurus.model.ocf.Container.OpenMode;

/**
 * A container part with minimal functionality.
 * It consists of files in a directory, without further information
 * about these. One file is designated the "root file", and only for this file
 * the MIME media type is captured.
 *
 * <p>This container part class is used for all root files 
 * in OCF containers that have an otherwise unknown MIME media type.
 * Particularly, this includes "alternate renditions" of OPF publications
 * (PDF files, etc.).</p>
 * 
 * <p>This class currently is a rather minimal implementation;
 * its purpose is to make OCF containers with alternate renditions
 * load correctly.</p>  
 */
public class DefaultContainerPart implements ContainerPart {
	
	private FileInfo rootFileInfo;
	private File rootFile;
	private File containerBase;
		
	/**
	 * Create a new default container part corresponding to a specified root file.
	 * 
	 * @param rootfile the root file to use
	 * @param containerBase the base directory of the OCF container
	 */
	public DefaultContainerPart(FileInfo rootfile, File containerBase) {
		this.rootFileInfo = rootfile;
		this.rootFile = new File(containerBase,rootfile.getFilePath());
		this.containerBase = containerBase;
	}

	public FileInfo getRootfile() {
		return rootFileInfo;
	}

	public void loadRootfile(InputStream stream, OpenMode openMode) {
		// nothing to load
	}

	
	private class SaveVisitor implements FileVisitor {

		OutputDescriptor out;
		
		private SaveVisitor(OutputDescriptor out) {
			this.out=out;
		}
		
		public void visitFile(File file) throws IOException {
			/* TODO MIME type detection? */
			String mimeType = "application/octet-stream";
			if (file.equals(rootFile)) {
				mimeType = rootFileInfo.getMediaType();
			}
			
			String fileFullName = file.getCanonicalPath();
			String containerFullPath = containerBase.getCanonicalPath();
			if (fileFullName.startsWith(containerFullPath)) {

				String fileRelName = fileFullName.substring(containerFullPath.length()+1); 
				
				try {
					out.addFileFromFilesystem( new FileInfo(fileRelName,mimeType) );
				} catch (ContainerException e) {
					/* TODO better error handling */
					System.err.println("Warning: could not package file "+file.toString());
				}
				
			}
			else {
				/* TODO better error handling */
				System.err.println("Warning: could not package file "+file.toString());				
			}
		}

		public void enterDirectory(File directory) {
			// nothing at this time
		}

		public void leaveDirectory(File directory) {
			// nothing at this time
		}
		
	}
	
	public void save(OutputDescriptor outputDesc) throws IOException {
		
		SaveVisitor visitor = new SaveVisitor(outputDesc);
		File searchDir = new File(containerBase,rootFileInfo.getDirectoryPart());
		FileUtility.browseFiles( searchDir,true,visitor);

	}

	public void checkConstraints(ConstraintTicket ticket) {
		// no constraints for this default implementation
	}


	/**
	 * @param rootFileType The rootFileType to set.
	 * @throws FileSyntaxException 
	 */
	/*
	public void setRootFile(String fileName, String fileType) throws FileSyntaxException {
		rootFile = new File(fileName);
		rootFileInfo = new FileInfo( fileName, rootFile.getParentFile(), fileType );
	}
	*/
 
}
