<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns="http://www.w3.org/1999/xhtml"
  version="2.0">


<xsl:output
    method="text"
    indent="no"/>

<xsl:template match='bml:script[@src]'>
  <xsl:value-of select="@src"/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match='element()'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='text()'/>

</xsl:stylesheet>
