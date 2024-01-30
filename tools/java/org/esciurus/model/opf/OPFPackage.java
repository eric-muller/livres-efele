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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.esciurus.common.FileUtility;
import org.esciurus.common.XmlUtility;
import org.esciurus.model.metadata.DCIdentifier;
import org.esciurus.model.metadata.MetadataRecord;
import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.ocf.ContainerDataException;
import org.esciurus.model.ocf.ContainerFormatException;
import org.esciurus.model.ocf.ContainerPart;
import org.esciurus.model.ocf.FileInfo;
import org.esciurus.model.ocf.FileSyntaxException;
import org.esciurus.model.ocf.OutputDescriptor;
import org.esciurus.model.ocf.Container.OpenMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Represents an OPF package.
 * 
 * <p>This class implements the main functionality of 
 * OPF packages. It is usually used in the context of Epub containers,
 * where it technically represents a <code>ContainerPart</code>.
 * </p>
 *   
 */
public class OPFPackage implements ContainerPart {

	/**
	 * This Enum lists possible deviations from the OPF 2.0 specification
	 * that can be allowed on request.
	 */
	public enum FormatModifier {
		
		/**
		 * Don't require that fallback items, spine entries, etc.
		 * are of <em>OPF core types</em> as defined in the OPF specification. 
		 */
		DONT_REQUIRE_CORETYPES,
		
		/**
		 * Do not check values of metadata fields against vocabulary in dictionaries.
		 */
		DONT_CHECK_DICTIONARIES
	}

	
	private File   fsBasePath;
	private String containerBasePath;
	private String opfFileName;
	
	private boolean standaloneMode;
	private EnumSet<FormatModifier> formatModifiers;
	
	
	private MetadataRecord metadata;
	private OPFMainList<ManifestEntry> manifest;
	private Spine spine;
	private OPFMainList<Tour> tours;
	private OPFMainList<GuideReference> guide;
	

	private XrefManager xrefManager;

	private class UniqueIdPtr extends XrefPointer<DCIdentifier> {

		@Override
		public DCIdentifier getTargetByPersistenceId(String persistenceId) {
			return getMetadata().getIdentifierById(persistenceId);
		}
		
	}
	
	private UniqueIdPtr uniqueIdPtr;

	
	/**
	 * Create an empty OPF package on the specified base path of the file system,
	 * wothout relation to an enclosing Epub container. 
	 * If so specified, the package will be in "standalone" mode; that is,
	 * only the package file is loaded from or saved to the file system, 
	 * and the package's content files will be disregarded. 
	 * @param fsBasePath the base path of the OPF content on the file system
	 * @param opfFileName the file name of the OPF package file
	 * @param standalone true if the package should be in standalone mode
	 */
	public OPFPackage(File fsBasePath, String opfFileName, boolean standalone) {
		this(fsBasePath,null,opfFileName,standalone);
	}
	
	
	/**
	 * Create an empty OPF package on the specified base path of the file system,
	 * as part of an Epub container. 
	 * If so specified, the package will be in "standalone" mode; that is,
	 * only the package file is loaded from or saved to the file system, 
	 * and the package's content files will be disregarded. 
	 *  
	 * @param fsBasePath the base path of the OPF content on the file system
	 * @param containerBasePath the base path of the OPF content within an Epub container, 
	 * relative to the container root 
	 * @param opfFileName the file name of the OPF package file
	 * @param standalone true if the package should be in standalone mode
	 */
	public OPFPackage(File fsBasePath, String containerBasePath, String opfFileName, boolean standalone) {
		
		this.fsBasePath = fsBasePath;
		this.containerBasePath = containerBasePath;
		this.opfFileName = opfFileName;
		
		this.xrefManager = new XrefManager();

		this.standaloneMode = standalone;
		this.formatModifiers = EnumSet.noneOf(FormatModifier.class);
		
		metadata = new MetadataRecord(this);
		manifest = new OPFMainList<ManifestEntry>(this,manifestTag,itemTag,ManifestEntry.getEntryFactory());
		spine = new Spine(this);
		tours = new OPFMainList<Tour>(this,toursTag,tourTag,Tour.getTourFactory());
		guide = new OPFMainList<GuideReference>(this,guideTag,referenceTag,GuideReference.getEntryFactory());
		
		uniqueIdPtr = new UniqueIdPtr();
	}
	
	
	/**
	 * Get a file system reference for a file in the package.
	 * 
	 * @param href the path of the file, relative to the package root
	 * @return a file system reference to the file
	 */
	protected File getFsReference(String href) {
		File result;
		if (fsBasePath.getName().equals("")) {
			result = new File(href);
		}
		else {
			result = new File(fsBasePath,href);
		}
		return result;
	}
	
	
	/**
	 * Get a FileInfo reference for a file in the package.
	 * This supposes that the OPF package is part of an Epub container. 
	 * 
	 * @param href the path to the file, relative to the package root
	 * @param mediaType the media type of the file
	 * @return a reference to the file within the container 
	 * @throws FileSyntaxException if the href parameter does not comply with file naming standards
	 */
	protected FileInfo getContainerReference(String href,String mediaType) throws FileSyntaxException {
		return new FileInfo(containerBasePath+href,mediaType);
	}
	
	
	/**
	 * Return the format modifiers for this package.
	 * This implementation allows, on request, certain deviations 
	 * from the OPF specification ("format modifiers"). 
	 * The method returns the format modifiers that have been specified 
	 * for this OPF package; the default value is the empty set.
	 * 
	 * @return the format modifiers
	 */
	public EnumSet<FormatModifier> getFormatModifiers() {
		return formatModifiers;
	}

