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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.esciurus.model.ocf.ConstraintTicket;


/**
 * An abstract list of package parts within an OPF package.
 * This class encapsulates a list of package parts, and
 * exposes access to it via the java.util.List interface.
 * It also adds functionality to propagate calls (e.g. for constraint checking)
 * to all members of the list. 
 * 
 * <p>Adding a package part to this list will automatically move
 * it to the encapsulating OPF package, and removing from the list
 * will remove it from the package.</p>
 *
 * @param <E> the class of package parts accepted as entries of this list
 */
public abstract class PackagePartList<E extends PackagePart> extends PackagePart implements List<E> {

	private List<E> entryList;

	
	/**
	 * Create a new list of package parts.
	 */
	public PackagePartList() {
		this.entryList = new Vector<E>();
	}
	
	/* methods inherited from the java.util.List interface */	
	
	/* (non-Javadoc)
	 * @see java.util.List#add(E)
	 */
	public boolean add(E entry) {
		 entry.moveToPackage(getParent());
		 return entryList.add(entry);
	}


	/* (non-Javadoc)
	 * @see java.util.List#add(int, E)
	 */
	public void add(int arg0, E arg1) {
		
		arg1.moveToPackage(getParent());
		entryList.add(arg0,arg1);
		
	}


	/* (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> arg0) {
		for (Iterator<? extends E> it = arg0.iterator(); it.hasNext();) {
			it.next().moveToPackage(getParent());
		}
		return entryList.addAll(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		for (Iterator<? extends E> it = arg1.iterator(); it.hasNext();) {
			it.next().moveToPackage(getParent());
		}
		return entryList.addAll(arg0,arg1);
	}


	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	public void clear() {
		for (Iterator<E> it = entryList.iterator(); it.hasNext();) {
			it.next().removeFromPackage();
		}

		entryList.clear();
		
	}


	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object arg0) {
		return entryList.contains(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> arg0) {
		return entryList.containsAll(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public E get(int arg0) {
		return entryList.get(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object arg0) {
		return entryList.indexOf(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return entryList.isEmpty();
	}


	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	public Iterator<E> iterator() {
		return entryList.iterator();
	}


	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object arg0) {
		return entryList.lastIndexOf(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator() {
		return entryList.listIterator();
	}


	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(int arg0) {
		return entryList.listIterator(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public E remove(int arg0) {
		return entryList.remove(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object arg0) {
		if (arg0 instanceof OPFElement) {
			((OPFElement)arg0).removeFromPackage();
		}
		return entryList.remove(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> arg0) {
		for (Iterator it = arg0.iterator(); it.hasNext();) {
			Object e = it.next();
			if (e instanceof OPFElement) {
				((OPFElement)e).removeFromPackage();
			}
		}
		return entryList.removeAll(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> arg0) {
		for (Iterator<E> it = entryList.iterator(); it.hasNext();) {
			E e = it.next();
			if (!arg0.contains(e)) {
				e.removeFromPackage();
			}
		}

		return entryList.retainAll(arg0);
	}


	/* (non-Javadoc)
	 * @see java.util.List#set(int, E)
	 */
	public E set(int arg0, E arg1) {
		return entryList.set(arg0,arg1);
	}


	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size() {
		return entryList.size();
	}


	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(int arg0, int arg1) {
		return entryList.subList(arg0,arg1);
	}


	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return entryList.toArray();
	}


	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(T[] arg0) {
		return entryList.toArray(arg0);
	}


	/* end java.util.List interface */
	
	
	
	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		super.checkConstraints(ticket);

		for (Iterator<E> it = this.iterator(); it.hasNext();) {
			it.next().checkConstraints(ticket);
		}
	}


}
