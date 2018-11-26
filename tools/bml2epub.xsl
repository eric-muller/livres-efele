<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:opf="http://www.idpf.org/2007/opf"
  xmlns="http://www.w3.org/1999/xhtml" 
  exclude-result-prefixes="bml"
  version="2.0">


<xsl:param name='targetdir'>zz</xsl:param>
<xsl:param name='page-template'>none</xsl:param>
<xsl:param name="fonts">no</xsl:param>
<xsl:param name="epub-fonts">no</xsl:param>
<xsl:param name="eml">xx</xsl:param>
<xsl:param name='singledtb'>true</xsl:param>
<xsl:param name='realpagenums'>yes</xsl:param>

<xsl:include href='metadata.xml'/>




<xsl:key name='id-key' match='*' use='@id'/>

<xsl:key name='noteref-key' match='bml:noteref' use='@noteid'/>

<xsl:key name='pageref-key' match='bml:pagenum' use='concat(@v,"-",@num)'/>


<xsl:template name='file-of'>
  <xsl:text>content-</xsl:text>
  <xsl:choose>
    <xsl:when test='ancestor-or-self::bml:page-sequence[@id]'>
      <xsl:value-of select='ancestor-or-self::bml:page-sequence/@id'/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:number count='bml:page-sequence' level='any'/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!--==================================================================== ncx -->

<xsl:template match='bml:toc' mode='ncx'>
  <navMap xmlns="http://www.daisy.org/z3986/2005/ncx/">
    <xsl:apply-templates mode='ncx'/>
  </navMap>
</xsl:template>


<xsl:template match='bml:tocentry' mode='ncx'>
  
  <navPoint id='navpoint.{generate-id()}' xmlns="http://www.daisy.org/z3986/2005/ncx/">

    <xsl:attribute name='playOrder'><xsl:number count='bml:tocentry' level='any'/></xsl:attribute>

    <navLabel><text><xsl:value-of select='@label'/></text></navLabel>

    <xsl:choose>
      <xsl:when test='@idref'>
        <xsl:variable name='targetfile'>
          <xsl:for-each select='key("id-key",@idref)'>
            <xsl:call-template name='file-of'/>
          </xsl:for-each>
        </xsl:variable>
        <content src='{$targetfile}.xhtml#{@idref}'/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name='targetfile'>
          <xsl:for-each select='key("pageref-key",concat(@v,"-",@pageref))'>
            <xsl:call-template name='file-of'/>
          </xsl:for-each>
        </xsl:variable>
        <content src='{$targetfile}.xhtml#page.{@v}.{@pageref}'/>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:apply-templates mode='ncx'/>
  </navPoint>
</xsl:template>

<xsl:template match='bml:page-sequences' mode='ncx'>
  <pageList xmlns="http://www.daisy.org/z3986/2005/ncx/">
    <xsl:apply-templates mode='ncx'/>
  </pageList>
</xsl:template>

<xsl:template match='bml:pagenum' mode='ncx'>
  <xsl:variable name='targetfile'>
    <xsl:call-template name='file-of'/>
  </xsl:variable>

  <xsl:variable name='playorder'>
    <xsl:value-of select='count(preceding::bml:pagenum) + 1 + count(preceding::bml:tocentry)'/>
  </xsl:variable>

  <pageTarget type="normal" id="page.{$playorder}" value="{$playorder}" playOrder="{$playorder}" xmlns="http://www.daisy.org/z3986/2005/ncx/">
    <navLabel><text><xsl:value-of select='@num'/></text></navLabel>
    <content src='{$targetfile}.xhtml#page.{@v}.{@num}'/>
  </pageTarget>
</xsl:template>

<xsl:template match='node()' mode='ncx'>
  <xsl:apply-templates mode='ncx'/>
</xsl:template>


<!--================================================================ pagemap -->

<xsl:template match='bml:pagenum' mode='pagemap'>
  <xsl:variable name='targetfile'>
    <xsl:call-template name='file-of'/>
  </xsl:variable>

  <page xmlns="http://www.idf.org/2007/opf" 
        href="{$targetfile}.xhtml#page.{@v}.{@num}"
        name='{@num}'/>
</xsl:template>

<xsl:template match='node()' mode='pagemap'>
  <xsl:apply-templates mode='pagemap'/>
</xsl:template>

<!--=================================================================== html -->

