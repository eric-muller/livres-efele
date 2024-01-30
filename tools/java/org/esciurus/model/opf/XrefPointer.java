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

package org.esciurus.model.opf;


/**
 * An abstract pointer to an object within an OPF package.
 * Crossref pointers are used for the in-memory representation
 * of relations between package parts. In persisted form (i.e. in XML
 * structures), these relations are represented as IDREF values.
 * 
 * This class also contains functionality for persisting the crossref
 * pointers to IDREF values, and for initializing them from XML representation.
 * 
 * @param <T> the type of target this pointer refers to
 */
public abstract class XrefPointer <T extends XrefTarget> {

	private T target;
	private String prenotedTargetId;
	
	/**
	 * Set the target of this pointer. This will automatically
	 * register this pointer with the target.
	 * 
	 * @param newTarget the new target to use
	 */
	public void setTarget(T newTarget) {
		
		if (target != null) {
			target.unregisterLink(this);
		}
		if (newTarget != null) {
			newTarget.registerLink(this);
		}
		target = newTarget;
	}
	
	/**
	 * Get the target of this crossref pointer.
	 * @return the target of the pointer; may be <code>null</code>
	 */
	public T getTarget() {
		return target;
	}
	
	/**
	 * Retrieve the target of this pointer, using its persisted IDREF value.
	 * 
	 * @param persistenceId the persisted IDREF value
	 * @return the target corresponding to this IDREF value
	 */
	public abstract T getTargetByPersistenceId(String persistenceId);
	
	/**
	 * Prenote a target during the load process.
	 * <p>
	 * While the in-memory representation is loaded from an XML representation,
	 * notal targets may already have been initialized. This method
	 * will register the link with the given XreManager for later
	 * initialization. This initialization occurs after the XML conversion
	 * process is finished. 
	 * </p>
	 * 
	 * @param targetId the IDREF value of the link target
	 * @param mgr the XrefManager object to use for registration
	 */
	public void prenoteTarget(String targetId, XrefManager mgr) {
		prenotedTargetId = targetId;
		mgr.prenotePointer(this);
	}
	
	/**
	 * Resolve a previously prenoted target. This converts 
	 * the target ID passed to <code>getTargetByPersistenceId()</code>
	 * to an actual pointer.
	 * 
	 *  @see #getTargetByPersistenceId(String)
	 */
	public void resolvePrenotedTarget() {
		if (prenotedTargetId != null) {
			T newTarget = getTargetByPersistenceId(prenotedTargetId);
			
			if (newTarget != null) {
				setTarget(newTarget);
			}
						
		}
	}
	
}