	/**
	 * Set the format modifiers for this OPF package.
	 * 
	 * @param formatModifiers the format modifiers to set
	 * @see #getFormatModifiers()
	 */
	public void setFormatModifiers(EnumSet<FormatModifier> formatModifiers) {
		this.formatModifiers = formatModifiers;
	}

	
	/**
	 * Retrieve the metadata record (all metadata fields combined) for this OPF package.
	 * 
	 * @return the metadata record
	 */
	public MetadataRecord getMetadata() {
		return metadata;
	}
	
	

	/******* Access to manifest / adding and removing files to package ***********/

	/**
	 * Retrieve the manifest of the package, as a read-only list.
	 * (Any write operations to the list will result in an
	 * OperationNotSupportedException.)
	 * 
	 * @return the manifest of the package
	 */
	public List<ManifestEntry> getManifest() {
		return Collections.unmodifiableList(manifest);
	}

	/**
	 * Retrieve an iterator for the manifest of the package.
	 * 
	 * @return the iterator object
	 * @deprecated Use getManifest().iterator() instead.
	 */
        @Deprecated
	public Iterator<ManifestEntry> getManifestIterator() {
		return manifest.iterator();
	}
	
	/**
	 * Retrieve a manifest entry by its textual (persisted) id value.
	 * @param id the textual id of the entry
	 * @return the manifest entry
	 */
	public ManifestEntry getManifestEntry(String id) {
		ManifestEntry result=null;
		int i = 0;
		while (i < manifest.size() && result==null) {
			ManifestEntry thisEntry = manifest.get(i);
			if (id.equals(thisEntry.getId())) {
				result = thisEntry;
			}
			i++;
		}
		return result;
	}

	/**
	 * Retrieve a manifest entry by its file name.
	 * Returns <code>null</code> if the entry was not found.
	 * The filename parameter may be <code>null</code> or the empty string,
	 * which will however always result in a <code>null</code> return value.
	 * 
	 * @param filename the name of the file to search for
	 * @return the manifest entry for this file, or <code>null</code> if none was found
	 */
	public ManifestEntry getManifestEntryByFilename (String filename) {
		ManifestEntry result=null;
		
		if (filename != null && filename.length() > 0) {
			int i = 0;
			while (i < manifest.size() && result==null) {
				ManifestEntry thisEntry = manifest.get(i);
				if (filename.equals(thisEntry.getHref())) {
					result = thisEntry;
				}
				i++;
			}
		}
		return result;
	}

