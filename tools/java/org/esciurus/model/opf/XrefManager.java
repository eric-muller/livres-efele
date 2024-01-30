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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * A class, global to an OPF package, that manages global aspects of crossref pointers.
 * It keeps a list of ID values, so that it can be determined whether
 * IDs exist and are unique in the package.
 * During the load process (from XML), it also keeps a list
 * of prenoted links that need to be initialized after the load process.
 *  
 * @see XrefPointer
 */
public class XrefManager {

	private int idCounter=0;
	
	private Set<String> registeredIds;
	
	
	/**
	 * Create a new Xref manager.
	 */
	public XrefManager () {
		registeredIds = new HashSet<String>();
		prenotedPointers = new Vector<XrefPointer>();
	}
	
	/**
	 * Register an ID value with this Xref manager.
	 * This does not actually check whether the id already exists.
	 * 
	 * @param id the ID value to register
	 */
	public void registerId(String id) {
		registeredIds.add(id);
	}
	
	/**
	 * Unregister an ID value; this will remove it from the Xref manager's list.
	 * @param id the id to remove
	 */
	public void unregisterId(String id) {
		registeredIds.remove(id);
	}
	
	/**
	 * Test whether an ID value is available, i.e., has not yet
	 * been registered with this Xref manager. 
	 * @param id the id value to test
	 * @return true if the ID is available, false if it is already on the Xref manager's list.
	 */
	public boolean isIdAvailable(String id) {
		return !registeredIds.contains(id);
	}
	
	/**
	 * Generate a unique ID that is not yet present in the list.
	 * Note that this will note register the ID with the Xref manager.
	 * However, subsequent calls to this method will always generate
	 * distinct IDs.
	 * 
	 * @return an ID value which is, at present, known to be available.
	 */
	public String generateId() {
		
		String newId;
		do {
			newId = "id"+Integer.toString(idCounter);
			idCounter++;
		} while (!isIdAvailable(newId));
		
		return newId;
	}
	
	
	/* initial setup during loading */

	
	private List<XrefPointer> prenotedPointers;
	
	/**
	 * Prenote a pointer in the Xref manager. 
	 * This will add it to a list of pointers that need to be 
	 * initialized after an XML load process.
	 * 
	 * @param p the pointer to add
	 * 
	 * @see XrefPointer#prenoteTarget(String, XrefManager)
	 */
	public void prenotePointer(XrefPointer p) {
		prenotedPointers.add(p);
	}

	
	/**
	 * Resolve the links that have been prenoted with this Xref manager
	 * during an XML load process. This method basically calls
	 * <code>resolvePrenotedTarget()</code> on every link that has been prenoted.
	 * 
	 *  @see XrefPointer#resolvePrenotedTarget()
	 */
	@SuppressWarnings("unchecked")
	public void resolvePrenotedLinks() {
	
		for (Iterator<XrefPointer> pointerIt = prenotedPointers.iterator(); pointerIt.hasNext();) {
			pointerIt.next().resolvePrenotedTarget();
		}
		
		prenotedPointers.clear();
	}
	
}