<xsl:variable name='align-classes'
              select='tokenize ("l0 l1 l3 c r3 r1 r0", "\s+")'/>


<xsl:template name='transfer-common-attributes'>
  <xsl:param name='class'/>
  <xsl:param name='default-align' required='no'/>

<!--
  <xsl:variable name='set-font'>
    <xsl:if test='@font'>
      <xsl:text>set-font-</xsl:text>
      <xsl:value-of select='(@font,"default")[1]'/>
    </xsl:if>
  </xsl:variable>

  <xsl:variable name='use-font'>
    <xsl:text>use-font-</xsl:text>
    <xsl:value-of select='(ancestor-or-self::*[@font][1]/@font, "default")[1]'/>
  </xsl:variable>
-->

  <xsl:variable 
      name='align'
      select="distinct-values (tokenize (@class, '\s+')[.=$align-classes])"/>

  <xsl:variable name='classes'>
    <xsl:value-of 
        separator=' '
        select='$class, @class, if ($align) then "" else $default-align'/>
  </xsl:variable>

  <xsl:if test="normalize-space($classes) != ''">
    <xsl:attribute name='class'>
      <xsl:value-of select='normalize-space($classes)'/>
    </xsl:attribute>
  </xsl:if>

  <xsl:for-each select='@id'>
    <xsl:copy/>
  </xsl:for-each>

  <xsl:for-each select='@xml:lang'>
    <xsl:copy/>
  </xsl:for-each>
</xsl:template>



<xsl:template match='bml:div' mode='html'>
  <div>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </div>
</xsl:template>

<xsl:template match='bml:blockquote' mode='html'>
  <blockquote>
    <xsl:call-template name='transfer-common-attributes'/>
    <div>   <!-- to make epubcheck happy -->
      <xsl:apply-templates mode='html'/>
    </div>
  </blockquote>
</xsl:template>

<xsl:template match='bml:smaller' mode='html'>
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>smaller</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </div>
</xsl:template>

<xsl:template match='bml:epigraphe' mode='html'>
  <div class="avantepigraphe"/>
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>epigraphe</xsl:with-param>
    </xsl:call-template>
      <xsl:apply-templates mode='html'/>
  </div>
  <div class="apresepigraphe"/>
</xsl:template>

<xsl:template match='bml:page-sequence/bml:div/bml:poem' mode='html'>
  <div style="margin-top:3em;">
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>poem</xsl:with-param>
    </xsl:call-template>
    <div class="poem-inner">
      <xsl:apply-templates mode='html'/>
    </div>
  </div>
</xsl:template>

<xsl:template match='bml:poem' mode='html'>
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">poem</xsl:with-param>
    </xsl:call-template>
    <div class="poem-inner">
      <xsl:apply-templates mode='html'/>
    </div>
  </div>
</xsl:template>

<xsl:template match='bml:lg' mode='html'>
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>lg</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </div>
</xsl:template>


<xsl:template name='collectpreviouslines'>
 <xsl:for-each select="preceding::bml:l[1]">
   <xsl:if test="@cont">
     <xsl:call-template name='collectpreviouslines'/>
   </xsl:if>
   <xsl:text> </xsl:text>
   <xsl:apply-templates mode='html'/>
 </xsl:for-each>
</xsl:template>

<xsl:template match='bml:l' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>l_<xsl:value-of select='@indent'/></xsl:with-param>
    </xsl:call-template>
    <xsl:if test="@cont">
      <span style="visibility:hidden;">
        <xsl:call-template name='collectpreviouslines'>
          <xsl:with-param name='hidden' tunnel='yes'>true</xsl:with-param>
        </xsl:call-template>
      </span>
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>



<xsl:template match='bml:letter' mode='html'>
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>letter</xsl:with-param>
    </xsl:call-template>

    <xsl:apply-templates mode='html'/>
  </div>
</xsl:template>


<xsl:template match='bml:ul' mode='html'>
  <ul>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </ul>
</xsl:template>

<xsl:template match='bml:li' mode='html'>
  <li>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </li>
</xsl:template>



<xsl:template match='bml:h1' mode='html'>
  <h1>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </h1>
</xsl:template>

<xsl:template match='bml:h2' mode='html'>
  <h2>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </h2>
</xsl:template>

<xsl:template match='bml:h3' mode='html'>
  <h3>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </h3>
</xsl:template>

