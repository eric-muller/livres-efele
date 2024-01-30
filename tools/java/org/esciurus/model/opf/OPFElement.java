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

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

/**
 * A part of an OPF package that corresponds to a single 
 * XML element in the package descriptor file.
 * This element can have an "id" attribute, which is
 * represented by a crossref pointer target in this class.
 *
 * @see org.esciurus.model.opf.OPFPackage
 * @see org.esciurus.model.opf.XrefPointer
 */
public class OPFElement extends PackagePart implements XrefTarget {

	private String id;
	private List<XrefPointer> backReferences;  
	
	private static final String idAttr = "id";
	
	/**
	 * Create a new OPF element.
	 */
	public OPFElement() {
		super();
		id = null;
		backReferences = new Vector<XrefPointer>();
	}
	
	
	/**
	 * Get the textual id of this element. This may
	 * be <code>null</code> while the element has not been saved
	 * to disk, or is not used as a crossref target.
	 *  
	 * @return the id value
	 */
	public String getId() {
		return id;
	}



	/**
	 * Set the id value of this OPF element.
	 * Note that it is possible, but not obligatory, to set the id
	 * value explicitely. If the OPF element is used as a crossref
	 * target, but no id value has been set, then an id will 
	 * be generated automatically.
	 * <p>
	 * Note that id values have to be unique within the OPF package.
	 * If an id value is passed to this method that already exists
	 * in the package, an exception will be thrown.
	 * </p>
	 *   
	 * @param newId the new id value to set 
	 * @throws EpubDataException if the id value already exists in the package
	 */
	public void setId(String newId) throws EpubDataException {
		
		XrefManager xm =getParent().getXrefManager();
		
		if (newId == null || newId.length()==0 ) {
			if (id != null) xm.unregisterId(id);
			id = null;
		}
		else {
			if (xm.isIdAvailable(newId)) {
				xm.registerId(newId);
				id = newId;
			}
			else {
				throw new EpubDataException("duplicate id in package: "+newId);
			}
		}
	}



	@Override
	public void readFromXml (Element element) throws EpubFormatException {
		String readId = readAttributeValue(element,idAttr,false);
		try {
			setId(readId);
		}
		catch (EpubDataException e) {
			// ignore -- duplicate ids in input file will be ignored
			// LATER add log/warning message
		}
	}

	@Override
	public void writeToXml(Element element) {
		writeAttributeValue(element,idAttr,id,false);
	}

	
	/**
	 * Ensure that this OPF element has a valid id value set.
	 * If to date none had been set, it is generated automatically
	 * by this method.
	 */
	protected void ensureId() {
		
		if (id == null) {
			XrefManager xm = getParent().getXrefManager();
			try {
				setId(xm.generateId());
			}
			catch (EpubDataException e) {
				throw new RuntimeException("internal error while setting generated id",e);
			}
		}
	}

	public void registerLink(XrefPointer p) {
		ensureId();
		backReferences.add(p);		
	}


	public void unregisterLink(XrefPointer p) {
		backReferences.remove(p);				
	}


	public String getPersistenceId() {
		return getId();
	}



	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#removeFromPackage()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void removeFromPackage() {
		super.removeFromPackage();
		
		while( backReferences.size() > 0 ) {
			backReferences.get(0).setTarget(null);
			// LATER extra measures to avoid infinite loop?
		}
	}



}