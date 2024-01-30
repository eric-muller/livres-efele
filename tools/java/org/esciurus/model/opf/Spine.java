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

import java.util.Iterator;

import org.esciurus.model.ocf.ConstraintTicket;
import org.w3c.dom.Element;


/**
 * Represents the spine (i.e. the list of content documents) of an OPF package. 
 * 
 * @see OPFPackage
 */
public class Spine extends OPFMainList<SpineEntry> {

	private ManifestPtr tocPtr;
	private ManifestPtr pagemapPtr;

	
	
	/**
	 * Create a new Spine object, associated with the given OPF package
	 * @param parent the parent OPF package 
	 */
	public Spine(OPFPackage parent) {
		super(parent,spineTag,itemrefTag, SpineEntry.getEntryFactory());
		tocPtr = new ManifestPtr();
		pagemapPtr = new ManifestPtr();
	}


	/**
	 * Retrieve the file that represents the table of contents (NCX),
	 * as a manifest entry. 
	 * 
	 * @return Returns the toc entry in the manifest; may be <code>null</code> if none specified
	 */
	public ManifestEntry getToc() {
		return tocPtr.getTarget();
	}
	

	public void setPagemap(ManifestEntry toc) {
	  pagemapPtr.setTarget(toc);
	}


	public ManifestEntry getPagemap() {
	  return pagemapPtr.getTarget();
	}

	  /**
	   * Set the table of contents (NCX).
	   * 
	   * @param toc The manifest entry to use as table of contents; may be <code>null</code>.
	   */
	  public void setToc(ManifestEntry toc) {
	    tocPtr.setTarget(toc);
	  }


	/**
	 * Add a new entry to the spine, pointing to a specified file in the manifest.
	 * 
	 * @param mf the manifest entry to which the new spine entry should point
	 * @return the new spine entry
	 */
	public SpineEntry addEntry(ManifestEntry mf) {
		
		SpineEntry spe = addNewEntry();
		spe.setRef(mf);
		
		return spe;
		
	}
	
	private static String spineTag = "spine";
	private static String itemrefTag = "itemref";
	private static String tocAttr = "toc";
	private static String pagemapAttr = "page-map";

	
	
	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFMainList#readFromXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public void readListTagAttributes(Element element) throws EpubFormatException {
		super.readListTagAttributes(element);
		readPointer(element,tocAttr,tocPtr);
		readPointer(element,pagemapAttr,pagemapPtr);
	}


	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.OPFMainList#writeToXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public void writeListTagAttributes(Element element) {
		super.writeListTagAttributes(element);
		writePointer(element,tocAttr,tocPtr);
		writePointer(element,pagemapAttr,pagemapPtr);
	}



	@Override
	public void checkConstraints(ConstraintTicket ticket){
		
		super.checkConstraints(ticket);

		// check for primary entrie in spine
		boolean containsPrimary = false;
		
		for (Iterator<SpineEntry> it = iterator(); it.hasNext(); ){
			containsPrimary = containsPrimary || it.next().isLinear();
			if (containsPrimary) break;
		}

		if (!containsPrimary) {
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.SPINE_NO_PRIMARY,
					null,
					false
			));
		}
		
		
		// check whether toc specification is valid
		
		ManifestEntry tocFile = getToc();
		
		// check whether toc is present at all
		boolean violated = (tocFile == null);
		
		// invalid if it has the wrong media type
		violated = violated || (!MimeTypes.NCX_TYPE.equals(tocFile.getMediaType()));
		// invalid if any fallback attributes set
		violated = violated || tocFile.getFallback()!=null;
		violated = violated || tocFile.getFallbackStyle()!=null;
		violated = violated || tocFile.getRequiredNamespace()!=null;
		
		if (violated) {
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.SPINE_BAD_TOC,
					null,
					false
			));
		}
		
		
	}

	
	/**
	 * Add a file reference to the spine.
	 * 
	 * @param mfEntry the file (manifest entry) to add 
	 * @param linear the "linear" attribute of the spine entry
	 * @return the newly created spine entry corresponding to the file added
	 */
	public SpineEntry addReference(ManifestEntry mfEntry, boolean linear) {
		
		SpineEntry spe = new SpineEntry();
		this.add(spe);
		spe.setRef(mfEntry);
		spe.setLinear(linear);
		
		return spe;
		
	}
	
}