<xsl:template match='bml:h4' mode='html'>
  <h4>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </h4>
</xsl:template>


<xsl:template match='bml:salutation' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">salutation</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>


<xsl:template match='bml:signature' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">signature</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>

<xsl:template match='bml:date' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>date</xsl:with-param>
      <xsl:with-param name='default-align'>r3</xsl:with-param>
    </xsl:call-template>

    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>



<xsl:template match='bml:p' mode='html'>
  <xsl:variable name='class'>
    <xsl:choose>
      <xsl:when test='bml:initcap'>initcap</xsl:when>
      <xsl:when test='parent::bml:note and parent::bml:note/bml:p[1] = .'>premier-alinea</xsl:when>
      <xsl:when test='@class'><xsl:value-of select='@class'/></xsl:when>
    </xsl:choose>
  </xsl:variable>

  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class" select='$class'/>
    </xsl:call-template>

    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>

<xsl:template match='bml:cast' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">cast</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>



<xsl:template match='bml:vsep[@class="emptyline"]' mode="html">
  <p>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:text>&#xa0;</xsl:text>
  </p>
</xsl:template>

<xsl:template match='bml:vsep[@class="fewlines"]' mode="html">
  <p>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:text>&#xa0;</xsl:text>
  </p>
  <p>&#xa0;</p>
  <p>&#xa0;</p>
  <p>&#xa0;</p>
  <p>&#xa0;</p>
</xsl:template>

<xsl:template match='bml:vsep[@class="threestars"]' mode="html">
  <p style='margin: 0.5em 0 0.5em 0; text-indent:0; text-align:center;'>
    <span style='line-height:0; vertical-align:-0.5em'>*</span>
    <xsl:text>*</xsl:text>
    <span style='line-height:0; vertical-align:-0.5em'>*</span>
  </p>
</xsl:template>

<xsl:template match='bml:vsep[@class="threestarsrow"]' mode="html">
  <p class="threestarsrow"
     style='margin: 0.5em 0 0.5em 0; text-indent:0; text-align:center;'>
    <xsl:text>*&#x2001;*&#x2001;*</xsl:text>
  </p>
</xsl:template>

<xsl:template match='bml:vsep[@class="onestar"]' mode="html">
  <p class="onestar"
     style='margin: 0.5em 0 0.5em 0; text-indent:0; text-align:center;'>
    <xsl:text>*</xsl:text>
  </p>
</xsl:template>

<xsl:template match='bml:vsep[@class="dots"]' mode="html">
<!-- the <span> has enough width to force it to be on a second line, 
     and 0 height so that it does not take space. Hence the content
     before the span is a single justified line. -->
  <p style="text-indent: 0; margin: 0; padding: 0; text-align:justify;">
    <xsl:text>. . . . . . . . . . . . . . . . . . .</xsl:text>
    <span style="display: inline-block; position: relative; width: 100%; height: 0;"></span>
  </p>
</xsl:template>

<xsl:template match='bml:vsep[@class="rule"]' mode="html">
  <hr class="rule"/>
</xsl:template>

<xsl:template match='bml:vsep[@class="fullwidth-rule"]' mode="html">
  <hr class="fullwidth-rule"/>
</xsl:template>

<xsl:template match='bml:vsep[@class="doublerule"]' mode="html">
  <hr class="rule-top"/>
  <hr class="rule-bottom"/>
</xsl:template>

<xsl:template match='bml:vsep[@class="tilderule"]' mode="html">
  <hr class="rule-top"/> 
  <hr class="rule-bottom"/>
</xsl:template>


<xsl:template match='bml:p//bml:img' mode='html'>
  <img id='{if (@id) then @id else generate-id()}'
       src='{@src}'
       alt='{@alt}'>
  </img>
</xsl:template>

<xsl:template match='bml:figure' mode='html'>
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">figure</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </div>
</xsl:template>

<xsl:template match='bml:figure/bml:img' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">c</xsl:with-param>
    </xsl:call-template>

    <img style='max-width: {@width}; margin: 1em 0 1em 0;'
         src='{@src}'
         alt='{@alt}'/>
  </p>
</xsl:template>

<xsl:template match='bml:img' mode='html'>
  <img src='{@src}'
       alt='{@alt}'>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:for-each select='@style'>
      <xsl:copy/>
    </xsl:for-each>
  </img>
</xsl:template>

