<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns='http://www.w3.org/1999/xhtml'
  exclude-result-prefixes='bml'
  version="2.0">


<xsl:include href="bml-common.xsl"/>

<xsl:param name='bookFile'/>
<xsl:param name='desc'/>
<xsl:param name='id'/>

<xsl:output
    method="html"
    doctype-public='-//W3C//DTD XHTML 1.0 Transitional//EN'
    doctype-system='http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'
    encoding="UTF-8"
    indent="no"/>

<xsl:template match="bml:bml">
  <xsl:variable name='author'>
    <xsl:for-each select='bml:metadata/*/bml:auteur[not(@role) or @role="aut"]'>
      <xsl:call-template name='list-sep'/>
      <xsl:value-of select='bml:nom-couverture'/>
    </xsl:for-each>
  </xsl:variable>

  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

      <meta name="twitter:card" content="summary"/>
      <meta name="twitter:site" content="@LivresEfele"/>
      <meta name="twitter:title">
        <xsl:attribute name="content">
          <xsl:value-of select='$author'/>
          <xsl:text> : </xsl:text>
          <xsl:apply-templates select='bml:metadata/bml:electronique/bml:titre'/></xsl:attribute>
      </meta>
      <meta name="twitter:description">
        <xsl:attribute name="content">
          <xsl:text>epub kindle daisy</xsl:text>
        </xsl:attribute>
      </meta>
      <meta name="twitter:image" content="http://efele.net/ebooks/livres/{$id}/thumbnail-tw.png"/>
      <meta name="twitter:url" content="."/>


      <title>ÉFÉLÉ : <xsl:value-of select='bml:metadata/bml:electronique/bml:titre'/></title>
      <link rel="stylesheet" type="text/css" href="../../style.css"/>
    </head>
  <body>
    <h2 style='text-align:center'>ÉFÉLÉ, réimprimeur la nuit</h2>

    <p  style='text-align:center'>
      <a href="../../index.html#presentation">Présentation</a> - 
    <a href="../../index.html#catalogue">Catalogue</a></p>

    <p>&#xa0;</p>

    <hr/>
    <xsl:apply-templates select='bml:metadata'/>
    <hr/>

    <p>&#xa0;</p>

    <div style='max-width: 30em; margin-left:auto; margin-right:auto;'>
      <xsl:for-each select='document($desc,.)'>
        <xsl:apply-templates select='.'/>
      </xsl:for-each>
    </div>
  </body>
  </html>
</xsl:template>


<xsl:template match='bml:extract'>
  <xsl:apply-templates/>
  <h2 style='text-align: center;'>&#x2766;</h2>
</xsl:template>


<xsl:template match='bml:word'>
  <xsl:apply-templates/>
  <h2 style='text-align: center;'>
    <xsl:choose>
      <xsl:when test='following-sibling::bml:word'>&#x2014;</xsl:when>
      <xsl:otherwise>&#x2766;</xsl:otherwise>
    </xsl:choose>
  </h2>
</xsl:template>

<xsl:template match='bml:definition'>
  <div style='padding-left: 3em; margin-top: 1em;'>
    <xsl:apply-templates/>
  </div>
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
    <xsl:for-each select='bml:*/bml:auteur[not(@role) or @role="aut"]'>
      <xsl:call-template name='list-sep'/>
      <xsl:value-of select='bml:nom-couverture'/>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name='uniqueid'>
    <xsl:value-of select='bml:electronique/@identificateur'/>
  </xsl:variable>

  <xsl:variable name='first-publication'>
    <xsl:value-of select='bml:electronique/@creation'/>
  </xsl:variable>

  <div style='margin:1em 0 0 1em;'>
    <table>
      <tr>
        <td>
          <img style='vertical-align:center; padding: 0 2em 0 2em; margin:0;' alt='couverture' src='thumbnail.png'/>
        </td>

        <td>
          <p style='font-size:1.2em; padding:0; margin:0;'><i><xsl:value-of select='$author'/></i></p>

          <div style='padding:1em 0 1em 0; margin:0;'>
            <p style='font-size:1.5em; padding: 0; margin:0;'><xsl:value-of select='bml:electronique/bml:titre'/></p>

            <xsl:if test='bml:electronique/bml:soustitre'>
              <p style='font-size:1.2em; padding: 0; margin:0'><xsl:value-of select='bml:electronique/bml:soustitre'/>
              </p>
            </xsl:if>
          </div>

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

          <xsl:if test='bml:monographie/bml:auteur[@role="aui"]'>
            <p style='padding:0; margin:0;'>
              <xsl:text>Préface : </xsl:text>
              <xsl:for-each select='bml:monographie/bml:auteur[@role="aui"]'>
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
          
          <xsl:apply-templates select='bml:monographie|bml:article' mode='publisher'/>
        </td>
      </tr>
    </table>
    
    <p><a href="../../index.html#formats">Tirages</a> faits le <xsl:call-template name='tirage-date'/> :
    <a href='{$bookFile}.epub'>epub</a>
    <xsl:text> </xsl:text>
    <a href="{$bookFile}.mobi">kindle</a>
    <xsl:text> </xsl:text>
    <a href="{$bookFile}.daisy.zip">daisy</a>
    </p>

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
            <a href='{@href}'><xsl:value-of select='position()'/></a>
            <xsl:text> </xsl:text>
          </xsl:for-each>
          <xsl:if test='count (bml:electronique/bml:collection) = 0'> — </xsl:if>
        </xsl:when>
        
        <xsl:when test='count (bml:volume/bml:facsimile) = 1'>
          <a href='{bml:volume/bml:facsimile/@href}'>Facsimilé</a>
          <xsl:if test='count (bml:electronique/bml:collection) = 0'> — </xsl:if>
        </xsl:when>
      </xsl:choose>

      <xsl:if test='count (bml:electronique/bml:collection) = 0'>
        <a href='{$bookFile}.sources.zip'>Source XML</a>
      </xsl:if>
    </p>
  </div>
</xsl:template>


<xsl:template match='bml:monographie' mode='publisher'>
  <p style='padding:0; margin:0;'>
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
  <p style='padding:0; margin:0;'>
    <xsl:apply-templates select='bml:periodique/bml:titre'/>
  </p>
  <p style='padding:0; margin:0;'>
    <xsl:apply-templates select='bml:numero/bml:nom'/>
  </p>
  <p style='padding:0; margin:0;'>
    <xsl:apply-templates select='bml:numero/bml:date'/>
  </p>
</xsl:template>






<xsl:template match='bml:p'>
  <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match='bml:i'>
  <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match='bml:sup'>
  <sup><xsl:apply-templates/></sup>
</xsl:template>

<xsl:template match='bml:a'>
  <a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match='bml:highlight'>
  <span style='color: #3366ff;'><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match='bml:s'>
  <span style='font-size: 80%;'><xsl:apply-templates/></span>
</xsl:template>

</xsl:stylesheet>
