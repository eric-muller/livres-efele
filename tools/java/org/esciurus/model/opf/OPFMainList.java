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

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A list of package parts which corresponds directly
 * to a single XML element of the package descriptor.
 * Entries of the list will correspond to child elements
 * of that one XML element.
 * 
 * <p>
 * The OPFMainList has an "id" attribute in XML representation.
 * This is implemented by encapsulating an OPFElement object as a facade.
 * Currently, OPFMainList can however not be used as a target for
 * crossref pointers (there is no application of this in the OPF
 * specification).
 * </p>
 *
 * @param <E> the class of pacakge parts accepted as entries of this list 
 *
 */
public class OPFMainList<E extends PackagePart> extends OPFList<E> {

	
	private OPFElement opfBaseElement;
	private String rootTagName;
	
	/**
	 * Create a new OPFMainList object, corresponding to the specified XML tags.
	 *  
	 * @param parent the parent OPF package of this list
	 * @param rootTagName the XML tag (element name) that corresponds to this list
	 * @param entryTagName the XML tag (element name) that corresponds to entries of this list
	 * @param entryFactory a factory that creates new entries of the list
	 */
	public OPFMainList (OPFPackage parent, String rootTagName, String entryTagName, EntryFactory<E> entryFactory ) {
		super(entryTagName,null,entryFactory);
	
		opfBaseElement = new OPFElement();
		this.rootTagName = rootTagName;
		
		moveToPackage(parent);
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFElement#readFromXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public final void readFromXml(Element element) throws EpubFormatException {
		Element listElm = findSingleElement(element,rootTagName,false);		
		if (listElm != null) {
			readListTagAttributes(listElm);
			readEntriesFromXml(listElm);
		}
	}
	
	/**
	 * Read attributes from the XML element that represents this list as a whole.
	 * 
	 * <p>Subclasses may override this method in order to add specific
	 * attributes. They must call <code>super()</code>, however.</p>
	 * 
	 * @param element the XML element to read from
	 * @throws EpubFormatException if problems with the input data are encountered
	 */
	protected void readListTagAttributes(Element element) throws EpubFormatException  {
		opfBaseElement.readFromXml(element);		
	}


	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFElement#writeToXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public final void writeToXml(Element element) {
		if (size()>0) {
			Document doc = element.getOwnerDocument();
			Element listElm = doc.createElement(rootTagName);
			element.appendChild(listElm);
			
			writeListTagAttributes(listElm);
			writeEntriesToXml(listElm);
		}
	}

	/**
	 * Write attributes to the XML element that corresponds to the
	 * list as a whole.
	 * 
	 * <p>Subclasses may override this method in order to add specific
	 * attributes. They must call <code>super()</code>, however.</p>
	 * 
	 * @param element the XML element to read from
	 */
	protected void writeListTagAttributes(Element element) {
		opfBaseElement.writeToXml(element);		
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
