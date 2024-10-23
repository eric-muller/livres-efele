package net.efele.epub;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.adobe.fontengine.font.Font;
import com.adobe.fontengine.fontmanagement.FontLoader;
import com.ibm.icu.text.UCharacterIterator;

public class FontEmbedder {

  public static Set<Integer> chars = new TreeSet<Integer> ();

  private static class CharacterCollector extends DefaultHandler {

    public void characters (char[] chs, int start, int length) throws SAXException {
      UCharacterIterator it = UCharacterIterator.getInstance (chs, start, start+length);
      while (true) {
        int usv = it.nextCodePoint ();
        if (usv == UCharacterIterator.DONE) {
          break; }
        chars.add (usv); }
    }

    //================== EntityResolver interface
    @Override
    public InputSource resolveEntity (String publicId, String systemId) throws IOException, SAXException {
      return new InputSource (new ByteArrayInputStream (new byte [0]));
    }
  }

  static CharacterCollector collector = new CharacterCollector();

  public static void collect (File f) throws Exception {

    if (f.isDirectory ()) {
      File[] list = f.listFiles ();
      for (File g : list) {
        collect (g); }}

    else if (f.getName ().endsWith ("xhtml")) {
      SAXParserFactory spf = SAXParserFactory.newInstance ();
      spf.setNamespaceAware (true);
      spf.setValidating (true);
      SAXParser sp = spf.newSAXParser ();

      try {
        sp.parse (new InputSource (f.getAbsolutePath()), collector); }
      catch (SAXParseException e) {
        System.err.println (f.getName() + "/" + e.getLineNumber () + ": " + e.getMessage ());
        System.exit (1); }}
  }


  public static void main (String[] args) throws Exception {

    collect (new File (args[0]));

    File targetDir = new File (args[1]);
    targetDir.mkdir ();

    for (int i = 2; i < args.length; i++) {
      FontLoader fl = new FontLoader ();
      Font f = fl.load (new File ("../../../fonts/" + args[i]).toURI ().toURL ()) [0];

      f.getSWFFont4Description ().streamFontData (chars.iterator (), new FileOutputStream (args[1] + "/" + (new File (args[i])).getName ())); }
  }
}
