<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:opf="http://www.idpf.org/2007/opf"
  xmlns="http://www.w3.org/1999/xhtml" 
  exclude-result-prefixes="bml"
  version="2.0">


<xsl:include href='metadata.xml'/>

<xsl:output method='text'/>

<xsl:template match='bml:pagenum[@a]'>
  <xsl:variable name='ab'>
    <xsl:value-of select='@b'/>
    <xsl:text>|</xsl:text>
    <xsl:value-of select='@a'/>
  </xsl:variable>

  <xsl:variable name='u'>
     <xsl:value-of select='.'/>
  </xsl:variable>

  <xsl:value-of select='substring(concat($ab,"                                            "), 1, 30)'/>
  <xsl:value-of select='substring(concat($u,"                                            "), 1, 30)'/>
  <xsl:value-of select='//bml:metadata/bml:monographie/bml:titre'/>
  <xsl:text>
</xsl:text>
</xsl:template>


<xsl:template match='*|text()'>
  <xsl:apply-templates/>
</xsl:template>

 
</xsl:stylesheet>