	private FileInfo getManifestFileInfo(ManifestEntry mf) {
		
		FileInfo result = null;
		if (mf != null) {
			try {
				result = new FileInfo (mf.getHref(),containerBasePath,mf.getMediaType());
			} catch (FileSyntaxException e) {
				// can only occur if internal data is corrupt/invalid
				throw new RuntimeException ("internal error - invalid file name "+mf.getHref(),e);
			}
		}
		return result;

	}
	

	
	// TODO why this method? what's the replacement with XRef?
	/**
	 * Retrieve a file from the manifest, or possibly its fallback item.
	 * <p><strong>(SUBJECT TO CHANGE)</strong></p>
	 * 
	 * @param id
	 * @param acceptedTypes
	 * @param acceptXmlStyle
	 * @return the file
	 */
	public FileInfo getPackagedFileByType(String id, List<String> acceptedTypes,
						boolean acceptXmlStyle ){

		FileInfo result = null;

		ManifestEntry mfRoot = getManifestEntry(id);
		if (mfRoot != null) {
			ManifestEntry mfFall = mfRoot.getFallbackByType(acceptedTypes,acceptXmlStyle);
			
			result = getManifestFileInfo(mfFall);
		}

		return result;
		
	}

	
	/**
	 * Add a file from the file system to the package.
	 * The file will be copied to the package directory.
	 * The MIME type of the file will be determined automatically by file extension.
	 * Files cannot be added if the package is in standalone mode.
	 * 
	 * @param fileToAdd the file to add to the package (must <em>not</em> be within the package directory)
	 * @param pathInPackage relative (IRI) path in the OPF package. May be empty or <code>null</code> 
	 * (in this case, the file is added to the base directory of the OPF package).
	 * 
	 * @return the manifest entry of the added file
	 * @throws EpubDataException if I/O errors occur while adding the file, or if the package is in standalone mode 
	 */
	public ManifestEntry addPhysicalFile(File fileToAdd, String pathInPackage) throws EpubDataException {
		
		String mimetype = MimeTypes.getInstance().resolveMimeType(fileToAdd);
		
		return addPhysicalFile(fileToAdd,pathInPackage,mimetype);
		
	}

		
	
	/**
	 * Add a file from the file system to the package, with a specified MIME type.
	 * The file will be copied to the package directory.
	 * Files cannot be added if the package is in standalone mode.
	 * 
	 * @param fileToAdd the file to add to the package (must <em>not</em> be within the package directory)
	 * @param pathInPackage relative (IRI) path in the OPF package. May be empty or null (in this case, the file
	 * @param mediaType the MIME media type of the file
	 * @return the manifest entry of the added file
	 * @throws EpubDataException if I/O errors occur while adding the file, or if the package is in standalone mode 
	 */
	public ManifestEntry addPhysicalFile(File fileToAdd, String pathInPackage, String mediaType) throws EpubDataException {
	
		if (isStandaloneMode()) {
			throw new EpubDataException("cannot add physical files in standalone mode");
		}
		
		String targetHref;
		
		if (pathInPackage == null || pathInPackage.equals("")) {
			targetHref = fileToAdd.getName();
		}
		else {
			targetHref = pathInPackage+fileToAdd.getName();
			// LATER what if sep. char needs to be inserted?
		}
			
		ManifestEntry mfEntry = addManifestEntry(targetHref,mediaType);
		
		try{
			File targetFile = getFsReference(targetHref);
			targetFile.getParentFile().mkdirs();
			
			FileUtility.copyFile (fileToAdd,targetFile);
			
		}
		catch (IOException e) {
			// remove already added entry from manifest
			manifest.remove(mfEntry);
			
			throw new EpubDataException("cannot copy file "+fileToAdd.getName(),e);
		}
		
		return mfEntry;
				
	}
	
