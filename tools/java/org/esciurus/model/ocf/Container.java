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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.*;

import javax.xml.transform.TransformerException;

import org.esciurus.common.FileUtility;
import org.esciurus.common.XmlUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Abstract base class for OCF containers.
 * 
 * <p>This class is the base for an OCF container; it is not specific
 *  to the actual content of this container (e.g. OPS data or similar).
 *  It only provides basic functionality for handling the ZIP file structure,
 *  the inventory, the manifest, and similar global structures.
 *  </p>
 * 
 * <p>
 * For each OCF-based file format, a subclass of <code>Container</code>
 * needs to be implemented, along with appropriate implementations
 * of <code>ContainerPart</code>, which then handle the specific content 
 * of the container.</p>
 * 
 * <p><em>Pattern:</em> Abstract product</p>
 * 
 * @see org.esciurus.model.ocf.ContainerFactory
 * @see org.esciurus.model.ocf.ContainerPart
 */
public abstract class Container {
	
	/**
	 * Opening modes that a container can be in.
	 *
	 */
	public enum OpenMode {
		/**
		 * preview mode - the ZIP contaier is not expended to the file system; only rootfiles of the container parts are accessible
		 */
		PREVIEW, 
		
		/**
		 * expanded mode - the ZIP container is expanded to a temporary directory, including all files
		 */
		EXPANDED 
	}
	

		
	private OpenMode openMode;
	
	private boolean removeTempDir = true;
	
	private File baseDir;
	private File containerFile;
	private String containerMediaType;
	
	
	private static String containerDescPath="META-INF/container.xml";
	private static String containerDescType="text/xml";
	private static String manifestPath="META-INF/manifest.xml";
	private static String manifestType="text/xml";
	private static String mimetypePath="mimetype";
	private static String mimetypeType="text/plain";
	private static String encryptionPath="META-INF/encryption.xml";
	

	/**
	 * Get the base directory of the container.
	 * Tis is only meaningful if the ZIP container has been 
	 * expanded to a temporary directory (i.e. the container is
	 * is EXPANDED mode).
	 * 
	 * @return Returns the base directory of the container;
	 * undefined if the container is in preview mode.
	 * 
	 * @see Container.OpenMode#EXPANDED
	 */
	public File getBaseDir() {
		return baseDir;
	}

	
	/**
	 * Set whether the temporary directory, created for the expansion
	 * of the ZIP container, will be removed by the cleanup() method.
	 * 
	 * <p>The default value is "true". Setting this property to "false"
	 * is recommended for debugging purposes only.</p>
	 *
	 * @param remove True if the temporary directory should be removed
	 * on clenaup (default); false if the temporary directory should not
	 * be removed.
	 */
	public void setRemoveTempDir(boolean remove) {
		this.removeTempDir = remove;
	}

	
	@SuppressWarnings("serial")
	private class FileSet extends TreeSet<FileInfo> {
				
		FileSet() {
			super();
		}
		
		void addFile(FileInfo file) throws IllegalArgumentException {
			this.add(file);
		}
		
		void addFile(String fullPath, String mediaType) throws FileSyntaxException {
			FileInfo fi = new FileInfo(fullPath,mediaType);
			this.addFile(fi);
		}
		
	}
			
	private FileSet rootfiles;
	private FileSet manifest;

	
	private class ContainerPartInfo {
		ContainerPart containerPart;
		FileInfo initialRootfile;
	}
	
	private List<ContainerPartInfo> containerParts;
	
	public List<EncryptionInfo> encryptedPieces;
	
	/**
	 * Constructor that performs basic initializations
	 * for the abstract base class.
	 * <p>
	 * Subclasses must define their own constructor
	 * (preferably of this signature or an extension of it),
	 * and <em>must</em> call this superclass constructor
	 * before doing own initializations. 
	 * </p>
	 * 
	 * @param baseDir the base directory (temporary directory) to be used
	 * for expansion of the container. May be <code>null</code> if the 
	 * container is to be loaded from a file in PREVIEW mode.
	 * 
	 * @see #load(File, OpenMode)
	 */
	protected Container(File baseDir) {
		super();
	    containerParts = new Vector<ContainerPartInfo>();
		rootfiles = new FileSet();
		manifest = new FileSet();
	    openMode = null;
	    this.baseDir = baseDir;
	    encryptedPieces = new Vector<EncryptionInfo>();
	    
	}
	
	
	
