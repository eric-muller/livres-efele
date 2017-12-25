<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  version="2.0">

<xsl:param name='colophon'/>
<xsl:param name='tirage-font'/>
<xsl:param name='correction'>[</xsl:param>
<xsl:param name='dotoi'>no</xsl:param>
<xsl:param name='mobi'/>

<xsl:include href="bml-common.xsl"/>

<xsl:output 
  method="xml"
  indent="no"
  encoding="UTF-8"/>


<!--_________________________________________________________________ toc ___-->

<!-- Enrich the toc of the source to look like:

   <toc>
     <tocentry idref="page-titre"         label="<titre du livre>">
       .. entries in the source ..
     </tocentry>
     <tocentry idref='illustrations' label="Illustrations"/>
     <tocentry idref='colophon'      label="Colophon"/>
   </toc>

  The Illustrations entry is generated only if dotoi is yes and 
  there are full page images in the text
-->

<xsl:template match='bml:toc'>
  <xsl:copy>
    <bml:tocentry idref="page-titre">
        <xsl:attribute name='label'>
          <xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/bml:titre'/>
        </xsl:attribute>

        <xsl:apply-templates select="@*|node()"/>

    </bml:tocentry>

    <xsl:if test="$dotoi='yes' and //bml:bml/bml:page-sequences//bml:img[@class='fullpageimage']">
      <bml:tocentry label="Illustrations" idref="illustrations"/>
    </xsl:if>

    <bml:tocentry label="Colophon" idref="colophon"/>
  </xsl:copy>
</xsl:template>

<!--______________________________________________________ page-sequences ___-->

<!-- Enrich the page sequences to look like

  <page-sequence>    with the cover image
  <page-sequence>    with the efele cover page
  ...                page sequences of the text
  <page-sequence>    table of illustrations, if asked for  
  <page-sequence>    colophon
-->

<xsl:template match='bml:page-sequences'>
  <xsl:variable name='metadata' as="node()" select='//bml:bml/bml:metadata'/>
  <xsl:copy>

    <xsl:for-each select='//bml:bml/bml:cover'>
      <bml:page-sequence recto="true" id='couverture'>
          <bml:img id="coverimage" class="fullpageimage"
                   src="{@src}" alt="{@alt}"/>
      </bml:page-sequence>
    </xsl:for-each>

    <xsl:call-template name='efelecover'/>

    <xsl:apply-templates select="@*|node()"/>

    <xsl:if test="$dotoi='yes' and //bml:bml/bml:page-sequences//bml:img[@class='fullpageimage']">
      <bml:page-sequence recto="true" id="illustrations">
          <bml:h2>ILLUSTRATIONS</bml:h2>

          <xsl:for-each select="//bml:bml/bml:page-sequences//bml:img[@class='fullpageimage']">
            <bml:p><bml:a idref="{if (@id) then @id else generate-id()}"><xsl:value-of select='@alt'/></bml:a></bml:p>
          </xsl:for-each>
      </bml:page-sequence>
    </xsl:if>

    <xsl:call-template name='colophon'>
      <xsl:with-param name='metadata' select='$metadata' tunnel="yes"/>
    </xsl:call-template>
      
  </xsl:copy>
</xsl:template>

<!--____________________________________________________________ colophon ___-->

<xsl:template name='list-sep'>
      <xsl:choose>
        <xsl:when test='position() = 1'/>
        <xsl:when test='position() = last()'> <xsl:text> et </xsl:text></xsl:when>
        <xsl:otherwise> <xsl:text>, </xsl:text></xsl:otherwise>
      </xsl:choose>
</xsl:template>

<xsl:template match='bml:monographie' mode='publisher'>
  <bml:br/>
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
      
      <xsl:apply-templates select='bml:nom/node()'/>
    </xsl:for-each>
    
    <xsl:text>, </xsl:text>
    <xsl:apply-templates select='bml:ville/node()'/>
  </xsl:for-each-group>
  
  <xsl:text>, </xsl:text>
  <xsl:apply-templates select='bml:date/node()'/>
</xsl:template>

<xsl:template match='bml:article' mode='publisher'>
  <bml:br/>
  <xsl:apply-templates select='bml:periodique/bml:titre/node()'/>
  <bml:br/>
  <xsl:apply-templates select='bml:numero/bml:nom/node()'/>
  <bml:br/>
  <xsl:apply-templates select='bml:numero/bml:date/node()'/>
</xsl:template>



<xsl:template name='colophon-catalogue'>
  <xsl:param name='metadata' tunnel="yes" as="node()"/>

  <bml:div class="l3">
    <bml:p class="sommaire">
      <xsl:text>Source :</xsl:text>
      <bml:br/>

      <xsl:for-each select='$metadata/bml:*/bml:auteur[not(@role) or @role="aut"]'>
        <xsl:call-template name='list-sep'/>
        <xsl:value-of select='bml:nom-couverture'/>
      </xsl:for-each>
      
      <bml:br/>
      <bml:i><xsl:apply-templates select='$metadata/bml:monographie/bml:titre/node()'/><xsl:apply-templates select='$metadata/bml:article/bml:titre/node()'/></bml:i>
      
      
      <xsl:if test='$metadata/bml:monographie/bml:auteur[@role="edt"]'>
        <bml:br/>
        <xsl:text>Édition : </xsl:text>
        <xsl:for-each select='$metadata/bml:monographie/bml:auteur[@role="edt"]'>
          <xsl:call-template name='list-sep'/>
          <xsl:value-of select='bml:nom-couverture'/>
        </xsl:for-each>
      </xsl:if>
      
      <xsl:if test='$metadata/bml:monographie/bml:auteur[@role="trl"]'>
        <bml:br/>
        <xsl:text>Traduction : </xsl:text>
        <xsl:for-each select='$metadata/bml:monographie/bml:auteur[@role="trl"]'>
          <xsl:call-template name='list-sep'/>
          <xsl:value-of select='bml:nom-couverture'/>
        </xsl:for-each>
      </xsl:if>
      
      <xsl:if test='$metadata/bml:monographie/bml:auteur[@role="aui"]'>
        <bml:br/>
        <xsl:text>Préface : </xsl:text>
        <xsl:for-each select='$metadata/bml:monographie/bml:auteur[@role="aui"]'>
          <xsl:call-template name='list-sep'/>
          <xsl:value-of select='bml:nom-couverture'/>
        </xsl:for-each>
      </xsl:if>
      
      <xsl:if test='$metadata/bml:monographie/bml:auteur[@role="ill"]'>
        <bml:br/>
        <xsl:text>Illustrations : </xsl:text>
        <xsl:for-each select='$metadata/bml:monographie/bml:auteur[@role="ill"]'>
          <xsl:call-template name='list-sep'/>
          <xsl:value-of select='bml:nom-couverture'/>
        </xsl:for-each>
      </xsl:if>
      
      <xsl:apply-templates select='$metadata/bml:monographie|$metadata/bml:article' mode='publisher'/>
      
      <xsl:if test='$metadata/bml:*/@bnf'>
        <bml:br/>
        <bml:i><bml:a href="{$metadata/bml:*/@bnf}"><xsl:value-of select="$metadata/bml:monographie/@bnf"/></bml:a></bml:i>
      </xsl:if>
    </bml:p>
    
    <xsl:if test='count($metadata/bml:volume/bml:facsimile)>=1'>
      <bml:p class="sommaire">
        <xsl:text>Fac-similé</xsl:text>
        <xsl:if test='count($metadata/bml:volume/bml:facsimile)>1'>
          <xsl:text>s</xsl:text>
        </xsl:if>
        <xsl:text> :</xsl:text>
        <xsl:for-each select='$metadata/bml:volume/bml:facsimile'>
          <bml:br/><bml:i><bml:a href="{@href}"><xsl:value-of select="@href"/></bml:a></bml:i>
        </xsl:for-each>
      </bml:p>
    </xsl:if>
  </bml:div>
</xsl:template>

<xsl:template name='colophon-erreurs'>
  <xsl:param name='metadata' tunnel="yes" as="node()"/>

  <bml:p>Retrouvez toutes les réimpressions <bml:s>ÉFÉLÉ</bml:s> sur <bml:i><bml:a href="http://efele.net/ebooks">http://efele.net/ebooks</bml:a></bml:i>.</bml:p>

  <bml:vsep class="emptyline"/>

  <bml:p>Si vous trouvez des erreurs, veuillez les signaler à <bml:i><bml:a href="mailto:livres@efele.net">livres@efele.net</bml:a></bml:i>.</bml:p>

  <bml:vsep class="emptyline"/>

  <xsl:apply-templates select='$metadata/bml:electronique/bml:merci/*'/>

</xsl:template>


<xsl:template name='colophon'>
  <xsl:param name='metadata' tunnel="yes" as="node()"/>

  <bml:page-sequence recto="true" id='colophon'>
    <bml:h2>COLOPHON</bml:h2>
      
    <bml:p>Cette réimpression <bml:s>ÉFÉLÉ</bml:s> a été faite le <xsl:call-template name='tirage-date-bml'/> et est composée en <xsl:value-of select='$tirage-font'/>.</bml:p>

    <bml:vsep class="emptyline"/>

    <xsl:call-template name='colophon-catalogue'/>

    <bml:vsep class="threestars"/>

    <xsl:if test='$metadata/bml:electronique/bml:note'>
      <xsl:apply-templates select='$metadata/bml:electronique/bml:note/*'/>
      <bml:vsep class="threestars"/>
    </xsl:if>

    <xsl:call-template name='colophon-erreurs'/>
  </bml:page-sequence>
</xsl:template>

<!--_________________________________________________________ corrections ___-->

<xsl:template match='bml:correction[@silent="yes"]'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='bml:correction'>
  <xsl:apply-templates/>
  <xsl:choose>
    <xsl:when test='$correction="["'> [</xsl:when>
    <xsl:otherwise> {</xsl:otherwise>
  </xsl:choose>
  <xsl:value-of select='@original'/>
  <xsl:choose>
    <xsl:when test='$correction="["'>]</xsl:when>
    <xsl:otherwise>}</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match='bml:sic'>
  <xsl:apply-templates/>
  <xsl:text> [</xsl:text><bml:i>sic</bml:i><xsl:text>]</xsl:text>
</xsl:template>

<!--_________________________________________________________________________-->

<xsl:template match='bml:img'>
  <xsl:copy>
    <xsl:attribute name='id' select="if (@id) then @id else generate-id()"/>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