	/**
	 * Add an entire directory from the file system to the OPF package,
	 * The files in this directory will be copied to the package directory.
	 * Files cannot be added if the package is in standalone mode.
	 * 
	 * <p>
	 * Files and subdirectories will be added to the package recursively 
	 * if this is so specified. The list of files to be added can further
	 * be modified by an inclusion and an exclusion pattern, applied to the file names.
	 * The exclude pattern takes precedence if both of them match.
	 * Patterns must be given as regular expressions; 
	 * see the documentation of the java.util.regex package for 
	 * a description of regular expression syntax.
	 * </p>
	 * 
	 * @param dirToAdd the directory to add to the package
	 * @param recursive true if files and directories should be added recursively (i.e. including all subdirectories;
	 * false if no recursion to subdirectories is desired
	 * @param pathInPackage the target path (relative to the package root) for added files
	 * @param includePattern a regular expression that specifies which file names to include in copying
	 * @param excludePattern a regular expression that specifies which file names to exclude from copying
	 * @throws EpubDataException if I/O errors occur while adding the file, or if the package is in standalone mode 
	 */
	public void addPhysicalDirectory(File dirToAdd, boolean recursive, String pathInPackage, String includePattern, String excludePattern) throws EpubDataException {

		if (!dirToAdd.isDirectory()) {
			throw new EpubDataException("not a directory: "+dirToAdd.getAbsolutePath());
		}
		
		Pattern includeP = (includePattern == null) ? null : Pattern.compile(includePattern);
		Pattern excludeP = (excludePattern == null) ? null : Pattern.compile(excludePattern);
		
		try {
		
			addSubdirectory(dirToAdd,recursive,pathInPackage,includeP,excludeP);
		}
		catch(IOException e) {
			throw new EpubDataException("problem while adding files",e);
		}
	}
	
	private void addSubdirectory(File directory,boolean recursive, String pathInPackage, Pattern includeP, Pattern excludeP) throws IOException, EpubDataException {

		File[] dirContents = directory.listFiles();
		
		for (int i = 0; i < dirContents.length; ++i) {
			if (dirContents[i].isDirectory()) {
				if (recursive) {
					String newPathInPackage = pathInPackage + dirContents[i].getName()+"/";
					addSubdirectory(dirContents[i],recursive,newPathInPackage,includeP,excludeP);
				}
			} else {
				String filename = dirContents[i].getName();
				boolean addFile= true;
				if (includeP != null) {
					addFile = addFile && includeP.matcher(filename).matches();
				}
				if (excludeP != null) {
					addFile = addFile && !excludeP.matcher(filename).matches();
				}
				if (addFile){
					addPhysicalFile(dirContents[i],pathInPackage);
				}
			}
		}

	}
	
	 
	private ManifestEntry addManifestEntry(String href, String mediaType) throws EpubDataException {
		
		if (isFileInManifest(href,true)) {
			throw new EpubDataException("file exists in manifest: "+href);
		}
		
		
		ManifestEntry mf = new ManifestEntry(href,mediaType); 
		manifest.add(mf);
		return mf;
		
	}

		
	/**
	 * Remove a file from the OPF package. This will delete all references
	 * to this file e.g. in fallbacks, guides, tours, spine as well.
	 * Also, the file will be <em>deleted on the file system</em>
	 * (within the package directory)
	 * if the package is not in standalone mode. 
	 * 
	 * @param mfEntry the manifest entry for the file to remove
	 */
	public void removeFile(ManifestEntry mfEntry) {
		
		String hrefToRemove = mfEntry.getHref();
		
		if (!isStandaloneMode()) {
			File fileToRemove = new File(fsBasePath,mfEntry.getHref());
			fileToRemove.delete();
		}
		
		
		//remove from spine
		
		for (Iterator<SpineEntry> it = spine.iterator(); it.hasNext();) {
			if (it.next().getRef() == mfEntry) {
				it.remove();
			}
		}
		
		
		// remove from tours
		for (Iterator<Tour> tourIt = tours.iterator(); tourIt.hasNext();) {
			
			List<TourSite> thisSiteList = tourIt.next();
			
			for (Iterator<TourSite> siteIt = thisSiteList.iterator(); siteIt.hasNext();) {
				TourSite thisSite = siteIt.next();
				if (stripFragment(thisSite.getHref()).equalsIgnoreCase(hrefToRemove)) {
					siteIt.remove();
				}
			}
			
		}
		
		// remove from guide
		for (Iterator<GuideReference> it = guide.iterator(); it.hasNext();) {
			GuideReference thisEntry = it.next();
			if (stripFragment(thisEntry.getHref()).equalsIgnoreCase(hrefToRemove)) {
				it.remove();
			}
		}
		
		// remove entry from manifest
		// This will also delete any remaining cross-references, e.g. fallbacks
		manifest.remove(mfEntry);
			
	}

	
	/**
	 * Strip the fragment identifier of a href string, if present.
	 * 
	 * @param href the href string to consider
	 * @return the same href, without the fragment identifier
	 */
	public static String stripFragment(String href) {
		String result = href;
		int fragmentPos = href.indexOf('#');
		if (fragmentPos>0) {
			result = href.substring(0,fragmentPos);
		}
		return result;
	}

	
	/**
	 * Test whether a file is contained in the package.
	 * 
	 * @param href the relative path (IRI) of the file, relative to the package root
	 * @return true if the file is contained in the package, false if not
	 */
	public boolean isFileInManifest (String href) {
		return isFileInManifest(href,false);
	}
	
