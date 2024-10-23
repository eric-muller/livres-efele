package net.efele.epub;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.Security;


public class ObfuscatedStream extends FilterOutputStream {
  static public enum Method {
    NONE, ADOBE, IDPF
  }


  int i = 0;
  Method method;
  byte[] key;


  public static byte[] makeObfuscationKey (Method method, String uniqueid) throws Exception {
    if (method == Method.IDPF)  {
      //      Security.addProvider (new com.sun.crypto.provider.SunJCE ());
      MessageDigest sha = MessageDigest.getInstance ("SHA-1");
      byte[] b = uniqueid.getBytes ("UTF-8");
      sha.update (b, 0, b.length);
      byte[] c = sha.digest();
      return c; }

    else if (method == Method.ADOBE) {
      if (! uniqueid.startsWith ("urn:uuid:")) {
        throw new Exception (); }

      ByteArrayOutputStream mask = new ByteArrayOutputStream();

      int acc = 0;
      int len = uniqueid.length();
      for (int i = 9; i < len; i++) {
        char c = uniqueid.charAt(i);
        int n;
        if ('0' <= c && c <= '9')
          n = c - '0';
        else if ('a' <= c && c <= 'f')
          n = c - ('a' - 10);
        else if ('A' <= c && c <= 'F')
          n = c - ('A' - 10);
        else
          continue;
        if (acc == 0) {
          acc = 0x100 | (n << 4); }
        else {
          mask.write(acc | n);
          acc = 0; }}

      if (mask.size() != 16) {
        throw new Exception (); }
      return  mask.toByteArray(); }

    else {
      return null; }
  }


  public ObfuscatedStream (Method method, String uniqueId, OutputStream out) throws Exception {
    super (out);
    this.method = method;
    this.key = makeObfuscationKey (method, uniqueId);
  }

  public void write (byte[] b) throws IOException {
    write (b, 0, b.length);
  }

  public void write (byte[] b, int off, int len) throws IOException {
    for (int i = off; i < off + len; i++) {
      write (b [i]); }
  }

  public void write (int val)  throws IOException {
    byte b = (byte) val;

    if (   (method == Method.ADOBE && i < 1024)
        || (method == Method.IDPF && i < 1040)) {
        b = (byte) (b ^ key [i % key.length]); }

    i++;
    super.write (b);
  }


  public static void main (String[] args) throws Exception {
    InputStream in = new FileInputStream (args [0]);
    OutputStream out = new ObfuscatedStream (Method.ADOBE, "urn:uuid:e9c194cf-2639-4ba4-be51-f8a87959fed0", new FileOutputStream (args [1]));

    int n;
    byte b[] = new byte [1024];

    while ((n = in.read (b)) > 0) {
      out.write (b, 0, n); }

    in.close ();
    out.close ();
  }
}
