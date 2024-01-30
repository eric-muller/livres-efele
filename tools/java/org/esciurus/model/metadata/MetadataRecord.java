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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Vector;

import org.esciurus.common.LanguageDictionary;
import org.esciurus.model.dictionaries.Dictionaries;
import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.opf.EpubFormatException;
import org.esciurus.model.opf.OPFConstraintViolation;
import org.esciurus.model.opf.OPFPackage;
import org.esciurus.model.opf.PackagePart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A collection of all metadata elements within an OPF package.
 * 
 * @see org.esciurus.model.opf.OPFPackage
 *
 */
public class MetadataRecord extends PackagePart {

	private StringEntryList titles;
	private StringEntryList subjects;
	
	private DCMetaEntryList<DCPerson> creators;
	private DCMetaEntryList<DCPerson> contributors;
	
	private StringEntryList descriptions;
	private StringEntryList publishers;
	
	private DCMetaEntryList<DCDate> dates;
	
	private StringEntryList types;
	private StringEntryList formats;
	private DCMetaEntryList<DCIdentifier> identifiers;
	private StringEntryList sources;
	
	private StringEntryList languages;
	private StringEntryList relations;
	private StringEntryList coverages;
	private StringEntryList rights;
	
	private XMetaList extraFields;  

	private Map<MetaField,MetaEntryList> allFields;

	private OPFPackage parent;
	