<xsl:template match='bml:img[@class="fullpageimage"]' mode='html'>
  <div class="fullpageimage-wrapper">
    <img src='{@src}'
         alt='{@alt}'>
      <xsl:call-template name='transfer-common-attributes'>
        <xsl:with-param name='class'>fullpageimage</xsl:with-param>
      </xsl:call-template>
      <xsl:for-each select='@style'>
        <xsl:copy/>
      </xsl:for-each>
    </img>
  </div>
</xsl:template>


<xsl:template match='bml:pagenum' mode='html'>
  <a id='page.{@v}.{@num}'>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">numero-de-page</xsl:with-param>
    </xsl:call-template>
  </a>
  <xsl:apply-templates mode='html'/>
</xsl:template>

<xsl:template match='bml:colnum' mode='html'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='bml:pageref' mode='html'>
  <xsl:variable name='targetfile'>
    <xsl:for-each select='key("pageref-key",concat(@v,"-",@num))'>
      <xsl:call-template name='file-of'/>
    </xsl:for-each>
  </xsl:variable>

  <a  href="{$targetfile}.xhtml#page.{@v}.{@num}"><xsl:apply-templates mode='html'/></a>
</xsl:template>

<xsl:template match='bml:noteref' mode='html'>
  <xsl:param name='hidden' tunnel='yes'>false</xsl:param>

  <xsl:variable name='targetfile'>
    <xsl:for-each select='key("id-key",@noteid)'>
      <xsl:call-template name='file-of'/>
    </xsl:for-each>
  </xsl:variable>

  <sup>
    <xsl:choose>
      <xsl:when test='$hidden = "false"'>
        <a id='noteref.{@noteid}' href="{$targetfile}.xhtml#note.{@noteid}">
          <xsl:apply-templates select='key("id-key",@noteid)[1]' mode='notelabel'/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select='key("id-key",@noteid)[1]' mode='notelabel'/>
      </xsl:otherwise>
    </xsl:choose>
  </sup>
</xsl:template>

<xsl:template match='bml:note' mode='notelabel'>
  <xsl:value-of select='if (@label) then @label else count(preceding-sibling::bml:note) + 1'/>
</xsl:template>


<xsl:template match="bml:notes" mode="html">
  <div>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">notes</xsl:with-param>
      </xsl:call-template>
    <xsl:apply-templates mode="html"/>
  </div>
</xsl:template>


<xsl:template match='bml:note' mode='html'>
  <xsl:variable name='targetfile'>
    <xsl:for-each select='key("noteref-key", @id)'>
      <xsl:call-template name='file-of'/>
    </xsl:for-each>
  </xsl:variable>

  <div class="note" id="note.{@id}">
    <div class="numero-de-note">
      <sup>
        <a href="{$targetfile}.xhtml#noteref.{@id}">
          <xsl:apply-templates select='.' mode='notelabel'/>
        </a>
      </sup>
    </div>
    <div class="texte-de-note"><xsl:apply-templates mode='html'/></div>
  </div>
</xsl:template>



<xsl:template match='bml:span' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </span>
</xsl:template>




<xsl:template match='bml:hstage' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">hstage</xsl:with-param>
    </xsl:call-template>

    <xsl:for-each-group 
        select='* | text()' 
        group-adjacent='if (self::bml:speaker) then "nowrap" else if (self::bml:stage) then "nowrap" else "wrap"'>

      <xsl:choose>
        <xsl:when test='current-grouping-key() = "nowrap"'>
          <xsl:apply-templates select='current-group()' mode='html'/>
        </xsl:when>

        <xsl:otherwise>
          <span class="stage">
            <xsl:apply-templates select='current-group()' mode='html'/>
          </span>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each-group>
  </p>
</xsl:template>

<xsl:template match='bml:pstage' mode='html'>
  <p>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">pstage</xsl:with-param>
    </xsl:call-template>
    <span class="stage">
      <xsl:apply-templates mode="html"/>
    </span>
  </p>
</xsl:template>

<xsl:template match='bml:speaker' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">speaker</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode="html"/>
  </span>
</xsl:template>

<xsl:template match='bml:stage' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">stage</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode="html"/>
  </span>
</xsl:template>






<xsl:template match='bml:bl' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </span>
</xsl:template>

<xsl:template match='bml:bl/bml:li' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name="class">braceelem</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </span>
</xsl:template>



