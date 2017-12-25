<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  exclude-result-prefixes="#all"
  version="2.0">

<xsl:output 
  method="xml"
  indent="no"
  encoding="UTF-8"/>
 
<xsl:template match="bml:pagenum[@a]">
  <xsl:apply-templates/>
  <bml:pagenum num='{@num}'/>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
