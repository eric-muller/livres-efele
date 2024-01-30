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
 * An exception that signalizes a generic problem while processing
 * a container, e.g. during reading or writing.
 * <p>
 * Implementations of Container and ContainerFactory may want
 * to create their own, specific subclasses of this exception.
 * </p>
 */
public class ContainerException extends Exception {

	/**
	 * Construct a new ContainerException with a specified detail message.
	 * @param message the detail message
	 */
	public ContainerException(String message) {
		super(message);
	}

	/**
	 * Construct a new ContainerException with a specified detail message
	 * and root cause.
	 * @param message the detail message
	 * @param cause the Exception that caused this one
	 */
	public ContainerException(String message, Exception cause) {
		super(message,cause);
	}
}
