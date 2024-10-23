package net.efele.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.efele.epub.TransformerHandlerPlus;

import org.esciurus.model.metadata.DCDate;
import org.esciurus.model.metadata.DCPerson;
import org.esciurus.model.metadata.MetadataRecord;
import org.esciurus.model.metadata.StringEntry;
import org.esciurus.model.ocf.Container.OpenMode;
import org.esciurus.model.opf.EpubContainer;
import org.esciurus.model.opf.EpubContainerFactory;
import org.esciurus.model.opf.ManifestEntry;
import org.esciurus.model.opf.OPFPackage;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class Epub2Bml {

  //private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
  private static final String BML_NAMESPACE = "http://efele.net/2010/ns/bml";
  private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";

  private static final Attributes noAttributes = new AttributesImpl ();

  private static class FlatteningHandler extends DefaultHandler {
    @Override
    public void notationDecl (String arg0, String arg1, String arg2) throws SAXException {
      if (pass) {
        System.err.println ("notationDecl: " + arg0 + " / " + arg1 + " / " + arg2); }
    }

    @Override
    public void unparsedEntityDecl (String arg0, String arg1, String arg2,
        String arg3) throws SAXException {
      if (pass) {
        System.err.println ("unparseEntityDecl: " + arg0 + " / " + arg1 + " / " + arg2); }
    }

    private ContentHandler sink;
    private boolean pass;
    private boolean dropAttributes;

    FlatteningHandler (boolean dropAttributes, ContentHandler sink) {
      this.sink = sink;
      this.pass = false;
      this.dropAttributes = dropAttributes;
    }

    protected String dropPrefix (String s) {
      if (s.contains (":")) {
        return s.substring (s.indexOf (':') + 1); }
      return s;
    }

    //============================ ContentHandler methods
    @Override
    public void startDocument () throws SAXException {
      // ignore
    }

    @Override
    public void endDocument () throws SAXException {
      // ignore
    }

    @Override
    public void startElement (String uri, String localname, String qname, Attributes at) throws SAXException {
      if (localname.equals ("body")) {
        pass = true;
        return; }

      if (pass) {
        sink.startElement (BML_NAMESPACE, qname, dropPrefix (localname),
                           dropAttributes ? noAttributes : at); }
    }

    @Override
    public void endElement (String uri, String localname, String qname) throws SAXException {
      if (localname.equals ("body")) {
        pass = false;
        return; }

      if (pass) {
        sink.endElement (BML_NAMESPACE, qname, dropPrefix (localname)); }
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException {
      if (pass) {
        for (int i = start; i < start + length; i++) {
          if (ch [i] == '\u00a0' || ch [i] == '\u202f') {
            ch [i] = ' '; }}
        sink.characters (ch, start, length); }
    }

    @Override
    public void ignorableWhitespace (char[] ch, int start, int length) throws SAXException {
      if (pass) {
        sink.ignorableWhitespace (ch, start, length); }
    }

    @Override
    public void startPrefixMapping (String prefix, String uri) throws SAXException {
      //sink.startPrefixMapping (prefix, uri);
    }

    @Override
    public void endPrefixMapping (String prefix) throws SAXException {
      //sink.endPrefixMapping (prefix);
    }

    @Override
    public void processingInstruction (String target, String data) throws SAXException {
      if (pass) {
        sink.processingInstruction (target, data); }
    }

    @Override
    public void setDocumentLocator (Locator locator) {
      sink.setDocumentLocator (locator);
    }

    @Override
    public void skippedEntity (String name) throws SAXException {
      System.err.println ("skipped: " + name);
      if (pass) {
        sink.skippedEntity (name); }
    }

    //================== EntityResolver interface
    @Override
    public InputSource resolveEntity (String publicId, String systemId) throws IOException, SAXException {
      int slash = systemId.lastIndexOf ('/');
      String s = systemId.substring (slash+1);
      return new InputSource (new FileInputStream ("c:/users/emuller/home/eric/epub/dtd/" + s));
    }
  }

  /**
   * This method extracts the HTML content from an epub document, and
   * flattens it in a single HTML file.
   *
   * Only the <body> fragments of source HTML files are retained.
   *
   * @param epub The source epub
   * @param out The OutputStream on which to send the resulting HTML
   *
   * @throws Exception
   */
  public static void toBml (EpubContainer epub, boolean dropAttributes, OutputStream out) throws Exception {
    toBml (epub, dropAttributes, TransformerHandlerPlus.getSink (out, "yes"));
  }

  /**
   * This method extracts the HTML content from an epub document, and
   * flattens it in a single HTML file.
   *
   * Only the <body> fragments of source HTML files are retained.
   *
   * @param epub The source epub
   * @param sink To receive SAX events for the result
   *
   * @throws Exception
   */
  public static void toBml (EpubContainer epub, boolean dropAttributes, TransformerHandlerPlus sink) throws Exception {
    OPFPackage opfPackage = epub.getOPFPackage ();

    FlatteningHandler f = new FlatteningHandler (dropAttributes, sink);

    SAXParserFactory spf = SAXParserFactory.newInstance ();
    spf.setNamespaceAware (true);
    spf.setValidating (true);
    SAXParser sp = spf.newSAXParser ();

    AttributesImpl noAttributes = new AttributesImpl ();

    sink.startDocument (); {

      sink.startPrefixMapping ("", BML_NAMESPACE);

      sink.startElement (BML_NAMESPACE, "bml", "bml", noAttributes); {
        sink.startElement (BML_NAMESPACE, "page-sequences", "page-sequences", noAttributes); {

          for (ManifestEntry entry : opfPackage.getManifest ()) {
            if (entry.getMediaType ().equals ("application/xhtml+xml")) {
              String currentDoc = entry.getContainerReference ().getFilenamePart ();
              InputStream in = epub.getContentStream (entry.getContainerReference ());
              String nl = "\n\n";
              sink.characters (nl.toCharArray (), 0, nl.length ());
              sink.startElement (BML_NAMESPACE, "page-sequence", "page-sequence", noAttributes);

              try {
                sp.parse (new InputSource (in), f); }

              catch (SAXParseException e) {
                System.err.println ("SAX Parse Exception: " + e.getMessage ());
                System.err.println (" at " + currentDoc + " " + e.getLineNumber () + " " + e.getColumnNumber ()); }

              sink.endElement (BML_NAMESPACE, "page-sequence", "page-sequence"); }}

          sink.endElement ("BML_NAMESPACE", "page-sequences", "page-sequences"); }

        sink.endElement ("BML_NAMESPACE", "bml", "bml"); }

      sink.endDocument (); }
  }

  private static void usage () {
    System.err.println ("(crude) conversion of an EPUB to a BML.");
    System.err.println ("... [-o <file>] [-dropattributes] [-usage] <epub>");
    System.err.println ("BML sent to stdout by default, use \"-o <file>\" to send to a file instead.");
    System.err.println ("-dropattributes to drop all attributes on the content");
  }


  public static void main (String[] args) throws Exception {

    String inName = null;
    String outName = null;
    boolean dropAttributes = false;

    for (int i = 0; i < args.length; i++) {
      if ("-usage".equals (args [i])) {
        usage ();
        continue; }

      if ("-o".equals (args [i])) {
        i++;
        if (i >= args.length) {
          System.err.println ("missing argument after -o");
          System.exit (1); }
        outName = args [i];
        continue; }

      if ("-dropattributes".equals (args[i])) {
        dropAttributes = true;
        continue; }

      inName = args [i]; }

    if (inName == null) {
      usage ();
      System.exit (1); }


    EpubContainer epub = new EpubContainerFactory ().createContainerFromFile (new File (inName), OpenMode.EXPANDED);

    toBml (epub, dropAttributes, outName == null ? System.out :  new FileOutputStream (outName));

    epub.cleanupNow ();
  }
}