	/**
	 * Retrieve the MIME media type for this container,
	 * written to the ZIP package on output.
	 * 
	 * @return the media type (MIME) to be used for output  
	 * 
	 */
	protected abstract String getOutputMediaType();
	
	
	/**
	 * Test whether a specific MIME media type (read from an input ZIP file)
	 * is acceptable as a media type for the container.
	 * <em>Note:</em> This refers to the media type of
	 * the container file itself, such as a
	 * <code>application/epub+zip</code>, not to the media types
	 * of the individual packaged files.</p>
	 * <p>
	 * The default behaviour is to accept a media type on input
	 * if (and only if) it matches getOutputMediaType().
	 * However, subclasses may want to override this method
	 * if there are "alternate" media types, say older versions
	 * of a container format, that the implementation can also process.
	 * </p><p>
	 * This method is called whenever a container is read from a ZIP file.
	 * If the method returns false, this will be regarded as an error
	 * condition, and the file will not be loaded.
	 * </p>
	 * @param mediaType the media type (MIME) that has been read from an input file.
	 * Must not be null. 
	 * @return true if the implementation is willing to handle this media type,
	 * false if it cannot be handled.
	 */
	protected boolean isInputMediaTypeAcceptable(String mediaType)
	{
		return mediaType.equals(getOutputMediaType());
	}
	
	
	/**
	 * Create a new ContainerPart object that corresponds to a root file 
	 * found in the package descriptor. 
	 * 
	 * <p>This method should be overridden in specific implementations
	 * of Container classes.</p>
	 * 
	 * <p>
	 * The method is called on loading a package from a ZIP file.
	 * It is assumed that each root file specified in <code>container.xml</code>
	 * corresponds to exactly one ContainerPart object. The present 
	 * method is used as a factory method to generate these ContainerPart objects,
	 * where their class may depend on file name and media type of the root file.
	 * </p><p>
	 * The returned ContainerPart should be in an "empty" state,
	 * suitable for initializing it by loading from a file. If the object
	 * needs the information on root file name and media type for further use, 
	 * it should store these upon creation.
	 * </p><p>
	 * If the implementation cannot create the container part for any reason
	 * (e.g., the media type in unknown or the root file name does not meet
	 * the expected scheme), it must either call the super implementation 
	 * (which will always create a container part of type <code>DefaultContainerPart</code>),
	 * or throw an exception. It is not permissible
	 * for this method to return <code>null</code>, or to return an uninitialized or
	 * otherwise invalid container part.</p>
	 * 
	 * @param rootfile file information (path and media type) of the root file
	 * corresponding to the new container part
	 * 
	 * @return the newly created ContainerPart object; must not be <code>null</code>.
	 * 
	 * @throws ContainerException if the implementation cannot determine how to
	 * create an appropriate container part (e.g., unknown media type)
	 */
	@SuppressWarnings("unused")
	protected ContainerPart createContainerPart(FileInfo rootfile) throws ContainerException {
	
		return new DefaultContainerPart(rootfile,getBaseDir());
	}

	
	/**
	 * Add a container part to this container.
	 * 
	 * @param newPart the container part to add; must not be null.
	 */
	protected void addContainerPart(ContainerPart newPart) {
		ContainerPartInfo cpi = new ContainerPartInfo();
		cpi.containerPart = newPart;
		containerParts.add(cpi);
	}
	

	/**
	 * Fill this container with its default structure. This includes,
	 * in particular, adding container parts to the container
	 * (with their respective default content), as applicable
	 * for the specific implementation.
	 */
	protected abstract void createDefaultStructure();

	void initializeEmptyContainer() throws ContainerException {
		if (this.openMode != null) {
			throw new ContainerException("container already opened in mode "+this.openMode.toString()+", cannot initialize with defaults");
		}
		this.openMode = OpenMode.EXPANDED;
    	baseDir.mkdirs();
		createDefaultStructure();
	}
	
	
	/* 
	 * Methods for getting opening / getting references to files in the container
	 */
	
