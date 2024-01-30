/*
 *  © 2009  Eric Muller.
 *  
 *  This file is part of the net.efele.epub software.
 *
 *  net.efele.epub is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with net.efele.epub. If not, see <http://www.gnu.org/licenses/>.
 */

package net.efele.epub;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SpaceHandler {
  
  static public TransformerHandler getSink (FileOutputStream file) throws Exception {
    TransformerFactory tfactory = TransformerFactory.newInstance ();
    
    if (tfactory.getFeature (SAXSource.FEATURE)) {
      SAXTransformerFactory sfactory = (SAXTransformerFactory) tfactory;
      
      // no transform; we just want a serializer
      TransformerHandler ch = sfactory.newTransformerHandler ();

      ch.setResult (new StreamResult (file));
      
      Transformer transformer = ch.getTransformer ();
      transformer.setOutputProperty (OutputKeys.INDENT, "no");
      transformer.setOutputProperty (OutputKeys.STANDALONE, "yes");
      transformer.setOutputProperty (OutputKeys.METHOD, "xml");
      
      return ch; }

      return null;
  }

  private static class Processor extends DefaultHandler {
    
    private TransformerHandler sink = null;
    private FileOutputStream sinkStream = null;
    
    private StringBuffer pendingChars = new StringBuffer ();
    private int nbCharsInBlock;
   
    char nnbsp;
    boolean nonefele;

    public Processor (String mode) {

      nnbsp= '\u202f';
      nonefele = false;

      if ("plain".equals (mode)) {
        nnbsp = '\u00a0'; }
      else if ("nonefele".equals (mode)) {
        nonefele = true; }
    }
    
    public void startPrefixMapping (String arg0, String arg1) throws SAXException {
      sink.startPrefixMapping (arg0, arg1);
    }

    public void endPrefixMapping (String prefix) throws SAXException {
      sink.endPrefixMapping (prefix);
    }

    public void startDocument () throws SAXException {
      sink.startDocument ();
    }

    public void endDocument () throws SAXException {
      sink.endDocument ();
    }

    private boolean matches (String s, StringBuffer sb, int start) {

      if (start + s.length () > sb.length ()) {
        return false; }

      for (int k = 0; k < s.length (); k++) {
        if (s.charAt (k) != sb.charAt (start + k)) {
          return false; }}

      return true;    
    }

    private void fix (StringBuffer sb) {
          
      for (int i = 0; i < sb.length (); i++) {
        if (sb.charAt (i) == '\u2015') {
          sb.setCharAt (i, '\u2014'); }}

      if (nbCharsInBlock == 0) {
        if (     matches ("\u00ab \u2014 ", sb, 0)
              || matches ("\u00bb \u2014 ", sb, 0)) {
          sb.setCharAt (1, nnbsp);
          sb.setCharAt (3, nnbsp); }
        else if (   matches ("\u00ab ", sb, 0)
                 || matches ("\u00bb ", sb, 0)
                 || matches ("\u2014 ", sb, 0)) {
          sb.setCharAt (1, nnbsp); }}
      
      for (int i = 0; i < sb.length (); i++) {
        if (   matches ("\u00ab ", sb, i)
            || matches ("\u201c ", sb, i)) {
          sb.setCharAt (i+1, nnbsp); }

        else if (   matches (" :", sb, i)
                 || matches (" ;", sb, i)
                 || matches (" ?", sb, i)
                 || matches (" !", sb, i)
                 || matches (" %", sb, i)
                 || matches (" ,", sb, i)   // old orthography; see Holbach
                 || matches (" \u00bb", sb, i)
                 || matches (" \u201d", sb, i)) {
          sb.setCharAt (i, nnbsp); }

        else if (matches ("\u2019 \u00ab", sb, i)) {
          sb.setCharAt (i+1, nnbsp); }

        else if (i + 2 < sb.length ()
                 && "0123456789".indexOf (sb.charAt (i)) != -1 
                 && sb.charAt (i+1) == ' '
                 && "0123456789".indexOf (sb.charAt (i+2)) != -1) {
          sb.setCharAt (i+1, nnbsp); }
        
        else if (sb.charAt (i) == '\u2026') {
          sb.deleteCharAt (i);
          sb.insert (i, '.');
          sb.insert (i, '.');
          sb.insert (i, '.'); }

        else if (sb.charAt (i) == '\u202F') {
          sb.setCharAt (i, nnbsp); }}
    }
    
    private void flushChars () throws SAXException {
      
      if (pendingChars.length () == 0) {
        return; }
      
      fix (pendingChars);

      char[] chars = new char [pendingChars.length ()];
      pendingChars.getChars (0, pendingChars.length(), chars, 0);

      sink.characters (chars, 0, chars.length);

      nbCharsInBlock += chars.length;
      pendingChars.setLength (0);     
    }
    
    public void characters (char[] chs, int start, int length) throws SAXException {
      pendingChars.append (chs, start, length);
    }
    
   
    public void processingInstruction (String piName, String piData) throws SAXException {
      sink.processingInstruction (piName, piData);
    }
    
    static private Set<String> blockElements;
    static {
      blockElements = new HashSet<String> ();
      blockElements.add ("auteur");
      blockElements.add ("cast");
      blockElements.add ("colnum");
      blockElements.add ("date");
      blockElements.add ("h1");
      blockElements.add ("h2");
      blockElements.add ("h3");
      blockElements.add ("l");
      blockElements.add ("li");
      blockElements.add ("nom");
      blockElements.add ("nom-couverture");
      blockElements.add ("noteref");
      blockElements.add ("p");
      blockElements.add ("PI");
      blockElements.add ("pagenum");
      blockElements.add ("pageref");
      blockElements.add ("salutation");
      blockElements.add ("signature");
      blockElements.add ("soustitre");
      blockElements.add ("speaker");
      blockElements.add ("surtitre");
      blockElements.add ("td");
      blockElements.add ("titre");
      blockElements.add ("titre-catalogue");
      blockElements.add ("ville");
    }

    public void startElement (String uri, String qname, String localname, Attributes at) throws SAXException { 
      flushChars ();
      if (blockElements.contains (localname)) {
        nbCharsInBlock = 0; }

      sink.startElement (uri, qname, localname, at);
    }

    public void endElement (String uri, String qname, String localname) throws SAXException { 
      flushChars ();             
      sink.endElement (uri, qname, localname);
    }

    public InputSource resolveEntity (String arg0, String arg1) throws SAXException, IOException {
      return new InputSource (new ByteArrayInputStream (new byte [0]));
    }


    void process (File inputFile, File outputFile) throws Exception {
      sinkStream = new FileOutputStream (outputFile);
      sink = getSink (sinkStream);

      InputStream in = new FileInputStream (inputFile);
      SAXParserFactory spf = SAXParserFactory.newInstance ();
      spf.setNamespaceAware (true);
      spf.setValidating (false);

      SAXParser sp = spf.newSAXParser ();
      
      try {      
        sp.parse (new InputSource (in), this); }
      catch (SAXParseException e) {
        System.err.println (inputFile.getName () + "/" + e.getLineNumber () + ": " + e.getMessage ()); 
        System.exit (1); }
      
      sinkStream.close ();
    }
  }
  
  public static void main (String[] args) throws Exception {
    new Processor (args[0]).process (new File (args[1]), new File (args[2]));
  }
}
