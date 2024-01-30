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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This class is used during the constraint verification process
 * of containers. It captures the parameters of the verification
 * process and collects a list of violations that have been found.
 * 
 * <p>
 * The ConstraintTicket is passed to all parts of the container
 * (and in turn their subparts) in order to check whether constraints
 * regarding the container's data are fulfilled; e.g. whether all
 * required fields have been filled with values. There are two variants
 * of this process: Either constraints are only checked and data not changed;
 * or constraint violations are resolved automatically where possible
 * (e.g. fields are filled with default values). </p>
 * 
 *  <p>This class only provides an abstract entry point to the constraint
 *  checking process. What constraint are actually checked, which of them
 *  are resolved, and in which way, is determined by the implementation
 *  of the container's <code>ContainerPart</code>s.
 * 
 * @see org.esciurus.model.ocf.Container#checkConstraints(ConstraintTicket)
 * @see org.esciurus.model.ocf.ContainerPart#checkConstraints(ConstraintTicket)
 *
 */
public class ConstraintTicket {

	private List<ConstraintViolation> violations;
	boolean tryResolve;
	
	/**
	 * Construct an empty constraint ticket.
	 * This must be done before the verification process starts. 
	 * The parameter specifies whether contraint violations
	 * that have been found should be resolved automatically,
	 * where possible.
	 * 
	 * @param tryResolve true if violations should be automatically
	 * resolved where possible, false if no automatic resolbing should
	 * be attempted (no data will be changed in this case) 
	 */
	public ConstraintTicket(boolean tryResolve) {
		this.violations = new Vector<ConstraintViolation>();
		this.tryResolve = tryResolve;
	}
	
	/**
	 * Add an entry to the list of constraint violations.
	 * 
	 * @param v the new violation to add
	 */
	public void addViolation (ConstraintViolation v) {
		this.violations.add(v);
	}
	
	/**
	 * Check whether this ticket contains unresolved violations.
	 * 
	 * @return true if there are any unresolved violations in the list;
	 * false if all violations have been resolved, or if the list 
	 * of violations is empty.
	 */
	public boolean hasUnresolvedViolations() {
		boolean result = false;
		for (Iterator<ConstraintViolation> it = violations.iterator(); it.hasNext();) {
			result = result | !it.next().wasResolved();
		}
		return result;
	}
	
	/**
	 * Get a list of all violations of this ticket. 
	 * The collection object returned is immutable.
	 * 
	 * @return the list of constraint violations
	 */
	public List<ConstraintViolation> getViolations() {
		return Collections.unmodifiableList(violations);
	}

	/**
	 * Check whether this ticket is set up for contraints
	 * to be automatically resolved.
	 *  
	 * @return true if violations should be resolved automatically;
	 * false otherwise.
	 */
	public boolean isTryResolve() {
		return tryResolve;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	
		StringBuffer sb = new StringBuffer();
	    for (Iterator<ConstraintViolation> it = violations.iterator(); it.hasNext();) {
	    	ConstraintViolation v = it.next();
	    	if (v.wasResolved()) {
	    		sb.append("resolved:   ");
	    	}
	    	else {
	    		sb.append("unresolved: ");
	    	}
	    	sb.append(v.getText());
	    	sb.append("\n");
	    }
	
	    return sb.toString();
	}
	
}
