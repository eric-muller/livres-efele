<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="#all"
  version="2.0">

  
<xsl:param name='targetdir'>zz</xsl:param>
<xsl:param name='style1'>none</xsl:param>
<xsl:param name='style2'>none</xsl:param>

<xsl:param name='singledtb'>true</xsl:param>


<xsl:template name='transfer-id'>
  <xsl:if test='@id'>
    <xsl:for-each select='@id'>
      <xsl:copy/>
    </xsl:for-each>
  </xsl:if>
</xsl:template>


<xsl:key name='id-key' match='*' use='@id'/>

<xsl:template name='file-containing'>
  <xsl:choose>
    <xsl:when test='$singledtb = "false"'>
      <xsl:text>text-</xsl:text>
      <xsl:number count='bml:page-sequence' level='any'/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>text</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name='file-containing-idref'>
  <xsl:for-each select='key("id-key",@idref)'>
    <xsl:call-template name='file-containing'/>
  </xsl:for-each>
</xsl:template>


<!--==================================================================== ncx -->

<xsl:template match='bml:toc' mode='ncx'>
  <navMap xmlns="http://www.daisy.org/z3986/2005/ncx/">
    <xsl:apply-templates mode='ncx'/>
  </navMap>
</xsl:template>



<xsl:template match='bml:tocentry' mode='ncx'>
  <xsl:variable name='targetfile'>
    <xsl:call-template name='file-containing-idref'/>
  </xsl:variable>

  <xsl:variable name='playorder'>
    <xsl:for-each select='key("id-key",@idref)'>
      <xsl:value-of select='count(preceding::bml:pagenum|preceding::bml:div|ancestor::bml:div)+1'/>
      </xsl:for-each>
  </xsl:variable>
  
  <navPoint id='navpoint.{generate-id()}' playOrder='{$playorder}'
            xmlns="http://www.daisy.org/z3986/2005/ncx/">

    <navLabel><text><xsl:value-of select='@label'/></text></navLabel>

    <content src='{$targetfile}.smil#{@idref}'/>

    <xsl:apply-templates mode='ncx'/>
  </navPoint>
</xsl:template>


<xsl:template match='bml:page-sequences' mode='ncx'>
  <pageList   xmlns="http://www.daisy.org/z3986/2005/ncx/">
    <xsl:apply-templates mode='ncx'/>
  </pageList>
</xsl:template>

<xsl:template match='bml:colnum' mode='ncx'/>

<xsl:template match='bml:pagenum' mode='ncx'>
  <xsl:variable name='id' select='if (@id) then @id else generate-id()'/>
  <xsl:variable name='targetfile'>
    <xsl:call-template name='file-containing'/>
  </xsl:variable>

  <xsl:variable name='playorder'>
    <xsl:value-of select='count(preceding::bml:pagenum|preceding::bml:div|ancestor::bml:div)+1'/>
  </xsl:variable>

  <pageTarget id='{$id}' type='normal' playOrder='{$playorder}' 
              xmlns="http://www.daisy.org/z3986/2005/ncx/">
    <navLabel><text><xsl:value-of select='@num'/></text></navLabel>

    <content src='{$targetfile}.smil#par.{$id}'/>

  </pageTarget>
</xsl:template>

<xsl:template match='node()' mode='ncx'>
  <xsl:apply-templates mode='ncx'/>
</xsl:template>


<!--================================================================= smil/dtb -->


<xsl:template match='bml:div | bml:blockquote | bml:smaller | bml:epigraphe | bml:dedicace | bml:poem | bml:letter | bml:ul | bml:li' mode='smil'>
  <xsl:param name='targetfile' tunnel='yes'/>
  <xsl:variable name='id' select='if (@id) then @id else generate-id()'/>

  <seq id='{$id}' xmlns="http://www.w3.org/2001/SMIL20/">
    <xsl:apply-templates mode='smil'/>
  </seq>
</xsl:template>


