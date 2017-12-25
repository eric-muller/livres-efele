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

    <xsl:variable name='age' select='xs:integer(days-from-duration(current-dateTime() - xs:dateTime(entry/updated)))'/>

    <xsl:variable name='id' select='tokenize (entry/id, "/") [last ()]'/>

    <xsl:if test='($age lt 30) and matches ($id, "^[0-9].*")'>
      <xsl:message><xsl:value-of select='entry/title'/></xsl:message>
      <xsl:apply-templates/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
