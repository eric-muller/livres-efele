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

package org.esciurus.model.metadata;

import java.util.Locale;

import org.esciurus.common.KeyNotInDictionaryException;
import org.esciurus.model.dictionaries.Dictionaries;
import org.esciurus.model.opf.EpubFormatException;
import org.w3c.dom.Element;

/**
 * A Dublin Core metadata entry.
 * <p>
 * This class contains support for the "language" and "encoding" fields
 * of the metadata entries, which are stored in the XML attributes
 * <code>xml:lang</code> and <code>xsi:type</code>. While these fields
 * and their access methods are both defined in this class, and in all classes
 * derived from it, each metadata entry will only support <em>one</em>
 * of these options ("language" for human-readable entries, "encoding"
 * for entries which can use encoding schemes). Attempts to set the
 * corresponding other field will result in an <code>UnsupportedOperationException</code>.   
 *
 */
public abstract class DCMetaEntry extends MetaEntry {

	private boolean encoded;
	
	
	/**
	 * Indicates that a metadata entry's content is encoded,
	 * not human readable.  
	 */
	public static boolean ENCODED=true;

	/**
	 * Indicates that a metadata entry's content is human readable,
	 * not encoded.  
	 */
	public static boolean HUMAN_READABLE=false;
	
	private String encoding;
	private static String encodingAttr = "xsi:type"; 

	private String language;	
	private static String languageAttr = "xml:lang"; 
	
	/**
	 * Create a new Dublin Core metadata entry.
	 * <p>
	 * The boolean parameter specifies whether the entry
	 * is encoded or human readable. This does not have a direct
	 * effect on the content of the entry, but selects whether
	 * the "language" or the "encoding" field can be used 
	 * for this entry.
	 * This "encoded" vs. "human readable" property is set once
	 * upon creation of the object, and cannot be changed afterwards.
	 * The static fields <code>ENCODED</code> and <code>HUMAN_READABLE</code>
	 * can be used as more self-explanatory values for the parameter. 
	 * </p> 
	 * @param encoded <code>true</code> if the content of this metadata entry wil 
	 * be encoded, <code>false</code> if it will be human readable
	 * 
	 * @see #isEncoded()
	 */
	public DCMetaEntry(boolean encoded) {
		super();
		this.encoded = encoded;
	}
	
	/**
	 * Test whether the content of this metadata entry
	 * is encoded or human readable.
	 * 
	 * @return <code>true</code> if this metadata record uses 
	 * (or can use) an encoding scheme;
	 * <code>false</code> if the metadata entry contains human-readable values
	 * 
	 * @see #DCMetaEntry(boolean)
	 */
	public boolean isEncoded() {
		return encoded;
	}
	
	/**
	 * Get the encoding scheme used for this metadata entry.
	 * If this entry is not encoded (i.e. human readable), 
	 * the method will always return <code>null</code>.  
	 * 
	 * @return the encoding scheme, or <code>null</code> if the field was not set
	 * or the record is not encoded
	 *  
	 * @see #DCMetaEntry(boolean)
	 */
	public String getEncoding() {
		return isEncoded() ? encoding :null;
	}

	/**
	 * Set the encoding scheme of this entry. 
	 * <p>
	 * Note that the "encoding" field is purely informative. 
	 * Setting the encoding scheme with this method does not have 
	 * any direct effect on how the value of this metadata entry is actually encoded.
	 * </p>
	 * <p>
	 * The encoding can only be set if the metadata entry is encoded
	 * (this property is set at creation time). If the entry is not encoded (i.e. it is human readable),
	 * an exception will be thrown. 
	 * </p>
	 *   
	 * @param encoding the encoding scheme to set
	 * @throws UnsupportedOperationException if this metadata entry is not encoded
	 * @see #DCMetaEntry(boolean)
	 */
	public void setEncoding(String encoding) {
		if (isEncoded())
			this.encoding = encoding;
		else {
			throw new UnsupportedOperationException("cannot set encoding on human-readable metadata entry");
		}
	}