<xsl:template match='bml:div | bml:blockquote | bml:smaller | bml:poem | bml:lg | bml:letter | bml:ul | bml:li' mode='dtb'>
  <xsl:variable name='id' select='if (@id) then @id else generate-id()'/>
  
  <xsl:variable name='targetelement'>
    <xsl:choose>
      <xsl:when test='self::bml:div | self::bml:smaller | self::bml:letter'><xsl:text>div</xsl:text></xsl:when>
      <xsl:when test='self::bml:blockquote'><xsl:text>blockquote</xsl:text></xsl:when>
      <xsl:when test='self::bml:poem'><xsl:text>poem</xsl:text></xsl:when>
      <xsl:when test='self::bml:lg'><xsl:text>linegroup</xsl:text></xsl:when>
      <xsl:when test='self::bml:ul'><xsl:text>list</xsl:text></xsl:when>
      <xsl:when test='self::bml:li'><xsl:text>li</xsl:text></xsl:when>
    </xsl:choose>
  </xsl:variable>

  <xsl:element name='{$targetelement}'
               xmlns="http://www.daisy.org/z3986/2005/dtbook/">
    <xsl:attribute name='id' select='$id'/>
    <xsl:apply-templates mode='dtb'/>
  </xsl:element>
</xsl:template>


<xsl:template match='bml:colnum' mode='smil'/>

<xsl:template match='bml:pagenum' mode='smil'>
  <xsl:param name='targetfile' tunnel='yes'/>
  <xsl:variable name='id' select='if (@id) then @id else generate-id()'/>

   <seq id='seq.{$id}' xmlns='http://www.w3.org/2001/SMIL20/'>
    <par id='par.{$id}' customTest='page'>
      <text region="texte" src='{$targetfile}.xml#page.{@num}'/>
    </par>
  </seq>
</xsl:template>


<xsl:template match='bml:h1 | bml:h2 | bml:h3 | bml:h4 | bml:h5 | bml:p | bml:l | bml:salutation | bml:signature | bml:date' mode='smil'>
  <xsl:param name='targetfile' tunnel='yes'/>
  <xsl:variable name='id' select='if (@id) then @id else generate-id()'/>

  <seq id='{$id}' xmlns="http://www.w3.org/2001/SMIL20/">
    <xsl:for-each-group 
        select='* | text()' 
        group-adjacent='if (self::bml:pagenum) then "pagenum" else if (self::bml:noteref) then "noteref" else ""'>

      <xsl:choose>
        <xsl:when test='current-grouping-key() = "pagenum"'>
          <par id='par.{generate-id()}' customTest='page'>
            <text region="texte" src='{$targetfile}.xml#{generate-id()}'/>
          </par>
        </xsl:when>
          
        <xsl:when test='current-grouping-key() = "noteref"'>
          <xsl:variable name='xx'>
            <xsl:choose>
              <xsl:when test='key("id-key",current-group()/@noteid)/@type = "v"'>variante</xsl:when>
              <xsl:otherwise>note</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <par id='par.noteref.{generate-id()}'>
            <xsl:attribute name='customTest'><xsl:value-of select="$xx"/>ref</xsl:attribute>
            <text region="texte" src='{$targetfile}.xml#{generate-id()}'/>
          </par>
          <par id='par.note.{generate-id()}'>
            <xsl:attribute name='customTest'><xsl:value-of select="$xx"/></xsl:attribute>
            <text region="notes" src='{$targetfile}.xml#note.{current-group()/@noteid}'/>
          </par>
        </xsl:when>
        
        <xsl:otherwise>
          <par id='par.{generate-id()}'>
            <text region="texte" src='{$targetfile}.xml#{generate-id()}'/>
          </par>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:for-each-group>
  </seq>
</xsl:template>

