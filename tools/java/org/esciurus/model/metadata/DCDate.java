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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.esciurus.model.ocf.ConstraintTicket;
import org.esciurus.model.opf.EntryFactory;
import org.esciurus.model.opf.EpubFormatException;
import org.w3c.dom.Element;


/**
 * A date/time field in Dublin Core metadata.
 * 
 * Differences to a standard java.util.Date object include:
 * <ul>
 * <li>The class keeps track of the precision of the date value
 * used when entering (e.g. year only, year and month, date and time 
 * including milliseconds).
 * </li>
 * 
 * <li> An optional "event" attribute can be used.</li>
 * 
 * </ul>
 *  
 * @see MetadataRecord
 *
 */
public class DCDate extends DCMetaEntry{
	
	private Date date;
	private int precision;
	private String event;
	
	
	/**
	 * date precision: year only
	 */
	public static final int PREC_YEAR=0;

	/**
	 * date precision: year and month
	 */
	public static final int PREC_MONTH=1;
	
	/**
	 * date precision: year, month, and day
	 */
	public static final int PREC_DAY=2;

	/**
	 * date precision: up to the minute
	 */
	public static final int PREC_MINUTE=3;

	/**
	 * date precision: up to the second
	 */
	public static final int PREC_SECOND=4;
	
	/**
	 * date precision: up to the millisecond
	 */
	public static final int PREC_MILLIS=5;

	
	private static final String stdTimeZone="GMT";
	
	
	/**
	 * Create a new DCDate object, with no date value set. 
	 */
	public DCDate() {
		this(null,0,null);
	}
	
	/**
	 * Create a new DCDate object with the specified date value, 
	 * precision, and event string 
	 * @param date the date/time value for the new object
	 * @param precision the date precision (use PREC_* constants as values)
	 * @param event the event text for this date field
	 */
	public DCDate (Date date, int precision, String event) {
	
		super(DCMetaEntry.ENCODED);
		
		this.precision = precision;
		this.date = date;
		this.event = event;
				
	}

	/**
	 * Create a factory that produces DCDate objects.
	 * 
	 * @return the factory created
	 * @see MetaEntryList (String,String,EntryFactory)
	 */
	public static EntryFactory<DCDate> getEntryFactory() {
		return new EntryFactory<DCDate> () {
			public DCDate createEntry() {
				return new DCDate();
			}
		};
	}

	
	/**
	 * Retrieve the date value in Java internal format.
	 * 
	 * Use jave.util.Calendar or java.text.DateTimeFormat
	 * in order to convert to the format of your choice.
	 * 
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Retrieve the event string, if any.
	 * 
	 * @return the event, or <code>null</code> if no event text is set.
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * Retrieve the precision of the date. 
	 * 
	 * @return the precision (values are taken out of the PREC_* constants)
	 */
	public int getPrecision() {
		return precision;
	}

	
	private static String getFormatPattern (int precision) {
		
		String dateFormatPattern="";
		switch (precision) {
		case PREC_YEAR:
			dateFormatPattern="yyyy"; break;
		case PREC_MONTH:
			dateFormatPattern="yyyy-MM"; break;
		case PREC_DAY:
			dateFormatPattern="yyyy-MM-dd"; break;
		case PREC_MINUTE:
			dateFormatPattern="yyyy-MM-dd'T'HH:mmZ"; break;
		case PREC_SECOND:
			dateFormatPattern="yyyy-MM-dd'T'HH:mm:ssZ"; break;
		case PREC_MILLIS:
			dateFormatPattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ"; break;
		
		}
		
		return dateFormatPattern;
		
	}
	
	/**
	 * Format this date into a standard textual form used for 
	 * file storage (in accordance with W3C recommendations).
	 * 
	 * @return the date as a text string
	 */
	public String getTextualValue() {

		String textValue = "";
		if (date != null) {
			String dateFormatPattern = getFormatPattern(precision);
			
			SimpleDateFormat fmt = new SimpleDateFormat(dateFormatPattern);
			fmt.setTimeZone( TimeZone.getTimeZone(stdTimeZone) );
			
			textValue = fmt.format(this.date);
			
			// correct time zone identifier	
			if (precision >= PREC_MINUTE) {
				int len = textValue.length();
				textValue = textValue.substring(0,len-2)+":"+textValue.substring(len-2,len);
			}
		}
		return textValue;
		
	}
	
	
	/**
	 * Parse the value of this date object from a string.
	 * The string is assumed to be formatted as specified in the OPF specification. 
	 * 
	 * @param input the input string to be parsed
	 */
	public void parseFromString(String input) {
		
		// correct time zone identifier
		
		String corrInput = input.trim();
		
		int inputLength = corrInput.length();
		if (corrInput.indexOf("T")>=0){
			if ((inputLength>3)&&(corrInput.charAt(inputLength-3)==':')) {
				corrInput = corrInput.substring(0,inputLength-3)+corrInput.substring(inputLength-2,inputLength);
			}
			else if (corrInput.charAt(inputLength-1)=='Z') {
				corrInput = corrInput.substring(0,inputLength-1)+"+0000";
			}
		}
		

		int testPrecision=PREC_MILLIS;
		boolean match = false;
		
		while ((testPrecision >= PREC_YEAR) && (!match)) {
			
			String pattern = getFormatPattern(testPrecision);
			SimpleDateFormat fmt = new SimpleDateFormat(pattern );
			fmt.setTimeZone(TimeZone.getTimeZone(stdTimeZone));
			fmt.setLenient(false);
			
			Date parsedDate = fmt.parse(corrInput, new ParsePosition(0));
			
			if (parsedDate != null) {
				this.date =  parsedDate;
				this.precision = testPrecision;
				match = true;
			}
			
			testPrecision--;
		}
		
	}

	@Override
	public String getDisplayValue(Locale locale) {

		
		DateFormat fmt=null;
		String result;
		
		if (precision <= PREC_SECOND) {
			if (precision == PREC_YEAR) {
				fmt = new SimpleDateFormat("yyyy",locale);
			}
			else if (precision == PREC_MONTH) {
				fmt = new SimpleDateFormat("MMMM yyyy",locale); 
			}
			else if (precision == PREC_DAY) {	
				fmt = DateFormat.getDateInstance(DateFormat.MEDIUM,locale); 
			}
			else if (precision == PREC_MINUTE) {
				fmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,locale);
			}
			else if (precision == PREC_SECOND) {
				fmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,locale);
			}
			
			result = fmt.format(date);
		}
		else {
			// precision == PREC_MILLIS 
			
			fmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,locale);
			result = fmt.format(date);
			
			fmt = new SimpleDateFormat(".SSS",locale);
			result += fmt.format(date);
		}
		return result;
	}

	
	private static String eventAttr="opf:event"; 
	
	@Override
	public void writeToXml(Element element) {
		
		super.writeToXml(element);
		
		writeAttributeValue(element,eventAttr,event,false);
		element.setTextContent( getTextualValue() );
		
	}

	@Override
	public void readFromXml(Element element) throws EpubFormatException {

		super.readFromXml(element);
		String dateStr = element.getTextContent();
		parseFromString(dateStr);
		
		event = readAttributeValue(element,eventAttr,false,true);
		
		
	}

	@Override
	public void checkConstraints(ConstraintTicket ticket) {
		super.checkConstraints(ticket);
		// currently no constraints for date elements
		
	}
	

}
