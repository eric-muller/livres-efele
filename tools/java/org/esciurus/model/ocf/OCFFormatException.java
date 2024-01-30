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

/**
 * This exception signalizes a data format problem during the
 * read process of a ZIP container; that is, the input file
 * does not conform to the OCF standards.
 * <p>
 * Detail message codes are provided through an enumeration.
 * </p>
 * @author B. Wolterding
 *
 */

/**
 * @author B. Wolterding
 *
 */
/**
 * @author B. Wolterding
 *
 */
@SuppressWarnings("serial")
public class OCFFormatException extends ContainerFormatException {

	private MessageCode messageCode;
	private String containerPath;
	
	/**
	 * Message codes for this exception class
	 */
	public enum MessageCode {
		/**
		 * First entry in the ZIP file is not named "mimetype"
		 */
		BAD_FILE_HEADER,
		
		/**
		 * MIME media type of the container (as specified by the "mimetype" file)
		 * is not known/accepted by this specific container implementation 
		 */
		UNKNOWN_MEDIATYPE,
		
		/**
		 * Container description file (container.xml) is missing,
		 *  not readable, or has format errors
		 */
		BAD_CONTAINERDESC,

		/**
		 * Container manifest (META-INF/manifest.xml)
		 *  not readable or has format errors
		 */
		BAD_MANIFEST,

		/**
		 * Rootfile (advertised in container.xml) 
		 *  could not be found in the archive
		 */
		ROOTFILE_NOT_FOUND,

		/**
		 * Invalid file name read from input 
		 *  or requested for output
		 *  (e.g., file name contains colons)
		 */
		INVALID_FILENAME
	}
	
	
	/**
	 * Create a new OCFFormatException, associated with a root cause exception.
	 * 
	 * @param code the message code
	 * @param containerFile the container file associated with the error
	 * @param message a textual description of the error
	 * @param e the root cause of this exception
	 */
	public OCFFormatException(MessageCode code, File containerFile, String message, Exception e) {
		super(message,e);
		this.messageCode = code;
		this.containerPath = containerFile.getAbsolutePath();
	}

	/**
	 * Create a new OCFFormatException.
	 * 
	 * @param code the message code
	 * @param containerFile the container file associated with the error
	 * @param message a textual description of the error
	 */
	public OCFFormatException(MessageCode code, File containerFile, String message) {
		super(message);
		this.messageCode = code;
		this.containerPath = containerFile.getAbsolutePath();
	}


	/**
	 * Retrieve the message code of this exception 
	 * @return Returns the message code.
	 */
	public MessageCode getMessageCode() {
		return messageCode;
	}


	/**
	 * Retrieve the path of the container file associated with this exception.
	 * 
	 * @return Returns the path of the container file.
	 */
	public String getContainerPath() {
		return containerPath;
	}

}
