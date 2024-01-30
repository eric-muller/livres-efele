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

package org.esciurus.model.ocf;

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface describes the operations necessary for
 * every container part in a container file.
 * <p>
 * A <em>container part</em> it the part of an OCF package
 * corresponding to <em>one</em> rootfile and its associated 
 * auxiliary files. E.g., for an OPF publication, the container
 * part corresponds to the set of files in the OEBPS/
 * directory, including the OPF file and all content files.
 * </p>
 * <p>
 * Every container part implementation must implement
 * this interface in <em>one</em> class. This class is then
 * responsible for load and save operations of all contents
 * of the container part.
 * </p>
 * 
 */
public interface ContainerPart {

	/**
	 * Retrieve information about the rootfile of this container part.
	 * This includes the file name of the root file (together with its
	 * path relative to the package root) and the media type of the file.
	 * <p>
	 * This method is called during the save operation of a container
	 * </p>
	 *  
	 * @return The file name and media type of the rootfile, 
	 * encapsulated in a FileInfo object.
	 */
	public abstract FileInfo getRootfile ();
	
	
	/**
	 * Load the rootfile of this package part to an in-memory representation.
	 * <p>
	 * This method is called during the load process of a container.
	 * Environment conditions differ according to the "open mode" of
	 * the container:
	 * </p>
	 * <ul>
	 * <li>In EXPANDED mode, the container part may assume that all additional
	 * files of the container, e.g. supplementary files in the main directory
	 * of this container part, are present on the file system. 
	 * They are therefore available for loading by this method.</li>
	 * 
	 * <li> In PREVIEW mode, only the rootfile itself is available for loading.
	 * It is availyble via the input stream passed to this method.</li>
	 *</ul> 
	 * 
	 * @see Container.OpenMode
	 * 
	 * @param stream the input stream from which the rootfile will be loaded
	 * @param openMode the opening mode of the container
	 * 
	 * @throws IOException if an I/O error results from an operation on the input stream 
	 * @throws ContainerFormatException if data format problems are encountered while reading the input data
	 */
	public abstract void loadRootfile (InputStream stream, Container.OpenMode openMode) throws IOException, ContainerFormatException;
	
	
	/**
	 * Save this container part from in-memory representation 
	 * to a file (i.e. to a ZIP container).
	 * This method will be called (for every container part) during
	 * the save process of a container. 
	 * 
	 * <p>
	 *  Within this method, the container part needs to save both the root
	 *  file and any additional files via the OutputDescriptor obtained
	 *  in the method parameter. This also includes marking files as "dirty"
	 *  that should not be kept from the previous version of the container.
	 *  See the <code>OutputDescriptor</code> interface for details.  
	 * </p>
	 * 
	 * @param outputDesc the output decsriptor to be used for save operations
	 * 
	 * @throws IOException if a stream I/O problem occurs during the save process
	 * @throws ContainerDataException if any data format problem occurs 
	 * (e.g., incomplete or inconsistent data in the in-memory representation 
	 * of the container part) 
	 */
	public abstract void save (OutputDescriptor outputDesc) throws IOException, ContainerDataException;

	
	/**
	 * 
	 * Check the container part's data (in-memory representation)
	 * regarding its constraints. 
	 * That is, verify whether all required fields are set,
	 * whether references are consistent, etc. 
	 * <p>
	 * The method does not return a value. Constraint violations found
	 * should be passed to the ConstraintTicket object which is passed
	 * as a parameter.
	 * </p>
	 * <p>
	 * If <code>ticket.isTryResolve()</code> is set to <tt>true</tt>,
	 * then inconsistencies will be corrected rather than reported via
	 * an exception, where possible. For example, the implementation 
	 * may decide to fill empty required field with their default value.
	 * 
	 * @param ticket a list of constraint violations found  
	 * 
	 */
	public abstract void checkConstraints (ConstraintTicket ticket);

	
}
