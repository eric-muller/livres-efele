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

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * A factory for container objects. This class
 * takes care of loading container objects from files,
 * or creating empty containers from scratch.
 * 
 * <p>Implementations of specific OCF formats
 * need to extend this class in order to math it with
 * the specific implementation of <code>Container</code>.
 * 
 * <p><em>Pattern:</em>Abstract Factory</p>
 * 
 * @param <C> the class of container objects that this factory produces
 */
public abstract class ContainerFactory <C extends Container> {

	private String tempDir;
	
	/**
	 * Create a new container factory.
	 */
	public ContainerFactory() {
		
		tempDir = System.getProperty("java.io.tmpdir");
	}
		
	
	/**
	 * Set the temporary directory that all Container objects
	 * created by this factory will use.
	 * 
	 * <p>
	 * The temporary directory is needed for extracting 
	 * data files from containers, and for temporarily storing these
	 * before packaging.</p>
	 * 
	 * <p><em>Note:</em> For each container, a separate subdirectory will
	 * be created in the directory specified here.</p>
	 * 
	 * @param tempDir the temporary directory for container data 
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}
	
	
	
	private File generateContainerTempDir () {
		
		UUID uuid = UUID.randomUUID();
		File containerDir = new File(tempDir, "ocf-"+uuid.toString());
				
		return containerDir;
	}

	
	/**
	 * Instantiate a new container and load its contents from a file.
	 * 
	 * @param file the file to load the container from
	 * @param mode the open mode of the container
	 * @return the newly created and loaded container
	 * 
	 * @throws IOException if an error in file I/O occurs
	 * @throws ContainerException if format errors are encountered while loading the container
	 */
	public C createContainerFromFile (File file, Container.OpenMode mode) throws IOException, ContainerException {
		
		C newContainer = getContainerInstance(generateContainerTempDir());
		newContainer.load(file,mode);
		
		return newContainer;
	}
	

	/**
	 * Create a new container object.
	 * The container will be initialized with default values and structure.
	 * It will always be instantiated in EXPANDED mode. 
	 * 
	 * @return the newly created container
	 * @see #createContainerFromFile(File, Container.OpenMode)
	 * @see Container.OpenMode#EXPANDED
	 */
	public C createEmptyContainer () {

		C newContainer = getContainerInstance(generateContainerTempDir());
		try {
			newContainer.initializeEmptyContainer();
		} catch (ContainerException e) {
			// can't possibly happen - container has not been initialized before
			throw new RuntimeException("internal error",e);
		}
		return newContainer;
	}
	


	/**
	 * Create a new instance of the container class.
	 * Subclasses must implement this method in order to
	 * provide containers of their specific class.
	 * 
	 * <p>
	 * This method does not create any specific data within the
	 * container. This must be done later, by either adding
	 * loading the contents from a file, or creating a default 
	 * structure.</p> 
	 * 
	 * @param tempDir the temporary directory for the container. May be null, depending on open mode. 
	 * @return the newly created container
	 * 
	 * @see Container#load(File, Container.OpenMode)
	 * @see Container#initializeEmptyContainer()
	 */
	protected abstract C getContainerInstance(File tempDir);

}
