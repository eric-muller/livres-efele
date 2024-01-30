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

package org.esciurus.common;

/*
 * AWT / Swing resource access temporarily disabled, currently not used
 * 
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * Loads text, image and icon resources from files
 * (both from the file system and from within a JAR file). 
 * All parts of Esciurus use this helper class to load 
 * any static file system resources needed.
 * </p>
 * <p>The class also contains some convenience methods
 * for accessing ResourceBundle objects for internationalization.
 * </p>
 * <p>The file system resources to be loaded here are always
 * assumed to be available; they are packaged with the application.
 * If a resource cannot e loaded for any reason,
 * this is considered an internal error and will result in a
 * <code>java.lang.RuntimeException</code> being thrown.</p>
 * 
 * <p><em>Pattern:</em> Singleton</p>
 * 
 */
public final class ResourceLoader /*extends Component*/ {

    
    /**
     * The single system-wide instance of the ResourceLoader class
     */
    private static ResourceLoader singleInstance;
    
    /**
     * Media Tracker object that is used for loading image resources. 
     * It is initialized only once globally (in the single instance of this class).
     */
    //private MediaTracker tracker;
    
    /**
     * The file path to all file system resources, relative to the base directory
     * of the project or JAR file. 
     */
    private static final String RESOURCE_PATH="/org/esciurus/common/"; 
    
    
    /**
     * Retrieve the globally unique instance of the ResourceLoader class.
     *  
     * @return An instance of RecourceLoader
     */
    public static ResourceLoader getInstance() {
        
        // initialize the global RecourceLoader object if it hasn't been initialized yet
        if (singleInstance==null) {            
            singleInstance = new ResourceLoader();
            //singleInstance.tracker = new MediaTracker (singleInstance);
        }
        
        // return the unique instance
        return singleInstance;
    }
    
    /**
     * Reads an image file from the file system into an java.awt.Image object. 
     * 
     * @param name The name of the image file relative to the resource path, inluding file extension
     * @return The fully loaded image
     */
    /*public Image getResourceImage(String name) {
        
        // initialize the Image object with the correct file name and path
        String filepath = RESOURCE_PATH+name;
        URL imageUrl = getClass().getResource(filepath);
        Image img = getToolkit().getImage(imageUrl);
        
        // load the image through a media tracker 
        tracker.addImage(img,0);
        try {
            tracker.waitForAll();
        }
        catch (InterruptedException e) {
            // ignore interruptions
        }
        return img;
        
    }*/
    
    /**
     * Reads an image file from the file system into an javax.swing.Icon object. 
     * 
     * @param name The name of the image file relative to the resource path, inluding file extension
     * @return The fully loaded image
     */
    /*public Icon getResourceIcon(String name) {
        
        // initialize the Icon object with the correct file name and path
        // (this automatically loads the icon)
        
        String filepath = RESOURCE_PATH+name;
        URL iconUrl = getClass().getResource(filepath);
        ImageIcon icon = new ImageIcon(iconUrl);
        
        return icon;
        
    }*/
    
    /**
     * Load a text string from a resource file.
     * The entire content of the file (including line breaks, if any)
     * is returned as a single string.
     * 
     * @param name the name of the resource file, within the global resource directory
     * @return the text loaded from the resource file
     */
    public String getResourceText (String name) {
        
        String filepath = RESOURCE_PATH+name;
		InputStream is = getClass().getResourceAsStream(filepath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		StringBuffer content = new StringBuffer();
		String line;
			
		try {
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
		} catch (IOException e) {
			throw new RuntimeException("internal error - cannot load text resource: "+filepath,e);
		}
		
        return content.toString();
    	
    }

    /**
     * Load a string from a property resource file.
     * 
     * @param name the name of the property file, within the global resource directory 
     * @param key the key to be queried in that property file
     * @return the value of that key; may be <code>null</code> 
     * or the empty string
     */
    public String getPropertyString (String name, String key) {

    	String filepath = RESOURCE_PATH+name;
		InputStream is = getResourceStream(filepath);
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException("internal error - cannot load property resource: "+filepath,e);
		}
		
		String result = p.getProperty(key);
		
		return result;
		    	
    }

    /**
     * Open a resource file as an input stream.
     * The input stream returned will never be <code>null</code>;
     * rather, if the file cannot be found, a <code>RuntimeException</code>
     * is thrown.
     * 
     * @param pathAndName the full filename of the resource file, including path
     * @return the input stream for that resource file; never <code>null</code>
     */
    public InputStream getResourceStream(String pathAndName) {
		InputStream is = getClass().getResourceAsStream(pathAndName);
		if (is == null) {
			throw new RuntimeException("internal error - cannot open property resource: "+pathAndName);
		}
		return is;
    }
    
    /**
     * Read a property resource file into memory.
     * 
     * @param propPathAndName the full filename of the resource file, including path
     * @return the properties read from the file; never <code>null</code>
     */
    public Properties readProperties (String propPathAndName) {

		InputStream is = getResourceStream(propPathAndName);
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException("internal error - cannot load property resource: "+propPathAndName,e);
		}
		
		return p;
    }
    
    /**
     * Get a list of internationalized strings from a resource bundle.
     * 
     * @param bundle the resource bundle to use
     * @param keys the keys of the strings to be retrieved
     * @return the internationalized values correspoding to these keys
     */
    public static Vector<String> getListFromBundle( ResourceBundle bundle, String[] keys) {
    	Vector<String> result = new Vector<String>();
    	
    	for (String key : keys) {
    		result.add( bundle.getString(key) );
    	}
    	return result;
    }
    
    
    /**
     * Get the internationalied display value for an Enum constant.
     * The value is loaded from a resource bundle.
     * <p>The entries in this resource bundle are supposed to be 
     * of the form <code>EnumClass.FIELD=DisplayValue</code>, where <code>EnumClass</code>
     * is the unqualified class name of the Enum to be queried for, <code>FIELD</code>
     * is a constant field value in this Enum,
     * and <code>DisplayValue</code> is the internationalized display
     * value for <code>FIELD</code>.</p>
     * 
     * @param e the enum object to retrieve the display value for
     * @param bundleName the name of the resource bundle to use (full path name)
     * @param locale the locale used for formatting
     * @return the display value for this enum object
     */
    public static String getEnumDisplayName(Enum e, String bundleName, Locale locale ) {
    	
    	ResourceBundle bundle = ResourceBundle.getBundle(bundleName,locale);
    	String key = e.getClass().getSimpleName()+"."+e.name();
    	return bundle.getString(key);
    }
    
}
