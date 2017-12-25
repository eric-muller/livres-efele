<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="bml dc"
  version="2.0">

<xsl:param name='file'/>

<xsl:output
    method="xml"
    indent="yes"/>

<xsl:template match="bml:bml">
  <entry>
    <xsl:apply-templates select='bml:metadata'/>
  </entry>
</xsl:template>




<xsl:template match="/">
  <xsl:apply-templates select='//bml:metadata'/>
</xsl:template>



<xsl:template name='list-sep'>
      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:when test='position() = last()'> <xsl:text> et </xsl:text></xsl:when>
        <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
      </xsl:choose>
</xsl:template>




<xsl:template match='bml:metadata'>
  <xsl:call-template name='entry'/>
</xsl:template>


<xsl:template name='entry'>

  <xsl:variable name='uniqueid'>
    <xsl:value-of select='bml:electronique/@identificateur'/>
  </xsl:variable>

  <xsl:variable name='title'>
    <xsl:value-of select='bml:electronique/bml:titre'/>
  </xsl:variable>

  <xsl:variable name='author'>
    <xsl:for-each select='bml:monographie/bml:auteur[not(@role) or @role="aut"]'>
      <xsl:call-template name='list-sep'/>
      <xsl:value-of select='bml:nom-couverture'/>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name="base">
    <xsl:value-of select='$uniqueid'/>
  </xsl:variable>

  <xsl:variable name='thumbnail'>
    <xsl:text>thumbnail.png</xsl:text>
  </xsl:variable>

  <entry xmlns="http://www.w3.org/2005/Atom">
    <id><xsl:value-of select='$uniqueid'/></id>

    <title>
      <xsl:value-of select='$title'/>
    </title>

    <author>
      <name>
        <xsl:value-of select='$author'/>
      </name>
    </author>

    <dc:language><xsl:value-of select='bml:monographie/bml:langue'/></dc:language>

    <dc:issued><xsl:value-of select='bml:monographie/bml:date'/></dc:issued>

    <link rel="http://opds-spec.org/image/thumbnail"
          href="{$base}/{$thumbnail}"
          type="image/png"/>

    <link rel="http://opds-spec.org/acquisition"
          href="{$base}/{$file}.epub"
          type="application/epub+zip"/>

    <link rel="http://opds-spec.org/acquisition"
          href="{$base}/{$file}.mobi"
          type="application/x-mobipocket-ebook"/>
  </entry>

</xsl:template>


</xsl:stylesheet>
