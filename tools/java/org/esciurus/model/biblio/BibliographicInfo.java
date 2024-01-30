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

package org.esciurus.model.biblio;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.esciurus.model.metadata.DCIdentifier;
import org.esciurus.model.metadata.MetaEntryList;
import org.esciurus.model.metadata.XMetaEntry;
import org.esciurus.model.metadata.XMetaList;
import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.opf.EpubFormatException;


/**
 * An extra metadata entry containing bibliographic information.
 * This structure captures additional bibliographic data
 * that is not contained in the standard Dublin Core 
 * metadata fields, such as: journal name, volume, issue, pagination.
 * <p>
 * This data is stored in the form of key-value pairs.
 * In XML representation, this data is stored in the "content"
 * attribute of the "meta" tag, using the OpenURL ContextObject
 * format.
 * </p>
 * 
 * @see CtxObjectKeys
 *
 */
public class BibliographicInfo extends XMetaEntry {

	
	/**
	 * Value of the "name" attribute in "meta" tags 
	 * used for bibliographic information 
	 */
	public static String xMetaName = "xmeta-bibliographicCitation";
	private static String xMetaScheme = "info:ofi/fmt:kev:mtx:ctx";
	private static String identifierScheme = "bibliographic citation";

	private static String mtxCharEncoding="UTF-8";
	
	private static String pairDelimiter = "&";
	private static String kvDelimiter = "=";
	
	private static String ctxVer = "Z39.88-2004";
	private static String ctxVerKey = "ctx_ver";
	private static String rftKeyPrefix="rft.";	
	private static String fmtKey="rft_val_fmt";

	
	private CtxObjectFormats format;
	private EnumSet<CtxObjectKeys> activeKeys;
	private EnumMap<CtxObjectKeys,String> kevMatrix; 
	
	/**
	 * Construct a new bibiliographic information record.
	 */
	public BibliographicInfo() {
		super();
		kevMatrix = new EnumMap<CtxObjectKeys,String>(CtxObjectKeys.class);
		setFormat (CtxObjectFormats.BOOK);
	}

	
	/**
	 * Retrieve the format of the publication.
	 * 
	 * @return the format.
	 */
	public CtxObjectFormats getFormat() {
		return format;
	}


	/**
	 * Set the format of the publication.
	 * 
	 * <p>
	 * <em>Note:</em> The publication format influences the set of keys
	 * which are allowed in this record. Values can be assigned only for keys 
	 * allowed for the current publication format.
	 * On changing the format, values will not be discarded
	 * even if the corresponding keys are no longer allowed for the format.
	 * Thus changing the format back and forth has no effect on the record.
	 * However, only the allowed key/value pairs will be stored to XML
	 * representation.
	 * </p>
	 * 
	 * @param format the format to set
	 * @see #getValue(CtxObjectKeys)
	 * @see CtxObjectKeys
	 */
	public void setFormat(CtxObjectFormats format) {
		this.format = format;
		this.activeKeys = CtxObjectKeys.getAllowedKeys(format);
	}

	
	/**
	 * Test whether a value for a specific key is stored in this record.
	 * 
	 * @param key the key to query for
	 * @return true if a value for that key has been set
	 */
	public boolean isValueSet(CtxObjectKeys key) {
		return kevMatrix.containsKey(key);
	}

	/**
	 * Get the value of a specific field in this record.
	 * 
	 * @param key the key to query for
	 * @return the value stored for this key, or the empty string
	 *   if no value has been set 
	 */
	public String getValue(CtxObjectKeys key) {
		String value = kevMatrix.get(key);
		return (value != null)? value : ""; 
	}


	/**
	 * Set the value for a specific key.
	 * 
	 * @param key the key to set the value for 
	 * @param value the value to be set
	 * @throws EpubFormatException if the specific key is not allowed for this publication format
	 * 
	 * @see #setFormat(CtxObjectFormats)
	 */
	public void setValue(CtxObjectKeys key, String value) throws EpubFormatException {
		if (activeKeys.contains(key)) {
			if (value != null && value.length()>0) {
				kevMatrix.put(key,value);
			}
			else {
				kevMatrix.remove(key);
			}
		}
		else {
			throw new EpubFormatException("BibliographicInfo key "+key.toString()+" not allowed for format "+format.toString());
		}
		
		// push data to DC:identifier element
		pushDCIdentifier();
	}

	
	

	@Override
	protected String getContent() {
		return toKeyValueString();
	}

	@Override
	protected String getName() {
		return xMetaName;
	}

	@Override
	protected String getScheme() {
		return xMetaScheme;
	}