<xsl:template match='bml:tl' mode='html'>
  <table style="border-spacing: 0;">
    <xsl:apply-templates mode='html'/>
  </table>
</xsl:template>

<xsl:template match='bml:tl/bml:li' mode='html'>
  <tr>
    <xsl:choose>
      <xsl:when test='bml:label'>
        <td>
          <xsl:if test='../@label-align="right"'>
            <xsl:attribute name='style'>text-align: right;</xsl:attribute>
          </xsl:if>
          <xsl:apply-templates select='bml:label' mode='html'/>
        </td>
        <td>&#xa0;</td>
        <td>
          <xsl:apply-templates select='*[not(self::bml:label)]' mode='html'/>
        </td>
      </xsl:when>
      <xsl:otherwise>
        <td colspan='2'>
          <xsl:apply-templates mode='html'/>
        </td>
      </xsl:otherwise>
    </xsl:choose>
  </tr>
</xsl:template>

<xsl:template match='bml:tl/bml:li/bml:label' mode='html'>
  <xsl:apply-templates mode="html"/>
</xsl:template>

<xsl:template match='bml:tl/bml:li/bml:p' mode='html'>
  <p class="sommaire left nohyphen">
    <xsl:apply-templates mode='html'/>
  </p>
</xsl:template>


<xsl:template match='bml:table[@class="center"]' mode='html'>
  <div style="text-align:center">
    <table>
      <xsl:call-template name='transfer-common-attributes'/>
      <xsl:attribute name='style'>display: inline-table; <xsl:value-of select='@style'/></xsl:attribute>
      <xsl:apply-templates mode='html'/>
    </table>
  </div>
</xsl:template>

<xsl:template match='bml:table' mode='html'>
  <table>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:for-each select='@style'>
      <xsl:copy/>
    </xsl:for-each>
    <xsl:apply-templates mode='html'/>
  </table>
</xsl:template>

<xsl:template match='bml:col' mode='html'>
<!--
  <col>
    <xsl:if test='@width'><xsl:attribute name='width' select='@width'/></xsl:if>
    <xsl:if test='@align'><xsl:attribute name='align' select='@align'/></xsl:if>
    <xsl:if test='@valign'><xsl:attribute name='valign' select='@valign'/></xsl:if>
  </col>
-->
</xsl:template>

<xsl:template match='bml:thead' mode='html'>
  <thead>
    <xsl:apply-templates mode='html'/>
  </thead>
</xsl:template>

<xsl:template match='bml:tbody' mode='html'>
  <tbody>
    <xsl:apply-templates mode='html'/>
  </tbody>
</xsl:template>

<xsl:template match='bml:tr' mode='html'>
  <tr>
    <xsl:apply-templates mode='html'/>
  </tr>
</xsl:template>

<xsl:template match='bml:td' mode='html'>
  <td>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:attribute name='style'>
      <xsl:if test='@text-align'>text-align: <xsl:value-of select='@text-align'/>;</xsl:if>
    <xsl:if test='ancestor::bml:table/@text-align'>text-align: <xsl:value-of select='ancestor::bml:table/@text-align'/>;</xsl:if>
      <xsl:if test='@vertical-align'>vertical-align: <xsl:value-of select='@vertical-align'/>;</xsl:if>
    <xsl:if test='ancestor::bml:table/@vertical-align'>vertical-align: <xsl:value-of select='ancestor::bml:table/@vertical-align'/>;</xsl:if>
      <xsl:if test="@style"><xsl:value-of select='@style'/></xsl:if>
    </xsl:attribute>
    <xsl:if test='@colspan'><xsl:attribute name='colspan' select='@colspan'/></xsl:if>
    <xsl:if test='@rowspan'><xsl:attribute name='rowspan' select='@rowspan'/></xsl:if>
    <xsl:apply-templates mode='html'/>
  </td>
</xsl:template>


<xsl:template match='bml:leader' mode='html'/>
<!-- no way to do leaders in epub, yet -->



<xsl:template match='bml:metadata | bml:toc' mode='html'/>


<xsl:template match='bml:initcap' mode='html'>
  <img class='initcap' src="{@img}" style="height: {@height};" alt=""/>
  <span style='display:none;'><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match='bml:s' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>smcp</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates mode='html'/>
  </span>
</xsl:template>