	private static String zipScheme = "zip";
	
	
	/**
	 * Get a file system reference for a file in the container.
	 * This is meaningful only in EXPANDED mode. If the container
	 * is in PREVIEW mode, the results are undefined.
	 * 
	 * @param fileInfo the file to use within the container
	 * @return a file system reference for this file
	 */
	public File getContentFile(FileInfo fileInfo) {
		return new File(getBaseDir(),fileInfo.getFilePath());
	}
	
	/**
	 * Get a URL reference for a file in the container.
	 * This will be a <code>file://</code> type reference if the container
	 * is in expanded mode, pointing to the file on the file system.
	 * If the container is in PREVIEW mode, 
	 * a <code>zip://</code> type URL will be returned,
	 * corresponding to the container file on the file system
	 * and its contents. Note that support for <code>zip://</code>
	 * URLs by external applications may vary. 
	 * 
	 * @param fileInfo the file to use within the container
	 * @return a URL reference for this file
	 */
	public URI getContentURI(FileInfo fileInfo) {
		URI result = null;
		if (openMode == OpenMode.EXPANDED) {
			result = getContentFile(fileInfo).toURI();
		}
		else if (openMode == OpenMode.PREVIEW){
			URI zipFileUri = containerFile.toURI();
			try {
				result = new URI (zipScheme, zipFileUri.getSchemeSpecificPart()+"/"+fileInfo.getFilePath(),null);
			} catch (URISyntaxException e) {
				// TODO is this always an internal error condition?
				throw new RuntimeException("error in constructing URI for "+fileInfo.getFilePath(),e); 
			}
		}
		return result;
	}

	
	/**
	 * Get an input stream for a file in the container.
	 * <p>
	 * This method will transparently work both in EXPANDED and in PREVIEW mode. 
	 * 
	 * @param fileInfo the file to use within the container
	 * @return an input stream for this file
	 * @throws IOException if any error in file I/O occurs
	 */
	public InputStream getContentStream(FileInfo fileInfo) throws IOException {
		
		InputStream result=null;
		
		if (openMode == OpenMode.EXPANDED) {
			result = new FileInputStream(getContentFile(fileInfo));
		}
		else if (openMode == OpenMode.PREVIEW){
			ZipInputStream zipStream = openInputZipFile(containerFile);
			ZipEntry zipEntry = findEntryInZip(zipStream,fileInfo.getFilePath());
			if (zipEntry != null) {
				result = zipStream;
			}
			else {
				throw new IOException("file not found in archive: "+fileInfo.getFilePath());
			}
		}
		return result;
	}

	
	private static ZipInputStream openInputZipFile(File zipFile) throws IOException {
		ZipInputStream result;
		
		InputStream is = new FileInputStream(zipFile);
		result = new ZipInputStream(is);
		
		return result;
	}
	
	private static ZipOutputStream openOutputZipFile(File zipFile) throws IOException {
		ZipOutputStream result;
		
		OutputStream os = new FileOutputStream(zipFile);
		result = new ZipOutputStream(os);
		
		return result;
	}
	
	private void loadContainerInventory() throws IOException, OCFFormatException, ContainerException {
		
		ZipInputStream zis = openInputZipFile(containerFile);
		
		// ERIC - suppress the verification of mimetype. Sometimes it's not the first entry in the zip file.
		
//		ZipEntry firstEntry = zis.getNextEntry();
//		if (!firstEntry.getName().equals(mimetypePath)) {
//			throw new OCFFormatException(OCFFormatException.MessageCode.BAD_FILE_HEADER,
//					this.containerFile,
//					"mime type entry not found at start of file; "+containerFile.getName());
//		}
//		
//		// read MIME type of container
//		containerMediaType = "";
//		int nextChar;
//		while ( (nextChar = zis.read()) != -1 ) {
//			containerMediaType += (char) nextChar;
//		}
//		zis.closeEntry();
//		containerMediaType = containerMediaType.trim ();
//		
//		if (!isInputMediaTypeAcceptable(containerMediaType)) {
//			throw new OCFFormatException(OCFFormatException.MessageCode.UNKNOWN_MEDIATYPE,
//					this.containerFile,
//					"unknown media type: "+containerMediaType);
//			
//		}

		ZipEntry cdEntry = findEntryInZip(zis,containerDescPath);
		if (cdEntry != null) {
			loadContainerDesc(zis);			
		}
		else {
			throw new OCFFormatException(OCFFormatException.MessageCode.BAD_CONTAINERDESC,
					this.containerFile,
					"container descriptor (container.xml) not found");
		}
		zis.close();
		
		zis = openInputZipFile(containerFile);
		ZipEntry manifestEntry = findEntryInZip(zis,manifestPath);
		
		if (manifestEntry != null) {
			loadManifest(zis);				
		}
		zis.close();
		
		
	}
	
	
	/************************************************************
	 * 
	 *Implementation of container descriptor file (container.xml)
	 *
	 *************************************************************/
	
