<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:opf="http://www.idpf.org/2007/opf"
  xmlns="http://www.w3.org/1999/xhtml" 
  exclude-result-prefixes="bml"
  version="3.0">

<xsl:output
    method="text"
    indent="no"/>

<xsl:template match='bml:fonts[@href]'>
  <xsl:apply-templates select='document (@href)'/>
</xsl:template>

<xsl:template match='bml:font'>
  <xsl:value-of select='@u'/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match='text()'/>

</xsl:stylesheet>