<xsl:template match='bml:u' mode='html'>
  <span style="text-decoration:underline"><xsl:apply-templates mode='html'/></span>
</xsl:template>

<xsl:template match='bml:br' mode='html'>
  <br/>
</xsl:template>

<xsl:template match='bml:r' mode='html'>
  <span>
    <xsl:call-template name='transfer-common-attributes'>
      <xsl:with-param name='class'>r</xsl:with-param>
    </xsl:call-template>
  <xsl:apply-templates mode='html'/>
  </span>
</xsl:template>

<xsl:template match='bml:i' mode='html'>
  <i>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </i>
</xsl:template>

<xsl:template match='bml:b' mode='html'>
  <b>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </b>
</xsl:template>

<xsl:template match='bml:sup' mode='html'>
  <sup>
    <xsl:call-template name='transfer-common-attributes'/>
    <xsl:apply-templates mode='html'/>
  </sup>
</xsl:template>

<xsl:template match='bml:a' mode='html'>
  <a>
    <xsl:if test='@href'>
      <xsl:attribute name='href'><xsl:value-of select='@href'/></xsl:attribute>
    </xsl:if>
    <xsl:if test='@idref'>
      <xsl:attribute name='href'>
        <xsl:for-each select='key("id-key",@idref)'>
          <xsl:call-template name='file-of'/>
        </xsl:for-each>
        <xsl:text>.xhtml#</xsl:text>
        <xsl:value-of select='@idref'/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates mode='html'/>
  </a>
</xsl:template>

<xsl:template match='bml:script' mode='html'>
  <script type="{@type}">
    <xsl:apply-templates mode='copy'/>
  </script>
</xsl:template>

<xsl:template match='*' mode='html'>
  <xsl:message>*********************** unhandled <xsl:value-of select='name()'/></xsl:message>
  <xsl:apply-templates mode='html'/>
</xsl:template>

<xsl:template match="@*|node()" mode="copy">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="copy"/>
  </xsl:copy>
</xsl:template>


<!--======================================================================== / -->

