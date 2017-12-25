<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE resources PUBLIC "-//NISO//DTD resource 2005-1//EN" "http://www.daisy.org/z3986/2005/resource-2005-1.dtd">
<!-- file and contents not yet tested -->

<resources xmlns="http://www.daisy.org/z3986/2005/resource/" version="2005-1">

  <scope nsuri="http://www.daisy.org/z3986/2005/ncx/">
    <nodeSet id="ns006" select="//smilCustomTest[@id='noteref']">
      <resource xml:lang="fr" id="r1">
        <text>Reference a la note</text>
      </resource>
    </nodeSet>
    <nodeSet id="ns007" select="//smilCustomTest[@id='note']">
      <resource xml:lang="fr" id="r2">
        <text>Corps de note</text>
      </resource>
    </nodeSet>
    <nodeSet id="ns006v" select="//smilCustomTest[@id='varianteref']">
      <resource xml:lang="fr" id="r10">
        <text>Reference a la variante</text>
      </resource>
    </nodeSet>
    <nodeSet id="ns007v" select="//smilCustomTest[@id='variante']">
      <resource xml:lang="fr" id="r20">
        <text>Corps de variante</text>
      </resource>
    </nodeSet>
    <nodeSet id="ns0067" select="//smilCustomTest[@id='page']">
      <resource xml:lang="fr" id="r3">
        <text>Page</text>
      </resource>
    </nodeSet>
  </scope>

<!--
  <scope nsuri="http://www.w3.org/2001/SMIL20/">   
    <nodeSet id="esns006" select="//seq[@class='pagenum']">
      <resource xml:lang="en" id="r3">
        <text>Page</text>
      </resource>
    </nodeSet>
  </scope>

  <scope nsuri="http://www.daisy.org/z3986/2005/dtbook/"> 
    <nodeSet id="ns015" select="//sidebar[@render='optional']">
      <resource xml:lang="en" id="r4">
        <text>Optional sidebar</text>
      </resource>
    </nodeSet>

    <nodeSet id="ns016" select="//table">
      <resource xml:lang="en" id="r5">
        <text>Table</text>
      </resource>
    </nodeSet>
  </scope>
-->
</resources>

