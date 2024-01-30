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


import org.esciurus.model.ocf.ConstraintTicket;


/**
 * A simple x-metadata entry within the publication metadata, 
 * without further application logic.
 * This entry merely stores the three values specified in the
 * OPF specification for x-metadata fields (name, scheme, content)
 * and provides access to those.
 * 
 * @see MetadataRecord
 */
public class XTextfield extends XMetaEntry {

	private String name;
	private String scheme;
	private String content;


	/**
	 * Create a new XTextfield object with default values.
	 */
	public XTextfield() {
		super();
		name = "";
		content = "";
	}

	/**
	 * Create a new XTextfield and fill it with specified values.
	 * @param name the value for the "name" field
	 * @param scheme the value for the "scheme" field
	 * @param content the value for the "content" field
	 */
	public XTextfield(String name, String scheme, String content) {
		this.name = name;
		this.scheme = scheme;
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}



	/**
	 * Set the "content" field of this x-metadata entry
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}



	/**
	 * @return Returns the name.
	 */
	@Override
	public String getName() {
		return name;
	}



	/**
	 * Set the "name" field of this x-metadata entry
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	@Override
	public String getScheme() {
		return scheme;
	}



	/**
	 * Set the "scheme" field of this x-metadata entry
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	protected void parseInput(String scheme, String content) {
		
		this.scheme=scheme;
		this.content=content;
		
	}

	
	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		super.checkConstraints(ticket);
		// currently no specific constraints
		
	}


	
}
