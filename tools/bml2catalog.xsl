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

<xsl:template match="bml:bml">
  <xsl:apply-templates select='bml:metadata'/>
</xsl:template>

<xsl:template name='list-sep'>
      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:when test='position() = last()'><xsl:text>&lt;br&gt;</xsl:text></xsl:when>
        <xsl:otherwise> <xsl:text>&lt;br&gt;</xsl:text></xsl:otherwise>
      </xsl:choose>
</xsl:template>



<xsl:template match='bml:metadata[bml:*/bml:langue = "fr"]'>
  <xsl:variable name='author'>
    <xsl:for-each select='bml:*/bml:auteur[not(@role) or @role="aut"]'>
      <xsl:call-template name='list-sep'/>
      <xsl:value-of select='bml:nom-bibliographie'/>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name='title'>
    <xsl:choose>
      <xsl:when test='bml:electronique/bml:titre-catalogue'>
        <xsl:value-of select='bml:electronique/bml:titre-catalogue'/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select='bml:electronique/bml:titre'/>
        <xsl:if test='bml:electronique/bml:soustitre'>
          <xsl:text> / </xsl:text>
          <xsl:value-of select='bml:electronique/bml:soustitre'/>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name='uniqueid'>
    <xsl:value-of select='bml:electronique/@identificateur'/>
  </xsl:variable>


  <xsl:value-of select="$author"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select="$title"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select="$uniqueid"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select='bml:electronique/@creation'/>
  <xsl:if test='bml:electronique/@modification'>
    <xsl:text>&#x9;</xsl:text>
    <xsl:value-of select='bml:electronique/@modification'/>
  </xsl:if>

  <xsl:text>
</xsl:text>
</xsl:template>


<xsl:template match='bml:metadata'/>

</xsl:stylesheet>
