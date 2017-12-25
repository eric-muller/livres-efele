<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns='http://www.w3.org/1999/xhtml'
  exclude-result-prefixes='bml'
  version="2.0">


<xsl:include href="bml-common.xsl"/>

<xsl:output
    method="xml"
    omit-xml-declaration="yes"
    encoding="UTF-8"
    indent="no"/>

<xsl:template match="bml:bml">
    <xsl:apply-templates select='bml:metadata'/>
</xsl:template>

<xsl:template name='list-sep'>
      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:when test='position() = last()'> <xsl:text> et </xsl:text></xsl:when>
        <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
      </xsl:choose>
</xsl:template>




<xsl:template match='bml:metadata'>
  <xsl:variable name='author'>
    <xsl:for-each select='bml:monographie/bml:auteur[not(@role) or @role="aut"]'>
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

  <xsl:variable name='first-publication'>
    <xsl:value-of select='bml:electronique/@creation'/>
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

  <div class="long" style='display:none; margin:1em 0 0 1em; padding:1em; border: 1px solid'>
    <table>
      <tr>
        <td>
          <img style='vertical-align:center; padding: 0 2em 0 2em; margin:0;' alt='couverture' src='{bml:electronique/@identificateur}/thumbnail.png'/>
        </td>

        <td>
          <p style='font-size:1.2em; padding:0; margin:0;'><i><xsl:value-of select='$author'/></i></p>
          <p style='font-size:1.5em; padding:1em 0 1em 0; margin:0;'><xsl:value-of select='$title'/></p>

          <xsl:if test='bml:monographie/bml:auteur[@role="edt"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Édition : </xsl:text>
              <xsl:for-each select='bml:monographie/bml:auteur[@role="edt"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>

          <xsl:if test='bml:monographie/bml:auteur[@role="trl"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Traduction : </xsl:text>
              <xsl:for-each select='bml:monographie/bml:auteur[@role="trl"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>

          <xsl:if test='bml:monographie/bml:auteur[@role="ill"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Illustrations : </xsl:text>
              <xsl:for-each select='bml:monographie/bml:auteur[@role="ill"]'>
                <xsl:call-template name='list-sep'/>
                <xsl:value-of select='bml:nom-couverture'/>
              </xsl:for-each>
            </p>
          </xsl:if>
          
          <p style='padding:0; margin:0;'><xsl:value-of select='$publisher'/></p>
        </td>
      </tr>
    </table>
    


    <p><a href="#formats">Tirages</a> faits le <xsl:call-template name='tirage-date'/> :
    <a href='{bml:electronique/@identificateur}/{bml:electronique/@fichier}.epub'>epub</a>
    <xsl:text> </xsl:text>
    <a href="{bml:electronique/@identificateur}/{bml:electronique/@fichier}.mobi">kindle</a>
    <xsl:text> </xsl:text>
    <a href="{bml:electronique/@identificateur}/{bml:electronique/@fichier}.daisy.zip">daisy</a>

    <xsl:if test='bml:electronique/bml:collection'>
      <p>Ce livre fait partie de la collection <a href='../{bml:electronique/bml:collection/@id}/index.html'><xsl:apply-templates select='bml:electronique/bml:collection/bml:titre'/></a>.</p>
    </xsl:if>

    <p>
      <xsl:if test='bml:monographie/@bnf'>
        <a href='{bml:monographie/@bnf}'>Catalogue BNF</a>
        <xsl:if test='   count (bml:volume/bml:facsimile) != 0
                      or count (bml:electronique/bml:collection) = 0'>
          <xsl:text> — </xsl:text>
        </xsl:if>
      </xsl:if>
    
      <xsl:choose>
        <xsl:when test='count (bml:volume/bml:facsimile) > 1'>
          Facsimilés : 
          <xsl:for-each select='bml:volume/bml:facsimile'>
            <a href='{@href}'>x</a>
          </xsl:for-each>
          <xsl:if test='count (bml:electronique/bml:collection) = 0'> — </xsl:if>
        </xsl:when>
        
        <xsl:when test='count (bml:volume/bml:facsimile) = 1'>
          <a href='{bml:volume/bml:facsimile/@href}'>Facsimilé</a>
          <xsl:if test='count (bml:electronique/bml:collection) = 0'> — </xsl:if>
        </xsl:when>
      </xsl:choose>

      <xsl:if test='count (bml:electronique/bml:collection) = 0'>
        <a href='{bml:electronique/@identificateur}/{bml:electronique/@fichier}.sources.zip'>Source XML</a>
      </xsl:if>
    </p>
  </div>
</xsl:template>


</xsl:stylesheet>