	private static String containerNamespace = "urn:oasis:names:tc:opendocument:xmlns:container";
	private static String containerTag = "container";
	private static String versionAttr = "version";
	private static String containerOutputVersion = "1.0";

	private static String rootfilesTag = "rootfiles";
	private static String rootfileTag = "rootfile";
	private static String fullPathAttr = "full-path";
	private static String mediaTypeAttr = "media-type";

	
	private void loadContainerDesc(InputStream is) throws ContainerFormatException, IOException {
		
		loadFileList( rootfiles, is, containerNamespace, rootfileTag, fullPathAttr,
				mediaTypeAttr,
				OCFFormatException.MessageCode.BAD_CONTAINERDESC, "container descriptor (container.xml)");
		
		
	}

	private void writeContainerDesc(OutputDescriptor outDesc) throws TransformerException, IOException, FileSyntaxException, ContainerDataException {
		
		rootfiles.clear();
		for (Iterator<ContainerPartInfo> it = containerParts.iterator();it.hasNext();) {
			FileInfo thisRootfile = it.next().containerPart.getRootfile();
			rootfiles.addFile(thisRootfile);
		}
						
		Document doc = XmlUtility.getInstance().createEmptyDocument();
		
		Element containerElm = doc.createElementNS(containerNamespace,containerTag);
		doc.appendChild(containerElm);
		
		containerElm.setAttribute(versionAttr,containerOutputVersion);

		Element rootfilesElm = doc.createElement(rootfilesTag);
		containerElm.appendChild(rootfilesElm);
		
		for (Iterator<FileInfo> it = rootfiles.iterator(); it.hasNext(); ) {
			
			FileInfo rootfile = it.next();
			
			Element rootfileElm = doc.createElement(rootfileTag);
			rootfilesElm.appendChild(rootfileElm);
			
			rootfileElm.setAttribute(fullPathAttr,rootfile.getFilePath());
			rootfileElm.setAttribute(mediaTypeAttr,rootfile.getMediaType());
			
		}
		
		outDesc.beginFile(new FileInfo(containerDescPath,containerDescType));
		XmlUtility.getInstance().writeXml(doc,outDesc.getOutputStream(),null,null);
		outDesc.endFile();
	}


	/************************************************************
	 * 
	 * Implementation of manifest file (META-INF/manifest.xml)
	 *
	 *************************************************************/

	private static String manifestTag = "manifest:manifest";
	private static String manifestNamespace = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

	private static String fileEntryTag = "manifest:file-entry";
	private static String manifestFullPathAttr = "manifest:full-path";
	private static String manifestMediaTypeAttr = "manifest:media-type";
	
	
	private void loadManifest(InputStream is) throws ContainerFormatException, IOException {

		loadFileList( manifest, is, manifestNamespace, fileEntryTag, manifestFullPathAttr,
				manifestMediaTypeAttr,
				OCFFormatException.MessageCode.BAD_MANIFEST, "container manifest (manifest.xml)");
		
	}
	
