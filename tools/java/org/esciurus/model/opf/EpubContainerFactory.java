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

import java.io.File;
import java.io.IOException;

import org.esciurus.model.ocf.ContainerException;
import org.esciurus.model.ocf.ContainerFactory;
import org.esciurus.model.ocf.Container.OpenMode;


/**
 * A concrete factory for epub containers. 
 *
 */
public class EpubContainerFactory extends ContainerFactory<EpubContainer> {

	/**
	 * Create a new EpubContainerFactory.
	 */
	public EpubContainerFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.esciurus.model.ocf.ContainerFactory#getContainerInstance(java.io.File)
	 */
	@Override
	protected EpubContainer getContainerInstance(File tempDir) {
		
		return new EpubContainer(tempDir);
	}

	/* (non-Javadoc)
	 * @see org.esciurus.model.ocf.ContainerFactory#createContainerFromFile(java.io.File, org.esciurus.model.ocf.Container.OpenMode)
	 */
	@Override
	public EpubContainer createContainerFromFile(File file, OpenMode mode) throws IOException, ContainerException {
		EpubContainer c = super.createContainerFromFile(file, mode);
		
		if (c.getOPFPackage() == null) {
			throw new EpubFormatException("no OPF package found in container");
		}
		
		return c;
	}
	
	

}
