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

import org.w3c.dom.Element;


/**
 * A list of package parts that is suited for inclusion in other lists.
 * In XML representation, it corresponds to a list of elements 
 * which are common children of another XML element (outside the
 * scope of this implementation).
 * 
 * <p>
 * The OPFSubList has an "id" attribute in XML representation.
 * This is implemented by encapsulating an OPFElement object as a facade.
 * Currently, OPFSubList can however not be used as a target for
 * crossref pointers (there is no application of this in the OPF
 * specification).
 * </p>
 *
 * @param <E> the class of pacakge parts accepted as entries of this list 
 *
 */
public abstract class OPFSubList<E extends OPFElement> extends OPFList<E> {

	
	private OPFElement opfBaseElement;

	/**
	 * Create a new OPFSubList object, corresponding to the specified XML tag.
	 *  
	 * @param entryTagName the XML tag (element name) that corresponds to entries of this list
	 * @param entryFactory a factory that creates new entries of the list
	 */
	public OPFSubList (String entryTagName, EntryFactory<E> entryFactory ) {
		super(entryTagName,null,entryFactory);
	
		opfBaseElement = new OPFElement();
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFElement#readFromXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		opfBaseElement.readFromXml(element);
		readEntriesFromXml(element);
	}


	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFElement#writeToXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public void writeToXml(Element element) {
		opfBaseElement.writeToXml(element);
		writeEntriesToXml(element);
	}


	/**
	 * Get the textual "id" value of this list.
	 * 
	 * @return the id value; may be <code>null</code>.
	 */
	public String getId() {
		return opfBaseElement.getId();
	}

	/**
	 * Set the "id" value of this list. 
	 * 
	 * @param id the id value
	 * @throws EpubDataException if the id value is already present in the package
	 */
	public void setId(String id) throws EpubDataException {
		opfBaseElement.setId(id);
	}


	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#moveToPackage(org.esciurus.model.opf.OPFPackage)
	 */
	@Override
	protected void moveToPackage(OPFPackage pack) {
		super.moveToPackage(pack);
		opfBaseElement.moveToPackage(pack);

	}



	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#removeFromPackage()
	 */
	@Override
	protected void removeFromPackage() {
		super.removeFromPackage();
		opfBaseElement.removeFromPackage();
	}
	


	
	
}
