<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns='http://www.w3.org/1999/xhtml'
  exclude-result-prefixes="bml dc"
  version="2.0">


  <xsl:variable name='contentdate' as='xs:dateTime'>
    <xsl:choose>
      <xsl:when test='//bml:bml/bml:metadata/bml:electronique/@modification'>
        <xsl:value-of select='xs:dateTime(//bml:bml/bml:metadata/bml:electronique/@modification)'/>
      </xsl:when>
      <xsl:when test='//bml:bml/bml:metadata/bml:electronique/@creation'>
        <xsl:value-of select='xs:dateTime(//bml:bml/bml:metadata/bml:electronique/@creation)'/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select='current-dateTime ()'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- date the xslt or css has been modified -->
  <xsl:variable name='formatdate' as='xs:dateTime'
                select='xs:dateTime("2014-07-06T14:35:30-07:00")'/>

  <xsl:variable name='date' as='xs:dateTime'>
    <xsl:choose>
      <xsl:when test='$formatdate lt $contentdate'>
        <xsl:value-of select='$contentdate'/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select='$formatdate'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

<xsl:template name='tirage-date'>
  <xsl:param name="ns" required="no"/>

  <xsl:variable name='jour' select='day-from-dateTime($date)'/>

  <xsl:value-of select="$jour"/>
  <xsl:if test='$jour=1'><sup>er</sup></xsl:if>

  <xsl:text> </xsl:text>
  <xsl:choose>
    <xsl:when test='month-from-dateTime($date)=1'>janvier</xsl:when>
    <xsl:when test='month-from-dateTime($date)=2'>février</xsl:when>
    <xsl:when test='month-from-dateTime($date)=3'>mars</xsl:when>
    <xsl:when test='month-from-dateTime($date)=4'>avril</xsl:when>
    <xsl:when test='month-from-dateTime($date)=5'>mai</xsl:when>
    <xsl:when test='month-from-dateTime($date)=6'>juin</xsl:when>
    <xsl:when test='month-from-dateTime($date)=7'>juillet</xsl:when>
    <xsl:when test='month-from-dateTime($date)=8'>août</xsl:when>
    <xsl:when test='month-from-dateTime($date)=9'>septembre</xsl:when>
    <xsl:when test='month-from-dateTime($date)=10'>octobre</xsl:when>
    <xsl:when test='month-from-dateTime($date)=11'>novembre</xsl:when>
    <xsl:when test='month-from-dateTime($date)=12'>décembre</xsl:when>
  </xsl:choose>
  <xsl:text> </xsl:text>
  <xsl:value-of select='year-from-dateTime($date)'/>
</xsl:template>

<xsl:template name='tirage-date-bml'>
  <xsl:variable name='jour' select='day-from-dateTime($date)'/>

  <xsl:value-of select="$jour"/>
  <xsl:if test='$jour=1'><bml:sup>er</bml:sup></xsl:if>

  <xsl:text> </xsl:text>
  <xsl:choose>
    <xsl:when test='month-from-dateTime($date)=1'>janvier</xsl:when>
    <xsl:when test='month-from-dateTime($date)=2'>février</xsl:when>
    <xsl:when test='month-from-dateTime($date)=3'>mars</xsl:when>
    <xsl:when test='month-from-dateTime($date)=4'>avril</xsl:when>
    <xsl:when test='month-from-dateTime($date)=5'>mai</xsl:when>
    <xsl:when test='month-from-dateTime($date)=6'>juin</xsl:when>
    <xsl:when test='month-from-dateTime($date)=7'>juillet</xsl:when>
    <xsl:when test='month-from-dateTime($date)=8'>août</xsl:when>
    <xsl:when test='month-from-dateTime($date)=9'>septembre</xsl:when>
    <xsl:when test='month-from-dateTime($date)=10'>octobre</xsl:when>
    <xsl:when test='month-from-dateTime($date)=11'>novembre</xsl:when>
    <xsl:when test='month-from-dateTime($date)=12'>décembre</xsl:when>
  </xsl:choose>
  <xsl:text> </xsl:text>
  <xsl:value-of select='year-from-dateTime($date)'/>
</xsl:template>

<xsl:template name='upper'>
  <xsl:param name="s"/>
  <xsl:value-of select='translate($s,"aàâbcçčdeéèêëfghiîïjklmnoôpqrstuüûùvwxyzœæ", "AAABCÇČDEÉÈÊËFGHIIIJKLMNOOPQRSTUÜÛÙVWXYZŒÆ")'/>
</xsl:template>

<!--__________________________________________________________ cover page ___-->

<xsl:template name='efelecover'>
    <bml:page-sequence recto="true" id='page-titre'>
        <xsl:for-each select="//bml:bml/bml:metadata/bml:monographie/bml:auteur[@role='aut' or not(@role)]/bml:nom-couverture">
          <bml:h1>
            <xsl:apply-templates mode='upper'/>
          </bml:h1>
        </xsl:for-each>

        <xsl:for-each select="//bml:bml/bml:metadata/bml:article/bml:auteur[@role='aut' or not(@role)]/bml:nom-couverture">
          <bml:h1>
            <xsl:apply-templates mode='upper'/>
          </bml:h1>
        </xsl:for-each>

        <bml:vsep class="rule"/>

        <xsl:for-each select='//bml:bml/bml:metadata/bml:electronique/bml:surtitre'>
          <bml:h2><bml:s><xsl:apply-templates mode='upper'/></bml:s></bml:h2>
        </xsl:for-each>
        
        <xsl:for-each select='//bml:bml/bml:metadata/bml:electronique/bml:titre'>
          <bml:h1><xsl:apply-templates mode='upper'/></bml:h1>
        </xsl:for-each>
        
        <xsl:for-each select='//bml:bml/bml:metadata/bml:electronique/bml:soustitre'>
          <bml:h1><bml:s><xsl:apply-templates mode='upper'/></bml:s></bml:h1>
        </xsl:for-each>

        <bml:vsep class="emptyline"/>

        <xsl:apply-templates select='//bml:bml/bml:metadata/bml:monographie'
                             mode='efele-cover'/>
        <xsl:apply-templates select='//bml:bml/bml:metadata/bml:article'
                             mode='efele-cover'/>


    </bml:page-sequence>
</xsl:template>

<xsl:template match='bml:monographie' mode='efele-cover'>
  <bml:h2>
    <xsl:for-each select='bml:editeur'>
      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:otherwise> 
                <xsl:text>, </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select='bml:nom/node()'/>
    </xsl:for-each>
          <xsl:text>, </xsl:text>
          <xsl:apply-templates select='bml:date/node()'/>
  </bml:h2>
</xsl:template>

<xsl:template match='bml:article' mode='efele-cover'>
  <bml:h2>
    <xsl:apply-templates select='bml:periodique/bml:titre/node()'/>
  </bml:h2>
  <bml:h2>
    <xsl:apply-templates select='bml:numero/bml:nom/node()'/>
  </bml:h2>
  <bml:h2>
    <xsl:apply-templates select='bml:numero/bml:date/node()'/>
  </bml:h2>
</xsl:template>







<xsl:template match='text()' mode='upper'>
  <xsl:value-of select='translate(.,"aàâbcçčdeéèêëfghiîïjklmnoôpqrstuüûùvwxyzœæ", "AAABCÇČDEÉÈÊËFGHIIIJKLMNOOPQRSTUÜÛÙVWXYZŒÆ")'/>
</xsl:template>

<xsl:template match='@*|element()' mode='upper'>
  <xsl:copy>
    <xsl:apply-templates select='@*|node()' mode='upper'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