	/**
	 * Retrieve the language of this metadata entry.
	 * 
	 * If this entry is not human redable (i.e. encoded), 
	 * the method will always return <code>null</code>.  
	 * 
	 * @return the language code, or <code>null</code> if the field was not set
	 * or the metadata entry is encoded
	 * @see #DCMetaEntry(boolean) 
	 */
	public String getLanguage() {
		return !isEncoded() ? language : null;
	}

	/**
	 * Retrieve the language of this metadata entry, 
	 * as a displayable value in the specified locale.
	 * 
	 * If this entry is not human redable (i.e. encoded),
	 * or no language was set, 
	 * the method will return the empty string.  
	 * 
	 * @param locale the locale to use for formatting
	 * @return the language code; my be the empty string, but never <code>null</code>
	 * @see #getLanguage()
	 * @see #DCMetaEntry(boolean) 
	 */
	public String getDisplayLanguage(Locale locale) {
		String langcode = getLanguage();
		if (langcode == null) {
			return "";
		}
		else {
			return Dictionaries.getLanguageDict().getDisplayValue(langcode,locale);
		}
	}

	/**
	 * Set the language of this entry. 
	 * The value should be an ISO code for the language, but this method does
	 * not check it against a dictionary.
	 * <p>
	 * The language can only be set if the metadata entry is human readable, i.e. not encoded
	 * (this property is set at creation time). If the entry is encoded,
	 * an exception will be thrown. 
	 * </p>
	 *  
	 * @param language the language to set, or <code>null</code> to reset the value to "unassigned"
	 * @throws UnsupportedOperationException if this metadata entry is not human readable
	 *
	 * @see #DCMetaEntry(boolean)
	 * @see #setLanguageStrict(String)
	 */
	public void setLanguage(String language) {
		if (!isEncoded()) {		
			this.language = language;
		}
		else {
			throw new UnsupportedOperationException("cannot set language on encoded metadata entry");
		}
	}

	/**
	 * Set the language of this entry. 
	 * The value must be a valid ISO language code.
	 * Currently, only two-letter codes are supported. 
	 * <p>
	 * The language can only be set if the metadata entry is human readable, i.e. not encoded
	 * (this property is set at creation time). If the entry is encoded,
	 * an exception will be thrown. 
	 * </p>
	 *  
	 * @param language the language to set, or <code>null</code> to reset the value to "unassigned"
	 * @throws KeyNotInDictionaryException if the parameter value is not a valid language code
	 * @throws UnsupportedOperationException if this metadata entry is not human readable
	 *
	 * @see #DCMetaEntry(boolean)
	 * @see #setLanguage(String)
	 */
	public void setLanguageStrict(String language) throws KeyNotInDictionaryException {
		if (!isEncoded()) {
			Dictionaries.getLanguageDict().checkKey(language);
			this.language = language;
		}
		else {
			throw new UnsupportedOperationException("cannot set language on encoded metadata entry");
		}
	}



	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#readFromXml(org.w3c.dom.Element)
	 */
	@Override
	public void readFromXml(Element element) throws EpubFormatException {
		super.readFromXml(element);
		if (isEncoded()) {
			encoding = readAttributeValue(element,encodingAttr,false,false);		
		}
		else {
			language = readAttributeValue(element,languageAttr,false,false);					
		}
	}

	/* (non-Javadoc)
	 * @see org.esciurus.model.opf.PackagePart#writeToXml(org.w3c.dom.Element)
	 */
	@Override
	public void writeToXml(Element element)  {
		super.writeToXml(element);
		if (isEncoded()) {
			writeAttributeValue(element,encodingAttr,encoding,false);
		}
		else {
			writeAttributeValue(element,languageAttr,language,false);
		}
	}
	
	/* No constraint checking is provided at this time 
	 * for "encoding" and "language" attributes, in line with the 
	 * OPF specification.
	 */


}
