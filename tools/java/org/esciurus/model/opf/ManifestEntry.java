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

import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.ocf.FileInfo;
import org.esciurus.model.ocf.FileSyntaxException;
import org.w3c.dom.Element;


/**
 * An entry of the manifest of an OPF package.
 * Represents a file (on the file system) that is part of the publication package. 
 * 
 */
public class ManifestEntry extends OPFElement {


	private String href;
	private String mediaType;
	private ManifestPtr fallback;
	private ManifestPtr fallbackStyle;
	private String requiredNamespace;
	private String requiredModules;
	
	
	
	/**
	 * Create a new manifest entry. 
	 */
	public ManifestEntry() {
		this(null,null);
	}
	
	/**
	 * Create a new manifest entry, with given href and mendia type
	 * 
	 * @param href the href (IRI, relative to the package root) of the file 
	 * that this manifest entry corresponds to. Must not contain a fragment identifer.
	 * @param mediaType the MIME media type of the file
	 */
	public ManifestEntry(String href, String mediaType) {

		this.href = href;
		this.mediaType = mediaType;
		this.fallback = new ManifestPtr();
		this.fallbackStyle = new ManifestPtr();

	}
	
	/**
	 * Create a factory that produces manifest entries.
	 * 
	 * @return a new manifest entry
	 * @see OPFList (String,String,EntryFactory)
	 */
	public static EntryFactory<ManifestEntry> getEntryFactory() {
		return new EntryFactory<ManifestEntry> () {
			public ManifestEntry createEntry() {
				return new ManifestEntry();
			}
		};
	}


	/**
	 * Get the href attribute of the file corresponding to this manifest entry;
	 * that is, get the IRI relative to the package root.
	 * 
	 * @return the href value
	 */
	public String getHref() {
		return href;
	}

	/**
	 * Set the MIME media type of this entry.;
	 * the value is not checked against any dictionary.
	 * @param mediaType the media type to set
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * Retrieve the MIME media type of this entry.
	 * 
	 * @return the media type
	 */
	public String getMediaType() {
		return mediaType;
	}

	/**
	 * Retrieve the fallback item for this entry, if any.
	 * See the OPF specification for a description of the fallback mechanism.
	 * 
	 * @return the fallback entry; may be <code>null</code> if none was set
	 */
	public ManifestEntry getFallback() {
		return fallback.getTarget();
	}

	/**
	 * Set the fallback item for this entry 
	 * See the OPF specification for a description of the fallback mechanism.

	 * @param fallback the fallback to set (as a manifest entry)
	 */
	public void setFallback(ManifestEntry fallback) {
		this.fallback.setTarget(fallback);
	}

	/**
	 * Retrieve the fallback stylesheet for this entry.
 	 * See the OPF specification for a description of the fallback mechanism.
 	 * 
	 * @return the fallback stylesheet, as a mainfest entry; may be <code>null</code>.
	 */
	public ManifestEntry getFallbackStyle() {
		return fallbackStyle.getTarget();
	}

	/**
	 * Set the fallback stylesheet for this entry.
 	 * See the OPF specification for a description of the fallback mechanism.
 	 * 
	 * @param fallbackStyle the fallback style to set, as a mainfest entry
	 */
	public void setFallbackStyle(ManifestEntry fallbackStyle) {
		this.fallbackStyle.setTarget(fallbackStyle);
	}


	/**
	 * Retrieve the required modules attribute for this entry.
 	 * See the OPF specification for details. 
 	 * 
	 * @return the required modules specification
	 */
	public String getRequiredModules() {
		return requiredModules;
	}

	/**
	 * Set the required modules attribute for this entry.
 	 * See the OPF specification for details.
 	 *  
	 * @param requiredModules the required modules to set.
	 */
	public void setRequiredModules(String requiredModules) {
		this.requiredModules = requiredModules;
	}

	
	/**
	 * Retrieve the required namespace for this entry.
 	 * See the OPF specification for details.
 	 *  
	 * @return the required namespace; may be <code>null</code>
	 */
	public String getRequiredNamespace() {
		return requiredNamespace;
	}

	/**
	 * Set the required namespace for this entry.
 	 * See the OPF specification for details.
 	 *  
	 * @param requiredNamespace the required namespace to set
	 */
	public void setRequiredNamespace(String requiredNamespace) {
		this.requiredNamespace = requiredNamespace;
	}
	
