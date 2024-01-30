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
 * An abstract factory that creates entries for lists of package parts.
 * 
 * @param <E> the class of package parts that this factory will produce
 * 
 * @see org.esciurus.model.opf.OPFList#OPFList(String, String, EntryFactory)
 */
public interface EntryFactory<E extends PackagePart> {

	/**
	 * Produce a new entry of the specified class.
	 * 
	 * @return the new package part
	 */
	public abstract E createEntry();
	
}
