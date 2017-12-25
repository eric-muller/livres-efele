<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/2005/Atom"
    xpath-default-namespace="http://www.w3.org/2005/Atom"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="#all"
    version="2.0">

<xsl:output
    method="xml"
    indent="no"/>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match='feed/updated'>
  <updated>
    <xsl:value-of select="format-dateTime (current-dateTime(), '[Y]-[M01]-[D01]T[H01]:[m]:[s][Z]')"/></updated> 
</xsl:template>

<xsl:template match='atoms'>
  <xsl:for-each select='document(tokenize(unparsed-text("theatoms"), "( |\r?\n)"))'>
    <xsl:variable name='id' select='tokenize (entry/id, "/") [last ()]'/>

    <xsl:message><xsl:value-of select='entry/title'/></xsl:message>
    <xsl:apply-templates/>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