	private void writeManifest(OutputDescriptor outDesc) throws TransformerException, IOException, FileSyntaxException, ContainerDataException  {
						
		Document doc = XmlUtility.getInstance().createEmptyDocument();
		
		Element manifestElm = doc.createElementNS(manifestNamespace,manifestTag);
		doc.appendChild(manifestElm);
				
		for (FileInfo fileEntry : manifest ) {
					
			Element fileEntryElm = doc.createElement(fileEntryTag);
			manifestElm.appendChild(fileEntryElm);
			
			fileEntryElm.setAttribute(manifestFullPathAttr,fileEntry.getFilePath());
			fileEntryElm.setAttribute(manifestMediaTypeAttr,fileEntry.getMediaType());
			
		}
		
		outDesc.beginFile(new FileInfo(manifestPath,manifestType));
		XmlUtility.getInstance().writeXml(doc,outDesc.getOutputStream(),null,null);
		outDesc.endFile();
		
	}

	
	private void writeEncryption (OutputDescriptor outDesc) throws ContainerDataException, FileSyntaxException, IOException, TransformerException {
	  if (encryptedPieces.size () == 0) {
	    return; }
	  
	  Document doc = XmlUtility.getInstance().createEmptyDocument();
	    
	  Element encryptionRoot = doc.createElementNS("urn:oasis:names:tc:opendocument:xmlns:container", "encryption");;
	  doc.appendChild(encryptionRoot);
	        
	  for (EncryptionInfo e : encryptedPieces) {
	    e.emit (doc, encryptionRoot); }

	  outDesc.beginFile(new FileInfo(encryptionPath,manifestType));
	  XmlUtility.getInstance().writeXml(doc,outDesc.getOutputStream(),null,null);
	  outDesc.endFile();
	}
	
	
	private ZipEntry findEntryInZip(ZipInputStream zipStream, String path) throws IOException {
		
		ZipEntry currentEntry;
		while ( true ) {
			currentEntry = zipStream.getNextEntry();
			if (currentEntry == null) break;
			
			if (path.equals(currentEntry.getName())) break;
		}
		return currentEntry;
		
	}
		
	
	private void loadFileList (FileSet fileList, InputStream is,
			String entryNamespace, String entryTag, String pathAttr,
			String typeAttr,
			OCFFormatException.MessageCode formatErrorCode, String fileDescForErrors ) throws ContainerFormatException, IOException {
		

		fileList.clear();
		
		try {
			Document doc = XmlUtility.getInstance().readXml(is);
			
			Element docElm = doc.getDocumentElement();
			NodeList fileElms = docElm.getElementsByTagNameNS(entryNamespace,entryTag);
			
			for (int i = 0; i < fileElms.getLength(); i++) {
				Element fileElm = (Element) fileElms.item(i);
				
				String path = fileElm.getAttribute(pathAttr);
				String type = fileElm.getAttribute(typeAttr);
				
				fileList.addFile(path,type);
			}
			
		}
		catch (SAXException e) {
			throw new OCFFormatException (formatErrorCode, containerFile,
					"XML parsing error in "+fileDescForErrors,e);
		} catch (FileSyntaxException e) {
			throw new OCFFormatException (formatErrorCode, containerFile,
					"invalid file path syntax in "+fileDescForErrors,e);
		}
		
	}


	private void initContainerParts() throws IOException, ContainerFormatException {
		
		
		for (ContainerPartInfo cis : containerParts) {
			
			InputStream is;
			is = getContentStream(cis.initialRootfile);

			cis.containerPart.loadRootfile(is,openMode);
			is.close();
		}

		
	}

	/*
	private void initPartsForPreview() throws IOException, ContainerFormatException {
		
		
		for (ContainerPartInfo cis : containerParts) {
			
			InputStream is = openFileInArchive(cis.initialRootfile.getFilePath());

			if (is != null) {
				cis.containerPart.loadRootfile(is,OpenMode.PREVIEW);
				is.close();
			}
			else {
				throw new OCFFormatException (OCFFormatException.MessageCode.ROOTFILE_NOT_FOUND,
						containerFile,
						"root file not found in archive: "+cis.initialRootfile.getFilePath());
				
			}
		}

		
	}
	
	private void initPartsFromFilesystem() throws IOException, ContainerFormatException {
		
		for (Iterator<ContainerPartInfo> it = containerParts.iterator(); it.hasNext();) {
			ContainerPartInfo cis = it.next();
			
			File rootfile = cis.initialRootfile.getFile(this.baseDir);
			InputStream is = new FileInputStream(rootfile);
			cis.containerPart.loadRootfile(is,OpenMode.EXPANDED);
			
		}
				
	}*/
	

