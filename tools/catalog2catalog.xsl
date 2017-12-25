<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns='http://www.w3.org/1999/xhtml'
  exclude-result-prefixes='bml'
  version="2.0">


<xsl:include href="bml-common.xsl"/>

<xsl:output
    method="text"
    encoding="UTF-16BE"/>

<xsl:template match="bml:collection">

  <xsl:value-of select="bml:author"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select="bml:titre"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select="@id"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select='@creation'/>
  <xsl:if test='@modification'>
    <xsl:text>&#x9;</xsl:text>
    <xsl:value-of select='@modification'/>
  </xsl:if>

  <xsl:text>
</xsl:text>
</xsl:template>

</xsl:stylesheet>