<xsl:template match='/'>
  <xsl:result-document
     method='text'
     href='{$targetdir}/mimetype'>
    <xsl:text>application/epub+zip</xsl:text>
  </xsl:result-document>

  <xsl:result-document
      method='xml'
      href='{$targetdir}/META-INF/container.xml'>
    <container version='1.0' xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
      <rootfiles>
        <rootfile full-path="OEBPS/package.opf" media-type="application/oebps-package+xml"/>
      </rootfiles>
    </container>
  </xsl:result-document>

  <xsl:result-document
      method='xml'
      href='{$targetdir}/META-INF/manifest.xml'>

    <manifest 
        xmlns="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">

      <file-entry 
          full-path="mimetype" 
          media-type="text/plain"/>

      <file-entry
          full-path="META-INF/container.xml"
          media-type="text/xml"/>

      <file-entry
          full-path="OEBPS/package.opf"
          media-type="application/oebps-package+xml"/>

      <file-entry
          full-path="OEBPS/toc.ncx"
          media-type="application/x-dtbncx+xml"/>

      <xsl:if test='//bml:pagenum and $realpagenums = "yes"'>
        <file-entry
            full-path="OEBPS/page-map.xml"
            media-type="application/oebps-page-map+xml"/>
      </xsl:if>

      <xsl:if test='$fonts = "yes"'>
        <xsl:for-each select='tokenize (normalize-space ($epub-fonts), " ")'>
          <file-entry
              full-path="OEBPS/{.}"
              media-type="application/x-font-opentype"/>
        </xsl:for-each>

        <file-entry full-path="OEBPS/style.css" media-type="text/css"/>
        <file-entry full-path="OEBPS/style-common.css" media-type="text/css"/>
      </xsl:if>

      <xsl:if test="page-template != 'none'">
        <file-entry
            full-path="OEBPS/{$page-template}"
            media-type="application/vnd.adobe-page-template+xml"/>
      </xsl:if>

      <xsl:for-each select='bml:bml/bml:page-sequences/bml:page-sequence'>
        <xsl:variable name='targetfile'>
          <xsl:call-template name='file-of'/>
        </xsl:variable>

        <file-entry 
            full-path="OEBPS/{$targetfile}"
            media-type="application/xhtml+xml"/>
      </xsl:for-each>

      <xsl:for-each select='distinct-values(bml:bml/bml:page-sequences//bml:img/@src | bml:bml//bml:initcap/@img)'>
        <xsl:variable name='src'><xsl:value-of select='.'/></xsl:variable>
        <file-entry full-path='OEBPS/{$src}'>
          <xsl:attribute name='media-type'>
            <xsl:choose>
              <xsl:when test='ends-with($src,".png")'>image/png</xsl:when>
              <xsl:when test='ends-with($src,".jpg")'>image/jpeg</xsl:when>
              <xsl:when test='ends-with($src,".gif")'>image/gif</xsl:when>
              <xsl:when test='ends-with($src,".svg")'>image/svg+xml</xsl:when>
            </xsl:choose>
          </xsl:attribute>
        </file-entry>
      </xsl:for-each>

    </manifest>
  </xsl:result-document>

  <xsl:result-document
     method='text'
     href='{$targetdir}/OEBPS/style.css'>
    <xsl:text>@import "style-common.css";</xsl:text>
    <xsl:apply-templates select='document($eml,.)' mode='style.css'/>
  </xsl:result-document>

  <xsl:result-document
      method='xml'
      indent='yes'
      href='{$targetdir}/OEBPS/package.opf'>
    
    <package unique-identifier="uniqueId" version="2.0" xmlns="http://www.idpf.org/2007/opf">

      <metadata>
        <xsl:apply-templates select='//bml:bml/bml:metadata' mode='dc'/>

        <xsl:if test="//bml:bml/bml:cover">
          <meta name='cover' content='{translate (//bml:bml/bml:cover/@src, "/", "-")}'/>
        </xsl:if>

      </metadata>

      <manifest>

        <item href="package.opf"  id='opf'  media-type="text/xml"/>
        <item href="toc.ncx"      id='ncx'  media-type="application/x-dtbncx+xml"/>

        <xsl:if test='//bml:pagenum and $realpagenums = "yes"'>
          <item href="page-map.xml" id="pagemap" media-type="application/oebps-page-map+xml"/>
        </xsl:if>

        <xsl:if test='$fonts = "yes"'>
          <xsl:for-each select='tokenize (normalize-space ($epub-fonts), " ")'>
            <item
                href="{.}"
                id="{.}"
                media-type="application/x-font-opentype"/>
          </xsl:for-each>
          
          <item href='style.css' id='style.css' media-type='text/css'/>
          <item href='style-common.css' id='style-common.css' media-type='text/css'/>
        </xsl:if>
        
        <xsl:if test="$page-template != 'none'">
          <item href="{$page-template}" id='page-template'
                media-type="application/vnd.adobe-page-template+xml"/>
        </xsl:if>

        <xsl:for-each select='bml:bml/bml:page-sequences/bml:page-sequence'>
          <xsl:variable name='xx'>
            <xsl:call-template name='file-of'/>
          </xsl:variable>
          <item href="{$xx}.xhtml"     id='{$xx}' media-type="application/xhtml+xml"/>
        </xsl:for-each>

        <xsl:for-each select='distinct-values(bml:bml/bml:page-sequences//bml:img/@src | bml:bml/bml:cover/@src | bml:bml//bml:initcap/@img)'>
          <xsl:variable name='src'><xsl:value-of select='.'></xsl:value-of></xsl:variable>
          <xsl:variable name='id'><xsl:value-of select='translate($src,"/","-")'/></xsl:variable>
          <item href='{$src}' id='{$id}'>
            <xsl:attribute name='media-type'>
              <xsl:choose>
                <xsl:when test='ends-with($src,".png")'>image/png</xsl:when>
                <xsl:when test='ends-with($src,".jpg")'>image/jpeg</xsl:when>
                <xsl:when test='ends-with($src,".gif")'>image/gif</xsl:when>
                <xsl:when test='ends-with($src,".svg")'>image/svg+xml</xsl:when>
              </xsl:choose>
            </xsl:attribute>
          </item>

        </xsl:for-each>
      </manifest>

      <spine toc="ncx">
        <xsl:if test='//bml:pagenum and $realpagenums = "yes"'>
          <xsl:attribute name='page-map'>pagemap</xsl:attribute>
        </xsl:if>
        <xsl:for-each select='bml:bml/bml:page-sequences/bml:page-sequence'>
          <xsl:variable name='xx'>
            <xsl:call-template name='file-of'/>
          </xsl:variable>
          <itemref idref="{$xx}"/>
        </xsl:for-each>
      </spine>
    </package>

  </xsl:result-document>

  <xsl:result-document
      method="xml"
      indent="no"
      href="{$targetdir}/OEBPS/toc.ncx"
      doctype-public="-//NISO//DTD ncx 2005-1//EN"
      doctype-system="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd">

    <ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
      <head>
        <meta name="dtb:uid" content="{//bml:bml/bml:metadata/bml:electronique/@identificateur}"/>
        <meta name="dtb:depth" content="1"/>
        <meta name="dtb:totalPageCount" content="0"/>
        <meta name="dtb:maxPageNumber" content="0"/>
      </head>

      <docTitle><text><xsl:value-of select='bml:bml/bml:metadata/bml:electronique/bml:titre'/></text></docTitle>

      <xsl:apply-templates mode='ncx'/>
    </ncx>
  </xsl:result-document>

  <xsl:if test='//bml:pagenum and $realpagenums = "yes"'>
    <xsl:result-document
        method="xml"
        indent="yes"
        href="{$targetdir}/OEBPS/page-map.xml">
      
      <page-map xmlns="http://www.idf.org/2007/opf" >
        <xsl:apply-templates mode='pagemap'/>
      </page-map>
    </xsl:result-document>
  </xsl:if>

  <xsl:apply-templates select='bml:bml/bml:page-sequences/bml:page-sequence'
                       mode='html'/>
