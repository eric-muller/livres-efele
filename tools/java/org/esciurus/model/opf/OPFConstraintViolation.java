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

import org.esciurus.model.ocf.ConstraintViolation;
import org.esciurus.model.ocf.ConstraintTicket;

/**
 * Represents a constraint violation in an OPF package.
 *
 */
public class OPFConstraintViolation extends ConstraintViolation {

	/**
	 * Types of constraint violations that can occur 
	 * according to the OPF specification.
	 * 
	 */
	public enum Type {
		/**
		 * The package does not contain a unique identifier.
		 * 
		 * This violation is automatically resolved by adding
		 * an automatically generated UUID to the package metadata
		 * and specifying it as the package unique id.
		 * 
		 * @see org.esciurus.model.opf.OPFPackage#checkConstraints(ConstraintTicket)
		 */
		PACKAGE_NO_UNIQUEID,

		/**
		 * There is no file in the package manifest.
		 * 
		 * This violation is not automatically resolved.
		 * 
		 * @see org.esciurus.model.opf.OPFPackage#checkConstraints(ConstraintTicket)
		 */
		MANIFEST_EMPTY,

		/**
		 * a file listed in the manifest is not actually found on the file system.
		 * or is not readable. 
		 * 
		 * This violation is not automatically resolved.
		 * 
		 * The constraint is not checked if the OPF package is in standalone mode.
		 * 
		 * @see org.esciurus.model.opf.OPFPackage#checkConstraints(ConstraintTicket)
		 * @see org.esciurus.model.opf.OPFPackage#isStandaloneMode()
		 */
		MANIFEST_FILE_MISSING,
		
		
		/**
		 * A manifest entry contains an invalid href specification,
		 * or no href at all.
		 * 
		 * A href in the manifest is invalid if it 
		 * contains a fragment identifier.
		 * 
		 * This violation is not automatically resolved.
		 * 
		 *  @see org.esciurus.model.opf.ManifestEntry#checkConstraints(ConstraintTicket)
		 */
		MANIFEST_BAD_HREF,

		/**
		 * A manifest entry does not specify a media type.
		 * 
		 * This violation is not automatically resolved.
		 * 
		 *  @see org.esciurus.model.opf.ManifestEntry#checkConstraints(ConstraintTicket)
		 */
		MANIFEST_NO_MEDIA_TYPE,
				
		
		/**
		 * There is a cyclic fallback chain in the manifest.
		 * 
		 * This violation is not automatically resolved.
		 *  
		 *  @see org.esciurus.model.opf.ManifestEntry#checkConstraints(ConstraintTicket)
		 */
		MANIFEST_CYCLIC_FALLBACK,
		
		/**
		 * No title was set in the package metadata.
		 * 
		 * This violation is automatically resolved by adding
		 * a dummy title element ("untitled").
		 * 
		 * @see org.esciurus.model.metadata.MetadataRecord#checkConstraints(ConstraintTicket)
		 */
		META_NO_TITLE,
		

		/**
		 * No language was set in the package metadata.
		 * 
		 * This violation is automatically resolved by adding
		 * a dummy language entry ("NONE").
		 * 
		 * @see org.esciurus.model.metadata.MetadataRecord#checkConstraints(ConstraintTicket)
		 */
		META_NO_LANGUAGE,
		
		
		/**
		 * A creator or contributor entry in the package metadata
		 * contains an invalid role specification.
		 * That is, the role
		 * is neither in the list of known MARC relator codes 
		 * nor does it start with "oth.".
		 * 
		 * This violation is not automatically resolved.
		 * 
		 * The constraint is not checked if the package uses the
		 * OPFPackage.FormatModifier.DONT_CHECK_DICTIONARIES
		 * modifier flag.
		 *  
		 *  @see org.esciurus.model.metadata.DCPerson#checkConstraints(ConstraintTicket)
		 *  @see org.esciurus.model.opf.OPFPackage.FormatModifier#DONT_CHECK_DICTIONARIES
		 */
		META_BAD_ROLE,

		/**
		 * A guide entry contains an invalid type specification,
		 * or no type at all.
		 * A type is invalid 
		 * if it is neither in the list of known reference type codes,
		 * defined by the OPF specification, 
		 * nor starts with "other.".
		 * 
		 * This violation is not automatically resolved.
		 * 
		 * The constraint is only partially checked if the package uses the
		 * OPFPackage.FormatModifier.DONT_CHECK_DICTIONARIES
		 * modifier flag. Namely, in this case, it is only checked
		 * whether the type field of all guide references is different from <code>null</code>.
		 *  
		 *  @see org.esciurus.model.opf.GuideReference#checkConstraints(ConstraintTicket)
		 *  @see org.esciurus.model.opf.OPFPackage.FormatModifier#DONT_CHECK_DICTIONARIES
		 */
		GUIDE_BAD_TYPE,
		