	/**
	 * Create a new metadata record for the specified package.
	 * 
	 * @param parent the OPF package that contains the metadata record
	 */
	public MetadataRecord(OPFPackage parent) {
		
		super();
		
		this.parent = parent;
		
		titles 		 = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, titleTag );
		creators 	 = new DCMetaEntryList<DCPerson>(parent, creatorTag, DCPerson.getEntryFactory());
		subjects 	 = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, subjectTag );
		contributors = new DCMetaEntryList<DCPerson>(parent, contributorTag, DCPerson.getEntryFactory());
		descriptions = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, descriptionTag );
		publishers 	 = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, publisherTag );
		dates 	     = new DCMetaEntryList<DCDate>(parent, dateTag, DCDate.getEntryFactory());
		types        = new StringEntryList( parent, DCMetaEntry.ENCODED, Dictionaries.getTypeDict(), typeTag );
		formats      = new StringEntryList( parent, DCMetaEntry.ENCODED, Dictionaries.getMimeDict(), formatTag );
		identifiers  = new DCMetaEntryList<DCIdentifier>(parent, identifierTag, DCIdentifier.getEntryFactory());
		sources      = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, sourceTag );
		languages    = new StringEntryList( parent, DCMetaEntry.ENCODED, Dictionaries.getLanguageDict(), languageTag );
		relations    = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, relationTag );
		coverages    = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, coverageTag );
		rights       = new StringEntryList( parent, DCMetaEntry.HUMAN_READABLE, null, rightsTag );
		
		extraFields  = new XMetaList( xMetaTag, parent );
		
		
		allFields = new HashMap<MetaField,MetaEntryList>();
		
		allFields.put(MetaField.TITLE,titles);
		allFields.put(MetaField.CREATOR,creators); 	 
		allFields.put(MetaField.SUBJECT,subjects); 	 
		allFields.put(MetaField.CONTRIBUTOR,contributors); 
		allFields.put(MetaField.DESCRIPTION,descriptions); 
		allFields.put(MetaField.PUBLISHER,publishers); 	 
		allFields.put(MetaField.DATE,dates); 	     
		allFields.put(MetaField.TYPE,types);        
		allFields.put(MetaField.FORMAT,formats);
		allFields.put(MetaField.IDENTIFIER,identifiers);
		allFields.put(MetaField.SOURCE,sources);      
		allFields.put(MetaField.LANGUAGE,languages);    
		allFields.put(MetaField.RELATION,relations);    
		allFields.put(MetaField.COVERAGE,coverages);    
		allFields.put(MetaField.RIGHTS,rights);       
		allFields.put(MetaField.EXTRA,extraFields);       

	}
	
	/* ********************** Title element ************** */
	
	/**
	 * Retrieve the "title" element of the metadata record.
	 * 
	 * If multiple title elements are present, returns only the
	 * first one. If none is present, returns an empty string.
	 * 
	 * @return the value of the "title" element
	 */
	public String getTitle() {
		
		String title="";
		if (titles.size()>0) {
			title = titles.get(0).getContent();
		}
		return title;
	}


	/**
	 * Set the "title" element of the metadata record.
	 * 
	 * All "title" elements that are currently present,
	 * whether one or multiple, will be deleted first.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		
		titles.addContent(title);
		
	}


	/**
	 * Retrieve a list of all "title" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "title" elements, as a writable list
	 */
	public StringEntryList getTitles() {
		return titles;
	}
	
	/* ********************** Subject element ************** */
	
	/**
	 * Retrieve a list of all "subject" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "subject" elements, as a writable list
	 *
	 */
	
	public StringEntryList getSubjects() {
		return subjects;
	}
	
	
	/* ********************** Creator element ************** */
	
	/* common internal routine with Contributor */
	/**
	 * Remove all persons of a specific role from a Vector.
	 * 
	 * @param persList a list of persons, e.g. creators
	 * @param role the code of the role to be removed
	 */
	private void removeRole (List<DCPerson> persList, String role) {
		
		Vector<DCPerson> peopleToRemove = new Vector<DCPerson>();
		
		for (Iterator<DCPerson> it = persList.iterator(); it.hasNext(); ) {
			DCPerson thisPerson = it.next();
			if ( thisPerson.getRole().equals(role) ) {
				peopleToRemove.add(thisPerson);
			}
		}
		
		persList.removeAll(peopleToRemove);
	}
	
	
	
	
	/**
	 * Retrieve the list of "creator" elements, each represented 
	 * by a DCPerson object.
	 *  
	 * @return the list of creators
	 */
	public DCMetaEntryList<DCPerson> getCreators() {
		return creators;
	}
	
		
	/**
	 * Remove all "creator" elements that correspond to a specific role,
	 * identified by a MARC relator code.
	 * 
	 * @param role the MARC relator code of creators to remove
	 */
	public void removeCreators(String role) {
		removeRole(creators,role);		
	}
		
	
	/* ********************** Contributor element ************** */
	
	/**
	 * Retriece the list of "contributor" elements, each represented 
	 * by a DCPerson object.
	 *  
	 * @return the list of contributors
	 */
	public DCMetaEntryList<DCPerson> getContributors() {
		return contributors;
	}
	
		
	/**
	 * Remove all "contributor" elements that correspond to a specific role,
	 * identified by a MARC relator code.
	 * 
	 * @param role the MARC relator code of contributors to remove
	 */
	public void removeContributors(String role) {
		removeRole(contributors,role);		
	}
		
	
	/* ********************** Description element ************** */


	/**
	 * Retrieve a list of all "description" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "description" elements, as a writable list
	 */
	public StringEntryList getDescriptions() {
		return descriptions;
	}

	/* ********************** Publisher element ************** */



	/**
	 * Retrieves a list of all "publisher" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "publisher" elements, as a writable list
	 */
	public StringEntryList getPublishers() {
		return publishers;
	}
	
	/*********************** Date element ***************/
	

	/**
	 * Retrieves a list of all "date" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "date" elements, as a writable list
	 *
	 */
	public DCMetaEntryList<DCDate> getDates() {
		return dates;
	}

	/* ********************** Type element ************** */



	/**
	 * Retrieves a list of all "type" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "type" elements, as a writable list
	 */
	public StringEntryList getTypes() {
		return types;
	}
	
	/* ********************** Format element ************** */

	

	/**
	 * Retrieves a list of all "format" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "format" elements, as a writable list
	 */
	public StringEntryList getFormats() {
		return formats;
	}
		
	/* ********************** Identifier element ************** */

	
	/**
	 * Retrieves a list of all "identifier" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "format" elements, as a writable list
	 */
	public DCMetaEntryList<DCIdentifier> getIdentifiers() {
		return identifiers;
	}
	
	
	
	/**
	 * Retrieve an identifier record by its textual ID value.
	 * This should be used during the load process from XML only.
	 * 
	 * @param id the ID of the identifier record to retrieve
	 * @return the identifier with this ID, or <code>null</code> if none is found. 
	 */
	public DCIdentifier getIdentifierById(String id) {
		
		DCIdentifier result = null;
		
		for (Iterator<DCIdentifier> it = identifiers.iterator(); it.hasNext();) {
			DCIdentifier thisIdent = it.next();
			if (id.equals(thisIdent.getId())) {
				result = thisIdent;
			}
		}
		
		return result;

	}

	/**
	 * Retrieve an identifier record with a specified scheme.
	 *  
	 * @param scheme the scheme that is searched for
	 * @return the first identifier found with this scheme, 
	 *         or <code>null</code> if none is found
	 */
	public DCIdentifier getIdentifierByScheme(String scheme) {
		
		DCIdentifier result = null;
		
		for (Iterator<DCIdentifier> it = identifiers.iterator(); it.hasNext();) {
			DCIdentifier thisIdent = it.next();
			if (scheme.equals(thisIdent.getScheme())) {
				result = thisIdent;
			}
		}
		
		return result;

	}

	
	private static String uniqueIdentScheme = "UUID";
	
	/**
	 * Generate an identifier record with a random uniqe ID (UUID) as its value,
	 * and add it to the metadata record.
	 * If the useAsPackageId parameter is set, then this identifier
	 * will be used as unique identifier for the OPF package.
	 * This is useful for automatically assigning the required
	 * unique identifier to the package.
	 * 
	 * <p>
	 * If the metadata record already contains an automatically generated
	 * identifier (scheme "UUID"), its value will be replaced,
	 * rather than generating a new indentifier record.
	 * </p>
	 * 
	 * @param useAsPackageId true if the generated identifier record
	 * should be used as unique identifier for the enclosing OPF package
	 * 
	 * @see OPFPackage#setUniqueId(DCIdentifier) 
	 */
	public void generateUniqueId(boolean useAsPackageId) {
		
		DCIdentifier uniqueIdent = getIdentifierByScheme(uniqueIdentScheme);
		
		if (uniqueIdent == null) {
			uniqueIdent = new DCIdentifier();
			identifiers.add(uniqueIdent);
		}
		uniqueIdent.setScheme(uniqueIdentScheme);
		uniqueIdent.setEditable(false);
		
		String uuid = UUID.randomUUID().toString();
		uniqueIdent.setValue(uuid);
		
		if (useAsPackageId) {
			parent.setUniqueId(uniqueIdent);
		}
		
		
	}
	
	/* ********************** Source element ************** */

	

	/**
	 * Retrieves a list of all "source" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "source" elements, as a writable list
	 */
	public StringEntryList getSources() {
		return sources;
	}

	/*********************** Language element ***************/


	/**
	 * Retrieves a list of all "language" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "language" elements, as a writable list
	 */
	public StringEntryList getLanguages() {
		return languages;
	}
	
	

	
	/* ********************** Relation element ************** */

	

	/**
	 * Retrieve a list of all "relation" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "relation" elements, as a writable list
	 */
	public StringEntryList getRelations() {
		return relations;
	}


	/* ********************** Coverage element ************** */

	

	/**
	 * Retrieve a list of all "coverage" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "coverage" elements, as a writable list
	 */
	public StringEntryList getCoverages() {
		return coverages;
	}

	/* ********************** Rights element ************** */

	

	/**
	 * Retrieves a list of all "rights" elements of the metadata record,
	 * in their proper order.
	 * 
	 * This is intended also for write operations: 
	 * Data changes in the List returned will directly affect the
	 * the metadata record.
	 * 
	 * @return the "rights" elements, as a writable list
	 */
	public StringEntryList getRights() {
		return rights;
	}

	
	/* ******** Extra metadata fields (as required for OPF metadata) ******** */
	
	/**
	 * Retrieve a list of all extra metadata fields.
	 * 
	 * @return Returns the extraFields.
	 */
	public XMetaList getExtraFields() {
		return extraFields;
	}
	
	/* ******* Generic method for all fields ************ */
	
	/**
	 * Retrieve a list of metadata entries for a specific metadata field.
	 * @param field the metadata field to query for
	 * @return a list of all metadata entries for this field
	 */
	@SuppressWarnings("unchecked")
	public MetaEntryList<MetaEntry> getFieldList(MetaField field) {
		return allFields.get(field);
	}
	
	/* ****** Other functionality ********* */
	
	private static String bundleName = "org.esciurus.model.dictionaries.MetadataBundle";
	
	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(bundleName,locale);
	}

	
	/* ******* XML access ****** */
	
	private static String dcNamespace = "http://purl.org/dc/elements/1.1/";
	private static String dcPrefix = "xmlns:dc";
	private static String opfNamespace = "http://www.idpf.org/2007/opf";
	private static String opfPrefix = "xmlns:opf";
	private static String xsiNamespace = "http://www.w3.org/2001/XMLSchema-instance";
	private static String xsiPrefix = "xmlns:xsi";
	
	
	private static String metadataTag = "metadata";

	private static String titleTag = "dc:title";
	private static String creatorTag = "dc:creator";
	private static String subjectTag = "dc:subject";
	private static String descriptionTag = "dc:description";
	private static String publisherTag = "dc:publisher";
	private static String contributorTag = "dc:contributor";
	private static String dateTag = "dc:date";
	private static String typeTag = "dc:type";
	private static String formatTag = "dc:format";
	private static String identifierTag = "dc:identifier";
	private static String sourceTag = "dc:source";
	private static String languageTag = "dc:language";
	private static String relationTag = "dc:relation";
	private static String coverageTag = "dc:coverage";
	private static String rightsTag = "dc:rights";

	private static String xMetaTag = "meta";
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.model.metadata.PackagePart#writeToXml(org.w3c.dom.Document, org.w3c.dom.Node)
	 */
	@Override
	public void writeToXml (Element element)  {

		Document doc = element.getOwnerDocument();

		Element metadataElm = doc.createElement(metadataTag);
		element.appendChild(metadataElm);
		metadataElm.setAttribute(dcPrefix,dcNamespace);
		metadataElm.setAttribute(opfPrefix,opfNamespace);
		metadataElm.setAttribute(xsiPrefix,xsiNamespace);
		
		for (Iterator<MetaEntryList> it = allFields.values().iterator(); it.hasNext();) {
			it.next().writeToXml(metadataElm);
		}
		
	}
	
	
	@Override
	public void readFromXml(Element parentElm) throws EpubFormatException {
				
		Element metadataElm = findSingleElement(parentElm,metadataTag,true);
		/* note: the above call finds elements also if they are not direct children
		 * of parentElm, e.g. nested within <dc:metadata>; so backward compatibility
		 * to OEBPS 1.2 is guaranteed.
		 */
		
		for (Iterator<MetaEntryList> it = allFields.values().iterator(); it.hasNext();) {
			it.next().readFromXml(metadataElm);
		}

	}
	
	/**
	 * Returns the OEBPS 1.2 compatible version of a tag name in the XML file.
	 * That is, if the tag name starts with the "dc:" prefix,
	 * this prefix is stripped.
	 *  
	 * @param tagName a tag name used for OPF 2.0 input/output
	 * @return the OEBPS 1.2 compatible tag name
	 */
	public static String getOEBPSFallbackTagName(String tagName) {
		String backTag = null;
		if (tagName.startsWith("dc:")) {
			backTag = "dc:"+tagName.substring(3,4).toUpperCase()+tagName.substring(4);
		}
		return backTag;
	}

	
	
	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		
		super.checkConstraints(ticket);

		if (titles.size()==0) {
			// no title found
			if (ticket.isTryResolve()) {
				// add default title
				ResourceBundle bundle = getBundle(Locale.getDefault());
				this.setTitle(bundle.getString("meta.untitled"));
			}
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.META_NO_TITLE,
					null,
					ticket.isTryResolve()
			));
		}
		
		/* no identifiers - does not need to be checked separately,
		  since it is less strict than PACKAGE_NO_UNIQUEID */

		if (languages.size()==0) {
			// no langauge info found
			if (ticket.isTryResolve()) {
				// add default language string
				String defaultLanguage=LanguageDictionary.LANG_UNDETERMINED;
				this.getLanguages().addContent(defaultLanguage);
			}
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.META_NO_LANGUAGE,
					null,
					ticket.isTryResolve()
			));
		}

		for (Iterator<MetaEntryList> it = allFields.values().iterator(); it.hasNext();) {
			it.next().checkConstraints(ticket);
		}
	}
		



}
