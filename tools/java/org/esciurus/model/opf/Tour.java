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
 * A "tour" through the publication, as described in the OPF specification
 *
 * @see OPFPackage
 */
public class Tour extends OPFSubList<TourSite> {
	
	private String title;
	
	/**
	 * Create a new Tour object.
	 */
	public Tour() {
		super(siteTag,TourSite.getEntryFactory());
		title = null;
	}

	/**
	 * Create a factory that produces tours.
	 * 
	 * @return a new tour
	 * @see OPFList (String,String,EntryFactory)
	 */
	public static EntryFactory<Tour> getTourFactory() {
		return new EntryFactory<Tour> () {
			public Tour createEntry() {
				return new Tour();
			}
		};
	}

	
	/**
	 * Retrieve the title of the tour. 
	 * 
	 * @return the title; may be <code>null</code> if no title was set
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the tour
	 * 
	 * @param title the title to set; may be <code>null</code> 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	
	private static String titleAttr = "title";
	private static String siteTag = "site";


	@Override
	public void readFromXml(Element element) throws EpubFormatException {

		title = readAttributeValue(element,titleAttr,true);
		super.readFromXml(element);
				
	}

	@Override
	public void writeToXml(Element element) {

		writeAttributeValue(element,titleAttr, title, true );
		super.writeToXml(element);
				
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
					OPFConstraintViolation.Type.TOUR_NO_TITLE,
					null,
					resolve
			));

		}
		
		
	}
	
	/**
	 * Add a site to this tour. This automatically adds the tour site
	 * object to the OPF package in which this tour is enclosed.
	 * 
	 * @param title the title of the new tour site
	 * @param href the IRI for the content of this tour site; 
	 * may contain a fragment identifier
	 * @throws EpubDataException if the href given is invalid, e.g. does not
	 * correspond to a file in the package manifest
	 */
	public void addSite (String title, String href) throws EpubDataException {
		
		TourSite newSite = new TourSite();
		this.add(newSite);
		newSite.setTitle(title);
		newSite.setHref(href);
		
	}


}