	/**
	 * Checks whether this entry is an out-of-line XML island.
	 * 
	 * This is determined by the "required-namespace" field being
	 * not null.
	 * 
	 * @return true if this manifest entry is an out-of-line XML island
	 */
	public boolean isXmlIsland() {
		return requiredNamespace != null;
	}

	
	/**
	 * Get an item from the fallback chain of this manifest entry
	 * which matches a given set of media types.
	 * Optionally, out-of-line XML islands which do not match the list of 
	 * media types, but have a fallback style specified,
	 * will be accepted too. This is specified in the <code>acceptXmlStyle</code> parameter.
	 * The entry returned may be the present entry itself, provided it matches the requirements.
	 * 
	 * @param acceptedTypes a list of accepted MIME media types
	 * @param acceptXmlStyle <code>true</code> if XML islands with fallback style specified
	 * should be accepted as a match
	 * @return the first item in the fallback chain (including the entry itself) 
	 * which matches the given requirements 
	 */
	public ManifestEntry getFallbackByType(List<String> acceptedTypes,
			boolean acceptXmlStyle ){
		
		Vector<ManifestEntry> visitedEntries = new Vector<ManifestEntry>();
		return queryFallbackChain(acceptedTypes,acceptXmlStyle,visitedEntries);
		
	}

	private ManifestEntry queryFallbackChain(List<String> acceptedTypes,
			boolean acceptXmlStyle, List<ManifestEntry> visitedEntries ){

		ManifestEntry result;	
		
		if (visitedEntries.contains(this)) {
			// we've run into a cyclic fallback chain
			result= null;
		}
		else if (acceptedTypes.contains(this.getMediaType())) {
			// media type of this entry matches one of the accepted types
			result= this; 
		}
		else if (acceptXmlStyle && isXmlIsland() && getFallbackStyle() != null) {
			// this is an "acceptable" XML island, by fallback style specified
			result= this;
		}
		else if (getFallback() != null){
			// proceed down the chain
			visitedEntries.add(this);
			result= getFallback().queryFallbackChain(acceptedTypes,acceptXmlStyle,visitedEntries);
		}
		else {
			// no valid fallback found
			result = null;
		}
		
		return result;
			
	}
	
	/**
	 * Get a FileInfo object that represents this file whithin the OCF container.
	 * This object can be used in order to obtain the contents of this file
	 * from the container.
	 * 
	 * @return the file info object
	 * @throws FileSyntaxException if the name of the file does not conform
	 * to the OCF standards
	 * 
	 * @see org.esciurus.model.ocf.Container#getContentFile(FileInfo)
	 * @see org.esciurus.model.ocf.Container#getContentStream(FileInfo)
	 */
	public FileInfo getContainerReference() throws FileSyntaxException {
		return getParent().getContainerReference(getHref(),getMediaType());
	}
	
	private static String hrefAttr = "href";
	private static String mediaTypeAttr = "media-type";
	private static String fallbackAttr = "fallback";
	private static String fallbackStyleAttr = "fallback-style";
	private static String requiredNamespaceAttr = "required-namespace";
	private static String requiredModulesAttr = "required-modules";

	
	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		super.readFromXml(element);
		
		href = readAttributeValue(element,hrefAttr,true);
		mediaType = readAttributeValue(element,mediaTypeAttr,true);
		readPointer(element,fallbackAttr,fallback);
		readPointer(element,fallbackStyleAttr,fallbackStyle);
		requiredNamespace = readAttributeValue(element,requiredNamespaceAttr,false);
		requiredModules = readAttributeValue(element,requiredModulesAttr,false);
		
	}

	@Override
	public void writeToXml(Element element) {
		ensureId();
		
		super.writeToXml(element);
		
		writeAttributeValue(element, hrefAttr, href, true);
		writeAttributeValue(element, mediaTypeAttr, mediaType, true);
		writePointer(element, fallbackAttr, fallback);
		writePointer(element, fallbackStyleAttr, fallbackStyle);
		writeAttributeValue(element, requiredNamespaceAttr, requiredNamespace, false);
		writeAttributeValue(element, requiredModulesAttr, requiredModules, false);
		
	}

	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		
		super.checkConstraints(ticket);
		
		boolean violated = false;
		
		// check for valid href
		if (this.href ==null)
			violated = true;
		else {
			violated = href.contains("#"); 
		}
		if (violated) {
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.MANIFEST_BAD_HREF,
					this.href,
					false
			));
		}

		// check for valid media type
		if (this.mediaType==null) {
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.MANIFEST_NO_MEDIA_TYPE,
					getId(),
					false
			));
		}
		
		
	}
	
	

}