	private void expandArchive() throws IOException, OCFFormatException {
		
		
		ZipInputStream zis = openInputZipFile(containerFile);
				
		ZipEntry currentEntry;
		while ( (currentEntry=zis.getNextEntry()) != null) {
			
			String path = currentEntry.getName();
			FileInfo fileInfo;
			try {
				fileInfo = new FileInfo(path,"");
			} catch (FileSyntaxException e) {
				throw new OCFFormatException(OCFFormatException.MessageCode.INVALID_FILENAME,
						containerFile,
						"invalid file name read from archive: "+path,e);
			}
			File outFile = getContentFile(fileInfo);;
			
			if (fileInfo.isDirectory()) {
				outFile.mkdirs();
			}
			else {
				// create parent dir if necessary
				File parentDir = outFile.getParentFile();
				if (parentDir != null) parentDir.mkdirs();

				// extract file from ZIP
				OutputStream outStream = new FileOutputStream(outFile);
				FileUtility.copyStream(zis,outStream);
			}
			
			zis.closeEntry();
			
			if (!manifest.contains(fileInfo)) {
				manifest.addFile(fileInfo);
			}
			
		}
		
		
	}


	/**
	 * Load the container's contents from a container file.
	 * <p>
	 * This method should never be called directly; it is called during
	 * the load process initiated through ContainerFactory.
	 * </p>
	 * 
	 * @see org.esciurus.model.ocf.ContainerFactory#createContainerFromFile(File, Container.OpenMode)
	 *
	 * @param inputFile the file to load the container from 
	 * @param openMode the opening mode of the container
	 * @throws ContainerException if a data format problem is encountered during the load process
	 * @throws IOException if a file I/O error is encountered
	 */
	void load (File inputFile, OpenMode openMode) throws ContainerException, IOException  {
		
		containerFile = inputFile;
		
		if (this.openMode != null) {
			throw new ContainerException("container already opened in mode "+this.openMode.toString()+", cannot load file");
		}
		this.openMode = openMode;
		
		// load inventory, i.e. container.xml and manifest
		loadContainerInventory();
		
	    // create container part objects
		for (Iterator<FileInfo> it=rootfiles.iterator(); it.hasNext();) {
			ContainerPartInfo cpi = new ContainerPartInfo();
			cpi.initialRootfile = it.next(); 
			cpi.containerPart = createContainerPart(cpi.initialRootfile);
			containerParts.add(cpi);
		}
		
		if (openMode == OpenMode.EXPANDED) {
			baseDir.mkdirs();
			expandArchive();
		}

		initContainerParts();
		
	}
	

	private class OutputDescriptorImpl implements OutputDescriptor {

		ZipOutputStream outStream;
		ZipEntry currentEntry;
		FileSet addedFiles;
		FileSet remainingFiles;
		
		OutputDescriptorImpl(ZipOutputStream outStream, FileSet originalManifest) {
			this.outStream = outStream;
			addedFiles = new FileSet();
			remainingFiles = new FileSet();
			remainingFiles.addAll(originalManifest);
		}

		void registerFile(FileInfo fileInfo) throws ContainerDataException {
			if (!addedFiles.contains(fileInfo)) {
				addedFiles.addFile(fileInfo);
				remainingFiles.remove(fileInfo);
				currentEntry = new ZipEntry(fileInfo.getFilePath());
			}
			else {
				throw new ContainerDataException("duplicate file entry requested: "+fileInfo.getFilePath());
			}
		}

		void beginUncompressedFile(FileInfo fileInfo, int size, long crc32) throws IOException, ContainerDataException {
			registerFile(fileInfo);
			currentEntry.setSize(size);
			currentEntry.setCompressedSize(size);
			currentEntry.setCrc(crc32);
			currentEntry.setMethod(ZipEntry.STORED);
			outStream.putNextEntry(currentEntry);
		}
		