	/**
	 * Test whether a file is contained in the package. 
	 * The file name comparison is case-insensitive.
	 * 
	 * @param href the relative path (IRI) of the file, relative to the package root
	 * @return true if the file is contained in the package, false if not
	 */
	private boolean isFileInManifest(String href, boolean ignoreCase) {
		
		String searchfile = stripFragment(href);
		
		boolean found = false;
		Iterator<ManifestEntry> it = manifest.iterator();
		while (it.hasNext() && !found) {
			String thisHref = it.next().getHref();
			if (ignoreCase) {
				found = searchfile.equalsIgnoreCase(thisHref);
			}
			else {
				found = searchfile.equals(thisHref);
			}
		}
		
		return found;
	}

	/******* Access to tours ***********/

	/**
	 * Return the list of tours contained in this OPF package.
	 * @return the tours object
	 */
	public List<Tour> getTours() {
		return tours;
	}
	
	/******* Access to guide ***********/
	
	/**
	 * Retrieve the guide section of this OPF package.
	 * @return the guide (list of guide references)
	 */
	public List<GuideReference> getGuide() {
		return guide;
	}


	/******* Access to spine ***********/

		
	/**
	 * Retrieve the spine of this OPF package.
	 * @return the spine object
	 */
	public Spine getSpine() {
		return spine;
	}
	
	
	/* *********** consistency checking ************ */
	
	
	/* (non-Javadoc)
	 * @see org.esciurus.model.ocf.ContainerPart#checkConstraints(org.esciurus.model.ocf.ConstraintTicket)
	 */
	public void checkConstraints (ConstraintTicket ticket) {
		
		checkPackageConstraints(ticket);
		checkManifestConstraints(ticket);
		
		manifest.checkConstraints(ticket);
		metadata.checkConstraints(ticket);
		spine.checkConstraints(ticket);
		tours.checkConstraints(ticket);
		guide.checkConstraints(ticket);
	}
	
	private void checkPackageConstraints(ConstraintTicket ticket) {
		
		// check whether package-unique-id is set
		if (getUniqueId() == null) {
			boolean resolve = ticket.isTryResolve();
			
			if (resolve){
				metadata.generateUniqueId(true);
			}
			
			ticket.addViolation( new OPFConstraintViolation (
					OPFConstraintViolation.Type.PACKAGE_NO_UNIQUEID,
					null,
					resolve
			));
		}
	}
	
	
	private void checkManifestConstraints (ConstraintTicket ticket)  {
		
		// check whether manifest is empty
		
		if (manifest.size()==0) {
			ticket.addViolation( new OPFConstraintViolation(
					OPFConstraintViolation.Type.MANIFEST_EMPTY,
					null,
					false
			));
			
		}
		
		// check whether files are physically present
		
		if (!isStandaloneMode()) {
			for (Iterator<ManifestEntry> it = manifest.iterator(); it.hasNext();) {
				
				File fileToCheck = getFsReference(it.next().getHref());
				
				if (!fileToCheck.exists() || !fileToCheck.canRead() ) {
					
					ticket.addViolation( new OPFConstraintViolation(
							OPFConstraintViolation.Type.MANIFEST_FILE_MISSING,
							fileToCheck.getAbsolutePath(),
							false
					));
				}
				
			}
		}
		
		
		// test for cyclic fallback chains 
		
		List<ManifestEntry> visitedRoots = new Vector<ManifestEntry>();
		List<ManifestEntry> visitedFallbacks = new Vector<ManifestEntry>();
		
		for (Iterator<ManifestEntry> rootIt = manifest.iterator(); rootIt.hasNext();) {
			
			ManifestEntry checkFile = rootIt.next(); 
			if (!visitedRoots.contains(checkFile)) {
				
				visitedFallbacks.clear();
				
				ManifestEntry nextFallback;
				do {
					nextFallback = checkFile.getFallback();
					
					if (nextFallback != null) {
						
						visitedRoots.add(checkFile);
						visitedFallbacks.add(checkFile);
						
						if (visitedFallbacks.contains(nextFallback)) {
							
							ticket.addViolation( new OPFConstraintViolation(
									OPFConstraintViolation.Type.MANIFEST_CYCLIC_FALLBACK,
									checkFile.getId(),
									false
							));
							nextFallback = null;
							
						}
						
						checkFile = nextFallback;
					}
					
				} while (nextFallback != null);
			}
		}
		
	}
	
