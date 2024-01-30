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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Abstract base class for a console-based application.
 * 
 * <p>This class provides basic functionality that is common
 * to all console applications, mainly: parameter parsing and
 * display of help and license messages. 
 * </p>
 * <p>The application's specific functionality is captured
 * in the abstract method runApplication(), which subclasses
 * need to implement accordingly.</p>
 */
public abstract class ConsoleApplication {

	/**
	 * Captures a command-line parameter as a key-value pair,
	 * coresponding to parameter name and argument.
	 *
	 */
	protected class ParamKeyValue {
		String key;
		String value;
	}
	
	private List<ParamKeyValue> params;
	
	
	/**
	 * Construct a new ConsoleApplication object.
	 */
	public ConsoleApplication() {
		super();
		params = new Vector<ParamKeyValue>(); 
	}
	
	
	private void parseParameters(String[] args) throws ConsoleAppException {
		int i = 0;
		while (i < args.length-1) {
			
			String firstArg = args[i];
			if (!firstArg.startsWith("-")) {
				throw new ConsoleAppException ("invalid parameters");
			}
			else {
				ParamKeyValue pkv = new ParamKeyValue();
				pkv.key = firstArg.substring(1);
				pkv.value = args[i+1];
				params.add(pkv);
			}
			
			i += 2;
		}
		
		if (i < args.length) {
			throw new ConsoleAppException ("invalid final parameter");
		}

	}
	
	/**
	 * Get the list of command-line parameters for this application.
	 * @return the list of parameters
	 */
	protected List<ParamKeyValue> getParameters() {
		return params;
	}
	
	
	/**
	 * Check whether a specific parameter has been set
	 * 
	 * @param param the parameter name
	 * @return true if the parameter has been set, i.e. occurs at least once
	 * in the list of parameters.
	 */
	protected boolean isParameterSet(String param) {
		return getParameter(param) != null;
	}
	
	/**
	 * Get the value of a command-line parameter, in String form
	 * 
	 * @param param the name of the parameter to be read
	 * @return the value (argument) for this parameter, 
	 * or <code>null</code> if the parameter was not found 
	 */
	protected String getParameter(String param) {
		
		String value = null;
		ListIterator<ParamKeyValue> it = params.listIterator();
		while (it.hasNext() && (value == null)) {
			ParamKeyValue pkv = it.next();
			if (pkv.key.equalsIgnoreCase(param)) {
				value = pkv.value;
			}
		}
		return value;
	}
	
	/**
	 * Get the value of a command-line parameter, as a Boolean value.
	 * Values "1", "yes", "true", and "+" will be considered as 
	 * boolean "true", while all other value are considered as "false".
	 * If the parameter has not been set, a default value (passed as 
	 * a parameter) will be returned.
	 * 
	 * @param param the name of the parameter to be read
	 * @param defaultValue the value to use if the parameter has not been set 
	 * @return the value of the parameter 
	 */
	protected boolean getParameterAsBoolean (String param, boolean defaultValue) {
		
		boolean result;
		String textValue = getParameter(param);
		
		if (textValue == null) {
			result = defaultValue;
		}
		else {
			result =  textValue.equalsIgnoreCase("yes") 
			|| textValue.equalsIgnoreCase("true") 
			|| textValue.equalsIgnoreCase("1") 
			|| textValue.equalsIgnoreCase("+"); 
		}
		return result;
	}
	
	/**
	 * Get the value of a command-line parameter, as a File object.
	 * 
	 * @param param the name of the parameter to be read
	 * @return the value (argument) for this parameter, as a File object, 
	 * or <code>null</code> if the parameter was not found 
	 */
	protected File getParameterAsFile (String param) {
		
		File result;
		String textValue = getParameter(param);
		
		if (textValue != null) {
			result = new File(textValue);
		}
		else {
			result =  null; 
		}
		return result;
	}

	private static final String helpParameter = "-help";
	private static final String licenseParameter = "-license";
	
	/**
	 * Main application body for a console application.
	 * 
	 * <p>This method covers parameter parsing, exception handling,
	 * and display of help and license messages. It will call
	 * runApplication() for the specific application functionality.</p> 
	 * 
	 * <p>This method should be called from the <code>main()</code> method
	 * of the application. The method will return a numeric return
	 * code, which should be used in the <code>main()</code> method to call 
	 * <code>System.exit(int)</code>. The present mehtod does <em>not</em>
	 * call <code>System.exit()</code>.</p>
	 * 
	 * @param args command line arguments, as passed to <code>main()</code>
	 * @return int the return code of the program. 
	 */
	public int consoleMain(String[] args) {
		// call this from your main method
				
		int returncode=0;
		
		try{			
			if (args.length==0 || args[0].equals(helpParameter)) {				
				printHelpMessage();
			}
			else if (args[0].equals(licenseParameter)) {				
				printLicense();
			}
			else {
				parseParameters(args);
				returncode = runApplication();
			}
		}
		catch (ConsoleAppException e) {
			System.err.println(e.getMessage());
			System.err.println("Program terminated with errors.");
			returncode=2;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Program terminated abnormally.");
			returncode=2;
		}
		
		return returncode;
		
	}
	
	
	
	/**
	 * Get the name of the resource file that represents 
	 * the help message for this application.
	 * 
	 * @return the path to the resource file, within the JAR context
	 */
	protected abstract String getHelpMessageFile();
	
	/**
	 * Retrieve the license note for this program, as a string.
	 * 
	 * @return the license note
	 */
	protected String getLicenseNote() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("This application is part of Esciurus; Copyright (C) 2007 B. Wolterding.\n");
		sb.append("Esciurus comes with ABSOLUTELY NO WARRANTY. It is free software,\n");
		sb.append("and you are welcome to redistribute it under certain conditions.\n");
		sb.append("For details, start the application with the \"-license\" parameter.\n");
		    
		return sb.toString();
	}


	/**
	 * Print the contents of a text file to stdout. 
	 * A "more" button feature will be provided.
	 * 
	 * @param resourceName the path to the text resource file
	 * @param prevLines number of previous line to be kept when scrolling the first page
	 * @throws ConsoleAppException if the resource was not found, or any other I/O error occurs
	 */
	protected void printMessageFile(String resourceName, int prevLines) throws ConsoleAppException {
		
		InputStream is = getClass().getResourceAsStream(resourceName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		int lineCount = prevLines;
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				lineCount++;
				if (lineCount > 20) {
					System.out.print("---more---");
					System.in.read();
					lineCount=0;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConsoleAppException("internal error");
		}
		
	}

	
	/**
	 * Print the detailed license message to stdout.
	 * 
	 * @throws ConsoleAppException if the text resource was not found, or any other I/O error occurs
	 */
	protected void printLicense() throws ConsoleAppException {
			
		printMessageFile("/org/esciurus/common/license.txt",0);
	}
	
	/**
	 * Print the help message to stdout.
	 * 
	 * @throws ConsoleAppException if the text resource was not found, or any other I/O error occurs
	 */
	protected void printHelpMessage() throws ConsoleAppException {
		
		System.out.println(getLicenseNote());
		printMessageFile(getHelpMessageFile(),5);

	}

	/**
	 * This method includes the main functionality of the application
	 * and is overridden accordingly by subclasses. It can either
	 * return a numerical code (0,1, or 2 for success, warning, and error
	 * respectively) or terminate with an exception. The numerical code
	 * will be passed as return code of the console application,
	 * where an exception will cause a return code of 2 (error).
	 * 
	 * @return the numerical return code (0, 1, or 2)
	 * @throws ConsoleAppException
	 */
	protected abstract int runApplication() throws ConsoleAppException;
	
	
}