<xsl:template match='bml:h1 | bml:h2 | bml:h3 | bml:h4 | bml:h5 | bml:p | bml:l | bml:salutation | bml:signature | bml:date' mode='dtb'>
  <xsl:variable name='id' select='if (@id) then @id else generate-id()'/>

  <xsl:variable name='targetelement'>
    <xsl:choose>
      <xsl:when test='self::bml:p | self::bml:salutation | self::bml:signature | self::bml:date'>
        <xsl:text>p</xsl:text>
      </xsl:when>
      <xsl:when test='self::bml:h1'><xsl:text>h1</xsl:text></xsl:when>
      <xsl:when test='self::bml:h2'><xsl:text>h2</xsl:text></xsl:when>
      <xsl:when test='self::bml:h3'><xsl:text>h3</xsl:text></xsl:when>
      <xsl:when test='self::bml:h4'><xsl:text>h4</xsl:text></xsl:when>
      <xsl:when test='self::bml:h5'><xsl:text>h5</xsl:text></xsl:when>
      <xsl:when test='self::bml:l'><xsl:text>line</xsl:text></xsl:when>
    </xsl:choose>
  </xsl:variable>

  <xsl:element name='{$targetelement}'
               namespace='http://www.daisy.org/z3986/2005/dtbook/'>
    <xsl:attribute name='id' select='$id'/>

    <xsl:if test='parent::bml:note/bml:p[1] = .'>
      <xsl:apply-templates select='..' mode='notelabel'/>
      <xsl:text>. </xsl:text>
    </xsl:if>

    <xsl:for-each-group 
        select='* | text()' 
        group-adjacent='if (self::bml:pagenum) then "page" else if (self::bml:noteref) then "noteref" else ""'>
      <span xmlns='http://www.daisy.org/z3986/2005/dtbook/' 
            id='{generate-id()}'><xsl:apply-templates mode='dtb' select='current-group()'/></span>
    </xsl:for-each-group>
  </xsl:element>
</xsl:template>



<xsl:template match='bml:img' mode='smil'>
  <xsl:param name='targetfile' tunnel='yes'/>
  <xsl:param name='id' select='if (@id) then @id else generate-id()'/>

  <par id='{$id}' xmlns='http://www.w3.org/2001/SMIL20/'>
    <text region="texte" src='{$targetfile}.xml#{$id}'/>
  </par>
</xsl:template>


<xsl:template match='bml:img' mode='dtb'>
  <xsl:param name='id' select='if (@id) then @id else generate-id()'/>
  <img id='{$id}' src='{@src}' alt='{@alt}' xmlns='http://www.daisy.org/z3986/2005/dtbook/'/>
</xsl:template>




<xsl:template match='bml:vsep[@class="emptyline"]' mode="dtb">
  <p xmlns="http://www.daisy.org/z3986/2005/dtbook/">&#xa0;</p>
</xsl:template>

<xsl:template match='bml:vsep[@class="fewlines"]' mode="dtb">
  <p xmlns="http://www.daisy.org/z3986/2005/dtbook/">&#xa0;</p>
  <p xmlns="http://www.daisy.org/z3986/2005/dtbook/">&#xa0;</p>
  <p xmlns="http://www.daisy.org/z3986/2005/dtbook/">&#xa0;</p>
  <p xmlns="http://www.daisy.org/z3986/2005/dtbook/">&#xa0;</p>
  <p xmlns="http://www.daisy.org/z3986/2005/dtbook/">&#xa0;</p>
</xsl:template>

<xsl:template match='bml:vsep[@class="threestars"]' mode="dtb">
  <h2 xmlns="http://www.daisy.org/z3986/2005/dtbook/">*&#xa;*&#xa0;&#xa0;*</h2>
</xsl:template>

<xsl:template match='bml:vsep[@class="dots"]' mode="dtb">
  <h2 style="text-align-last: justify" xmlns="http://www.daisy.org/z3986/2005/dtbook/">. . . . . . . . . . . . . . . . . . . . . .</h2>
</xsl:template>

<xsl:template match='bml:vsep[@class="rule"]' mode="dtb">
  <hr/>
</xsl:template>







<xsl:template match='bml:colnum' mode='dtb'>
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match='bml:pagenum' mode='dtb'>
  <pagenum id='page.{@num}' xmlns="http://www.daisy.org/z3986/2005/dtbook/">[page <xsl:value-of select='@num'/>]</pagenum>