	/************** File input / output ************/
	
	
	private static String outputXmlVersion="1.1";
	
	private static String packageNamespace="http://www.idpf.org/2007/opf";
		
	private static String packageTag = "package";
	private static String uniqueIdAttr = "unique-identifier";
	private static String versionAttr = "version";
	private static String outputPackageVersion="2.0";
	
	private static String manifestTag = "manifest";
	private static String itemTag = "item";
		
	private static String toursTag = "tours";
	private static String tourTag = "tour";

	private static String guideTag = "guide";
	private static String referenceTag = "reference";
	
	
	
	/**
	 * Write the OPF package file (the XML package descriptor, not the files contained in the package) 
	 * to a stream.
	 * @param stream the stream to write to
	 * @throws EpubDataException if a data inconsitency or file I/O error is detected during output
	 */
	public void writeOpfToStream ( OutputStream stream ) throws EpubDataException {
		
		XmlUtility xmlu = XmlUtility.getInstance();
		
		Document doc = xmlu.createEmptyDocument();
		doc.setXmlVersion(outputXmlVersion);
		
		this.writeToXml(doc);
				
		try {
			xmlu.writeXml(doc,stream,null,null);
		} catch (TransformerException e) {
			throw new EpubDataException ("error while writing XML representation of OPF file",e);
		}
		
		
	}
	

	/**
	 * Read the OPF package file (i.e., the XML package descriptor) from a stream.
	 * 
	 * @param stream the stream to read from
	 * @throws EpubFormatException if the input file violates the OPF specification 
	 * (not all deviations from the spec are detected, though)
	 * @throws IOException if an error in file I/O occurs.
	 */
	public void readOpfFromStream ( InputStream stream ) throws EpubFormatException, IOException  {
				
		XmlUtility xmlu = XmlUtility.getInstance();
	
		// read document from file
		
		Document inputDoc;
		try {
			inputDoc = xmlu.readXml(stream);
		} catch (SAXException e) {
			throw new EpubFormatException ("error while parsing OPF file",e);
		}
		
		// fill object structure from DOM tree
		
		readFromXmlDocument(inputDoc);
		
	}
	

	/**
	 * Create a new OPF package from a stream, in standalone mode.
	 * That is, the OPF package file is read from the stream after creation
	 * of the package object.
	 *  
	 * @param stream the stream to read from
	 * @return the newly created OPF package object 
	 * @throws EpubFormatException if the input file violates the OPF specification 
	 * (not all deviations from the spec are detected, though)
	 * @throws IOException if an error in file I/O occurs.
	 */
	public static OPFPackage createFromStreamStandalone (InputStream stream) throws EpubFormatException, IOException {
		
		OPFPackage pack = new OPFPackage(null,null,true);
		pack.readOpfFromStream(stream);
		return pack;
	}

