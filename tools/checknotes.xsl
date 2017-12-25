<?xml version='1.0' encoding='UTF-8'?>


<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:bml="http://efele.net/2010/ns/bml"
  version="2.0">

<xsl:output 
  method="text"
  indent="no"
  encoding="UTF-8"
  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
  doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>


<xsl:template match='bml:metadata/bml:electronique/bml:note'/>

<xsl:template match='bml:note'>
  <xsl:variable name='id' select='@id'/>

  <xsl:if test='count(//bml:note[@id=$id]) != 1'>
    <xsl:message>
      <xsl:value-of select='/bml:bml/bml:metadata/bml:electronique/@identificateur'/>
      <xsl:text>Multiple notes </xsl:text><xsl:value-of select='@id'/>
    </xsl:message>
  </xsl:if>

  <xsl:if test='count(//bml:noteref[@noteid=$id]) = 0'>
    <xsl:message>
      <xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/@identificateur'/>
      <xsl:text>no references to  </xsl:text><xsl:value-of select='@id'/>
    </xsl:message>
  </xsl:if>

  <xsl:if test='count(//bml:noteref[@noteid=$id]) > 1'>
    <xsl:message>
      <xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/@identificateur'/>
      <xsl:text>multiple references to  </xsl:text><xsl:value-of select='@id'/>
    </xsl:message>
  </xsl:if>

  <xsl:apply-templates/>
</xsl:template>


<xsl:template match='bml:noteref'>
  <xsl:variable name='id' select='@noteid'/>

  <xsl:if test='count(//bml:note[@id=$id]) = 0'>
    <xsl:message>
      <xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/@identificateur'/>
      <xsl:text>missing note </xsl:text><xsl:value-of select='@noteid'/>
    </xsl:message>
  </xsl:if>

  <xsl:if test='count(//bml:note[@id=$id]) > 1'>
    <xsl:message>
      <xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/@identificateur'/>
      <xsl:text>ambiguous R </xsl:text><xsl:value-of select='@noteid'/>
    </xsl:message>
  </xsl:if>
</xsl:template>

<xsl:template match='text()'/>

</xsl:stylesheet>

