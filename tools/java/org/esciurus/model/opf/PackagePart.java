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
import org.w3c.dom.NodeList;


/**
 * The base class for all parts of an OPF package.
 * <p>
 * A package part may correspond to an element in the XML
 * representation of the package descriptor (such as, a "tour site"),
 * or a list of those. The common functionality 
 * defined in this abstract class is as follows:
 * <ul>
 * <li>Reading and writing the package part from and to XML representation
 * </li>
 * <li>Moving this package part to a package and removing it from there,
 * </li>
 * </ul>
 * </p>
 * <p>
 * It should be noted that a PackagePart object may, but need not,
 * correspond directly to a node of the XML representation.
 * It may as well be a list of such elements which has no direct
 * counterpart as an XML node. 
 * </p>
 * <p>
 * The class also implements some static convenvience methods
 * for reading and writing attributes and crossref pointed to and from
 * XML representation. These methods are frequently used by subclasses
 * of PackagePart.
 * </p>
 * 
 * @see OPFPackage
 *
 */
public abstract class PackagePart {
	
	private OPFPackage parent;
	
	/**
	 * Create a new package part. 
	 */
	public PackagePart() {
		parent = null;
	}
	
	/**
	 * Read this package part from XML representation.
	 * This includes creating and reading all sub-elements, if any,
	 * recursively from the XML document.
	 * 
	 * @param element the XML element to read from. The precise specification
	 * as to what this XML element represents ("parent node", "XML element to read from")
	 * depends on implementation
	 * 
	 * @throws EpubFormatException if a data format problem is detected in the input data
	 */
	public abstract void readFromXml(Element element) throws EpubFormatException;

	/**
	 * Write this package part to XML representation.
	 * This includes writing all sub-elements, if any,
	 * recursively to the XML document.
	 * 
	 * @param element the XML element to write to. The precise specification
	 * as to what this XML element represents ("parent node", "XML element to write to")
	 * depends on implementation
	 */
	public abstract void writeToXml(Element element);
	
	
	/**
	 * Check data constraints within this package part.
	 * This includes recursive propagation of the check process
	 * to subparts of this package part, if any. 
	 * 
	 * <p>Implementing subclasses need to override this method
	 * in order to add specific constraint checks, or to add
	 * propagation to subparts. They should always
	 * call the <code>super()</code> method.</p>
	 * 
	 * @param ticket the contraint ticket to add violations to
	 */
	public void checkConstraints(ConstraintTicket ticket) {
		// currently no constraint checks at this level
	}

	
	/**
	 * Retrieve the OPF package in which this package part is contained.
	 * 
	 * @return the parent OPF package
	 */
	public OPFPackage getParent() {
		return parent;
	}

	/**
	 * Move this package part to a package.
	 * This method shold typically be called directly after creation
	 * of a package part. For all lists of package parts, it is
	 * obligatory to call this method for any package part that
	 * is added to them.
	 * 
	 * @param pack the new parent package of this package part 
	 */
	protected void moveToPackage (OPFPackage pack) {
		parent = pack;
	}
	
	/**
	 * Remove this package part from its package.
	 */
	protected void removeFromPackage () {
		parent = null;
	}
	
	/**
	 * Read an attribute from XML representation.
	 * For the case that the attribute is not found, 
	 * two types of behavior can be specified:
	 * If the "obligatory" parameter is set, an exception will be trown;
	 * otherwise, a <code>null</code> value will be returned.
	 * 
	 * @param element the XML element to read the attribute from
	 * @param attrName the name of the attribute
	 * @param obligatory true if the attribute is considered obligatory
	 * @return the value of the attribute; may be <code>null</code> if the "obligatory" parameter is false
	 * @throws EpubFormatException if the attribute is obligatory, but has not been found
	 */
	public static String readAttributeValue(Element element, String attrName, boolean obligatory) throws EpubFormatException {
		return readAttributeValue(element,attrName,obligatory,false);
	}

	
	/**
	 * Read an attribute from XML representation, with fallback provisions 
	 * for OEBPS 1.2 backward compatibility. 
	 * The method works like readAttribute(Element,String,boolean).
	 * However, if the attribute specified is not found,
	 * and the provideOEBPSFallback parameter is set,
	 * the implementation will try to remove the namespace from 
	 * the attribute, and try reading again. Only if this fails as well,
	 * the attribute is considered as missing. 
	 * 
	 * @param element the XML element to read the attribute from
	 * @param attrName the name of the attribute
	 * @param obligatory true if the attribute is considered obligatory
	 * @param provideOEBPSFallback true if OEBPS 1.2 fallback should be provided, as described above
	 * @return the value of the attribute; may be <code>null</code> if the "obligatory" parameter is false
	 * @throws EpubFormatException if the attribute is obligatory, but has not been found
	 * 
	 * @see #readAttributeValue(Element, String, boolean)
	 */
         protected static String readAttributeValue(Element element, String attrName, boolean obligatory, boolean provideOEBPSFallback) throws EpubFormatException {
		
		String value = null;
		
		if (element.hasAttribute(attrName)) {
			value = element.getAttribute(attrName);
		}
		else if (provideOEBPSFallback) {
			String oldAttrName = attrName.substring( attrName.indexOf(":")+1);
			if (element.hasAttribute(oldAttrName)) {
				value = element.getAttribute(oldAttrName);
			}
		}
		
		if (obligatory && (value ==null)) {

                  //                  System.err.println ("required attribute not found: "+attrName + " in " + element);
                  //			throw new EpubFormatException ("required attribute not found: "+attrName);
		}
		
		
		return value;
	}