	@Override
	protected void parseInput(String scheme, String content) throws EpubFormatException {
		if (xMetaScheme.equals(scheme)) {
			parseKevString(content);
		}
		else {
			throw new EpubFormatException("unknown scheme for bibligraphic data: "+scheme);
		}		
	}
	


	
	private void addKeyValue(StringBuffer buf, String key, String value) {
		buf.append(pairDelimiter);
		buf.append(key);
		buf.append(kvDelimiter);
		try {
			buf.append( URLEncoder.encode(value,mtxCharEncoding) );
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException ("unexpected internal error - unsupported encoding",e);
		}
	}
	
	private String toKeyValueString() {
		StringBuffer buf = new StringBuffer();
		
		addKeyValue(buf,ctxVerKey,ctxVer);
		addKeyValue(buf,fmtKey,format.getFormatIdentifier());
		
		for (Iterator<CtxObjectKeys> it = activeKeys.iterator(); it.hasNext();) {
			CtxObjectKeys thisKey = it.next();
			String thisVal = kevMatrix.get(thisKey);
			
			if (thisVal != null && !thisVal.equals("")) {
				addKeyValue(buf,rftKeyPrefix+thisKey.toCtxKey(),thisVal);				
			}
		}
		
		return buf.toString();
	}

		
	private void parseKevString(String content) throws EpubFormatException {
		
		String[] kvPairs = content.split(pairDelimiter);
		
		Map<String,String> kvMap = new HashMap<String,String>();
		
		for (String thisKvPair: kvPairs) {
			
			if (thisKvPair.length()>0) {
				
				int eqIndex = thisKvPair.indexOf(kvDelimiter);
				
				if (eqIndex <= 0 || eqIndex+1 >= thisKvPair.length()) {
					throw new EpubFormatException ("bad KEV pair: "+thisKvPair);
				}
				
				String key = thisKvPair.substring(0,eqIndex);
				String encValue =  thisKvPair.substring(eqIndex+1);
				
				try {
					kvMap.put(key,URLDecoder.decode(encValue,mtxCharEncoding));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("unsupported encoding",e);
				}
			}
		}
		
		String inputCtxVer = kvMap.get(ctxVerKey);
		if (!ctxVer.equals(inputCtxVer)) {
			throw new EpubFormatException("unknown CtxObject version: "+inputCtxVer);
		}
		
		
		String inputFmt = kvMap.get(fmtKey);
		try{
			setFormat( CtxObjectFormats.fromFormatIdentifier(inputFmt) );
		}
		catch (IllegalArgumentException e) {
			throw new EpubFormatException("unknown BibliographicInfo metadata format: "+inputFmt);
		}
		
		for (CtxObjectKeys thisKey: activeKeys ) {

			String inputValue = kvMap.get(rftKeyPrefix+thisKey.toCtxKey());
			if (inputValue != null && inputValue.length()>0) {
				kevMatrix.put(thisKey,inputValue);				
			}
			
		}
		
	}

	
	/* 
	 * Routines for converting bibliographic information to a displayable value
	 */

	
	private void appendSeparator(StringBuffer buf, String separator) {
		if (buf.length()>0) {
			buf.append(separator);
		}
	}
	
	private void appendValue(StringBuffer buf, CtxObjectKeys key, String separator) {
		appendValue(buf,key,separator,"","");
	}

		private void appendValue(StringBuffer buf, CtxObjectKeys key, String separator,
				String prefix, String suffix) {
		if (isValueSet(key)) {
			appendSeparator(buf,separator);
			buf.append(prefix);
			buf.append(getValue(key));
			buf.append(suffix);
		}
	}
	
	String onepagePrefix="p.";
	String manypagePrefix="pp.";
	
	private void appendPageinfo(StringBuffer buf, String separator) {
		
		
		if (isValueSet(CtxObjectKeys.ARTNUM)) {
			appendValue(buf,CtxObjectKeys.ARTNUM,separator);
		}
		else {
			String spage = getValue(CtxObjectKeys.SPAGE);
			String epage = getValue(CtxObjectKeys.EPAGE);
			
			if(spage.length()>0) {
				appendSeparator(buf,separator);
				if (epage.length()>0) {
					buf.append(manypagePrefix);
					buf.append(spage);
					buf.append("-");
					buf.append(epage);
				} 
				else {
					buf.append(onepagePrefix);
					buf.append(spage);
				}
			}
			else {
				appendValue(buf,CtxObjectKeys.PAGES,separator);
			}
		}
		
		
	}
	
	private String getDisplayJournalName(boolean abbreviated) {
		
		String result=getValue(CtxObjectKeys.JTITLE);
		
		if ( (result.length()==0) || (abbreviated && isValueSet(CtxObjectKeys.STITLE))) {
			result = getValue(CtxObjectKeys.STITLE);
		}
		
		if (isValueSet(CtxObjectKeys.PART)) {
			result += " "+getValue(CtxObjectKeys.PART);
		}
		
		return result;
	}
	
