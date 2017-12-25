<?xml version='1.0' encoding='UTF-8'?>

<xsl:stylesheet 
  xmlns="http://efele.net/2010/ns/bml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml='http://efele.net/2010/ns/bml'
  version="2.0">

<xsl:import href="../../../tools/toefele.xsl"/>

<xsl:param name='review'>no</xsl:param>

<xsl:template match='bml:oa'>
  <xsl:choose>
    <xsl:when test='$review="yes"'><u>a</u></xsl:when>
    <xsl:otherwise>o</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match='bml:OA'>
  <xsl:choose>
    <xsl:when test='$review="yes"'><u>A</u></xsl:when>
    <xsl:otherwise>O</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match='bml:stage'>
  <bml:stage>(<xsl:apply-templates/>)</bml:stage>
</xsl:template>

<xsl:template match='bml:pstage'>
  <bml:pstage>(<xsl:apply-templates/>)</bml:pstage>
</xsl:template>

</xsl:stylesheet>
