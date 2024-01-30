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

import java.util.EnumSet;
import java.util.Locale;

import org.esciurus.common.ResourceLoader;

/**
 * Keys for bibliographic information in the OpenURL ContextObject format.
 * <p>
 * This enumerations captures the keys used in the OpenURL BibliographicInfo
 * format. All subformats (book, journal, etc.) have been captured into 
 * one enumeration. Certain fields have been removed if they duplicate
 * information contained in the other metadata fields. In particular,
 * information regarding authorship and identifiers (ISSN, ISBN, etc.),
 * as well as most date/time information, has been left out.
 * </p>
 */
public enum CtxObjectKeys {
	
	// TODO add handling for GENRE field (allowed values)
	
	/**
	 * The title of the book, the conference proceeding volume, etc.
	 * Use only if distinct from the title recorded in the DC metadata.
	 * E.g.: "Proceedings of the XCI. workshop on extraterrestrial zoology"
	 */
	BTITLE,
	
	
	/**
	 * Place of publication. "New York"
	 */
	PLACE,
	
	/**
	 * Publisher name. "Harper and Row"
	 */
	PUB,
	
	/**
	 * Date of publication. 
	 * This should be used <em>only</em> for the date required in bibliographic
	 * citations - usually the year of publication.
	 * Book dates are assumed to be a single year.
	 * <p>
	 * More specific dates (such as: date the article was published online,
	 * etc.) should be recorded in the DC metadata elements.</p> 
	 */
	DATE,
	
	/**
	 * Statement of the edition of the book. 
	 * This will usually be a phrase, with or without numbers, 
	 * but may be a single number. I.e. "First edition", "4th ed."
	 */
	EDITION,
	
	/**
	 * Total pages. Total pages is the largest recorded number of pages, 
	 * if this can be determined. I.e., "ix, 392 p." would be recorded 
	 * as "392" in tpages. This data element is usually available only 
	 * for monographs (books and printed reports). In some cases, 
	 * tpages may not be numeric, i.e. "F36"
	 */
	TPAGES,
	
	/** 
	 * The title of a series in which the book or document was issued. 
	 * There may also be an ISSN associated with the series.
	 */
	
	SERIES,
	
	/**
	 * First page number of a start/end (spage-epage) pair. 
	 * Note that pages are not always numeric.
	 */
	SPAGE,
	
	/** 
	 * Second (ending) page number of a start/end (spage-epage) pair.
	 */
	
	EPAGE,
	
	/** 
	 * Start and end pages for parts of a book, i.e. "124-147". 
	 * This can also be used for an unstructured pagination statement 
	 * when data relating to pagination cannot be interpreted as a start-end pair, 
	 * i.e. "A7, C4-9", "1-3,6". 
	 */
	PAGES,
	
	
	/**
	 * Journal title. Use the most complete title available. 
	 * Abbreviated titles, when known, are records in stitle. 
	 * "journal of the american medical association"
	 */
	JTITLE,
	
	/**
	 * Abbreviated or short journal title. 
	 * This is used for journal title abbreviations, 
	 * where known, i.e. "J Am Med Assn"
	 */
	STITLE,
	
	
	/** 
	 * Enumeration or chronology in not-normalized form, i.e. "1st quarter". 
	 * Where numeric dates are also available, place the numeric portion in 
	 * the "date" Key. So a recorded date of publication of "1st quarter 1992" 
	 * becomes date=1992&chron=1st quarter. Normalized indications of 
	 * chronology can be provided in the ssn and quarter Keys.
	 */
	CHRON,
	
	/**
	 * Season (chronology). Legitimate values are spring, summer, fall, winter
	 */
	SSN,
	
	/**
	 * Quarter (chronology). Legitimate values are 1, 2, 3, 4.
	 */
	QUARTER,
	
	/** 
	 * Volume designation. 
	 * Volume is usually expressed as a number but could 
	 * be roman numerals or non-numeric, i.e. "124", or "VI".
	 */
	VOLUME,
	