		/* (non-Javadoc)
		 * @see org.esciurus.model.ocf.OutputDescriptor#beginFile(org.esciurus.model.ocf.FileInfo)
		 */
		public void beginFile(FileInfo fileInfo) throws IOException, ContainerDataException {
			registerFile(fileInfo);
			outStream.putNextEntry(currentEntry);
		}

		/* (non-Javadoc)
		 * @see org.esciurus.model.ocf.OutputDescriptor#endFile()
		 */
		public void endFile() throws IOException {
			outStream.closeEntry();
		}

		/* (non-Javadoc)
		 * @see org.esciurus.model.ocf.OutputDescriptor#getOutputStream()
		 */
		public OutputStream getOutputStream() {
			return outStream;
		}

		/* (non-Javadoc)
		 * @see org.esciurus.model.ocf.OutputDescriptor#addFileFromFilesystem(org.esciurus.model.ocf.FileInfo)
		 */
		public void addFileFromFilesystem(FileInfo fileInfo) throws IOException, ContainerDataException {
			beginFile(fileInfo);
			if (!fileInfo.isDirectory()) {
				InputStream inStream = getContentStream(fileInfo);
				FileUtility.copyStream(inStream,getOutputStream());
			}
			endFile();
		}


		/* (non-Javadoc)
		 * @see org.esciurus.model.ocf.OutputDescriptor#markDirty(org.esciurus.model.ocf.FileInfo)
		 */
		public void markDirty(FileInfo fileInfo) {
			if (fileInfo.isDirectory()) {
				String dirToRemove = fileInfo.getFilePath();
				
				Vector<FileInfo> filesToRemove = new Vector<FileInfo>();
				for (FileInfo thisEntry : remainingFiles) {
					if (thisEntry.getFilePath().startsWith( dirToRemove )) {
						filesToRemove.add(thisEntry);
					}
				}
				remainingFiles.removeAll(filesToRemove);
				
			}
			else {
				remainingFiles.remove(fileInfo);
			}
		}

		void addRemainingFiles() throws IOException, ContainerDataException {
			Vector<FileInfo> filesToCopy = new Vector<FileInfo>();
			filesToCopy.addAll(remainingFiles);
			
			for (Iterator<FileInfo> it = filesToCopy.iterator(); it.hasNext(); ) {
				addFileFromFilesystem( it.next() );
			}
		}

				
	}

	
	private void writeContainerMimeType(OutputDescriptorImpl outDesc) throws IOException, ContainerDataException {
		
		String outputType = getOutputMediaType();
		byte[] outBytes = outputType.getBytes();
		
		CRC32 crc = new CRC32();
		crc.update(outBytes);

		try{
			outDesc.beginUncompressedFile(new FileInfo(mimetypePath,mimetypeType),outBytes.length,crc.getValue());
		}
		catch (FileSyntaxException e) {
			throw new ContainerDataException("internal error - cannot write mime type",e);
		}
		
		
		outDesc.getOutputStream().write(outBytes);
		
		outDesc.endFile();
	}


	/**
	 * Save this container, i.e. package it as a ZIP file
	 * to the original file it has been loaded from.
	 * Saving the container is only possible in EXPANDED mode.
	 * 
	 * @throws ContainerException if any I/O error occurs; 
	 * or if the container is not in EXPANDED mode.
	 * 
	 * @see OpenMode#EXPANDED
	 */
	public void save () throws ContainerException {
		saveAs(containerFile);
	}

