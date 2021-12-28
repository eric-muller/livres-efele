<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="bml dc"
  version="2.0">

<xsl:output
    method="xml"
    indent="no"
    omit-xml-declaration="no"/>

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
<!-- do not announce modifications
  <xsl:choose>
    <xsl:when test='bml:electronique[@modification]'>
      <xsl:call-template name='newsfeedentry'>
        <xsl:with-param name='date'><xsl:value-of select='bml:electronique/@modification'/></xsl:with-param>
        <xsl:with-param name='type'>u</xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
-->
      <xsl:call-template name='newsfeedentry'>
        <xsl:with-param name='date'><xsl:value-of select='bml:electronique/@creation'/></xsl:with-param>
        <xsl:with-param name='type'>n</xsl:with-param>
      </xsl:call-template>
<!--
    </xsl:otherwise>
  </xsl:choose>
-->
</xsl:template>


<xsl:template name='newsfeedentry'>
  <xsl:param name='date'/>
  <xsl:param name='type'/>

  <xsl:variable name='author'>
    <xsl:for-each select='bml:*/bml:auteur[not(@role) or @role="aut"]'>
      <xsl:call-template name='list-sep'/>
      <xsl:value-of select='bml:nom-couverture'/>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name='title'>
    <xsl:value-of select='bml:electronique/bml:titre'/>
  </xsl:variable>

  <xsl:variable name='uniqueid'>
    <xsl:value-of select='bml:electronique/@identificateur'/>
  </xsl:variable>

  <xsl:variable name='publisher'>
    <xsl:for-each-group select='bml:monographie/bml:editeur'
                        group-by='bml:ville'>

      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
      </xsl:choose>

      <xsl:for-each select='current-group()'>
        <xsl:choose>
          <xsl:when test='position() = 1'/>
          <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
        </xsl:choose>

        <xsl:value-of select='bml:nom'/>
      </xsl:for-each>

      <xsl:text>, </xsl:text>
      <xsl:value-of select='bml:ville'/>
    </xsl:for-each-group>

    <xsl:text>, </xsl:text>
    <xsl:value-of select='bml:monographie/bml:date'/>
  </xsl:variable>

  <entry xmlns="http://www.w3.org/2005/Atom">
    <id><xsl:value-of select='$uniqueid'/></id>

    <title>
      <xsl:choose>
        <xsl:when test='$type="n"'>Nouveauté : </xsl:when>
        <xsl:otherwise>Mise à jour : </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select='$author'/>
      <xsl:text> - </xsl:text>
      <xsl:value-of select='$title'/>
      <xsl:if test='bml:electronique/bml:soustitre'>
        <xsl:text> — </xsl:text>
        <xsl:value-of select='bml:electronique/bml:soustitre'/>
      </xsl:if>      

    </title>

    <updated><xsl:value-of select='$date'/></updated>

    <content type='xhtml'>
      <div xmlns='http://www.w3.org/1999/xhtml'>
    <table>
      <tr>
        <td>
          <img style='vertical-align:center; padding: 0 2em 0 2em; margin:0;' alt='couverture' src='{$uniqueid}/thumbnail.png'/>
        </td>

        <td>
          <p style='font-size:1.2em; padding:0; margin:0;'><i><xsl:value-of select='$author'/></i></p>
          <div style='padding:1em 0 1em 0; margin:0;'>
            <p style='font-size:1.5em; padding:0; margin:0;'><xsl:value-of select='$title'/></p>
            <xsl:if test='bml:electronique/bml:soustitre'>
              <p style='font-size:1.2em; padding: 0; margin:0'><xsl:value-of select='bml:electronique/bml:soustitre'/>
              </p>
            </xsl:if>
          </div>

          <xsl:if test='bml:*/bml:auteur[@role="edt"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Édition : </xsl:text>
              <xsl:for-each select='bml:*/bml:auteur[@role="edt"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>

          <xsl:if test='bml:*/bml:auteur[@role="trl"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Traduction : </xsl:text>
              <xsl:for-each select='bml:*/bml:auteur[@role="trl"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>

          <xsl:if test='bml:*/bml:auteur[@role="ill"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Illustrations : </xsl:text>
              <xsl:for-each select='bml:*/bml:auteur[@role="ill"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>
          
          <xsl:if test='bml:*/bml:auteur[@role="aui"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Préface : </xsl:text>
              <xsl:for-each select='bml:*/bml:auteur[@role="aui"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>
          
          <xsl:apply-templates select='bml:monographie|bml:article' mode='publisher'/>
        </td>
      </tr>
    </table>
    
    <p>Disponible sur le site <a href='{bml:electronique/@identificateur}'>ÉFÉLÉ</a>.</p>
      </div>
    </content>
  </entry>
</xsl:template>



<xsl:template match='bml:monographie' mode='publisher'>
  <p style='padding:0; margin:0;' xmlns='http://www.w3.org/1999/xhtml'>
  <xsl:for-each-group select='bml:editeur' group-by='bml:ville'>
    <xsl:choose>
      <xsl:when test='position() = 1'/>
      <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
    </xsl:choose>
    
    <xsl:for-each select='current-group()'>
      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
      </xsl:choose>
      
      <xsl:value-of select='bml:nom'/>
    </xsl:for-each>
    
    <xsl:text>, </xsl:text>
    <xsl:value-of select='bml:ville'/>
  </xsl:for-each-group>
  
  <xsl:text>, </xsl:text>
  <xsl:value-of select='bml:date'/>
  </p>
</xsl:template>

<xsl:template match='bml:article' mode='publisher'>
  <p style='padding:0; margin:0;'  xmlns='http://www.w3.org/1999/xhtml'>
    <xsl:apply-templates select='bml:periodique/bml:titre'/>
  </p>
  <p style='padding:0; margin:0;'  xmlns='http://www.w3.org/1999/xhtml'>
    <xsl:apply-templates select='bml:numero/bml:nom'/>
  </p>
  <p style='padding:0; margin:0;' xmlns='http://www.w3.org/1999/xhtml'>
    <xsl:apply-templates select='bml:numero/bml:date'/>
  </p>
</xsl:template>

<xsl:template match='bml:sup'>
  <sup><xsl:apply-templates/></sup>
</xsl:template>

</xsl:stylesheet>