	/**
	 * Part can be a special subdivision of a volume or it can be the highest 
	 * level division of the journal. 
	 * Parts are often designated with letters or names, i.e. "B", "Supplement".
	 */
	PART,
	
	/** 
	 * This is the designation of the published issue of a journal, 
	 * corresponding to the actual physical piece in most cases. 
	 * While usually numeric, it could be non-numeric. 
	 * Note that some publications use chronology in the place of enumeration, i.e. Spring, 1998.
	 */
	ISSUE,
	
	/** 
	 * Article number assigned by the publisher. 
	 * Article numbers are often generated for publications that do not have usable pagination, 
	 * in particular electronic journal articles, i.e. "unifi000000090". 
	 */
	ARTNUM,
	
	/** 
	 * Genre of the publication. 
	 * Values depend on the KEV-Subformat chosen.
	 */
	GENRE,

	/**
	 * 	Country of publication. E.g., "United States".
	 */
	CO,
	
	/**
	 * 	Country of publication code, in ISO 2-character format. E.g., "US".
	 */
	CC,
	
	/**
	 * Institution that issued the disseration. 
	 * E.g., "University of California, Berkeley".
	 */
	INST,
	
	
	/**
	 * Degree conferred, as listed in the metadata. E.g., "PhD", "laurea".
	 */
	DEGREE,
	
	/**
	 * The patent kind code indicates the stage of the patent. 
	 * Kind codes are meaningful within a country code, ie.e. "AU A1".
	 */
	KIND,
	
	/** 
	 * Application country code; the country in which the patent 
	 * application was made (mainly used for patents pending).
	 */
	APPLCC,
	
	/** 
	 * Application number assigned to the patent, i.e. "2000028896".
	 */
	APPLNUMBER,
	
	/**
	 * Patent number. This number is usually combined 
	 * with the country code for retrieval.
	 */
	NUMBER,
	
	/** 
	 * Year the application was made. Note: for specifying the exact date 
	 * of application, use the approprite DC metadata element.
	 */
	APPLYEAR;
	

	
	/**
	 * Render this key to a keyword string used in ContextObject format.
	 * 
	 * @return keyword string for this value
	 */
	public String toCtxKey() {
		return this.name().toLowerCase();
	}

	
	/**
	 * Retrieve the set of allowed keys for a specific ContextObject format.
	 * Not all keys listed in this enumeration are allowable in each subformat
	 * (book, journal, etc.).
	 * 
	 * @param format the ContextObject format to query for
	 * @return the set of allowed keys for that format
	 * 
	 * @see BibliographicInfo#setFormat(CtxObjectFormats)
	 */
	public static EnumSet<CtxObjectKeys> getAllowedKeys(CtxObjectFormats format) {
		
		EnumSet<CtxObjectKeys> result = null;
		
		if (format == CtxObjectFormats.BOOK ) {
			result = EnumSet.of(
				BTITLE, PLACE, PUB, DATE, EDITION, TPAGES,
				SERIES, SPAGE, EPAGE, PAGES, GENRE
					);
		}
		else if (format == CtxObjectFormats.JOURNAL) {
			result = EnumSet.of(
					JTITLE, STITLE, 
					DATE, CHRON, SSN, QUARTER, VOLUME, PART, ISSUE, 
					SPAGE, EPAGE, PAGES, ARTNUM, 
					GENRE
						);			
		}
		else if (format == CtxObjectFormats.DISSERTATION) {
			result = EnumSet.of(
					CO, CC, INST, DATE, TPAGES, DEGREE
						);			
		}
		else if (format == CtxObjectFormats.PATENT) {
			result = EnumSet.of(
					CO, CC, KIND, APPLCC, 
					APPLNUMBER, NUMBER, DATE, APPLYEAR
						);			
		}
		
		return result;
		
	}

	private static String bundleName = "org.esciurus.model.dictionaries.BiblioBundle";
	
	/**
	 * Get a displayable description for this key.
	 * 
	 * @param locale the locale to use for formatting
	 * @return a displayable string for this key
	 */
	public String getDisplayName(Locale locale) {
		return ResourceLoader.getEnumDisplayName(this,bundleName,locale);
	}

}