	/**
	 * Create a new OPF package from a stream, with a given base directory.
	 * That is, the OPF package file is read from the stream after creation
	 * of the package object. The package will <em>not</em> be 
	 * in standalone mode.
	 *  
	 * @param stream the stream to read from
	 * @param basePath the base path for the package content files
	 * @return the newly created OPF package object 
	 * @throws EpubFormatException if the input file violates the OPF specification 
	 * (not all deviations from the spec are detected, though)
	 * @throws IOException if an error in file I/O occurs.
	 */
	public static OPFPackage createFromStream (InputStream stream, String basePath) throws EpubFormatException, IOException {
		
		OPFPackage pack = new OPFPackage(new File(basePath),null,false);
		pack.readOpfFromStream(stream);
		return pack;
	}
	

	/**
	 * Write the contents of the OPF package structure to an XML representation.
	 * @param doc the XML document to write to
	 */
	public void writeToXml(Document doc) {

		
		// create root element
		
		Element packageElm = doc.createElementNS(packageNamespace,packageTag);
		doc.appendChild(packageElm);
		
		PackagePart.writePointer(packageElm,uniqueIdAttr,uniqueIdPtr);
		packageElm.setAttribute(versionAttr,outputPackageVersion);
		
		// add subelements to packageElm
		
		metadata.writeToXml(packageElm);
		manifest.writeToXml(packageElm);
		spine.writeToXml(packageElm);
		tours.writeToXml(packageElm);
		guide.writeToXml(packageElm);
		
	}

	
	private void readFromXmlDocument(Document doc) throws EpubFormatException {
		
		// find root element
		Element packageElm = doc.getDocumentElement();
		
		// read attributes
		PackagePart.readPointer(packageElm,uniqueIdAttr,uniqueIdPtr,getXrefManager());
		
		metadata.readFromXml(packageElm);
		manifest.readFromXml(packageElm);
		spine.readFromXml(packageElm);
		tours.readFromXml(packageElm);
		guide.readFromXml(packageElm);

		xrefManager.resolvePrenotedLinks();
	}


	

	public FileInfo getRootfile() {
		FileInfo result = null;
		try{
			result = new FileInfo(containerBasePath+opfFileName,MimeTypes.OPF_PACKAGE_TYPE);
		}
		catch (FileSyntaxException e) {
			throw new RuntimeException ("internal error - invalid root file name",e);
		}
		return result;
	}


	public void loadRootfile(InputStream stream, OpenMode openMode) throws IOException, ContainerFormatException {
		
		standaloneMode = (openMode == OpenMode.PREVIEW);
		readOpfFromStream(stream);
		
	}


	public void save(OutputDescriptor outputDesc) throws IOException, ContainerDataException {
		
		try{
			outputDesc.markDirty( new FileInfo(containerBasePath,"") );
			
			outputDesc.beginFile(getRootfile());
			writeOpfToStream( outputDesc.getOutputStream() );
			outputDesc.endFile();
			
			for (Iterator<ManifestEntry> it = manifest.iterator(); it.hasNext();) {
				ManifestEntry mf = it.next();
				FileInfo fi = new FileInfo(containerBasePath+mf.getHref(),mf.getMediaType());
				outputDesc.addFileFromFilesystem( fi );
			}
		}
		catch (FileSyntaxException e) {
			throw new EpubDataException("incorrect file name syntax",e);
		}
		
	}

	/**
	 * Retrieve the unique identifier of the package, as a Metadata entry.
	 * @return Returns the uniqueid  metadata entry. May be <code>null</code> if not unique id has been set. 
	 */
	public DCIdentifier getUniqueId() {
		return uniqueIdPtr.getTarget();
	}

	
	/**
	 * Retrieve the textual value of the unique identifier of the package.
	 * @return the textual value of the unique id, or the empty string if no unique id is set. 
	 */
	public String getUniqueIdValue() {
		
		String result = "";
		
		if (getUniqueId() != null) {
			result = getUniqueId().getValue();
		}
		
		return result;
	}

	
	/**
	 * Set the unique id for this package
	 * @param uniqueId the new unique id, as a metadata entry
	 */
	public void setUniqueId(DCIdentifier uniqueId) {
		uniqueIdPtr.setTarget(uniqueId);
	}

	/**
	 * @return Returns the xrefManager.
	 */
	public XrefManager getXrefManager() {
		return xrefManager;
	}

	/**
	 * @return Returns the standaloneMode.
	 */
	public boolean isStandaloneMode() {
		return standaloneMode;
	}

	
}
