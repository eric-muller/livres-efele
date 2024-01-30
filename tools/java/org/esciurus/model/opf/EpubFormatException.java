package org.esciurus.model.opf;

import org.esciurus.model.ocf.ContainerFormatException;

/**
 * Represents a problem with the input file format.
 * This could refer to a malformed XML document, or to
 * valid XML documents not comforming to the OPF
 * specification.
 *  
 */
public class EpubFormatException extends ContainerFormatException {

	/**
	 * Create an EpubFormatException with a specific message.
	 * 
	 * @param message the error message
	 */
	public EpubFormatException(String message) {
		super(message);
	}

	/**
	 * Create an EpubFormatException with a specific message and root cause.
	 * 
	 * @param message the error message
	 * @param cause the root cause of this exception
	 */
	public EpubFormatException(String message, Exception cause) {
		super(message, cause);
	}

}