		/**
		 * A guide entry contains an invalid href specification,
		 * or no href at all.
		 * 
		 * A href is invalid if it points to a file that 
		 * is not found in the package manifest.
		 * 
		 * This violation is not automatically resolved.
		 * 
		 *  @see org.esciurus.model.opf.GuideReference#checkConstraints(ConstraintTicket)
		 */
		GUIDE_BAD_HREF,
		
		/**
		 * The spine of the package does not contain a primary entry 
		 * (with <code>linear="yes"</code> set). It may either be empty, or contain
		 * only auxiliary content (<code>linear="no"</code>).
		 * 
		 * This violation is not automatically resolved.
		 * 
		 *  @see org.esciurus.model.opf.Spine#checkConstraints(ConstraintTicket)
		 */
		SPINE_NO_PRIMARY,

		/**
		 * The spine contains an invalid TOC entry.
		 * 
		 * The TOC entry is invalid if it has ot been set, 
		 * does not point to a valid entry of the manifest, 
		 * that entry is of an incorrect media type,
		 * or that entry specifies a fallback.
		 * 
		 * This violation is not automatically resolved.
		 * 
		 *  @see org.esciurus.model.opf.Spine#checkConstraints(ConstraintTicket)
		 */
		SPINE_BAD_TOC,

				
		/**
		 * A manifest entry, referenced from the spine,
		 * needs to provide fallback information, but none was specified.
		 * 
		 * <p>
		 * By the OPF specification, only the following types of files
		 * are allowed in the spine:
		 * <ul>
		 * <li><code>application/xhtml+xml</code></li>, 
		 * <li><code>application/x-dtbook+xml</code></li>, 
		 * <li><code>text/x-eob1-document</code></li>,
		 * </ul> 
		 * and Out-Of-Line XML Islands.
		 * Manifest entries referenced in the spince
		 * must either match one of these media types, 
		 * or provide a fallback mechanism that includes one of these. 
		 * This can be done either via the fallback field
		 * or, as an Out-of-Line XML Island, via the fallback-style field. 
		 * </p>
		 * This violation is not automatically resolved.
		 * 
		 * The constraint is not checked if the package uses the
		 * OPFPackage.FormatModifier.DONT_REQUIRE_CORETYPES
		 * modifier flag.
		 *  
		 *  @see org.esciurus.model.opf.SpineEntry#checkConstraints(ConstraintTicket)
		 *  @see org.esciurus.model.opf.OPFPackage.FormatModifier#DONT_REQUIRE_CORETYPES
		 */
		SPINE_MISSING_FALLBACK,
		
		/**
		 * No title was set for a certain tour.
		 * 
		 * This violation is automatically resolved by setting
		 * a dummy title ("untitled").
		 * 
		 * @see org.esciurus.model.opf.Tour#checkConstraints(ConstraintTicket)
		 */
		TOUR_NO_TITLE,

		/**
		 * No title was set for a certain tour site.
		 * 
		 * This violation is automatically resolved by setting
		 * a dummy title ("untitled").
		 * 
		 * @see org.esciurus.model.opf.TourSite#checkConstraints(ConstraintTicket)
		 */
		TOURSITE_NO_TITLE,
		
		/**
		 * A tour site contains an invalid href specification,
		 * or no href at all.
		 * 
		 * A href is invalid if it points to a file that 
		 * is not found in the package manifest.
		 * 
		 * This violation is not automatically resolved.
		 * 
		 *  @see org.esciurus.model.opf.TourSite#checkConstraints(ConstraintTicket)
		 */
		TOURSITE_BAD_HREF		
	}
	
	private Type type;
	private String target;
	private boolean resolved;
	
	
	/**
	 * Create a new constraint violation object of a specified type.
	 * 
	 * @param type the type of this contraint violation
	 * @param target a textual description of the contraint violation 
	 * @param resolved true if the constraint violation has automatically been resolved
	 */
	public OPFConstraintViolation(Type type, String target, boolean resolved) {
		super();
		this.type = type;
		this.target = target;
		this.resolved = resolved;
	}
	
	@Override
	public String getText() {
		// TODO add better display values
		return type.toString() + " - " + target;
	}

	/* (non-Javadoc)
	 * @see org.esciurus.model.ocf.ConstraintViolation#wasResolved()
	 */
	@Override
	public boolean wasResolved() {
		return resolved;
	}

	/**
	 * Retrieve the type of this contraint violation, as a code.
	 * 
	 * @return the type of this contraint violation
	 */
	public Type getType() {
		return type;
	}

	
}