	/**
	 * Write an attribute to XML representation.
	 * 
	 * Empty strings and <code>null</code> values will only be written
	 * if the "obligatory" parameter is set. In this case,
	 * they are written as an empty string.
	 * If the "obligatory" parameter is not set,
	 * empty strings and <code>null</code> values will be ignored
	 * and not written to XML.
	 * 
	 * @param element the XML element to write the attribute to
	 * @param attrName the name of the attribute
	 * @param attrValue the value of the attribute
	 * @param obligatory true if the attribute should be written even 
	 * if its value is empty or <code>null</code>.
	 */
	protected static void writeAttributeValue(Element element, String attrName, String attrValue, boolean obligatory) {

		String val = (attrValue==null) ? "" : attrValue;
		
		if (obligatory || val.length()>0){
			element.setAttribute(attrName,val);
		}
	}

	
	/**
	 * Find a direct subelement of an XML node, by name.
	 * If the subelement is not found, and the "obligatory" parameter
	 * is set, an exception will be thrown. If the "obligatory" parameter
	 * is not set, the method will return a <code>null</code> value
	 * for subelements that have not been found. 
	 * 
	 * @param parentElm the XML element in which the subelement should be located
	 * @param tagName the name (tag name) of the subelement
	 * @param obligatory true if failure to find the subelement should result in an exception
	 * @return the subelement found; or <code>null</code> if it has not been found, 
	 *         and the "obligatory" parameter is false
	 * @throws EpubFormatException if the subelement is not found, and the "obligatory"
	 *         parameter is true
	 */
	public static Element findSingleElement(Element parentElm, String tagName, boolean obligatory) throws EpubFormatException {
		
		NodeList nl = parentElm.getElementsByTagNameNS ("http://www.idpf.org/2007/opf", tagName);
		if (nl.getLength() > 0) {
			Element foundElm = (Element) nl.item(0);
			return foundElm;
		}
		else {
			if (obligatory) {
				throw new EpubFormatException("required element not found: "+tagName);
			}
			return null;
		}
	}

	/**
	 * Read a crossref pointer from XML representation.
	 * 
	 * @param element the XML element to read from
	 * @param attrName the attribute name of the IDREF value that represents the pointer
	 * @param ptr the XrefPointer object to store the pointer in
	 */
	public void readPointer(Element element,String attrName, XrefPointer ptr)  {
		readPointer(element,attrName,ptr,getParent().getXrefManager());
	}


	/**
	 * Read a crossref pointer from XML representation, using a specified XrefManager.
	 * 
	 * @param element the XML element to read from
	 * @param attrName the attribute name of the IDREF value that represents the pointer
	 * @param ptr the XrefPointer object to store the pointer in
	 * @param mgr the XrefManager to use
	 */
	public static void readPointer(Element element,String attrName, XrefPointer ptr, XrefManager mgr)  {
		String attrValue;
		try {
			attrValue = readAttributeValue(element,attrName,false);
		} catch (EpubFormatException e) {
			// cannot occur, since the "obligatory" parameter was false
			throw new RuntimeException("internal error while reading pointer",e);
		}
		ptr.prenoteTarget(attrValue,mgr);
	}

	/**
	 * Write a crossref pointer to XML representation, that is, to an 
	 * attribute with IDREF value.
	 * 
	 * @param element the XML element to write to
	 * @param attrName the name of the target attribute
	 * @param ptr the crossref point to be written
	 */
	public static void writePointer(Element element,String attrName, XrefPointer<? extends OPFElement> ptr) {
		XrefTarget target = ptr.getTarget();
		if (target != null) {
			writeAttributeValue(element,attrName,target.getPersistenceId(),false);
		}
	}
	
	
	/**
	 * A crossref pointer that points to a file in the manifest of the package.
	 */
	protected class ManifestPtr extends XrefPointer<ManifestEntry> {

		@Override
		public ManifestEntry getTargetByPersistenceId(String persistenceId) {
			return getParent().getManifestEntry(persistenceId);
		}
		
	}


}