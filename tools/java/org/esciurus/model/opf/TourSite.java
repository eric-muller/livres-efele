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


import org.esciurus.model.ocf.ConstraintTicket;
import org.w3c.dom.Element;


/**
 * A tour site within a tour of the publication. 
 * See the OPF specification for a description of the "tours" concept.
 * 
 * @see Tour
 * @see OPFPackage
 *
 */
public class TourSite extends OPFElement {
	
	private String title;
	private String href;
	
	
	/**
	 * Create a new tour site.
	 */
	public TourSite () {
		super();
	}

	/**
	 * Create a factory that produces tour sites.
	 * 
	 * @return a new tour site
	 * @see OPFList (String,String,EntryFactory)
	 */
	public static EntryFactory<TourSite> getEntryFactory() {
		return new EntryFactory<TourSite> () {
			public TourSite createEntry() {
				return new TourSite();
			}
		};
	}

	
	/**
	 * Retrieve the href (i.e. relative IRI) of the content file
	 * that corrseponds to this tour site.
	 * 
	 * @return the href (IRI, relative to the package root)
	 */
	public String getHref() {
		return href;
	}
	
	/**
	 * Set the href attribute of the tour site.
	 * 
	 * @param href the href to set (IRI, relative to the package root)
	 * @throws EpubDataException if the file corresponding
	 * to the given IRI is not contained in the package manifest
	 * @see #getHref()
	 */
	public void setHref(String href) throws EpubDataException {
		if (getParent().isFileInManifest(href)) {
			this.href = href;			
		}
		else {
			throw new EpubDataException ("file not in manifest: "+href);
		}
	}
	
	/**
	 * Retrieve the title of this tour site. 
	 * 
	 * @return the title; may be <code>null</code> if no title was set
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of this tour site.
	 * 
	 * @param title the title to set; may be <code>null</code>
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	
	private static String titleAttr="title";
	private static String hrefAttr="href";
	
	@Override
	public void readFromXml(Element element) throws EpubFormatException {

		super.readFromXml(element);
		title = readAttributeValue(element,titleAttr,false);
		href = readAttributeValue(element,hrefAttr,true);
		
	}

	@Override
	public void writeToXml(Element element) {
				
		super.writeToXml(element);
		writeAttributeValue (element,titleAttr, title, false );
		writeAttributeValue (element,hrefAttr, href, true );
		
	}

	@Override
	public void checkConstraints(ConstraintTicket ticket) {

		super.checkConstraints(ticket);
		
		// check for empty title
		if (title == null) {
			
			boolean resolve = ticket.isTryResolve();
			
			if (resolve){
				//LATER add proper i18n
				title = "(untitled)";
			}
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.TOURSITE_NO_TITLE,
					null,
					resolve
			));

		}
		
		// check for bad href
		boolean violated = false;
		
		if (this.href==null)
			violated = true;
		else {
			violated = 
				! getParent().isFileInManifest(href);
		}
		if (violated) {
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.TOURSITE_BAD_HREF,
					this.href,
					false
			));
		}

		
	}

}