</xsl:template>

<xsl:template match='bml:page-sequence' mode='html'>
  <xsl:variable name='targetfile'>
    <xsl:text>OEBPS/</xsl:text>
    <xsl:call-template name='file-of'/>
  </xsl:variable>
  
  <xsl:result-document
      method="xml"
      indent="no"
      href="{$targetdir}/{$targetfile}.xhtml"
      doctype-public="-//W3C//DTD XHTML 1.1//EN" 
      doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
    
    <html 
          xml:lang="{//bml:bml/bml:metadata/bml:*[self::bml:monographie or self::bml:article]/bml:langue}">
      <head>
        <meta http-equiv="Content-type" content="text/html;charset=UTF-8"/>
        
        <link rel="stylesheet" type="text/css" href="style.css"/>
        
        <xsl:if test="$page-template != 'none'">
          <link rel="stylesheet" type="application/vnd.adobe-page-template+xml"
                href="{$page-template}"/>
        </xsl:if>
        
        <title>
          <xsl:value-of select="//bml:bml/bml:metadata/bml:electronique/bml:titre"/>
        </title>
      </head>
      
      <body>
        <!-- epubcheck complains if <a name=''> is directly 
             inside a <body>; wrap in a <div> to avoid that.
             Another reason is that kindlegen does not see the
             @id on the <body>, so we need something (the <div>)
             to transfer it to. -->
        <div>
          <xsl:call-template name='transfer-common-attributes'/>
          <xsl:apply-templates mode='html'>
            <xsl:with-param name='targetfile' select='$targetfile' tunnel='yes'/>
          </xsl:apply-templates>
        </div>
      </body>
    </html>
  </xsl:result-document>
</xsl:template>




<xsl:template match='bml:style[@body]' mode='style.css'>
  <xsl:text>body { font-family: </xsl:text>
  <xsl:value-of select='@body'/>
  <xsl:text>; }</xsl:text>

  <xsl:apply-templates mode='style.css'/>
</xsl:template>

<xsl:template match='bml:css' mode='style.css'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='bml:fonts[@href]' mode='style.css'>
  <xsl:apply-templates select='document (@href)' mode='style.css'/>
</xsl:template>

<xsl:template match='bml:font' mode='style.css'>
  <xsl:text>@font-face {</xsl:text>

  <xsl:text>font-family: </xsl:text>
  <xsl:value-of select='@f'/>
  <xsl:text>;</xsl:text>

  <xsl:text>font-weight: </xsl:text>
  <xsl:value-of select='@w'/>
  <xsl:text>;</xsl:text>

  <xsl:text>font-style: </xsl:text>
  <xsl:value-of select='@s'/>
  <xsl:text>;</xsl:text>

  <xsl:text>src: url('</xsl:text>
  <xsl:value-of select='@u'/>
  <xsl:text>'); }</xsl:text>

</xsl:template>
  
</xsl:stylesheet
>