</xsl:template>

<xsl:template match='bml:pageref' mode='dtb'>
  <a href='#page.{@num}'><xsl:apply-templates/></a>
</xsl:template>



<xsl:template match='bml:note' mode='notelabel'>
  <xsl:value-of select='if (@label) then @label else count(preceding-sibling::bml:note) + 1'/>
</xsl:template>

<xsl:template match='bml:noteref' mode='dtb'>
  <noteref xmlns="http://www.daisy.org/z3986/2005/dtbook/" idref='#note.{@noteid}'><xsl:apply-templates select='key("id-key",@noteid)[1]' mode='notelabel'/></noteref>
</xsl:template>


<xsl:template match='bml:note' mode='smil'/>
<!-- generated just after the noteref -->

<xsl:template match='bml:note' mode='dtb'>
  <note id='note.{@id}' xmlns="http://www.daisy.org/z3986/2005/dtbook/">
    <xsl:apply-templates mode='dtb'/>
  </note>
</xsl:template>





<xsl:template match='bml:metadata | bml:toc' mode='smil'/>

<xsl:template match='bml:metadata | bml:toc' mode='dtb'/>








<xsl:template match='bml:s | bml:u' mode='dtb'>
  <xsl:apply-templates mode='dtb'/>
</xsl:template>

<xsl:template match='bml:br' mode='dtb'>
  <br xmlns='http://www.daisy.org/z3986/2005/dtbook/'/>
</xsl:template>

<xsl:template match='bml:i' mode='dtb'>
  <em xmlns='http://www.daisy.org/z3986/2005/dtbook/'>
    <xsl:apply-templates mode='dtb'/>
  </em>
</xsl:template>

<xsl:template match='bml:b' mode='dtb'>
  <em xmlns='http://www.daisy.org/z3986/2005/dtbook/'>
    <xsl:apply-templates mode='dtb'/>
  </em>
</xsl:template>

<xsl:template match='bml:sup' mode='dtb'>
  <sup xmlns='http://www.daisy.org/z3986/2005/dtbook/'>
    <xsl:apply-templates mode='dtb'/>
  </sup>
</xsl:template>


<xsl:template match='node()' mode='smil'>
  <xsl:apply-templates mode='smil'/>
</xsl:template>

<xsl:template match='*' mode='dtb'>
  <xsl:apply-templates mode='dtb'/>
</xsl:template>



<!--======================================================================== / -->


<xsl:template name='generate-dtb-and-smil'>
  <xsl:variable name='targetfile'>
    <xsl:call-template name='file-containing'/>
  </xsl:variable>

    <xsl:result-document
        method="xml"
        indent="yes"
        href="{$targetdir}/{$targetfile}.smil"
        doctype-public="-//NISO//DTD dtbsmil 2005-2//EN" 
        doctype-system="http://www.daisy.org/z3986/2005/dtbsmil-2005-2.dtd">
      
      <smil xmlns="http://www.w3.org/2001/SMIL20/">
        <head>
          <meta name="dtb:uid" content='{//bml:bml/bml:metadata/bml:electronique/bml:id}'/>
          <meta name="dtb:generator" content="efele 1.0"/>
          <meta name="dtb:totalElapsedTime" content="0"/>
<!--
          <layout>
            <region id='texte' top='0%' left='0%' right='0%' bottom='15%'/>
            <region id='notes' top='85%' left='0%' right='0%' bottom='0%'/>
          </layout>
