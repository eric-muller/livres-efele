package org.esciurus.model.ocf;

/**
 * Thrown if a file format error is detected in the input file
 * while reading the container from its ZIP representation.
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
/**
 * @author B. Wolterding
 *
 */
public class ContainerFormatException extends ContainerException {

	/**
	 * Create a new ContainerFormatException.
	 * @param message a textual message for this exception 
	 */
	public ContainerFormatException(String message) {
		super(message);
	}

	/**	
	 * Create a new ContainerFormatException, associated with a root cause.
	 * @param message a textual message for this exception 
	 * @param cause the root cause of this exception
	 */
	public ContainerFormatException(String message, Exception cause) {
		super(message, cause);
	}

}
