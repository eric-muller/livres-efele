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

package org.esciurus.model.metadata;

import java.util.Locale;

import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.opf.EntryFactory;
import org.esciurus.model.opf.EpubFormatException;
import org.w3c.dom.Element;


/**
 * An identifier record in the OPF package metadata.
 * Identifier records corresond to "dc:identifier" elements
 * in the XML package descriptor.
 * 
 * <p>
 * In addition, this implementation adds a boolean field "editable".
 * This is used to mark the identifier as "not editable by user"
 * in a GUI, for example. This field is currently informative
 * and does not influence any functionality of the model.
 * It is not stored to the XML representation. 
 * </p>
 * 
 * @see MetadataRecord
 *
 */
public class DCIdentifier extends DCMetaEntry {

	private String scheme;
	private String value;
	private boolean editable;
	
	
	/**
	 * Create a new identifier record.
	 */
	public DCIdentifier() {
		super(DCMetaEntry.ENCODED);
		editable = true;
	}
	
	/**
	 * Create a new identifier record with the specified scheme and value.
	 * 
	 * @param scheme the scheme of the new identifier record
	 * @param value the value of the new identifier record
	 */
	public DCIdentifier(String scheme, String value) {
		this();
		this.scheme = scheme;
		this.value = value;
	}

	/**
	 * Create a factory that produces identifier records.
	 * 
	 * @return the factory created
	 * @see MetaEntryList (String,String,EntryFactory)
	 */
	public static EntryFactory<DCIdentifier> getEntryFactory() {
		return new EntryFactory<DCIdentifier> () {
			public DCIdentifier createEntry() {
				return new DCIdentifier();
			}
		};
	}
	
	

	/**
	 * Retrieve whether the identifier value is editable by user.
	 * 
	 * @return true if the identifier is editable by the user
	 */
	public boolean isEditable() {
		return editable;
	}



	/**
	 * Set whether the identifier value is editable by user.
	 * @param editable true if the identifier is editable by the user
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}


	/**
	 * Retrieve the scheme of the identifier record.
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}



	/**
	 * Set the scheme of the identifier record.
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}



	/**
	 * Retrieve the value of the identifier record
	 * 
	 * @return the value of the identifier
	 */
	public String getValue() {
		return value;
	}



	/**
	 * Set the value of the identifier record
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	private static String schemeAttr = "opf:scheme"; 


	@Override
	public void writeToXml(Element element) {
		
		super.writeToXml(element);
		
		writeAttributeValue(element,schemeAttr,scheme,false);
		element.setTextContent(value);
		
	}


	
	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		
		super.readFromXml(element);
		
		value = element.getTextContent().trim();
		scheme = readAttributeValue(element,schemeAttr,false,true);
		
	}





	@Override
	public String getDisplayValue(Locale locale) {
		String result;
		result = (scheme==null)? "" : scheme+": ";
		result += value;
		return result;
	}

	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		
		super.checkConstraints(ticket);
		
	}


	
}
