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

import org.esciurus.model.ocf.Container;
import org.esciurus.model.ocf.ContainerException;
import org.esciurus.model.ocf.ContainerPart;
import org.esciurus.model.ocf.FileInfo;


/**
 * Represents an Epub container (OCF+OPF).
 * 
 * <p>This implementation of <code>Container</code>
 * is suited for OCF containers with OPF packages as their content.
 * The container contains exactly one <code>ContainerPart</code>,
 * which represents the OPF content</p>
 *
 * @see OPFPackage
 */
public class EpubContainer extends Container {

	private static String opfStandardDir="OEBPS";
	private static String opfStandardFilename="package.opf";
	
	private OPFPackage opfPackage;
	
	/**
	 * Create a new EpubContainer with the specified base directory.
	 *  
	 * @param baseDir the base directory (temporary directory) to be 
	 * used for expansion of the container. 
	 * May be null if the container is to be loaded from a file in PREVIEW mode.
	 */
	public EpubContainer(File baseDir) {
		super(baseDir);
	}

	@Override
	protected void createDefaultStructure()  {
		OPFPackage cp = new OPFPackage(new File(getBaseDir(),opfStandardDir),opfStandardDir+"/",opfStandardFilename,false);
		opfPackage = cp;
		this.addContainerPart(cp);		
	}

	
	/* (non-Javadoc)
	 * @see org.esciurus.model.ocf.Container#getOutputMediaType()
	 */
	@Override
	protected String getOutputMediaType() {
		return MimeTypes.EPUB_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.esciurus.model.ocf.Container#createContainerPart(org.esciurus.model.ocf.FileInfo)
	 */
	@Override
	protected ContainerPart createContainerPart(FileInfo rootfile)
			throws ContainerException {

		ContainerPart newCP = null;
		if (rootfile.getMediaType().equals(MimeTypes.OPF_PACKAGE_TYPE)) {
			
			String basedirInContainer = rootfile.getDirectoryPart();
			File basedirOnFilesystem = new File(getBaseDir(),basedirInContainer);
			String filename = rootfile.getFilenamePart();
			
			opfPackage = new OPFPackage(basedirOnFilesystem,basedirInContainer,filename,false);
			newCP = opfPackage;
		}
		else {
			// generate default container part, e.g. for alternate PDF renditions
			newCP = super.createContainerPart(rootfile);
		}
		return newCP;
	}
	
	
	/**
	 * Retrieve the OPF package associated with this container.
	 * 
	 * @return the OPF package
	 */
	public OPFPackage getOPFPackage() {
		
		return this.opfPackage;
	}



}
