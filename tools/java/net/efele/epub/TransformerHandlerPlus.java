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

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TransformerHandlerPlus implements TransformerHandler {

  static public TransformerHandlerPlus getSink (OutputStream file, String indent) throws Exception {
    TransformerFactory tfactory = TransformerFactory.newInstance ();
       
    if (tfactory.getFeature (SAXSource.FEATURE)) {
      SAXTransformerFactory sfactory = (SAXTransformerFactory) tfactory;

      // no transform; we just want a serializer
      TransformerHandler th = sfactory.newTransformerHandler ();

      th.setResult (new StreamResult (file));

      Transformer transformer = th.getTransformer ();
      transformer.setOutputProperty (OutputKeys.INDENT, indent);
      transformer.setOutputProperty (OutputKeys.STANDALONE, "yes");
      transformer.setOutputProperty (OutputKeys.METHOD, "xml");

      return new TransformerHandlerPlus (th); }

    return null;
  }
 

  protected TransformerHandler dest;
  protected Attributes noAttributes = new AttributesImpl ();
  
  public TransformerHandlerPlus (TransformerHandler dest) {
    this.dest = dest;
  }

  @Override
  public String getSystemId () {
    return dest.getSystemId ();
  }

  @Override
  public Transformer getTransformer () {
    return dest.getTransformer ();
  }

  @Override
  public void setResult (Result result) throws IllegalArgumentException {
    dest.setResult (result);
  }

  @Override
  public void setSystemId (String systemID) {
    dest.setSystemId (systemID);
  }

  @Override
  public void characters (char[] ch, int start, int length) throws SAXException {
    dest.characters (ch, start, length);
  }

  public void characters (String s) throws SAXException {
    dest.characters (s.toCharArray (), 0, s.length ());
  }
  
  @Override
  public void endDocument () throws SAXException {
    dest.endDocument ();
  }

  @Override
  public void endElement (String uri, String localName, String name) throws SAXException {
    dest.endElement (uri, localName, name);
  }

  public void endElement (String uri, String localName) throws SAXException {
    dest.endElement (uri, localName, localName);
  }
  
  @Override
  public void endPrefixMapping (String prefix) throws SAXException {
    dest.endPrefixMapping (prefix);
  }

  @Override
  public void ignorableWhitespace (char[] ch, int start, int length) throws SAXException {
    dest.ignorableWhitespace (ch, start, length);
  }

  @Override
  public void processingInstruction (String target, String data) throws SAXException {
    dest.processingInstruction (target, data);
  }

  @Override
  public void setDocumentLocator (Locator locator) {
    dest.setDocumentLocator (locator);
  }

  @Override
  public void skippedEntity (String name) throws SAXException {
    dest.skippedEntity (name);
  }

  @Override
  public void startDocument () throws SAXException {
    dest.startDocument ();
  }

  @Override
  public void startElement (String uri, String localName, String name, Attributes atts) throws SAXException {
    dest.startElement (uri, localName, name, atts);
  }

  public void startElement (String uri, String localName) throws SAXException {
    dest.startElement (uri, localName, localName, noAttributes);
  }

  public void startElement (String uri, String localName, Attributes atts) throws SAXException {
    dest.startElement (uri, localName, localName, atts);
  }

  public void startElement (String uri, String localName, String[] atts) throws SAXException {
    AttributesImpl at = new AttributesImpl ();
    for (int i = 0; i < atts.length; i += 2) {
      at.addAttribute ("", atts [i], atts [i], "CDATA", atts [i+1]); }
    dest.startElement (uri, localName, localName, at);
  }

  public void element (String uri, String localName) throws SAXException {
    dest.startElement (uri, localName, localName, noAttributes);
    dest.endElement (uri, localName, localName);
  }

  public void element (String uri, String localName, String content) throws SAXException {
    dest.startElement (uri, localName, localName, noAttributes);
    dest.characters (content.toCharArray (), 0, content.length ());
    dest.endElement (uri, localName, localName);
    
  }
  public void element (String uri, String localName, Attributes atts) throws SAXException {
    dest.startElement (uri, localName, localName, atts);
    dest.endElement (uri, localName, localName);
  }

  public void element (String uri, String localName, String[] atts) throws SAXException {
    startElement (uri, localName, atts);
    endElement (uri, localName);
  }

  @Override
  public void startPrefixMapping (String prefix, String uri) throws SAXException {
    dest.startPrefixMapping (prefix, uri);
  }

  @Override
  public void comment (char[] ch, int start, int length) throws SAXException {
    dest.comment (ch, start, length);
  }

  @Override
  public void endCDATA () throws SAXException {
    dest.endCDATA ();
  }

  @Override
  public void endDTD () throws SAXException {
    dest.endDTD ();
  }

  @Override
  public void endEntity (String name) throws SAXException {
    dest.endEntity (name);
  }

  @Override
  public void startCDATA () throws SAXException {
    dest.startCDATA ();
  }

  @Override
  public void startDTD (String name, String publicId, String systemId)
      throws SAXException {
    dest.startDTD (name, publicId, systemId);
  }

  @Override
  public void startEntity (String name) throws SAXException {
    dest.startEntity (name);
  }

  @Override
  public void notationDecl (String name, String publicId, String systemId)
      throws SAXException {
    dest.notationDecl (name, publicId, systemId);
  }

  @Override
  public void unparsedEntityDecl (String name, String publicId,
      String systemId, String notationName) throws SAXException {
    dest.unparsedEntityDecl (name, publicId, systemId, notationName);
  }

}