-->

          <customAttributes>
            <xsl:if test="//bml:note">
              <customTest id='noteref'     defaultState='true' override='visible'/>
              <customTest id='note'        defaultState='true' override='visible'/>
            </xsl:if>
            <xsl:if test="//bml:note[@type='v']">
              <customTest id='varianteref' defaultState='true' override='visible'/>
              <customTest id='variante'    defaultState='true' override='visible'/>
            </xsl:if>
            <xsl:if test="//bml:pagenum">
              <customTest id='page'        defaultState='true' override='visible'/>
            </xsl:if>
          </customAttributes>
        </head>
        <body>
          <seq id='{generate-id()}'>
            <xsl:apply-templates mode='smil'>
              <xsl:with-param name='targetfile' select='$targetfile' tunnel='yes'/>
            </xsl:apply-templates>
          </seq>
        </body>
      </smil>
    </xsl:result-document>
    
    <xsl:result-document
        method="xml"
        indent="no"
        href="{$targetdir}/{$targetfile}.xml"
        doctype-public="-//NISO//DTD dtbook 2005-3//EN" 
        doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-3.dtd">
      
      <xsl:if test="$style1 != 'none'">
        <xsl:processing-instruction
            name="xml-stylesheet">
          <xsl:text>href="</xsl:text>
          <xsl:value-of select='$style1'/>
          <xsl:text>" type="text/css"</xsl:text>
        </xsl:processing-instruction>
      </xsl:if>

      <dtbook version="2005-3" xmlns="http://www.daisy.org/z3986/2005/dtbook/">
        <head>
          <meta name="dtb:uid"  content="{//bml:bml/bml:metadata/bml:electronique/bml:id}"/>
          <meta name="dc:Title" content="{//bml:bml/bml:metadata/bml:original/bml:titre}"/>
        </head>
        <book>
          <bodymatter>
            <level>
              <xsl:apply-templates mode='dtb'>
                <xsl:with-param name='targetfile' select='$targetfile' tunnel='yes'/>
            </xsl:apply-templates>
            </level>
          </bodymatter>
        </book>
      </dtbook>
    </xsl:result-document>
</xsl:template>


