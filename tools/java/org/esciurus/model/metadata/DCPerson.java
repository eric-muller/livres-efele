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

import org.esciurus.common.KeyNotInDictionaryException;
import org.esciurus.model.dictionaries.Dictionaries;
import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.opf.EntryFactory;
import org.esciurus.model.opf.EpubFormatException;
import org.esciurus.model.opf.OPFConstraintViolation;
import org.esciurus.model.opf.OPFPackage;
import org.w3c.dom.Element;


/**
 * A person record within the package metadata.
 * May be used for both "creator" and "constributor" fields.
 * 
 * @see MetadataRecord
 */
public class DCPerson extends DCMetaEntry {

	private String name;
	private String role;
	private String fileAs;

	
	/**
	 * Create a new DCPerson object with default values.
	 */
	public DCPerson() {
		super(DCMetaEntry.HUMAN_READABLE);
		name="";
		role=null;
		fileAs=null;
	}

	/**
	 * Create a factory that produces DCPerson entries.
	 * 
	 * @return the factory created
	 * @see MetaEntryList (String,String,EntryFactory)
	 */
	public static EntryFactory<DCPerson> getEntryFactory() {
		return new EntryFactory<DCPerson> () {
			public DCPerson createEntry() {
				return new DCPerson();
			}
		};
	}

	
	
	/**
	 * Retrieve the "file-as" attribute of the person.
	 * 
	 * @return Returns the fileAs.
	 */
	public String getFileAs() {
		return fileAs;
	}
	/**
	 * Set the "file-as" attribute of the person.
	 * 
	 * @param fileAs The fileAs value to set.
	 */
	public void setFileAs(String fileAs) {
		this.fileAs = fileAs;
	}
	
	/**
	 * Retrieve the name of the person, as used for display purposes.
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
		
		
	}
	
	/**
	 * Set the name of the person.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Retrieve the role of the person, as 3-letter MARC relator code.
	 * 
	 * @return Returns the role.
	 */
	
	
	public String getRole() {
		return role;
	}
	
	
	/**
	 * Retrieve the role of the person, 
	 * as a string suitable for display in the given locale.
	 * 
	 * @param locale The locale to use for formatting.
	 * @return Returns the role, or the empty string if no role is set
	 */
	
	public String getDisplayRole(Locale locale) {
		String result = "";
		if (role != null) {
			result = Dictionaries.getMarcDict().getDisplayValue(role,locale);
		}
		return result;
	}

	
	/**
	 * Set the role of the person.
	 * The value will not be checked against dictionaries.
	 * 
	 * @param role The role to set.
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	/**
	 * Set the role of the person. 
	 * The new value will be checked against the
	 * dictionary of MARC relator codes, as specified in the 
	 * second parameter.
	 * 
	 * @param role The role to set.
	 * 
	 * @throws KeyNotInDictionaryException if strict checking
	 * is requested, but the passed value is not found in the dictionary.
	 */
	public void setRoleStrict(String role) throws KeyNotInDictionaryException {
		Dictionaries.getMarcDict().checkKey(role);
		
		setRole(role);
	}


	@Override
	public String getDisplayValue(Locale locale) {
		String result = getDisplayRole(locale)+": "+name;
		return result;
	}

	
	private static String roleAttr = "opf:role";
	private static String fileAsAttr = "opf:file-as"; 

	@Override
	public void writeToXml(Element element) {
		
		super.writeToXml(element);
		
		writeAttributeValue(element,roleAttr,role,false);
		writeAttributeValue(element,fileAsAttr,fileAs,false);
		element.setTextContent(name);
		
	}


	
	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		
		super.readFromXml(element);
		
		name = element.getTextContent().trim();
		role = readAttributeValue(element,roleAttr,false,true);
		fileAs = readAttributeValue(element,fileAsAttr,false,true);
		
	}

	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		
		super.checkConstraints(ticket);
		
		if (!getParent().getFormatModifiers().contains(OPFPackage.FormatModifier.DONT_CHECK_DICTIONARIES) ) {
		
			if (this.role != null) {
				if (! Dictionaries.getMarcDict().hasKey( this.role )) {
					
					ticket.addViolation( new OPFConstraintViolation (
						OPFConstraintViolation.Type.META_BAD_ROLE,
						this.role,
						false
					));
					
				}
			}
		}
				
	}


	
	
}