	/**
	 * Save this container, i.e. package it as a ZIP file.,
	 * to a specified output file.
	 * Saving the container is only possible in EXPANDED mode.
	 * 
	 * @param outputFile the file system location to save the container to
	 * @throws ContainerException if any I/O error occurs; 
	 * or if the container is not in EXPANDED mode.
	 * 
	 * @see OpenMode#EXPANDED
	 */
	public void saveAs (File outputFile) throws ContainerException {

		
		if (this.openMode != OpenMode.EXPANDED) {
			throw new ContainerException("cannot save container - not in EXPANDED mode");
		}
		
		try{
			File tempZipFile = File.createTempFile("temp-save-",".zip",baseDir);
			ZipOutputStream outStream = openOutputZipFile(tempZipFile);
			
			OutputDescriptorImpl outDesc = new OutputDescriptorImpl(outStream,manifest);
		
			writeContainerMimeType(outDesc);

			writeContainerDesc(outDesc);
			
			outDesc.markDirty(new FileInfo(manifestPath,manifestType));
			
			for (Iterator<ContainerPartInfo> it = containerParts.iterator(); it.hasNext();) {
				it.next().containerPart.save(outDesc);
			}
			
			outDesc.addRemainingFiles();
			
			manifest.clear();
			manifest.addAll(outDesc.addedFiles);
			
			writeManifest(outDesc);
			
			writeEncryption (outDesc);
			
			outStream.close();
			FileUtility.copyFile(tempZipFile,outputFile);
		}
		catch (IOException e) {
			throw new ContainerException("I/O error while writing to output file",e);
		}
		catch (TransformerException e) {
			throw new ContainerException("XML output error while writing to output file",e);			
		}
		catch (FileSyntaxException e) {
			throw new ContainerException("internal error - bad file name syntax",e);			
		}
	}

	
	
	/**
	 * Test whether all constraint of the container are fulfilled.
	 * 
	 * @param tryResolve if contraint violations should be automatically resolved where possible
	 * @return true if all constraints are fulfilled, or all violations could be resolved;
	 * false if violations remain.
	 * 
	 * @see #getConstraintReport(boolean)
	 * @see ConstraintTicket
	 */
	public boolean constraintsFulfilled (boolean tryResolve) {

		ConstraintTicket ticket = getConstraintReport(tryResolve);
		return !ticket.hasUnresolvedViolations();
	}


	/**
	 * Check the container for constraint violations, 
	 * retrieving a list of all violations found.
	 * If the parameter <code>tryResolve</code> is true,
	 * then an automated recovery from violations will be attempted.
	 * The list returned provides information as to whether 
	 * the violations have ben resolved or not.
	 * 
	 * @param tryResolve if contraint violations should be automatically resolved where possible
	 * @return a list of constraint violations
	 * 
	 * @see #constraintsFulfilled(boolean)
	 * @see ConstraintTicket
	 */
	public ConstraintTicket getConstraintReport (boolean tryResolve) {
		
		ConstraintTicket ticket = new ConstraintTicket(tryResolve);
		
		checkConstraints(ticket);
		
		return ticket;
		
	}
	
	/**
	 * Check data constraints in the container. 
	 * 
	 * <p>This method passes the contraint ticket to all registered container parts.
	 * It should not be called directly. Rather, subclasses may want 
	 * override this method in order to add their own contraint checks 
	 * on the container level. In this case, they must not forget
	 * to call the <code>super()</code> method.
	 * 
	 * @param ticket the constraint ticket to use in verification
	 */
	protected void checkConstraints (ConstraintTicket ticket) {
		
		for (Iterator<ContainerPartInfo> it = containerParts.iterator(); it.hasNext();) {
			it.next().containerPart.checkConstraints(ticket);
		}
		
	}




	/**
	 * Delete the temporary directory.
	 * The directory is only deleted if the <code>removeTempDir</code> field
	 * is set to <code>true</code> (which is however the default setting).
	 * Note that all directory contents will be deleted.
	 * <p>
	 * Call this method only if this Container object is no longer needed.
	 * Any further calls to its method may yield unpredictable results.
	 * </p>
	 * <p>
	 * Especially in batch processing, this method should be called 
	 * after processing of the Container object is finished; otherwise
	 * large amounts of temporary data may remain on the file system.
	 * There is no automated procedure that deletes the container's
	 * temporary data. 
	 * </p>  
	 */
	public void cleanupNow() {
		if (removeTempDir && openMode.equals(OpenMode.EXPANDED)) {
			try {
				FileUtility.deleteDirectory(baseDir);
			} catch (IOException e) {
				// ignore all errors that might occur during cleanup
			}			
		}
	}
	
	// LATER add implementation for "later" cleanup, e.g. at system exit

	/*
	public void cleanupLater() {
		if (removeTempDir) {
			// ...
		}
	}
	*/


}
