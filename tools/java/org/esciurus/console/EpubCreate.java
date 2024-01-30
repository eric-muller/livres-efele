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
   
package org.esciurus.console;

import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.esciurus.common.KeyNotInDictionaryException;
import org.esciurus.model.metadata.DCPerson;
import org.esciurus.model.metadata.MetadataRecord;
import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.ocf.ContainerException;
import org.esciurus.model.opf.EpubDataException;
import org.esciurus.model.opf.EpubContainer;
import org.esciurus.model.opf.EpubContainerFactory;
import org.esciurus.model.opf.ManifestEntry;
import org.esciurus.model.opf.OPFPackage;
import org.esciurus.model.opf.OPFPackage.FormatModifier;

/**
 * This console-based application creates Epub packages by packaging
 * their content files from the file system, and adding metadata
 * based on command-line parameters.
 */
public class EpubCreate extends ConsoleApplication {

	
	/**
	 * Construct a new console application object of class EpubCreate.
	 */
	public EpubCreate() {
		super();
	}
	
	/**
	 * Main method for this application.
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		
		EpubCreate app = new EpubCreate();
		int retcode = app.consoleMain(args);
		
		System.exit(retcode);

	}

	@Override
	protected String getHelpMessageFile() {
		return "/org/esciurus/console/EpubCreate_help.txt";
	}

	
	private EpubContainer createEmptyEpub() {
				
		EpubContainerFactory factory = new EpubContainerFactory();
		
		EpubContainer epub = factory.createEmptyContainer();
		epub.getOPFPackage().setFormatModifiers(EnumSet.of(FormatModifier.DONT_REQUIRE_CORETYPES));
		return epub;
		
	}


	private void setEpubMetadata(OPFPackage opfPack) throws ConsoleAppException {

		List<ParamKeyValue> paramList = getParameters();
		MetadataRecord metadata = opfPack.getMetadata();
		
		for (Iterator<ParamKeyValue> it = paramList.iterator(); it.hasNext();) {
			
			ParamKeyValue pkv = it.next();
			
			if (pkv.key.equalsIgnoreCase("author")) {
				
				DCPerson author = metadata.getCreators().addNewEntry();
				author.setName(pkv.value);
				author.setRole("aut");
				
			}
			else if (pkv.key.equalsIgnoreCase("language")) {
				
				try {
					metadata.getLanguages().addContentStrict(pkv.value);
				}
				catch (KeyNotInDictionaryException e) {
					throw new ConsoleAppException("unknown language: "+pkv.value);
				}
				
			}
			else if (pkv.key.equalsIgnoreCase("title")) {
				
				metadata.getTitles().addContent(pkv.value);	
				
			}
			else if (pkv.key.equalsIgnoreCase("subject")) {
				
				metadata.getSubjects().addContent(pkv.value);
				
			}
			else if (pkv.key.equalsIgnoreCase("description")) {
				
				metadata.getDescriptions().addContent(pkv.value);
				
			}
			else if (pkv.key.equalsIgnoreCase("publisher")) {
				
				metadata.getPublishers().addContent(pkv.value);
				
			}
			else if (pkv.key.equalsIgnoreCase("type")) {
				
				try {
					metadata.getTypes().addContentStrict(pkv.value);
				}
				catch (KeyNotInDictionaryException e) {
					throw new ConsoleAppException("unknown DCMI type: "+pkv.value);
				}
				
			}
			else if (pkv.key.equalsIgnoreCase("relation")) {
				
				metadata.getRelations().addContent(pkv.value);
				
			}
			else if (pkv.key.equalsIgnoreCase("coverage")) {
				
				metadata.getCoverages().addContent(pkv.value);
				
			}
			else if (pkv.key.equalsIgnoreCase("rights")) {
				
				metadata.getRights().addContent(pkv.value);
				
			}

			
		}
		
	}

	
	private void addEpubFiles(OPFPackage opfPack) throws ConsoleAppException {

		List<ParamKeyValue> paramList = getParameters();
		
		for (Iterator<ParamKeyValue> it = paramList.iterator(); it.hasNext();) {
			
			ParamKeyValue pkv = it.next();
			
			try {
				if (pkv.key.equalsIgnoreCase("include")) {
					
					opfPack.addPhysicalFile(new File(pkv.value),"" );
					
				}
				else if (pkv.key.equalsIgnoreCase("includedir")) {
					
					opfPack.addPhysicalDirectory(new File(pkv.value),true,"",null, ".*\\.epub" );
					
				}
			}
			catch(EpubDataException e) {
				e.printStackTrace(System.err);
				throw new ConsoleAppException("problem while adding file to container: "+pkv.value);
			}
			
		}
		
	}

	
	private void setupEpubSpine(OPFPackage opfPack) throws ConsoleAppException {
		
		List<ParamKeyValue> paramList = getParameters();
		
		for (Iterator<ParamKeyValue> it = paramList.iterator(); it.hasNext();) {
			
			ParamKeyValue pkv = it.next();
			
			if (pkv.key.equalsIgnoreCase("spine")) {
				
				ManifestEntry spinefile = opfPack.getManifestEntryByFilename(pkv.value);
				if (spinefile != null) {												
					opfPack.getSpine().addEntry(spinefile);
				}
				else {
					throw new ConsoleAppException ("file not in package: "+pkv.value);
				}
			}
		}

		// setup NCX
		String tocFileName = getParameter("toc");
		if (tocFileName != null){
			ManifestEntry tocfile = opfPack.getManifestEntryByFilename(tocFileName);
			if (tocfile != null) {												
				opfPack.getSpine().setToc(tocfile);
			}
			else {
				throw new ConsoleAppException ("file not in package: "+tocFileName);
			}
		}
		else {
			throw new ConsoleAppException ("no table of contents specified");
		}
		

	}
	


	private int checkEpubConstraints(EpubContainer epub) {
		
		int result;
		ConstraintTicket report = epub.getConstraintReport(true);
		
		if (report.getViolations().size() == 0) {
			result = 0; //success
		}
		else if (report.hasUnresolvedViolations()) {
			result = 2; // error
		}
		else {
			result = 1; // warning
		}
		
		if (result > 0) { 
			System.err.println(report.toString());
		}
		
		return result;
	}

	
	@Override
	protected int runApplication() throws ConsoleAppException {
		
		int result=0;
		
		System.out.println("Beginning to create Epub file...");

		EpubContainer epub = createEmptyEpub();
		
		try {
			addEpubFiles(epub.getOPFPackage());
			
			setupEpubSpine(epub.getOPFPackage());
			
			setEpubMetadata(epub.getOPFPackage());
			
			epub.getOPFPackage().getMetadata().generateUniqueId(true);
			
			result = checkEpubConstraints(epub);
			epub.getConstraintReport(true);
			
			if (result < 2) {
				
				
				File outFile = getParameterAsFile("outfile");
				
				if (outFile != null) {
					try {
						epub.saveAs(outFile);
					} catch (ContainerException e) {
						throw new ConsoleAppException("error saving epub file: "+e.getMessage());
					}
				}
				else {
					throw new ConsoleAppException("output file name not specified");
				}
				System.out.println("Epub file has been created.");
			}
			else {
				System.err.println("There were errors. Epub file has not been created.");
			}
		}
		finally {
			epub.cleanupNow();
		}
		
		return result;
	}

}
