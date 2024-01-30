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
 * Indicates that a constraint within the container
 * has been violated. 
 * This abstract class is provided for extension by  
 * implementations of specific OCF container formats.
 * 
 * @see org.esciurus.model.ocf.ConstraintTicket
 */
public abstract class ConstraintViolation {

	
	/**
	 * Retrieve a textual decription of this constraint violation.
	 * 
	 * @return the textual decription
	 */
	public abstract String getText();
	
	/**
	 * Check whether this contraint violation has been
	 * automatically resolved during verification.
	 * 
	 * @return true if the violation has been resolved 
	 * @see ConstraintTicket#ConstraintTicket(boolean)
	 */
	public boolean wasResolved() {
		return false;
	}
	
}
