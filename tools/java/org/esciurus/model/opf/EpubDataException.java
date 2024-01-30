package org.esciurus.model.opf;

import org.esciurus.model.ocf.ContainerDataException;

/**
 * Represents a consistency problem with the in-memory
 * representation of an OPF package.
 *
 */
public class EpubDataException extends ContainerDataException {

	/**
	 * Create an EpubDataException with a specific message.
	 * 
	 * @param message the error message
	 */
	public EpubDataException(String message) {
		super(message);
	}

	/**
	 * Create an EpubDataException with a specific message and root cause.
	 * 
	 * @param message the error message
	 * @param cause the root cause of this exception
	 */
	public EpubDataException(String message, Exception cause) {
		super(message, cause);
	}

}
