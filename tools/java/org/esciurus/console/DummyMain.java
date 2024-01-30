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

import java.io.IOException;

import org.esciurus.common.Globals;

/**
 * Dummy "main" application, which simply displays a fixed text to the user.
 * Used as the main class of the JAR file, as long as no GUI is present.
 *
 */
public class DummyMain  {


	/**
	 * Main method - simply prints a help message to the console
	 * @param args command line arguments (ignored)
	 */
	public static void main(String[] args) {

		System.out.println("");
		System.out.println("--- Esciurus - A personal electronic library (Release "+Globals.getReleaseId()+")---");
		System.out.println("");
		System.out.println("In the present release, Esciurus is not yet feature-complete. In particular,");
		System.out.println("there is no graphical interface included. This release only contains");
		System.out.println("the back-end Java API, and a sample console-based application.");
		System.out.println("");
		System.out.println("For starting this console application, please refer to the");
		System.out.println("\"Installation and Usage Guide\" (usage_guide.pdf), which is included");
		System.out.println("with the documentation package.");
		System.out.println("");
		System.out.println("You can download the documentation package, and possibly newer versions");
		System.out.println("of Esciurus, from the project website:");
		System.out.println("");
		System.out.println("  http://www.esciurus.org/");
		System.out.println("");
		System.out.println("(hit <Return> to exit)");
		try {
			System.in.read();
		} catch (IOException e) {
			// ignore
		}

	}


}
