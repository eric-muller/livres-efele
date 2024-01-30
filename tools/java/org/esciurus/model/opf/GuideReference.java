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

import java.util.Locale;

import org.esciurus.common.KeyNotInDictionaryException;
import org.esciurus.model.dictionaries.Dictionaries;
import org.esciurus.model.ocf.ConstraintTicket;
import org.w3c.dom.Element;


/**
 * An entry (reference) within a guide in the OPF package.
 * See the OPF sepcification for a description of the "guide" concept.
 * 
 * @see OPFPackage
 *
 */
public class GuideReference extends OPFElement {

	private String type;
	private String title;
	private String href;
	
	/**
	 * Create a new guide reference
	 */
	public GuideReference() {
		super();
	}
	
	/**
	 * Create a factory that produces guide references.
	 * 
	 * @return a new guide reference
	 * @see OPFList (String,String,EntryFactory)
	 */
	public static EntryFactory<GuideReference> getEntryFactory() {
		return new EntryFactory<GuideReference> () {
			public GuideReference createEntry() {
				return new GuideReference();
			}
		};
	}

	
	
	/**
	 * Retrieve the href (i.e. relative IRI) of the content file
	 * that corrseponds to this guide reference.
	 * 
	 * @return the href (IRI, relative to the package root)
	 */
	public String getHref() {
		return href;
	}

	/**
	 * Set the href attribute of the guide reference.
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
	 * Retrieve the title of this guide reference. 
	 * 
	 * @return the title; may be <code>null</code> if no title was set
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of this guide reference.
	 * 
	 * @param title the title to set; may be <code>null</code>
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Return the type of this guide reference, as a key.
	 * The set of keys is described in section 2.6 of the OPF specification.
	 * This method does not guarantee, however, that the returned
	 * value is always included in that list. 
	 *  
	 * @return the type of the guide reference, as a key 
	 * (e.g.: "cover" for the book cover). May be <code>null</code> 
	 * if no type was set.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get a displayable value for the type of this guide reference.
	 * The type of reference, internally stored as a symbolic key,
	 * is formatted to a displayable value in the locale given.
	 * Non-standard keys, e.g. starting with "other." or not contained
	 * in the dictionary, will be returned in symbolic form without conversion.
	 * 
	 * @param locale the locale (language) to use for conversion
	 * @return the type of this guide reference, formatted for display purposes
	 * 
	 * @see #getType()
	 * @see org.esciurus.model.dictionaries.Dictionaries#getRefTypeDict()
	 */
	public String getDisplayType(Locale locale) {
		return Dictionaries.getRefTypeDict().getDisplayValue(type,locale);
	}
	

	/**
	 * Set the type of this guide reference.
	 * The type is expected as a symbolic key, e.g. "cover",
	 * but this mehthod does not check the value against the dictionary
	 * of allowed values.
	 *  
	 * @param type the type to set, as a key
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Set the type of this guide reference, verifying the new value
	 * against the dictionary of allowed values.
	 * The type is expected as a symbolic key, e.g. "cover".
	 * See section 2.6 of the OPF specification for a list of allowed values.
	 * If the parameter "strict" is set, then values will be checked
	 * against the dictionary before setting, and the method will throw
	 * an exception if the key is invalid.
	 *   
	 * @param type the type to set
	 * @param strict true if the type should be checked against the dictionary
	 * @throws KeyNotInDictionaryException if the type is not found in the dictionary
	 * 
	 * @see #setType(String)
	 * @see org.esciurus.model.dictionaries.Dictionaries#getRefTypeDict()
	 */
	public void setTypeStrict(String type, boolean strict) throws KeyNotInDictionaryException  {
		
		Dictionaries.getRefTypeDict().checkKey(type);
		setType(type);
	}


	private static String titleAttr = "title";
	private static String typeAttr = "type";
	private static String hrefAttr = "href";

	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		super.readFromXml(element);
		href =  readAttributeValue(element,hrefAttr,true);
		title = readAttributeValue(element,titleAttr,false);
		type =  readAttributeValue(element,typeAttr,true);
		
	}


	@Override
	public void writeToXml(Element element) {
		
		super.writeToXml(element);
		writeAttributeValue(element,hrefAttr,href,true);
		writeAttributeValue(element,titleAttr,title,false);
		writeAttributeValue(element,typeAttr,type,true);
		
	}

	
	@Override
	public void checkConstraints(ConstraintTicket ticket) {

		super.checkConstraints(ticket);
		
		boolean violated = false;
		
		// check type field for consistency
		if (this.type ==null)
			violated = true;
		else {
			violated = 
				!getParent().getFormatModifiers().contains(OPFPackage.FormatModifier.DONT_CHECK_DICTIONARIES) 
				&& ! Dictionaries.getRefTypeDict().hasKey( this.type );
		}
		if (violated) {
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.GUIDE_BAD_TYPE,
					this.type,
					false
			));
		}

		// check href field for consistency
		violated = false;
		
		if (this.href==null)
			violated = true;
		else {
			violated = 
				! getParent().isFileInManifest(href);
		}
		if (violated) {
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.GUIDE_BAD_HREF,
					this.href,
					false
			));
		}

	}
	
	
}
