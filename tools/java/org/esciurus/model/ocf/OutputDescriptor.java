package org.esciurus.model.ocf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This interface describes all operations necessary for saving an OCF container
 * to its ZIP Container representation. 
 * <p>
 * Files can be written to the ZIP Container either by passing them to a
 * stream object, or by passing a reference to a local file which will be copied
 * to the ZIP Container.
 * </p>
 * <p>
 * In addition to writing files to the ZIP Container, the interface also provides
 * methods to mark files as "dirty"; that is, specifying that previous versions
 * of files, which might be present in the ZIP Container during loading,
 * should not be copied over to the saved version, even if no new version is written
 * via this interface.
 * </p> 
 * <p>
 * A typical implementation will use the interface as follows:
 * <pre>
 *   markDirty( [all files in my directory] );
 *   for [thisFile -> all files of this container part] {
 *		beginFile(...);
 *		thisFile.writeToOutputStream (outputDescriptor.getOutputStream());   
 *		endFile();   
 *   }
 * </pre>
 * 
 * @author B. Wolterding
 *
 */
public interface OutputDescriptor {

	/**
	 * Start writing a new file to the ouput ZIP container. 
	 * The relative path and the media type of the file need to be given.
	 * 
	 * @param fileInfo path and media type of the file
	 * 
	 * @throws IOException if an error occurs during stream I/O
	 * @throws ContainerDataException if some format problem occurs when starting the file 
	 * (e.g., duplicate file name in the container)
	 */
	public abstract void beginFile(FileInfo fileInfo) throws IOException, ContainerDataException;
	
	
	/**
	 * End the output of the current file.
	 * 
	 * @throws IOException if an error occurs during stream I/O
	 */
	public abstract void endFile() throws IOException;

	
	/**
	 * Retrieve the output stream object associated with the ZIP container.
	 * All output must go to this stream.
	 * 
	 * @return the output stream to be used for any output
	 */
	public abstract OutputStream getOutputStream();
	
	
	/**
	 * Convenvience method: Add a file from the file system to the ZIP container.
	 * This method copies the file to the output stream,
	 * including calls to beginFile(...) and endFile(...).
	 * 
	 * @param fileInfo path and media type of the file to be copied
	 * @throws IOException if an error occurs during stream I/O
	 * @throws ContainerDataException if a data format problem occurs
	 * (e.g. duplicate file name in the ZIP container)
	 */
	public abstract void addFileFromFilesystem(FileInfo fileInfo) throws IOException, ContainerDataException;
	
	
	/**
	 * Mark a file or directory in the container as "dirty".
	 * <p>
	 * The output descriptor keeps a list of files,
	 * initially copied from the manifest of the previous version of the ZIP Container.
	 * Files are removed from this list if
	 * <ul>
	 * <li> a file of identical name is written to the new container, or 
	 * </li>
	 * <li> the file is marked "dirty" by means of this method.
	 * </li>
	 * </ul>
	 * Any files remaining on the list at the end of the save process
	 * will automatically be copied over to the new version of the ZIP container. 
	 * </p>
	 * <p>
	 * If applied to a directory (file path ending in "/"), all files 
	 * in this directory or in any subdiretory will be marked dirty. 
	 * </p>
	 * 
	 * @param fileInfo the file or directory to be marked "dirty"
	 */
	public abstract void markDirty (FileInfo fileInfo);
	
}
