/**
 * 
 */
package org.esciurus.model.ocf;

/**
 * This exception is thrown if the in-memory representation
 * of the container is found to be inconsistent (e.g., 
 * required fields are null, or constraints are violated).
 * 
 * @author B. Wolterding
 *
 */
public class ContainerDataException extends ContainerException {

	/**
	 * @param message
	 */
	public ContainerDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ContainerDataException(String message, Exception cause) {
		super(message, cause);
	}

}
