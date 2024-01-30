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

import org.esciurus.model.ocf.ConstraintTicket;
import org.w3c.dom.Element;


/**
 * An entry in the spine of an OPF package.
 * 
 * @see Spine
 * @see OPFPackage
 */
public class SpineEntry extends OPFElement {

	private ManifestPtr refPtr;
	private boolean linear;
	
	/**
	 * Create a new spine entry
	 */
	public SpineEntry() {
		
		refPtr = new ManifestPtr();
		linear = true;

	}
	
	
	/**
	 * Create a factory that produces spine entries.
	 * 
	 * @return a new spine entry
	 * @see OPFList (String,String,EntryFactory)
	 */
	public static EntryFactory<SpineEntry> getEntryFactory() {
		return new EntryFactory<SpineEntry> () {
			public SpineEntry createEntry() {
				return new SpineEntry();
			}
		};
	}
	
	

	/**
	 * Retrieve the file in the manifest that this spine entry refers to
	 * @return the manifest entry corresponding to this spine entry
	 */
	public ManifestEntry getRef() {
		return refPtr.getTarget();
	}




	/**
	 * Set the manifest entry that this spine entry refers to
	 * @param idref the new menifest entry to use
	 */
	public void setRef(ManifestEntry idref) {
		refPtr.setTarget(idref);
	}




	/**
	 * Retrieve whether this entry is in the "linear reading order".
	 * (This fiels corresponds to the "linear" attribute in the package file.)
	 * @return true if this entry is in the linear reading order
	 */
	public boolean isLinear() {
		return linear;
	}



	/**
	 * Set whether this entry is in the "linear reading order".
	 * 
	 * @param linear true if this entry is in the linear reading order
	 * @see #isLinear()
	 */
	public void setLinear(boolean linear) {
		this.linear = linear;
	}


	private static String idrefAttr = "idref";
	private static String linearAttr = "linear";
	private static String linearFalseVal = "no";


	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		super.readFromXml(element);
		
		readPointer(element,idrefAttr,refPtr);
		String linearVal = readAttributeValue(element,linearAttr,false);
		linear = !(linearFalseVal.equals(linearVal));
	}

	@Override
	public void writeToXml(Element element) {
		
		super.writeToXml(element);
		
		writePointer(element,idrefAttr,refPtr);
		if (!linear) element.setAttribute(linearAttr,linearFalseVal);

	}


	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#checkConstraints(org.esciurus.model.ocf.ConstraintTicket)
	 */
	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		super.checkConstraints(ticket);
		
		/* TODO new constraint: spine entry must contain non-null ref? */
		
		/* check whether media type of reference is acceptable, or appropriate fallback as been set */
		if (!getParent().getFormatModifiers().contains(OPFPackage.FormatModifier.DONT_REQUIRE_CORETYPES)) {
			if (getRef() != null) {
				ManifestEntry mf = getRef();
				
				List<String> acceptableTypes = MimeTypes.getInstance().getOpfContentTypes();
				
				if ( mf.getFallbackByType(acceptableTypes,true) == null) {
					
					// no valid fallback found
					ticket.addViolation( new OPFConstraintViolation (
							OPFConstraintViolation.Type.SPINE_MISSING_FALLBACK,
							getId(),
							false
					));
				}
			}
		}
		
		
	}
	
	


}