<xsl:template match='/'>
  <xsl:variable name="tocdepth">
    <xsl:for-each select="bml:bml/bml:toc/descendant::bml:tocentry">
      <xsl:sort select="count(ancestor::bml:tocentry)" data-type="number"/>
      <xsl:if test="position()=last()">
        <xsl:copy-of select="count(ancestor::bml:tocentry|ancestor::bml:toc)"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name='pagecount' select="count(descendant::bml:pagenum)"/>


  <xsl:result-document
      method="xml"
      indent="yes"
      href="{$targetdir}/toc.ncx"
      doctype-public="-//NISO//DTD ncx 2005-1//EN"
      doctype-system="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd">

    <ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
      <head>
        <xsl:if test="//bml:note">
          <smilCustomTest id='noteref'     defaultState='true' override='visible' bookStruct='NOTE_REFERENCE'/>
          <smilCustomTest id='note'        defaultState='true' override='visible' bookStruct='NOTE'/>
        </xsl:if>
        <xsl:if test="//bml:note[@type='v']">
          <smilCustomTest id='varianteref' defaultState='true' override='visible' bookStruct='NOTE_REFERENCE'/>
          <smilCustomTest id='variante'    defaultState='true' override='visible' bookStruct='NOTE'/>
        </xsl:if>
        <xsl:if test="//bml:pagenum">
          <smilCustomTest id='page'        defaultState='true' override='visible' bookStruct='PAGE_NUMBER'/>
        </xsl:if>

        <meta name="dtb:uid" content="{//bml:bml/bml:metadata/bml:electronique/bml:id}"/>
        <meta name="dtb:generator" content="efele 1.0"/>
        <meta name="dtb:depth" content="{$tocdepth}"/>
        <meta name="dtb:totalPageCount" content="{$pagecount}"/>
        <meta name="dtb:maxPageNumber" content="0"/>
      </head>

      <docTitle><text><xsl:value-of select='bml:bml/bml:metadata/bml:original/bml:titre'/></text></docTitle>

      <xsl:apply-templates mode='ncx'/>
    </ncx>
  </xsl:result-document>

  <xsl:choose>
    <xsl:when test='$singledtb = "false"'>
      <xsl:for-each select='bml:bml/bml:page-sequences/bml:page-sequence'>
        <xsl:call-template name='generate-dtb-and-smil'/>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:for-each select='bml:bml/bml:page-sequences'>
        <xsl:call-template name='generate-dtb-and-smil'/>
      </xsl:for-each>
    </xsl:otherwise>
  </xsl:choose>
  
  <xsl:result-document
      method='xml'
      indent='yes'
      href='{$targetdir}/package.opf'
      doctype-public="+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN"
      doctype-system="http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd">
    
    <package unique-identifier="uniqueId" xmlns="http://openebook.org/namespaces/oeb-package/1.0/">
      <metadata>
        <dc-metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
          <dc:Format>ANSI/NISO Z39.86-2005</dc:Format>

          <dc:Title><xsl:value-of select="//bml:bml/bml:metadata/bml:electronique/bml:titre"/></dc:Title>

          <dc:Creator>
            <xsl:for-each select='//bml:bml/bml:metadata/bml:monographie/bml:auteur'>
              <xsl:value-of select='bml:nom-couverture'/>
            </xsl:for-each>
          </dc:Creator>

          <dc:Publisher>
            <xsl:for-each select='//bml:bml/bml:metadata/bml:monographie/bml:editeur'>
              <xsl:if test='bml:nom'>
                <xsl:value-of select='bml:nom'/>
                <xsl:text>, </xsl:text>
              </xsl:if>
              <xsl:value-of select='bml:ville'/>
            </xsl:for-each>
          </dc:Publisher>

          <dc:Date><xsl:value-of select="//bml:bml/bml:metadata/bml:monographie/bml:date"/></dc:Date>

          <dc:Language><xsl:value-of select="//bml:bml/bml:metadata/bml:*/bml:langue"/></dc:Language>

          <dc:Identifier id="uniqueId"><xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/@identificateur'/></dc:Identifier>
        </dc-metadata>

        <x-metadata>
          <meta content="textNCX" name="dtb:multimediaType"/>
          <meta content="text" name="dtb:multimediaContent"/>
          <meta content="0" name="dtb:totalTime"/>
        </x-metadata>
      </metadata>

      <manifest>

        <item href="package.opf"  id='opf'  media-type="text/xml"/>
        <item href="toc.ncx"      id='ncx'  media-type="application/x-dtbncx+xml"/>
        <item href="resource.res" id='resource'  media-type="application/x-dtbresource+xml"/>

        <xsl:if test="$style1 != 'none'">
          <item href="{$style1}"  id='style1' media-type="text/css"/>
        </xsl:if>

        <xsl:if test="$style2 != 'none'">
          <item href="{$style2}"   id='style2' media-type="text/css"/>
        </xsl:if>

        <xsl:choose>
          <xsl:when test='$singledtb = "false"'>
            <xsl:for-each select='bml:bml/bml:page-sequences/bml:page-sequence'>
              <xsl:variable name='xx'>
                <xsl:call-template name='file-containing'/>
              </xsl:variable>
              
              <item href="{$xx}.xml"     id='text-{$xx}' media-type="application/x-dtbook+xml"/>
              <item href="{$xx}.smil"    id='smil-{$xx}' media-type="application/smil"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <item href="text.xml" id="text" media-type="application/x-dtbook+xml"/>
            <item href="text.smil" id="smil" media-type="application/smil"/>
          </xsl:otherwise>
        </xsl:choose>

        <xsl:for-each select='bml:bml/bml:page-sequences//bml:img'>
          <item href='{@src}' id='{generate-id()}' media-type='image/png'/>
        </xsl:for-each>
      </manifest>

      <spine>
        <xsl:choose>
          <xsl:when test='$singledtb = "false"'>
            <xsl:for-each select='bml:bml/bml:page-sequences/bml:page-sequence'>
              <xsl:variable name='xx'>
                <xsl:call-template name='file-containing'/>
              </xsl:variable>
              <itemref idref="smil-{$xx}"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <itemref idref="smil"/>
          </xsl:otherwise>
        </xsl:choose>
      </spine>

    </package>
  </xsl:result-document>



</xsl:template>


</xsl:stylesheet>
