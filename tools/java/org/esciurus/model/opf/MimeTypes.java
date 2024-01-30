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
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.Collections;

/**
 * This class provides access to string constants
 * representing the MIME types relevant to OPF files.
 * In particular, it provides a list of all "OPS core types".
 * 
 * <p>
 * <em>Pattern:</em> Singleton
 * </p>
 * 
 * @author B. Wolterding
 *
 */
public class MimeTypes {

	/**
	 * The MIME media type for Epub files. 
	 */
	public static final String EPUB_TYPE="application/epub+zip";

	/**
	 * The MIME media type for OPF packages. 
	 */
	public static final String OPF_PACKAGE_TYPE="application/oebps-package+xml";

	/**
	 * The MIME media type for GIF raster graphics. 
	 */
	public static final String GIF_TYPE = "image/gif";

	/**
	 * The MIME media type for JPEG raster graphics. 
	 */
	public static final String JPEG_TYPE = "image/jpeg";

	/**
	 * The MIME media type for PNG raster graphics. 
	 */
	public static final String PNG_TYPE = "image/png";

	/**
	 * The MIME media type for SVG vector graphics. 
	 */
	public static final String SVG_TYPE = "image/svg+xml";

	/**
	 * The MIME media type for XHTML files. 
	 */
	public static final String XHTML_TYPE = "application/xhtml+xml";
	
	/**
	 * The MIME media type for DTBook files. 
	 */
	public static final String DTBOOK_TYPE = "application/x-dtbook+xml";
	
	/**
	 * The MIME media type for Cascading Stylesheets. 
	 */
	public static final String CSS_TYPE = "text/css";
	
	/**
	 * The MIME media type for generic XML data. 
	 */
	public static final String XML_TYPE = "application/xml";
	
	/**
	 * The MIME media type for OEBPS 1.2 content documents. 
	 */
	public static final String OEB1_DOC_TYPE = "text/x-oeb1-document";
	
	/**
	 * The MIME media type for OEBPS 1.2 stylsheets. 
	 */
	public static final String OEB1_CSS_TYPE = "text/x-oeb1-css";
	
	/**
	 * The MIME media type for NCX (Navigation Center Extended) files. 
	 */
	public static final String NCX_TYPE = "application/x-dtbncx+xml";
		
	/**
	 * The MIME media type for generic binary data, used as default when 
	 * no other type applies. 
	 */
	public static final String DEFAULT_TYPE = "application/octet-stream";
	
	
	private static final String fileExtensionPropertyFile="FileExtensionsToMime.properties";
	
	private List<String> opfCoreTypes;
	private List<String> opfCoreTypesWithNcx;
	private List<String> opfContentTypes;
	private Properties fileExtensionMap;
	
	private static MimeTypes singleInstance = null;
	
	/**
	 * Get the single instance of the MimeTypes singleton.
	 * @return an instance of MimeTypes
	 */
	public static MimeTypes getInstance() {
		
		if (singleInstance == null) {
			singleInstance = new MimeTypes();
		}
		
		return singleInstance;
	}
	
	private MimeTypes() {
		
		Vector<String> typeList = new Vector<String>();

		typeList.add(XHTML_TYPE);
		typeList.add(DTBOOK_TYPE);
		typeList.add(OEB1_DOC_TYPE);

		opfContentTypes = Collections.unmodifiableList(typeList);
		
		typeList = new Vector<String>(typeList);

		typeList.add(GIF_TYPE);
		typeList.add(JPEG_TYPE);
		typeList.add(PNG_TYPE);
		typeList.add(SVG_TYPE);
		typeList.add(CSS_TYPE);
		typeList.add(XML_TYPE);
		typeList.add(OEB1_CSS_TYPE);
		
		opfCoreTypes = Collections.unmodifiableList(typeList);
		
		typeList = new Vector<String>(typeList);
		typeList.add(NCX_TYPE);
		
		opfCoreTypesWithNcx = Collections.unmodifiableList(typeList);
		
		fileExtensionMap = new Properties();
		try {
			fileExtensionMap.load( getClass().getResourceAsStream(fileExtensionPropertyFile) );
		} catch (IOException e) {
			throw new RuntimeException("cannot access property file "+fileExtensionPropertyFile);
		}

	}
	
	/**
	 * Retrieve a list of all OPF core types.
	 *  
	 * The NCX type is only regarded as a core type in this context if
	 * the appropriate parameter is set. 
	 * 
	 * @param includeNcx true if NCX should be included as a core type.
	 * 
	 * @return the list of core types 
	 */
	public List<String> getOpfCoreTypes (boolean includeNcx) {
		if (includeNcx) {
			return opfCoreTypesWithNcx;
		}
		else {
			return opfCoreTypes;
		}
	}

	/**
	 * Retrieve a list of all OPF content document types.
	 * 
	 * @return the list of content document types 
	 */
	public List<String> getOpfContentTypes () {
		return opfContentTypes;
	}
	

	/**
	 * Automatically determine the MIME media type of a file.
	 * This is currently done by evaulating its file extension.
	 * The file is not required to exist.
	 * 
	 * @param file the file, the MIME type of which is to be determined
	 * @return the MIME type of the file
	 */
	public String resolveMimeType (File file) {
	
		String result = DEFAULT_TYPE;

		String fileName = file.getName();
		int dotPos = fileName.lastIndexOf(".");
		
		if (dotPos >= 0) {
			String extension = fileName.substring(dotPos+1).toLowerCase();
			result = fileExtensionMap.getProperty(extension);
			
			if (result == null) result = DEFAULT_TYPE;
		}
		
		return result;
			
	}
}