	private String getDisplayValueJournal() {
		
		StringBuffer buf=new StringBuffer();

		buf.append(getDisplayJournalName(false));
		
		appendValue(buf,CtxObjectKeys.VOLUME," ");
		appendValue(buf,CtxObjectKeys.ISSUE,"","(",")");

		appendPageinfo(buf,", ");
		
		appendValue(buf,CtxObjectKeys.DATE," ","(",")");
		
		return buf.toString();
	}
	
	
	private String getDisplayValueBook() {
		StringBuffer buf=new StringBuffer();
		
		appendValue (buf,CtxObjectKeys.BTITLE,"");
		appendValue (buf,CtxObjectKeys.EDITION,", ");
		appendValue (buf,CtxObjectKeys.SERIES,", ");
		appendValue (buf,CtxObjectKeys.PUB,", ");
		appendValue (buf,CtxObjectKeys.PLACE,", ");
		appendValue (buf,CtxObjectKeys.DATE,", ");
		appendPageinfo(buf,"; ");

		return buf.toString();
	}

	
	private String getDisplayValuePatent() {
		
		StringBuffer buf=new StringBuffer();

		// LATER better customization for patent format - or remove PATENT
		buf.append ("patent ");
		appendValue (buf,CtxObjectKeys.CC,"","","-");
		appendValue (buf,CtxObjectKeys.NUMBER,"","","");		
		appendValue (buf,CtxObjectKeys.CO,", ");
		appendValue (buf,CtxObjectKeys.DATE,", ");
		
		return buf.toString();
	}


	private String getDisplayValueDissertation() {
		StringBuffer buf=new StringBuffer();
		
		// LATER add internationalization
		appendValue (buf,CtxObjectKeys.DEGREE,"",""," thesis");
		
		appendValue (buf,CtxObjectKeys.INST,", ");
		appendValue (buf,CtxObjectKeys.CO,", ");
		appendValue (buf,CtxObjectKeys.DATE,", ");

		return buf.toString();
	}



	
	private String getDisplayValue() {
		
		String result = "";
		
		if (format==CtxObjectFormats.JOURNAL) { 
			result = getDisplayValueJournal(); 
		}
		else if (format==CtxObjectFormats.BOOK){
			
			result = getDisplayValueBook(); 
		}
		else if (format==CtxObjectFormats.DISSERTATION)  { 
			result = getDisplayValueDissertation(); 
		}
		else if (format==CtxObjectFormats.PATENT) { 
			result = getDisplayValuePatent(); 	
		}
		
		return result;
		
	}
	
	@Override
	public String getDisplayValue (Locale locale) {
		// LATER add proper i18n
		return getDisplayValue();
	}
	
	/* 
	 * Output of information to DC:identifier field
	 */
	
	private void pushDCIdentifier() {
		
		if (getParent() != null) {
			String biblioData = this.getDisplayValue();
			
			DCIdentifier biblioId = getBiblioIdentifier();
			
			biblioId.setScheme(identifierScheme);
			biblioId.setValue(biblioData);
			biblioId.setEditable(false);
		}
		
	}
	
	private DCIdentifier getBiblioIdentifier() {
		
		DCIdentifier result = null;
		
		MetaEntryList<DCIdentifier> identifierList = getParent().getMetadata().getIdentifiers();
		
		for (Iterator<DCIdentifier> it = identifierList.iterator(); it.hasNext();) {
			DCIdentifier thisIdent = it.next();
			if (identifierScheme.equals(thisIdent.getScheme())) {
				result = thisIdent;
			}
		}
		
		if (result == null) {
			result = new DCIdentifier();
			result.setEditable(false);
			identifierList.add(result);
		}
		
		return result;
		
	}


	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		super.checkConstraints(ticket);
		// currently no specific constraints
		
	}

	
	/* 
	 * convenience methods for accessing specific metadata extensions 
	 */
	
	/**
	 * Retrieve the bibliographic information from an "extra metadata" list
	 * 
	 * @param xmetaList the list of extra metadata to use
	 * @param createIfAbsent true if the bibliographic information record
	 *      should be created if it is not present
	 * @return the bibliographic information record in the list. 
	 *   May be <code>null</code> if none is found, and the createIfAbsent
	 *   parameter is <code>false</code>.
	 *  
	 */
	public static BibliographicInfo getBiblioData (XMetaList xmetaList, boolean createIfAbsent) {
		
		return (BibliographicInfo) xmetaList.getMetaByName(BibliographicInfo.xMetaName,createIfAbsent);
		
	}
	



}

