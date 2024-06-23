package net.efele.epub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import org.esciurus.model.metadata.DCPerson;
import org.esciurus.model.metadata.DCDate;
import org.esciurus.model.metadata.MetadataRecord;
import org.esciurus.model.metadata.StringEntry;
import org.esciurus.model.ocf.Container.OpenMode;
import org.esciurus.model.opf.EpubContainer;
import org.esciurus.model.opf.EpubContainerFactory;

public class Epub2Meta {

  
  static Map<String, String> roles;
  static {
    roles = new HashMap<String, String> ();
    roles.put ("edt", "éditeur");
    roles.put ("ill", "illustrateur");
    roles.put ("trl", "traducteur");
  }
  
  private static int count = 0;
  private static int failures = 0;
  
  public static void explore (File f, String loc, PrintStream outStream) throws Exception {
    if (f.isDirectory ()) {
        for (File ff: f.listFiles ()) {
          explore (ff, loc + f.getName () + "/", outStream); }}
    
    else  if (f.getName().endsWith (".epub")) {
      toMeta (f, loc + f.getName (), outStream); }
  }
  
  public static String c (String s) {
    return s.replace ("\n", " ").replace ("&", "&amp;").replace ("\"", "\\\"").trim ();
  }
  
  public static void toMeta (File f, String loc, PrintStream outStream) {
    String prefix; 
    Locale locale = new Locale ("fr");

    try {
      EpubContainer epub = new EpubContainerFactory ().createContainerFromFile (f, OpenMode.PREVIEW);

      MetadataRecord meta = epub.getOPFPackage ().getMetadata ();
      
      if (count != 0) { 
          outStream.println (","); }
      
      outStream.println ("[");
      
      outStream.println ("  \"" + loc + "\",");

      outStream.print ("  \"");
      prefix = "";
      for (StringEntry title : meta.getTitles ()) {
        outStream.print (prefix);
        outStream.print (c (title.getDisplayValue (locale)));
        prefix = " - "; }
      outStream.println ("\",");

      
      outStream.print ("  \"");
      prefix = "";
      for (DCPerson creator : meta.getCreators ()) {
        String s = creator.getFileAs ();
        if (s == null) {
          s = creator.getName (); }
        if (s == null) {
          s = "?"; }
        
        outStream.print (prefix);
        prefix = ", ";
        outStream.print (c (s));
        String role = creator.getRole ();
        if (role != null && ! role.equals ("aut")) {
          outStream.print (" [" + roles.get (creator.getRole ()) + "]"); }}
      outStream.println ("\",");

      outStream.print ("  \"");
      prefix = "";
      for (StringEntry lang : meta.getLanguages ()) {
        outStream.print (prefix);
        prefix = " ";
        outStream.print (c (lang.getContent ())); }
      outStream.println ("\",");

      outStream.print ("  \"");
      prefix = "";
      for (StringEntry publisher: meta.getPublishers ()) {
        outStream.print (prefix);
        prefix = ", ";
        outStream.print (c (publisher.getContent ())); }
      outStream.println ("\",");

      outStream.print ("  \"");
      prefix = "";
      for (DCDate date : meta.getDates ()) {
        outStream.print (prefix);
        prefix = ", ";
        outStream.print (date.getYear (locale)); }
      outStream.println ("\",");
      
//      outStream.print ("  <td>");
//      prefix = "";
//      if (contributor == null) {
//        for (DCPerson contributor: meta.getContributors ()) {
//          outStream.print (prefix);
//          prefix = "<br>";
//          outStream.print (contributor.getName ().replace ("&", "&amp;")); }}
//      else {
//        outStream.print (contributor); }
//      outStream.println ("</td>");

      outStream.print ("  ]");

      count++;
      epub.cleanupNow (); }
    
    catch (Exception e) {
      System.err.println (f.getPath ());
      System.err.println ("   " + e.getMessage () + " " + e.getStackTrace()[0].getMethodName () + " line " + e.getStackTrace()[0].getLineNumber());
      System.err.println ("");
//      e.printStackTrace ();
      failures++; }
}


  
  private static PrintStream startOutput (PrintStream o) throws Exception {
    return o;
  }
  
  private static void endOutput (PrintStream o) throws Exception {
    o.close ();
  }

  public static void main (String[] args) throws Exception {
    
    PrintStream outStream = null;
    outStream = new PrintStream (new FileOutputStream (new File ("books-epub.json")), true, "UTF-8");

    startOutput (outStream);
    for (int i = 0; i < args.length; i++) {
      explore (new File (args [i]), "", outStream); }
    endOutput (outStream);

    System.out.println (count + " books "  + failures + " failures");
  }
}

