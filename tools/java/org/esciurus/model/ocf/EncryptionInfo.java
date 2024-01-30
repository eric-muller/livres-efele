package org.esciurus.model.ocf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EncryptionInfo {
  private String filepath;
  private String algorithm;
  private String compression;
  
  public EncryptionInfo (String filepath, String algorithm, String compression) {   
    this.filepath = filepath;
    this.algorithm = algorithm;
    this.compression = compression;
  }
  
  private static final String NAMESPACE_ENC = "http://www.w3.org/2001/04/xmlenc#";
  private static final String NAMESPACE_DEENC = "http://ns.adobe.com/digitaleditions/enc";

  public void emit (Document doc, Element parent) {
    Element e = doc.createElementNS (NAMESPACE_ENC, "EncryptedData");
    parent.appendChild (e);
    
    Element m = doc.createElementNS (NAMESPACE_ENC, "EncryptionMethod");
    m.setAttribute ("Algorithm", algorithm);
    e.appendChild (m);
    
    Element cd = doc.createElementNS (NAMESPACE_ENC, "CipherData");
    e.appendChild (cd);
    
    Element cr = doc.createElementNS (NAMESPACE_ENC, "CipherReference");
    cr.setAttribute ("URI", filepath);
    cd.appendChild (cr);
  }
}



